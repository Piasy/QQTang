package com.example.client.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.client.model.Config;
import com.example.client.model.Flag;
import com.example.client.model.GameModel;
import com.example.client.model.HallModel;
import com.example.client.model.SigninModel;
import com.example.client.model.SignupModel;


/**
 * <p>This class is the controller part of the MVC design pattern,
 * and it also completes the singletom design pattern, any viewer
 * or model could get the instance of controller via the static
 * method getController().</p>
 * 
 * <p>This class control all the logic parts of this software, 
 * corresponding with viewer's request, drive models to handle
 * these requests, and control the software work status transfer
 * during its running.</p>
 * 
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class Controller
{
	private static int instanceCount = 0;
	private static Controller instance;
	
	SigninModel signinModel = null;
	HallModel hallModel = null;
	GameModel gameModel = null;
	Handler viewerHandler = null;
	Thread controllerThread = null;
	
	Flag signinFlag, enterFlag, leaveFlag, readyFlag, unreadyFlag, signoutFlag;
	boolean keepRunning = true;
	
	/**
	 * This method will return the sigletom instance
	 * of controller, all viewers or models will get the
	 * same controller, no matter when and where they call
	 * this method.
	 * @param none
	 * @return the only instance of controller
	 * */
	public static Controller getController()
	{
		if (instanceCount == 0)
		{
			instance = new Controller();
			instanceCount ++;
		}
		Log.d(Config.LOG_TAG, "controller ref at : " + instance + " , count : " + instanceCount);
		
		return instance;
	}
	
	String hallIP = "", gameIP = "";
	int hallPort, gamePort;
	String key = "";
	String gameKey = "";

	String username = "";	
	/**
	 * The signin method, most robust.
	 * When a signin request is made by viewer, controller will
	 * let  {@link com.example.client.model.SigninModel}  to
	 * communicate with LoginServer.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.SigninModel}.
	 * @param username, password, target hall name
	 * @return send message via handler
	 * */
	public void signin(String username, String password, String target)
	{
		this.username = username;
		synchronized (signinFlag)
		{
		//	if (signinModel == null)
				signinModel = new SigninModel(Config.loginIP, Config.loginPort, username, password, target);
			signinFlag.setValue(true);
			Log.v("myinfo2", "ok1");
		}
	}

	/**
	 * The signout method.
	 * This method won't send message to
	 * server any more, due to the changes of LoginServer.
	 * It just let the software status change to signin status.
	 * @param none
	 * @return none
	 * */
	public void signout()
	{
		period = 1;
		curPos = Config.CUR_POS_SIGN_IN;
		hallModel.sendSignoutMSG();
//		synchronized (signoutFlag)
//		{
//			signoutFlag.setValue(true);
//		}
	}

	SignupModel signupModel;
	Flag signupFlag;
	/**
	 * The signup method.
	 * When a signup request is made by viewer, controller will
	 * let  {@link com.example.client.model.SignupModel}  to
	 * communicate with RegisterServer.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.SignupModel}.
	 * @param username, password, password confirm, nickname
	 * @return send message via handler
	 * */
	public void signup(String username, String password,
			String passwordConfirm, String name)
	{
		synchronized (signupFlag)
		{
			signupModel = new SignupModel(Config.loginIP, Config.SIGNUP_PORT, username, password, passwordConfirm, name);
			signupFlag.setValue(true);
			curPos = Config.CUR_POS_SIGN_UP;
		}
	}

	/**
	 * Get the detail info of given range rooms.
	 * This method will return the status of rooms
	 * which number range between startRoomNum and endRoomNum.</br>
	 * When viewers want to know what is in the hall, 
	 * especially in some rooms, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * give those information, which will be uphold during the running
	 * of this software.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * change: pageNum -> room range
	 * @param [startRoomNum, endRoomNum]
	 * @return json format string
	 * */
	//public String getHallSnapShot(int pageNum)
	public String getHallSnapShot(int startRoomNum, int endRoomNum)
	{
		//Log.d("fuck4", "finish 4");
		return hallModel.getHallSnapShot(startRoomNum, endRoomNum);
	}
	
	/**
	 * Get user self details.
	 * This method will return the detail information of the player
	 * himself. When viewers want to know it, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * give this information, which will be uphold during the running
	 * of this software.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param none
	 * @return json format string
	 * */
	public JSONObject getMyDetails()
	{
		return hallModel.getMyDetails();
	}
	
	/**
	 * Get player himself current room number.
	 * When viewers want to know it, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * give this information, which will be uphold during the running
	 * of this software.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param none
	 * @return current room number, -1 if not in any room
	 * */
	public int getMyRoomNum()
	{
		return hallModel.getMyRoomNum();
	}
	
	/**
	 * Query whether game starts.
	 * @param none
	 * @return whether game starts
	 * */
	public boolean isGameStart()
	{
		boolean ret = false;
		if (period == 3)
			ret = true;
		return ret;
	}

	int enterRoomNum, enterSeatPos;
	/**
	 * Enter the specific room and specific position.
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * send this request to HallServer. The result will be 
	 * send to viewer via handler when 
	 * {@link com.example.client.model.HallModel} receive 
	 * the message which say that the player himself
	 * has got in this room.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param room num, seat position
	 * @return send via handler
	 * */
	public void enter(int roomNum, int seatPos)
	{
		synchronized (enterFlag)
		{
			enterRoomNum = roomNum;
			enterSeatPos = seatPos;
			enterFlag.setValue(true);
		}
	}
	
	/**
	 * Enter the specific room and any position.
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * send this request to HallServer. The result will be 
	 * send to viewer via handler when 
	 * {@link com.example.client.model.HallModel} receive 
	 * the message which say that the player himself
	 * has got in this room.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param room num
	 * @return send via handler
	 * */
	public void enter(int roomNum)
	{
		synchronized (enterFlag)
		{
			enterRoomNum = roomNum;
			enterSeatPos = Config.LEAST_FREE_SEAT;
			enterFlag.setValue(true);
		}
	}
	
	/**
	 * Enter any room and any position.
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * send this request to HallServer. The result will be 
	 * send to viewer via handler when 
	 * {@link com.example.client.model.HallModel} receive 
	 * the message which say that the player himself
	 * has got in this room.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param none
	 * @return send via handler
	 * */
	public void enter()
	{
		synchronized (enterFlag)
		{
			enterRoomNum = Config.LEAST_FREE_ROOM;
			enterSeatPos = Config.LEAST_FREE_SEAT;
			enterFlag.setValue(true);
		}		
	}

	/**
	 * Leave current room.
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * send this request to HallServer. The result will be 
	 * send to viewer via handler when 
	 * {@link com.example.client.model.HallModel} receive 
	 * the message which say that the player himself
	 * has left this room.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 *  @param none
	 *  @return send via handler
	 * */
	public void leave()
	{
		synchronized (leaveFlag)
		{
			leaveFlag.setValue(true);
		}
	}

	/**
	 * Ready for game.
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * send this request to HallServer. The result will be 
	 * send to viewer via handler when 
	 * {@link com.example.client.model.HallModel} receive 
	 * the message which say that the player himself
	 * has been ready.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param none
	 * @return send via handler
	 * */
	public void ready()
	{
		synchronized (readyFlag)
		{
			readyFlag.setValue(true);
		}
	}

	/**
	 * Unready for game.
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * send this request to HallServer. The result will be 
	 * send to viewer via handler when 
	 * {@link com.example.client.model.HallModel} receive 
	 * the message which say that the player himself
	 * has been unready.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param none
	 *  @return send via handler
	 * */
	public void unready()
	{
		synchronized (unreadyFlag)
		{
			unreadyFlag.setValue(true);
		}
	}
	
	/**
	 * Set handler of viewer activity, used for send message.
	 * 
	 * @param viewerHandler
	 * @return none
	 * */
	public void setHandler(Handler viewerHandler)
	{
		this.viewerHandler = viewerHandler;
	}
	
	int mapID = 0;
	/**
	 * Set the map ID of game.
	 * 
	 * @param map ID
	 * @return none
	 * */
	public void setMapID(int id)
	{
		mapID = id;
	}
	
	/**
	 * Query the map ID
	 * @param none
	 * @return map ID
	 * */
	public int getMapID()
	{
		return mapID;
	}
		
	int moveStatusCode = 0;
	Flag moveChangeFlag, placeBombCodeFlag;
	/**
	 * When user touch the screen, call this method with code [0, 3].
	 * When user release, call this method with code 4
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.GameModel}  to
	 * send this request to GameServer. 
	 * For more specific information, please see 
	 *  {@link com.example.client.model.GameModel}.
	 * @param code:
	 * 0 up
	 * 1 down
	 * 2 left
	 * 3 right
	 * 4 stop
	 * others illegal
	 * @return none
	 * */
	public void changeMoveStatus(int code)
	{
		if (0 <= code && code <= 4)
		{
			moveStatusCode = code;
			//Log.d("gameview", "testtest controller go");
			
			synchronized (moveChangeFlag)
			{
				moveChangeFlag.setValue(true);
				//Log.d("gameview", "testtest controller go2 period = " + period);
			}
		}
	}
	
	/**
	 * Place a bomb.
	 * When viewers send this request, controller will 
	 * let  {@link com.example.client.model.GameModel}  to 
	 * send this request to GameServer. 
	 * For more specific information, please see 
	 *  {@link com.example.client.model.GameModel}.
	 *  
	 * @param none
	 * @return none
	 * */
	public void placeBomb()
	{
		synchronized (placeBombCodeFlag)
		{
			placeBombCodeFlag.setValue(true);
		}
	}
	
	//get prop need'nt viewer tell me, I will calculate it
	
	boolean gameServerOK = true;
	/**
	 * Get game status, the map, prop, player, bomb info, which will be
	 * used for viewer to draw the game view.
	 * When viewers want to know what is in the hall, 
	 * especially in some rooms, controller will 
	 * let  {@link com.example.client.model.GameModel}  to
	 * give those information, which will be uphold during the running
	 * of this software.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.GameModel}.
	 *  @param none
	 * @return game status info
	 * */
	public String getGameStatus()
	{
		String ret = null;
		JSONObject error = new JSONObject();
		try
		{
			error.put("type", "error");
			ret = error.toString(); 
		} 
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "Json exception");
		}
		if (gameServerOK)
			ret = gameModel.getGameStatus();
		//Log.d(Config.LOG_TAG, "prepare1" + ret);
		return ret;
	}
	
	/**
	 * Set game status to over.
	 * When {@link com.example.client.model.GameModel} 
	 * receive the game over information, it will call this 
	 * method to let controller change the software status to
	 * room.
	 * @param none
	 * @return none
	 * */
	public void setGameToOver()
	{
		period = 2;
		curPos = Config.CUR_POS_ROOM;
		gameBegin = false;
		Log.d("fuck2", "finish 2");
	}
	
	/**
	 * Get talk messages.
	 * When viewers want to know it, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * give this information, which will be uphold during the running
	 * of this software.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param message count
	 * @return json format string
	 * */
	public String getCurrentMessage(int length)
	{
		return hallModel.getCurrentMessage(length);
	}
	
	/**
	 * Send talk messages
	 * When viewers want to send message, controller will 
	 * let  {@link com.example.client.model.HallModel}  to
	 * send it to HallServer.</br>
	 * For more specific information, please see 
	 *  {@link com.example.client.model.HallModel}.
	 * @param message to send
	 * @return none
	 * */
	public void sendTalkMessage(String message)
	{
		hallModel.sendTalkMessage(message);
	}
	
	int curPos = Config.CUR_POS_SIGN_IN;
	/**
	 * Get current position of the player himself.
	 * 
	 * @param none
	 * @return current position code, For more specific information, please see 
	 *  {@link com.example.client.model.Config}. 
	 * */
	public int getCurPos()
	{
		return curPos;
	}
	
	int period;
	Flag gameFlag;
	/**
	 * main task thread
	 * */
	Runnable controllerRunnable = new Runnable()
	{
		
		@Override
		public void run()
		{
			while (keepRunning)
			{
				switch (period)
				{
				case 1:
					{
						synchronized (signinFlag)
						{
							if (signinFlag.getValue())
							{
								String status = "InternetError";
								Log.v("myinfo", "going to signin");
								boolean signinSuccess = signinModel.signin();
								Log.v("myinfo2", "signin result : " + signinSuccess);
								if (signinSuccess)
								{
									JSONObject signinResult = signinModel.getSigninResponse();
									
									try
									{
										boolean legalMSG = (signinResult != null) && signinResult.has("key") && 
											    signinResult.has("ip") && signinResult.has("port") && 
											    signinResult.has("details") && (signinResult.getString("key") != null) &&
											    (signinResult.getString("ip") != null) && (signinResult.getJSONObject("details") != null);
										if (legalMSG)
										{
											/**
											 * JsonObject do not validate the key
											 * */
											key = signinResult.getString("key"); 
											hallIP = signinResult.getString("ip");
											hallPort = signinResult.getInt("port");
											
											hallModel = new HallModel(hallIP, hallPort, signinResult.getString("user"), key, controllerHandler);
											boolean hallSuccess = hallModel.init();
											if (hallSuccess)
											{
												status = "Success";
												hallModel.listen();
												period = 2;
												curPos = Config.CUR_POS_HALL;
											}
											else
											{
												curPos = Config.CUR_POS_SIGN_IN;
											}
										}
										else
										{
											if (signinResult.has("reason"))
												status = signinResult.getString("reason");
										}
										
									} 
									catch (JSONException e)
									{
										status = "InternetError";
									}
								}
								JSONObject data = new JSONObject();
								try
								{
									data.put("type", "signin");
									data.put("status", status);
								} 
								catch (JSONException e)
								{
									/**
									 * couldn't have exception
									 * */
								}
								synchronized (viewerHandler)
								{
									Message msg = Message.obtain();
									msg.obj = data.toString();
									viewerHandler.sendMessage(msg);
									signinFlag.setValue(false);
								}
							}
						}
						
						synchronized (signupFlag)
						{
							if (signupFlag.getValue())
							{
								String result = signupModel.signup();
								synchronized (viewerHandler)
								{
									Message msg = Message.obtain();
									msg.obj = result;
									viewerHandler.sendMessage(msg);
								}
								signupFlag.setValue(false);
							}
						}
					}
					break;
				case 2:
					{
						//Log.d("fuck3", "finish 3");
						synchronized (enterFlag)
						{
							if (enterFlag.getValue())
							{
								hallModel.sendEnterMSG(enterRoomNum, enterSeatPos);
								enterFlag.setValue(false);
							}
						}
						
						synchronized (leaveFlag)
						{
							if (leaveFlag.getValue())
							{
								hallModel.sendLeaveMSG();
								leaveFlag.setValue(false);
							}
						}
						
						synchronized (readyFlag)
						{
							if (readyFlag.getValue())
							{
								hallModel.sendReadyMSG();
								readyFlag.setValue(false);
							}
						}
						
						synchronized (unreadyFlag)
						{
							if (unreadyFlag.getValue())
							{
								hallModel.sendUnreadyMsg();
								unreadyFlag.setValue(false);
							}
						}
						
						//this won'nt be called
						synchronized (signoutFlag)
						{
							if (signoutFlag.getValue())
							{
								hallModel.sendSignoutMSG();
								signoutFlag.setValue(false);
								period = 1;
							}
						}
						
					}
					break;
				case 3:
					{
						synchronized (moveChangeFlag)
						{
							if (moveChangeFlag.getValue())
							{
								moveChangeFlag.setValue(false);
								Log.d("gameview", "testtest controller call");
								gameModel.moveStatusChange(new String(Config.DIRECTION[moveStatusCode]));
							}
						}
						
						synchronized (placeBombCodeFlag)
						{
							if (placeBombCodeFlag.getValue())
							{
								gameModel.placeBomb();
								placeBombCodeFlag.setValue(false);
							}
						}
						

						synchronized (gameFlag)
						{
							if (gameFlag.getValue())
							{
								int delay = gameModel.init();
								Log.d(Config.LOG_TAG, "game delay " + delay);
								if (delay != -1)
								{
									gameModel.listen();
									curPos = Config.CUR_POS_GAME;
								}
								else
								{
									gameServerOK = false;
									period = 1;
									curPos = Config.CUR_POS_SIGN_IN;
								}
								//Log.d(Config.LOG_TAG, "game listen ok ");
								gameFlag.setValue(false);
							}
						}
					}
					break;
				default:
					break;
				}
			}
		}
	};

	/**
	 * private constructor, implement the sigletom design pattern
	 * */
	private Controller()
	{
		Log.d(Config.LOG_TAG, "new controller here");
		period = 1;
		signinFlag = new Flag(false);
		signupFlag = new Flag(false);
		enterFlag = new Flag(false);
		leaveFlag = new Flag(false);
		unreadyFlag = new Flag(false);
		readyFlag = new Flag(false);
		signoutFlag = new Flag(false);
		moveChangeFlag = new Flag(false);
		placeBombCodeFlag = new Flag(false);
		gameFlag = new Flag(false);
		controllerThread = new Thread(controllerRunnable);
		controllerThread.start();
	}
	
	boolean gameBegin = false;
	@SuppressLint("HandlerLeak")
	Handler controllerHandler = new Handler()
	{
		/**
		 * transmit to viewer, and change my status
		 * */
		public void handleMessage(Message msg)
		{
			/**
			 * handle message according to different type
			 * */
			if (viewerHandler == null)
				return;
			String data = (String) msg.obj;
			Log.v("myinfo", "in controller: " + data);
			try
			{
				JSONObject info = new JSONObject(data);
				boolean legalMSG = info.has("type") && info.getString("type") != null;
				if (!legalMSG)
					return;
				String type = info.getString("type");
				synchronized (viewerHandler)
				{
					/**
					 * in any condition, it should return to login, cause no valid key
					 * */
					if (type.equals("error") || type.equals("signout"))
					{
						if (!gameBegin)
						{
							Message sendMsg = Message.obtain();
							sendMsg.obj = data;
							viewerHandler.sendMessage(sendMsg);
							period = 1;
						}
					}
					else
					{
						if (type.equals("enter") || type.equals("leave") || type.equals("ready") || type.equals("unready"))
						{
							if (!gameBegin)
							{
								Message sendMsg = Message.obtain();
								sendMsg.obj = data;
								Log.v("myinfo", "in controller send " + type + " info");
								viewerHandler.sendMessage(sendMsg);
								period = 2;
								if (type.equals("enter"))
									curPos = Config.CUR_POS_ROOM;
								
								if (type.equals("leave"))
									curPos = Config.CUR_POS_HALL;
							}
							

						}
						else
						{
							if (type.equals("game"))
							{
								//initialize game model
								gameIP = info.getString("ip");
								gamePort = info.getInt("port");
								gameKey = info.getString("key");
								gameModel = new GameModel(gameIP, gamePort, gameKey, username);
								
								//notify viewer(room)
								Message sendMsg = Message.obtain();
								JSONObject toViewer = new JSONObject();
								toViewer.put("type", "game");
								toViewer.put("status", "prepare");
								sendMsg.obj = toViewer.toString();
								viewerHandler.sendMessage(sendMsg);
								synchronized (gameFlag)
								{
									period = 3;
									gameBegin = true;
									gameFlag.setValue(true);
								}
							}
						}
					}
				}
				
			} 
			catch (JSONException e)
			{
				/**
				 * couldn't have exception
				 * */
			}
		}
	};
}
