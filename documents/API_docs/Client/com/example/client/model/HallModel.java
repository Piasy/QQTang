package com.example.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * <p>This class is the model in the MVC design pattern, the hall model.
 * It takes charge of all communication with hall server, upholding 
 * the hall information, which include the room information, users information
 * and so on, and giving all information when necessary.</p>
 * 
 * <p>Our basic assumption is that once the data has been sent
 * to here, it must be json format. Because the TCP has error
 * check mechanism. The user self is also a common user, 
 * so I won't treat him specially.</p> 
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class HallModel
{
	String hallIP = "";
	int hallPort;
	String username = "";
	String key = "";
	Socket hallSocket;
	BufferedReader is;
	PrintWriter os;
	
	Room rooms[] = new Room[Config.HALL_SIZE];
	HashMap<String, User> users = null;
	Handler controllerHandler = null;
	
	JSONArray talkMessages = new JSONArray();
	
	Thread listenThread = null;
	boolean keepRunning = true;
	
	Timer heartBeatTimer = new Timer();
	
	String gameIP = "";
	int gamePort;
	
	/**
	 * public constructor, set the server info, connection info, and controller handler
	 * @param server info, connection info, and controller handler
	 * @return none
	 * */
	public HallModel(String hallIP, int hallPort, String username, String key, Handler controllerHandler)
	{
		this.hallIP = hallIP;
		this.hallPort = hallPort;
		this.username = username;
		this.key = key;
		this.controllerHandler = controllerHandler;
		users = new HashMap<String, User>();
		Log.v("myinfo", "get to hall constructor");
	}
	
	/**
	 * initialize socket, read, and writer; connect to hall
	 * server, and receive the initial hall info.
	 * Create socket ,BufferedReader, and
	 * PrintWriter before all works, to 
	 * avoid the scenario that these tools
	 * are used whiles they are not
	 * prepared.
	 * @param none
	 * @return none
	 * */
	public boolean init()
	{
		Log.v("myinfo", "get to hall init");
		boolean ret = false;
		for (int i = 0; i < Config.HALL_SIZE; i ++)
			rooms[i] = new Room();
		
		/**
		 * init socket here, to guarantee that it has been ready
		 * for data transmit.
		 * */
		if (createSocket())
			ret = connectToHallServer();
		return ret;
	}
	
	/**
	 * Listen to hall server, and parse events receive from server,
	 * manage the hall info, including rooms info
	 * and users info.
	 * @param none
	 * @return none
	 * */
	public void listen()
	{
		if ((listenThread == null) || ((listenThread != null) && !listenThread.isAlive()))
		{
			listenThread = new Thread(listenRunnable);
			listenThread.start();
		}
	}
	
	/**
	 * Get user self details info
	 * @param none
	 * @return JSONObject, user self details
	 * */
	public JSONObject getMyDetails()
	{
		synchronized (users)
		{
			return users.get(username).getDetails();
		}
	}
	
	/**
	 * Get current room number of user
	 * @param none
	 * @return current room number of user, -1 if not in any one
	 * */
	public int getMyRoomNum()
	{
		synchronized (users)
		{
			return users.get(username).getRoomNum();
		}
	}
	
	/**
	 * Signout method.
	 * It won't send message to hall server any more, due to the change of 
	 * hall server, just stop the listening thread, and cancel the
	 * heart beat packet send task.
	 * @param none
	 * @return none
	 * */
	public void sendSignoutMSG()
	{
		keepRunning = false;
		heartBeatTimer.cancel();
//		JSONObject sendInfo = new JSONObject();
//		try
//		{
//			sendInfo.put("type", "logout");
//			String sendStr = sendInfo.toString();
//			
//			synchronized (os)
//			{
//				os.println(sendStr);
//				os.flush();
//				Log.d(Config.LOG_TAG, "hall send : " + sendStr);
//			}
//		} 
//		catch (JSONException e)
//		{
//			/**
//			 * couldn't have exception
//			 * */
//		}
	}
	
	/**
	 * Send enter message to hall server.
	 * The message is send as json format string.
	 * For more information, please see the wiki page
	 * {client and hall server}.
	 * The server's response won't be returned immediately
	 * by this method, but will be transmitted via handler
	 * of android. 
	 * @param roomNum, seatPos
	 * @return send via handler
	 * */
	public void sendEnterMSG(int roomNum, int seatPos)
	{
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "enter");
			sendInfo.put("room", roomNum);
			sendInfo.put("pos", seatPos);
			String sendStr = sendInfo.toString();
			
			synchronized (os)
			{
				os.println(sendStr);
				os.flush();
				Log.d(Config.LOG_TAG, "hall send : " + sendStr);
			}
		}
		catch (JSONException e)
		{
			/**
			 * couldn't have exception
			 * */
		}
	}
	
	/**
	 * Send leave message
	 * The message is send as json format string.
	 * For more information, please see the wiki page
	 * {client and hall server}.
	 * The server's response won't be returned immediately
	 * by this method, but will be transmitted via handler
	 * of android. 
	 * @param none
	 * @return send via handler
	 * */
	public void sendLeaveMSG()
	{
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "leave");
			String sendStr = sendInfo.toString();
			
			synchronized (os)
			{
				os.println(sendStr);
				os.flush();
				Log.d(Config.LOG_TAG, "hall send : " + sendStr);
			}
		}
		catch (JSONException e)
		{
			/**
			 * couldn't have exception
			 * */
		}
	}
	
	/**
	 * Send ready message
	 * The message is send as json format string.
	 * For more information, please see the wiki page
	 * {client and hall server}.
	 * The server's response won't be returned immediately
	 * by this method, but will be transmitted via handler
	 * of android. 
	 * @param none
	 * @return send via handler
	 * */
	public void sendReadyMSG()
	{
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "ready");
			String sendStr = sendInfo.toString();
			
			synchronized (os)
			{
				os.println(sendStr);
				os.flush();
				Log.d(Config.LOG_TAG, "hall send : " + sendStr);
			}
		}
		catch (JSONException e)
		{
			/**
			 * couldn't have exception
			 * */
		}
	}
	
	/**
	 * Send unready message
	 * The message is send as json format string.
	 * For more information, please see the wiki page
	 * {client and hall server}.
	 * The server's response won't be returned immediately
	 * by this method, but will be transmitted via handler
	 * of android. 
	 * @param none
	 * @return send via handler
	 * */
	public void sendUnreadyMsg()
	{
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "unready");
			String sendStr = sendInfo.toString();
			
			synchronized (os)
			{
				os.println(sendStr);
				os.flush();
				Log.d(Config.LOG_TAG, "hall send : " + sendStr);
			}
		}
		catch (JSONException e)
		{
			/**
			 * couldn't have exception
			 * */
		}
	}
	
	/**
	 * Get the detail info of given range rooms.
	 * These information will be used by viewer to
	 * draw the hall view and room view. They are
	 * presented in json format. For more information, 
	 * please see the wiki page {Client Viewer And Controller Interface}.
	 * PLEASE NOTE: If the arguments are invalid, this method will 
	 * return a null pointer, so before you use it, check whether 
	 * the return value is null at first. That will make this software
	 * more robust. 
	 * change: pageNum -> room range
	 * @param [startRoomNum, endRoomNum]
	 * @return json format string, if params are 
	 * invalid, null will be returned
	 * */
	//public String getHallSnapShot(int pageNum)
	public String getHallSnapShot(int startRoomNum, int endRoomNum)
	{
		boolean legalParams = (0 <= startRoomNum) && (startRoomNum < Config.HALL_SIZE) && 
							  (0 <= endRoomNum) && (endRoomNum < Config.HALL_SIZE) && (startRoomNum <= endRoomNum);
		if (!legalParams)
			return null; 
		String newSnapShotStr;
		String ret = null;
		synchronized (hallSnapShot)
		{
			newSnapShotStr = new String(hallSnapShot);
		}
		try
		{
			JSONObject data = new JSONObject();
			data.put("start", startRoomNum);
			data.put("end", endRoomNum);
			JSONArray rooms_JsonArray = new JSONArray(newSnapShotStr);
			JSONArray part_roomsJsonArray = new JSONArray();
			for (int i = startRoomNum; i <= endRoomNum; i ++)
				part_roomsJsonArray.put(rooms_JsonArray.getJSONObject(i));
			data.put("rooms", part_roomsJsonArray);
			ret = data.toString();
		} 
		catch (JSONException e)
		{
			//could not have exception
		}
		//Log.d(Config.LOG_TAG, "snap shot : " + ret);
		return ret;
	}
	
	/**
	 * Get talk messages.
	 * The return value will be presented in json format.
	 * For more information, please see the wiki page 
	 * {Client Talk Interface}.
	 * @param message count
	 * @return json format string
	 * */
	public String getCurrentMessage(int length)
	{
		synchronized (talkMessages)
		{
			JSONArray retMessages = new JSONArray();
			int totalMSGNum = talkMessages.length();
			
			//if no so many msgs, return all of them
			try
			{
				if (totalMSGNum < length)
				{
					for (int i = 0; i < totalMSGNum; i ++)
						retMessages.put(talkMessages.get(i));
				}
				else
				{
					for (int i = 0; i < length; i ++)
						retMessages.put(talkMessages.get(totalMSGNum - length + i));
				}
			}
			catch (JSONException e)
			{
				if (e.getMessage() != null)
					Log.d(Config.LOG_TAG, e.getMessage());
			}
			//Log.d("myinfo3", "in hall return : " + retMessages.toString());
			return new String(retMessages.toString());
		}
	}
	
	/**
	 * Send talk messages to hall server.
	 * The message is send as json format string.
	 * For more information, please see the wiki page
	 * {client and hall server}. These messages will
	 * be available when calling the getCurrentMessage(int length)
	 * method after messages are sent to client by hall
	 * server.
	 * @param message to send
	 * */
	public void sendTalkMessage(String message)
	{
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "message");
			sendInfo.put("message", message);
			
			synchronized (os)
			{
				os.println(sendInfo.toString());
				os.flush();
				Log.d("myinfo2", "hall send talk msg : " + sendInfo.toString());
			}
		}
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
		}
		
	}
	
	/**
	 * Handle messages from server.
	 * Different type has different way. But the first 
	 * principle is to design this software for robust.
	 * Any data from the outside of this model will
	 * be validated before using. If there has any
	 * invalid data, this software will turn to the proper
	 * status according to the level of error.
	 * 
	 * */
	protected void handleEvents(String rcvStr) 
	{
		JSONObject rcvInfo;
		try
		{
			rcvInfo = new JSONObject(rcvStr); 
			/**
			 * if the messages has error, just ignore this queue of messages
			 * */
			boolean legalMSG = rcvInfo.has("type") && rcvInfo.getString("type") != null;
			if (!legalMSG)
				return;
			
			String infoType = rcvInfo.getString("type");
			if (infoType.equals("events"))
			{
				JSONArray events = rcvInfo.getJSONArray("events"); //if not contain this key, here will throw JsonException
				
				JSONArray roomsStatus = rcvInfo.getJSONArray("rooms");
				for (int i = 0; i < roomsStatus.length(); i ++)
				{
					int busy = roomsStatus.getInt(i);
					if (busy == 1 && i < Config.HALL_SIZE)
						rooms[i].startGame();
					if (busy == 0 && i < Config.HALL_SIZE)
						rooms[i].endGame();
				}
				
				for (int i = 0; i < events.length(); i ++)
				{
					JSONObject event = events.getJSONObject(i);
					legalMSG = (event != null) && event.has("type") && (event.getString("type") != null);
					if (!legalMSG)
						continue;
					String type = event.getString("type");
					
					/**
					 * this event must not be mine, cause I'm in the hall from the initial info
					 * */
					if (type.equals("login"))
					{
						legalMSG = event.has("details") && event.has("user") && (event.getJSONObject("details") != null) && (event.getString("user") != null);
						if (!legalMSG)
							continue;
						User user = new User(event.getJSONObject("details"));
						users.put(event.getString("user"), user);
					}
					else
					{
						if (type.equals("logout"))
						{
							legalMSG = event.has("user") && (event.getString("user") != null);
							if (!legalMSG)
								continue;
							String name = event.getString("user");
							if (name.equals(username))
							{
								synchronized (controllerHandler)
								{
									JSONObject data = new JSONObject();
									data.put("type", "signout");
									data.put("status", "Success");
									Message msg = Message.obtain();
									msg.obj = data.toString();
									Log.v("myinfo", "in hall model: " + data.toString());
									controllerHandler.sendMessage(msg);
									keepRunning = false;
									heartBeatTimer.cancel();
								}
							}
							User user = users.get(name);
							int roomNum = user.getRoomNum();
							int seatPos = user.getSeatPos();
							boolean legalPos = (((0 <= roomNum) && (roomNum < Config.HALL_SIZE)) && ((0 <= seatPos) && (seatPos < Config.ROOM_SIZE)));
							if (!legalPos)
								continue;
							rooms[roomNum].leave(seatPos);
							user.leave();
							users.remove(name);
						}
						else 
						{
							if (type.equals("refresh"))
							{
								legalMSG = event.has("details") && event.has("user") && (event.getJSONObject("details") != null) && (event.getString("user") != null);
								if (!legalMSG)
									continue;
								User user = new User(event.getJSONObject("details"));
								User oldOne = users.get(event.getString("user"));
								user.sit(oldOne.getRoomNum(), oldOne.getSeatPos());	//keep old user position info!!!
								//user.unready();
								//user.ready = false;
								users.put(event.getString("user"), user); //HashMap: if has this key-value pair, the old one will be replaced
							}
							else
							{
								if (type.equals("enter"))
								{
									
									legalMSG = event.has("user") && (event.getString("user") != null);
									if (!legalMSG)
										continue;
									String name = event.getString("user");
									if (users.containsKey(name))
									{
										if (name.equals(username))
										{
											talkMessages = new JSONArray();
											synchronized (controllerHandler)
											{
												JSONObject data = new JSONObject();
												data.put("type", "enter");
												data.put("status", "Success");
												Message msg = Message.obtain();
												msg.obj = data.toString();
												Log.v("myinfo", "in hall model: " + data.toString());
												controllerHandler.sendMessage(msg);
											}
										}
										User user = users.get(name);
										int roomNum = event.getInt("room");
										int seatPos = event.getInt("pos");
										boolean legalPos = (((0 <= roomNum) && (roomNum < Config.HALL_SIZE)) && ((0 <= seatPos) && (seatPos < Config.ROOM_SIZE)));
										if (!legalPos)
											continue;
										user.sit(roomNum, seatPos);
										if (rooms[roomNum].getStatus().equals("Free"))
											rooms[roomNum].enter(user, seatPos);
									}
									//else, I could do nothing to amend it.							
								}
								else
								{
									if (type.equals("leave"))
									{
										legalMSG = event.has("user") && (event.getString("user") != null);
										if (!legalMSG)
											continue;
										String name = event.getString("user");
										if (users.containsKey(name))
										{
											if (name.equals(username))
											{
												talkMessages = new JSONArray();
												synchronized (controllerHandler)
												{
													JSONObject data = new JSONObject();
													data.put("type", "leave");
													data.put("status", "Success");
													Message msg = Message.obtain();
													msg.obj = data.toString();
													Log.v("myinfo", "in hall model send leave info");
													controllerHandler.sendMessage(msg);
												}
											}
											User user = users.get(name);
											int roomNum = user.getRoomNum();
											int seatPos = user.getSeatPos();
											boolean legalPos = (((0 <= roomNum) && (roomNum < Config.HALL_SIZE)) && ((0 <= seatPos) && (seatPos < Config.ROOM_SIZE)));
											if (!legalPos)
												continue;
											rooms[roomNum].leave(seatPos);
											user.leave();
										}
										
									}
									else 
									{
										if (type.equals("ready"))
										{
											legalMSG = event.has("user") && (event.getString("user") != null);
											if (!legalMSG)
												continue;
											String name = event.getString("user");
											if (users.containsKey(name))
											{
												if (name.equals(username))
												{
													synchronized (controllerHandler)
													{
														JSONObject data = new JSONObject();
														data.put("type", "ready");
														data.put("status", "Success");
														Message msg = Message.obtain();
														msg.obj = data.toString();
														Log.v("myinfo", "in hall model: " + data.toString());
														controllerHandler.sendMessage(msg);
													}
												}
												users.get(name).ready();
												Log.d(Config.LOG_TAG, " after set to ready at hall : " + users.get(name).ready);
											}
										}
										else
										{
											if (type.equals("unready"))
											{
												legalMSG = event.has("user") && (event.getString("user") != null);
												if (!legalMSG)
													continue;
												String name = event.getString("user");
												if (users.containsKey(name))
												{
													if (name.equals(username))
													{
														synchronized (controllerHandler)
														{
															JSONObject data = new JSONObject();
															data.put("type", "unready");
															data.put("status", "Success");
															Message msg = Message.obtain();
															msg.obj = data.toString();
															Log.v("myinfo", "in hall model: " + data.toString());
															controllerHandler.sendMessage(msg);
														}
													}
													users.get(name).unready();
												}
											}
											//start and finish event won't be sent here, but leave those code here.
											else
											{
												if (type.equals("start"))
												{
													int roomNum = event.getInt("room");
													if (0 <= roomNum && roomNum < Config.HALL_SIZE)
														rooms[roomNum].startGame();
												}
												else
												{
													if (type.equals("finished"))
													{
														int roomNum = event.getInt("room");
														if (0 <= roomNum && roomNum < Config.HALL_SIZE)
															rooms[roomNum].endGame();
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				updateHallSnapShot();
			}
			else
			{
				if (infoType.equals("game"))
				{
					legalMSG = rcvInfo.has("ip") && (rcvInfo.getString("ip") != null) && rcvInfo.has("port") &&
							   rcvInfo.has("key") && rcvInfo.getString("key") != null;
					if (!legalMSG)
					{
						//what should I do?
						//just ignore this message, the game server will get a timeout,
						//and the game will be cancel
						return;
					}
					gameIP = rcvInfo.getString("ip");
					gamePort = rcvInfo.getInt("port");
					String gameKey = rcvInfo.getString("key");
					synchronized (controllerHandler)
					{
						JSONObject data = new JSONObject();
						data.put("type", "game");
						data.put("ip", gameIP);
						data.put("port", gamePort);
						data.put("key", gameKey);
						Message msg = Message.obtain();
						msg.obj = data.toString();
						Log.v("myinfo", "in hall model: " + data.toString());
						controllerHandler.sendMessage(msg);
					}
				}
				else //talk packet
				{
					if (infoType.equals("messages"))
					{
						JSONArray messages = rcvInfo.getJSONArray("messages");
						//Log.d("myinfo2", "in hall receive " + messages.toString());
						for (int i = 0; i < messages.length(); i ++)
						{
							synchronized (talkMessages)
							{
								talkMessages.put(messages.get(i));
							}
						}
					}
				}
			}
		} 
		catch (JSONException e)
		{
			//do nothing
		}
		//Log.d(Config.LOG_TAG, "handle finish..");
	}
	
	/**
	 * this runnable define the works of listening
	 * to the server
	 * */
	Runnable listenRunnable = new Runnable()
	{
		
		//@SuppressLint("NewApi")
		@Override
		public void run()
		{
			try
			{
				String rcvStr = "";
				while (keepRunning)
				{
					rcvStr = is.readLine();
					//Log.v("myinfo", "in hall receive " + rcvStr);
					//TODO multi-thread or not?
					handleEvents(new String(rcvStr));
				}
			} 
			catch (SocketTimeoutException e)
			{
				readTimeOut();
			}
			catch (IOException e)
			{
				readTimeOut();
			}
			finally
			{
				try
				{
					is.close();
					os.close();
					hallSocket.close();
					//TrafficStats.untagSocket(hallSocket);
				} 
				catch (IOException e)
				{
					//so just let the resource be garbage
				}
				
			}
		}
	};
	
	//@SuppressLint("NewApi")
	/**
	 * Create socket ,BufferedReader, and
	 * PrintWriter before all works, to 
	 * avoid the scenario that these tools
	 * are used whiles they are not
	 * prepared.
	 * */
	protected boolean createSocket()
	{
		boolean ret = false;
		try
		{
			//TrafficStats.setThreadStatsTag(0xF00D);
			hallSocket = new Socket(hallIP, hallPort);
			hallSocket.setSoTimeout(Config.HALL_RESPONSE_TIMEOUT);
			//TrafficStats.tagSocket(hallSocket);
			is = new BufferedReader(new InputStreamReader(hallSocket.getInputStream()));
			os = new PrintWriter(hallSocket.getOutputStream());
			Log.v("myinfo", "socket ok");
			ret = true;
		} 
		catch (UnknownHostException e)
		{
			ret = false;
		} 
		catch (IOException e)
		{
			ret = false;
		}
		return ret;
	}
	
	/**
	 * After the socket and other tools are prepared,
	 * this method will do the first interaction communication
	 * with hall server, to exchange the identification,
	 * and also receive the initial hall information
	 * from it, which will be used by viewer once 
	 * the player has signed in successfully. 
	 * */
	protected boolean connectToHallServer()
	{
		heartBeatTimer.schedule(heartBeatTask, Config.HEART_BEAT_DELAY, Config.HEART_BEAT_DELAY);
		boolean ret = false;
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("user", username);
			sendInfo.put("key", key);
			os.println(sendInfo.toString());
			os.flush();
			Log.v("myinfo", "send : " + sendInfo.toString());
			
			//receive initial hall information, including self
			String initInfoStr = is.readLine();
			Log.v("myinfo", "receive : " + initInfoStr);
			JSONObject initInfo = new JSONObject(initInfoStr);
			
			boolean legalMSG = initInfo.has("type") && (initInfo.getString("type") != null) &&
						       initInfo.has("users") && (initInfo.getJSONObject("users") != null) &&
						       initInfo.has("rooms") && (initInfo.getJSONArray("rooms") != null);
			
			if (!legalMSG)
			{
				//what should I do?
				//just tell user fail, and let him retry,
				//the login server will change my status to offline after timeout
				return false;
			}
			
			String type = initInfo.getString("type");
			
			/**
			 * Tolerate at most 1 live package before initial hall info, otherwise, treat it as InternetError
			 * */
			if (!type.equals("success"))
			{
				initInfoStr = is.readLine();
				initInfo = new JSONObject(initInfoStr);
				legalMSG = initInfo.has("type") && (initInfo.getString("type") != null) &&
					       initInfo.has("users") && (initInfo.getJSONObject("users") != null) &&
					       initInfo.has("rooms") && (initInfo.getJSONArray("rooms") != null);
				if (!legalMSG)
					return false;
				type = initInfo.getString("type");
			}
			
			if (type.equals("success"))
			{
				JSONObject initUsers = initInfo.getJSONObject("users");
				@SuppressWarnings("rawtypes")
				Iterator it = initUsers.keys();
				while (it.hasNext())
				{
					String key = (String) it.next();
					JSONObject userInfo = initUsers.getJSONObject(key);
					
					legalMSG = userInfo.has("details") && userInfo.getJSONObject("details") != null && 
							   userInfo.has("room") && userInfo.has("pos") && userInfo.has("ready") && 
							   userInfo.getString("ready") != null;
					
					if (!legalMSG)
						continue;
					JSONObject details = userInfo.getJSONObject("details");
					User user = new User(details);
					int _roomNum = userInfo.getInt("room");
					int _seatPos = userInfo.getInt("pos");
					user.sit(_roomNum, _seatPos);
					String isReady = userInfo.getString("ready");
					if (isReady.equals("ready"))
						user.ready();
					if (_roomNum != -1)
						rooms[_roomNum].enter(user, _seatPos);
					users.put(key, user);
				}
				
				
				/**
				 * whether a room is free or full will be upheld during the
				 * process above, only playing should be set here
				 * */
				JSONArray initRooms = initInfo.getJSONArray("rooms");
				for (int i = 0; i < initRooms.length(); i ++)
				{
					int busy = initRooms.getInt(i);
					if (busy == 1 && i < Config.HALL_SIZE)
						rooms[i].startGame();
				}
				/**
				 * when get initial info of hall, I should update the snapshot string
				 * to let viewer get the valid value about the hall.
				 * */
				updateHallSnapShot();
				ret = true;
			}
		} 
		catch (SocketTimeoutException e)
		{
			ret = false;
		}
		catch (JSONException e)
		{
			ret = false;
		}
		catch (IOException e)
		{
			ret = false;
		}
		catch (Exception e) 
		{
			//what should I do?
			return false;
		}
		return ret;
	}
	
	/**
	 * once socket timeout, return to login
	 * */
	protected void readTimeOut()
	{
		heartBeatTimer.cancel();
		try
		{
			//notify controller
			JSONObject data = new JSONObject();
			data.put("type", "error");
			data.put("status", "InternetError");
			Message msg = Message.obtain();
			msg.obj = data.toString();
			Log.v("myinfo", "in hall model: " + data.toString());
			controllerHandler.sendMessage(msg);
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Send heart beat packet to hall server.
	 * Actually, the Socket has provided the KeepAlive
	 * method, while we haven't noted it before, so we
	 * design this task to tell server that this client
	 * is still alive.
	 * */
	TimerTask heartBeatTask = new TimerTask()
	{
		
		@Override
		public void run()
		{
			JSONObject sendInfo = new JSONObject();
			try
			{
				sendInfo.put("type", "heartbeat");
				String sendStr = sendInfo.toString();
				synchronized (os)
				{
					os.println(sendStr);
					os.flush();
				}
			} 
			catch (JSONException e)
			{
				//couldn't have exception
			}
		}
	};
	
	/**
	 * Each time when receive messages from hall server, 
	 * after parse these events, this method will be
	 * called to take an up-to-date snapshot for 
	 * the hall, which will be used by viewer to
	 * draw the hall view and room view.
	 * */
	String hallSnapShot = new String(Config.INIT_STR);
	protected void updateHallSnapShot()
	{
		JSONArray rooms_JsonArray = new JSONArray();
		try 
		{
			
			for (int i = 0; i < Config.HALL_SIZE; i ++)
			{
				JSONObject room = new JSONObject();
				room.put("status", rooms[i].getStatus());
				JSONArray users_JsonArray = new JSONArray();
				for (int j = 0 ; j < Config.ROOM_SIZE; j ++)
				{
					User old_user = rooms[i].userAt(j);
					if (old_user != null)
					{
						JSONObject old_userDetails = old_user.getDetails();
						String user_name = old_userDetails.getString("user");
						
						User user = users.get(user_name);
						JSONObject userDetails = user.getDetails();
						JSONObject userJsonObject = new JSONObject();
						
						userJsonObject.put("user", user_name);
						if (user.getStatus())
							userJsonObject.put("ready", "ready");
						else
							userJsonObject.put("ready", "unready");
						
						Log.d(Config.LOG_TAG, "after update : " + user_name + " ready? " + user.getStatus() + ", " + userJsonObject.getString("ready"));
						userJsonObject.put("details", userDetails);
						userJsonObject.put("pos", j);
						users_JsonArray.put(userJsonObject);
					}
				}
				room.put("users", users_JsonArray);
				rooms_JsonArray.put(room);
			}
		}
		catch (JSONException e)
		{
			//couldn't have exception
		}	
		synchronized (hallSnapShot)
		{
			hallSnapShot = rooms_JsonArray.toString();
		}	
	}
	
}

