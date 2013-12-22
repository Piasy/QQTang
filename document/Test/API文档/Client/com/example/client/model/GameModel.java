package com.example.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.example.client.controller.Controller;

/**
 * <p>This class is the model in the MVC design pattern, the game model.
 * It takes charge of all communication with game server, upholding 
 * the game status information, which include the player information, 
 * map information, bomb information, prop information, etc,  
 * and providing all those information to viewer for their drawing game
 * view.</p>
 * 
 * <p>Our basic assumption is that once the data has been sent
 * to here, it must be json format. Because the TCP has error
 * check mechanism. The user self is also a common user, 
 * so I won't treat him specially.</p> 
 * 
 * <p>We need to point out that the TCP transmission is very slow,
 * the round travel time is approximately 150 mili-second, but players need
 * to view 30 frames per second, so the game model will add 5 frames
 * between the two frames receiving from game server, which send 
 * two frames in every 200 mili-second. If the game model's calculation
 * has error, the game model will apply the information receiving
 * from game server in force, cause the 200 mili-second won't make
 * the obvious error. This method is create by Yong Ren, the developer 
 * of game server.
 * </p>
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class GameModel
{
	String gameIP = "", key = "";
	int gamePort;
	String username = "";
	Controller myController;
	
	/**
	 * public constructor, set the server info, connection info, and controller handler
	 * @param server info, connection info, and controller handler
	 * @return none
	 * */
	public GameModel(String gameIP, int gamePort, String key, String username)
	{
		this.gameIP = gameIP;
		this.gamePort = gamePort;
		this.key = key;
		this.username = username;
		myController = Controller.getController();
		updateInfo = loadMap(myController.getMapID());
	}
	
	/**
	 * The method to load the initial map
	 * information, we design it at the begin 
	 * period of develop, but at the end of our
	 * develop, this information won't make sense,
	 * viewer will get these information from
	 * server.
	 * @param ID, the  map id
	 * @return the json format string of map information
	 * */
	protected String loadMap(int ID)
	{
		JSONArray map = new JSONArray();
		JSONArray raw = new JSONArray();
		for (int i = 0; i < Config.MAP_HIGHT; i ++)
		{
			for (int j = 0; j < Config.MAP_WIDTH; j ++)
			{
				JSONObject elem = new JSONObject();
				try 
				{
					elem.put("status", 0);
					raw.put(elem);
				} 
				catch (JSONException e) 
				{
					if (e.getMessage() != null)
						Log.d(Config.LOG_TAG, e.getMessage());
					else
						Log.d(Config.LOG_TAG, "Json exception");
				}
			}
			map.put(raw);
		}
		
		JSONObject player = new JSONObject();
		JSONArray bomb = new JSONArray();
		
		JSONObject init = new JSONObject();
		try 
		{
			init.put("type", "prepare");
			init.put("map", map);
			init.put("player", player);
			init.put("bomb", bomb);
			//Log.d(Config.LOG_TAG, init);
			//TO-DO initial prop info
		} 
		catch (JSONException e) 
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "Json exception");
		}
		return init.toString();
	}
	
	Socket gameSocket;
	BufferedReader is;
	PrintWriter os;
	/**
	 * Initialize socket, read, and writer; connect to game
	 * server, and prepare for game.
	 * Create socket ,BufferedReader, and
	 * PrintWriter before all works, to 
	 * avoid the scenario that these tools
	 * are used whiles they are not prepared.
	 * The data transmitted are all in json
	 * format. For more information, please see
	 * the wili page
	 * {Game Server And Users}
	 * @param none
	 * @return none
	 * */
	public int init()
	{
		int ret = -1;
		try
		{
			gameSocket = new Socket(gameIP, gamePort);
			
			is = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
			os = new PrintWriter(gameSocket.getOutputStream());
			
			JSONObject sendInfo = new JSONObject();
			sendInfo.put("type", "connect");
			sendInfo.put("user", username);
			sendInfo.put("key", key);
			
			os.println(sendInfo.toString());
			os.flush();
			//Log.d(Config.LOG_TAG, "game init send : " + sendInfo.toString());
			
			String rcvStr = is.readLine();
			//Log.d(Config.LOG_TAG, "game init receive : " + rcvStr);
			if (rcvStr == null)
				return -1;
			JSONObject rcvInfo = new JSONObject(rcvStr);
			if (rcvInfo.has("type") && rcvInfo.getString("type") != null && rcvInfo.getString("type").equals("accept"))
			{
				rcvStr = is.readLine();
				//Log.d(Config.LOG_TAG, "game init receive : " + rcvStr);
				if (rcvStr == null)
					return -1;
				rcvInfo = new JSONObject(rcvStr);
				boolean legalMSG = rcvInfo.has("type") && rcvInfo.getString("type") != null && rcvInfo.getString("type").equals("start") &&
								   rcvInfo.has("delay") && rcvInfo.getString("delay") != null;
				if (legalMSG)
				{
					ret = Integer.parseInt(rcvInfo.getString("delay"));
				}
			}
		} 
		catch (SocketTimeoutException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "game server timeout");
			ret = -1;
		}
		catch (UnknownHostException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "UnknownHostException");
			ret = -1;
		} 
		catch (IOException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "IOException");
			ret = -1;
		} 
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "JSONException");
			ret = -1;
		}
		return ret;
	}
	
	
	/**
	 * Only need to change users info, prop info according to
	 * the delta between curTime and rcvTime.
	 * map info and bomb info will be sent to viewer
	 * without change.
	 * The data transmitted are all in json
	 * format. For more information, please see
	 * the wili page
	 * {Game Server And Users}
	 * @return json format string, game status
	 * */
	public String getGameStatus()
	{
		String statusNow;
		String retStr = null;
		synchronized (updateInfo)
		{
			statusNow = new String(updateInfo);
		}
		
		try
		{
			JSONObject status = new JSONObject(statusNow);
			String type = status.getString("type");
			boolean legalMSG = status.has("type") && status.getString("type") != null;
			if (!legalMSG)//fatal error occur, give error info
			{
				Log.d(Config.LOG_TAG, "game illegal msg 1");
				gameError();
				return new String(updateInfo);
			}
			if (type.equals("info"))
			{
				long curTime = System.currentTimeMillis();
				JSONObject ret = new JSONObject();
				
				//if time error, give viewer the earlier info
				if (rcvTime != -1 && rcvTime <= curTime)
				{
					double deltaTime = (curTime - rcvTime) / 1000.0;
					ret.put("type", "info");
					//ret.put("map", status.getJSONArray("map"));
					JSONArray map = status.getJSONArray("map");
					//JSONArray props = status.getJSONArray("prop");
					JSONArray bombs = status.getJSONArray("bubbles");
					
					JSONArray newMap = new JSONArray();
					JSONArray newProps = new JSONArray();
					JSONArray newBombs = new JSONArray();					
					
					//update bomb info
					for (int i = 0; i < bombs.length(); i ++)
					{
						JSONObject bomb = bombs.getJSONObject(i);
						if (1 <= bomb.getInt("status") && bomb.getInt("status") <= 100)
						{
							JSONObject newBomb = new JSONObject();
							newBomb.put("x", bomb.getInt("x"));
							newBomb.put("y", bomb.getInt("y"));
							newBomb.put("model", bomb.getInt("model"));
							newBomb.put("status", bomb.getInt("status") + (int) (Config.BRAKE_DIS_SPEED * deltaTime));
							
							JSONObject range = bomb.getJSONObject("range");
							JSONObject newRange = new JSONObject();
							//TODO max value limit
							newRange.put("l", range.getInt("l") + (int) (Config.BOMB_EXPAND_SPEED * deltaTime));
							newRange.put("r", range.getInt("r") + (int) (Config.BOMB_EXPAND_SPEED * deltaTime));
							newRange.put("u", range.getInt("u") + (int) (Config.BOMB_EXPAND_SPEED * deltaTime));
							newRange.put("d", range.getInt("d") + (int) (Config.BOMB_EXPAND_SPEED * deltaTime));
							newBomb.put("range", newRange);
							
							newBombs.put(newBomb);
						}
						else
						{
							newBombs.put(bomb);
						}
					}

					//update prop info here
					//update map info
					synchronized (props)
					{
						int loopN = map.length(), loopM = map.getJSONArray(0).length();
						for (int i = 0; i < loopN; i ++)
						{
							JSONArray mapRow = map.getJSONArray(i);
							JSONArray propRow = props.getJSONArray(i);
							
							JSONArray newMapRow = new JSONArray();
							JSONArray newPropRow = new JSONArray();
							for (int j = 0; j < loopM; j ++)
							{
								if (101 <= mapRow.getJSONObject(j).getInt("status") && mapRow.getJSONObject(j).getInt("status") <= 200)
								{
									JSONObject mapUnit = new JSONObject();
									mapUnit.put("status", mapRow.getJSONObject(j).getInt("status") + (int) (Config.BRAKE_DIS_SPEED * deltaTime));
									newMapRow.put(mapUnit);
									
									int propInfo = propRow.getJSONObject(j).getInt("status");
									int newPropInfo = 0;
									switch (propInfo)
									{
									case 1:
										newPropInfo = 5;
										break;
									case 2:
										newPropInfo = 6;
										break;
									case 3:
										newPropInfo = 7;
										break;
									case 4:
										newPropInfo = 8;
										break;
									default:
										break;
									}
									JSONObject newProp = new JSONObject();
									newProp.put("status", newPropInfo);
									newPropRow.put(newProp);
								}
								else
								{
									newMapRow.put(mapRow.getJSONObject(j));
									newPropRow.put(propRow.getJSONObject(j));
								}
							}
							newMap.put(newMapRow);
							newProps.put(newPropRow);
						}
						
						props = new JSONArray(newProps.toString());
					}
					
					//TO-DO update user position info, add conflict with brake
					JSONObject users = status.getJSONObject("users");
					@SuppressWarnings("rawtypes")
					Iterator it = users.keys();  
					JSONObject updateUsers = new JSONObject();
					while (it.hasNext())
					{
						String key = (String) it.next();
						
						JSONObject old_user = users.getJSONObject(key);
						boolean onLine = old_user.getBoolean("isOline");
						if (!onLine) //if one user is off line, omit it
							continue;
						double speed = old_user.getDouble("speed");
						int dir = old_user.getInt("status");
						double oldXX = old_user.getDouble("x"), oldYY = old_user.getDouble("y");
						double xx = 0, yy = 0;
						switch (dir)
						{
						case 1:
							{
								yy = oldYY + (double) (speed * deltaTime);
								xx = oldXX;
							}
							break;
						case 3:
							{
								xx = oldXX + (double) (speed * deltaTime);
								yy = oldYY;
							}
							break;
						case 5:
							{
								yy = oldYY - (double) (speed * deltaTime);
								xx = oldXX;
							}
							break;
						case 7:
							{
								xx = oldXX - (double) (speed * deltaTime);
								yy = oldYY;
							}
							break;
						default:
							{
								xx = oldXX;
								yy = oldYY;
							}
							break;
						}
						if (xx < 0)
							xx = 0;
						if (yy < 0)
							yy = 0;
						//here should contain the equal condition
						if (Config.MAP_WIDTH <= xx)
							xx = Config.MAP_WIDTH - 1;
						if (Config.MAP_HIGHT <= yy)
							yy = Config.MAP_HIGHT - 1;
						
//						JSONObject mapUnit = newMap.getJSONArray((int) yy).getJSONObject((int) xx);
//						boolean goodPos = (0 <= mapUnit.getInt("status") && mapUnit.getInt("status") <= 49) ||
//										  200 < mapUnit.getInt("status");
//						if (!goodPos)
//						{
//							switch (dir)
//							{
//							case 1:
//								yy --;
//								break;
//							case 3:
//								xx --;
//								break;
//							case 5:
//								yy ++;
//								break;
//							case 7:
//								xx ++;
//								break;
//							default:
//								break;
//							}
//						}
						
						JSONObject new_user = new JSONObject();
						new_user.put("x", xx);
						new_user.put("y", yy);
						new_user.put("model", old_user.getInt("model"));
						new_user.put("status", dir);
						new_user.put("speed", speed);
						new_user.put("remain", remain);
						new_user.put("isOline", onLine);
						
						//Log.d(Config.LOG_TAG, "testtest : oldXX " + oldXX + ", oldYY " + oldYY + ", xx " + xx + ", yy " + yy);
						//Log.d(Config.LOG_TAG, "testtest : curTime " + curTime + ", rcvTime " + rcvTime + ", speed " + speed);
						//Log.d(Config.LOG_TAG, "xjlxjl draw " + xx + " " + yy);
						updateUsers.put(key, new_user);
					}
					ret.put("player", updateUsers);
					ret.put("bomb", newBombs);
					ret.put("map", newMap);
					ret.put("prop", newProps);
					retStr = new String(ret.toString());
				}
				else
					retStr = statusNow;
			}
			else
			{
				if (type.equals("result"))
				{
					JSONObject ret = new JSONObject();
					playing = false;
					ret.put("type", status.get(username));
					retStr = new String(ret.toString());
					Log.d("fuck", "finish 1");
					myController.setGameToOver();
					
				}
				else
					retStr = statusNow;
			}
		} 
		catch (JSONException e)
		{
			gameError();
			Log.d(Config.LOG_TAG, "game illegal msg 2 jsonexception");
			return new String(updateInfo);
		}
		synchronized (props)
		{
			if (props.length() != 0)
			{
				int prop = propAvialable();
				if (prop != -1)
					prop(new String(Config.CATEGORY[prop]));
			}
		}
		//Log.d(Config.LOG_TAG, "game return : " + retStr);
		
		return retStr;
	}
	
	/**
	 * Listen to game server, and parse information
	 *  receive from server.
	 * The data transmitted are all in json
	 * format. For more information, please see
	 * the wili page
	 * {Game Server And Users}
	 * @param none
	 * @return none
	 * */
	Thread listenThread = null;
	public void listen()
	{
		if ((listenThread == null) || ((listenThread != null) && !listenThread.isAlive()))
		{
			try
			{
				gameSocket.setSoTimeout(Config.GAME_RESPONSE_TIMEOUT);
			}
			catch (SocketException e)
			{
				if (e.getMessage() != null)
					Log.d(Config.LOG_TAG, e.getMessage());
				else
					Log.d(Config.LOG_TAG, "SocketException");
			}
			listenThread = new Thread(listenRunnable);
			listenThread.start();
		}
	}
	
	/**
	 * Send move status change message to server
	 * The data transmitted are all in json
	 * format. For more information, please see
	 * the wili page
	 * {Game Server And Users}
	 * @param direction
	 * */
	public void moveStatusChange(String direction)
	{
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "move");
			sendInfo.put("direction", direction);
			JSONObject pos = new JSONObject();
			JSONObject curPos = getCurPos();
			pos.put("x", curPos.getDouble("x"));
			pos.put("y", curPos.getDouble("y"));
			sendInfo.put("pos", pos);
			synchronized (os)
			{
				os.println(sendInfo.toString());
				os.flush();
				//Log.d(Config.LOG_TAG, "testtest game send : " + sendInfo.toString());
			}
		} 
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "JSONException");
		}
	}
	
	
	/**
	 * The method to get the player's position,
	 * we design it at the begin 
	 * period of develop, but at the end of our
	 * develop, this information won't make sense,
	 * game server won't use it any more.
	 * server.
	 * @param none
	 * @return the position json object
	 * */
	protected JSONObject getCurPos()
	{
		JSONObject pos = new JSONObject();
		double xx = 0, yy = 0;
		long curTime = System.currentTimeMillis();
		double deltaTime = (curTime - rcvTime) / 1000.0;
		switch (speedDir)
		{
		case 1:
			{
				yy = rcvYY + (double) (rcvSpeed * deltaTime);
				xx = rcvXX;
			}
			break;
		case 3:
			{
				xx = rcvXX + (double) (rcvSpeed * deltaTime);
				yy = rcvYY;
			}
			break;
		case 5:
			{
				yy = rcvYY - (double) (rcvSpeed * deltaTime);
				xx = rcvXX;
			}
			break;
		case 7:
			{
				xx = rcvXX - (double) (rcvSpeed * deltaTime);
				yy = rcvYY;
			}
			break;
		default:
			{
				xx = rcvXX;
				yy = rcvYY;
			}
			break;
		}
		if (xx < 0)
			xx = 0;
		if (yy < 0)
			yy = 0;
		//here should contain the equal condition
		if (Config.MAP_WIDTH <= xx)
			xx = Config.MAP_WIDTH - 1;
		if (Config.MAP_HIGHT <= yy)
			yy = Config.MAP_HIGHT - 1;
		try
		{
			pos.put("x", xx);
			pos.put("y", yy);
		} 
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "JSONException");
		}
		//Log.d(Config.LOG_TAG, "xjlxjl get pos" + xx + " " + yy);
		return pos;
	}
	
	/**
	 * Send place bomb message to server
	 * The data transmitted are all in json
	 * format. For more information, please see
	 * the wili page
	 * {Game Server And Users}
	 * */
	public void placeBomb()
	{
		if (remain <= 0)
			return;
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "deploy");
			JSONObject pos = new JSONObject();
			JSONObject curPos = getCurPos();
			pos.put("x", (int) curPos.getDouble("x"));
			pos.put("y", (int) curPos.getDouble("y"));
			sendInfo.put("pos", pos);
			synchronized (os)
			{
				os.println(sendInfo.toString());
				os.flush();
				Log.d(Config.LOG_TAG, "game send : " + sendInfo.toString());
			}
		} 
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "JSONException");
		}
	}
	
	JSONArray props = new JSONArray();
	/**
	 * The method to calculate whether there is prop available
	 * to the player
	 * @param none
	 * @return if has one, return the code, otherwise , -1 will be returned
	 * */
	protected int propAvialable()
	{
		//TO-DO add function here
		JSONObject curPos = getCurPos();
		int ret = -1;
		synchronized (props)
		{
			try
			{
				int xx = (int) (curPos.getDouble("x") + 0.5), yy = (int) (curPos.getDouble("y") + 0.5);
				//if (0.5 <= curPos.getDouble("x") - xx)
				//	xx ++;
				//if (0.5 <= curPos.getDouble("y") - yy)
				//	yy ++;
				JSONObject prop = props.getJSONArray(yy).getJSONObject(xx);
				switch (prop.getInt("status"))
				{
				case 5:
					ret = 0;
					break;
				case 6:
					ret = 1;
					break;
				case 7:
					ret = 2;
					break;
				case 8:
					ret = 3;
					break;
				default:
					ret = -1;
					break;
				}
			}
			catch (JSONException e)
			{
				if (e.getMessage() != null)
					Log.d(Config.LOG_TAG, e.getMessage());
				else
					Log.d(Config.LOG_TAG, "JSONException");
			}

		}
		
		return ret;
	}
	
	/**
	 * The method to send get prop information
	 * to game server.
	 * The data transmitted are all in json
	 * format. For more information, please see
	 * the wili page
	 * {Game Server And Users}
	 * @param the category string of the prop
	 * @return none
	 * */
	protected void prop(String cate)
	{
		JSONObject sendInfo = new JSONObject();
		try
		{
			sendInfo.put("type", "prop");
			sendInfo.put("category", cate);
			JSONObject pos = new JSONObject();
			JSONObject curPos = getCurPos();
			pos.put("x", (int) (curPos.getDouble("x") + 0.5));
			pos.put("y", (int) (curPos.getDouble("y") + 0.5));
			sendInfo.put("pos", pos);
			synchronized (os)
			{
				os.println(sendInfo.toString());
				os.flush();
			}
		} 
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "JSONException");
		}
	}
	
	boolean playing = true;
	String updateInfo = null;
	long rcvTime = -1;
	double rcvXX, rcvYY;
	double rcvSpeed;
	int remain;
	int speedDir;
	String win = null;
	Runnable listenRunnable = new Runnable()
	{
		
		/**
		 * receive info from server,
		 * if network error occur, the time will be set to -1
		 * */
		@Override
		public void run()
		{
			while (playing)
			{
				try
				{
					String rcvStr = is.readLine();
					//Log.d(Config.LOG_TAG, "game receive : " + rcvStr);
					synchronized (updateInfo)
					{
						updateInfo = new String(rcvStr);
					}
					
					rcvTime = System.currentTimeMillis();
					JSONObject rcvInfo = new JSONObject(rcvStr);
					
					String type = Config.INIT_STR;
					if (rcvInfo.has("type") && rcvInfo.getString("type") != null)
						type = rcvInfo.getString("type");
					else
					{
						gameError();
						Log.d(Config.LOG_TAG, "game illegal msg 11");
						playing = false;
						return;
					}
					boolean legalMSG = rcvInfo.has("users") && rcvInfo.getJSONObject("users") != null && 
									   rcvInfo.has("prop") && rcvInfo.getJSONArray("prop") != null;
					if (type.equals("info") && legalMSG)
					{
						JSONObject users = rcvInfo.getJSONObject("users");
						//Log.d(Config.LOG_TAG, "game msg 4 username " + username);
						//Log.d(Config.LOG_TAG, "game msg 4 has ? " + users.has(username));
						//Log.d(Config.LOG_TAG, "game msg 4 null ? " + (users.getJSONObject(username) == null));
						legalMSG = users.has(username) && users.getJSONObject(username) != null;
						if (legalMSG)
						{
							JSONObject self = users.getJSONObject(username);
							legalMSG = self.has("x") && self.has("y") && self.has("speed") && self.has("remain");
							if (legalMSG)
							{
								rcvXX = self.getDouble("x");
								rcvYY = self.getDouble("y");
								rcvSpeed = self.getDouble("speed");
								remain = self.getInt("remain");
								speedDir = self.getInt("status");
								
								synchronized (props)
								{
									props = new JSONArray(rcvInfo.getJSONArray("prop").toString());
								}
							}
							else
							{
								gameError();
								Log.d(Config.LOG_TAG, "game illegal msg 3");
								playing = false;
								return;
							}
						}
						else
						{
							gameError();
//							Log.d(Config.LOG_TAG, "game msg 4 username " + username);
//							Log.d(Config.LOG_TAG, "game msg 4 has ? " + users.has(username));
//							Log.d(Config.LOG_TAG, "game msg 4 null ? " + (users.getJSONObject(username) == null));
//							Log.d(Config.LOG_TAG, "game illegal msg 4");
							playing = false;
							return;
						}
					}
					else
					{
						if (!type.equals("result"))
						{
							gameError();
							Log.d(Config.LOG_TAG, "game illegal msg 4");
							playing = false;
							return;
						}
					}
				} 
				catch (SocketTimeoutException e) 
				{
					if (e.getMessage() != null)
						Log.d(Config.LOG_TAG, e.getMessage());
					else
						Log.d(Config.LOG_TAG, "SocketTimeoutException");
					gameError();
					Log.d(Config.LOG_TAG, "game illegal msg 5");
					playing = false;
					return;
				}
				catch (IOException e)
				{
					if (e.getMessage() != null)
						Log.d(Config.LOG_TAG, e.getMessage());
					else
						Log.d(Config.LOG_TAG, "IOException");
					gameError();
					Log.d(Config.LOG_TAG, "game illegal msg 6");
					playing = false;
					return;
				} 
				catch (JSONException e)
				{
					if (e.getMessage() != null)
						Log.d(Config.LOG_TAG, e.getMessage());
					else
						Log.d(Config.LOG_TAG, "JSONException");
					gameError();
					Log.d(Config.LOG_TAG, "game illegal msg 7");
					playing = false;
					return;
				} 
			}
		}
	};
	
	
	/**
	 * Issue game error method.
	 * This method will set the game status
	 * information's type as error, to let
	 * viewer know that it should exit the game,
	 * and notify controller, too.
	 * @param none
	 * @return none
	 * */
	protected void gameError()
	{
		JSONObject error = new JSONObject();
		try
		{
			error.put("type", "error");
			synchronized (updateInfo)
			{
				updateInfo = error.toString();
				myController.setGameToOver();
			} 
		} 
		catch (JSONException e1)
		{
			if (e1.getMessage() != null)
				Log.d(Config.LOG_TAG, e1.getMessage());
			else
				Log.d(Config.LOG_TAG, "JSONException");
		}
	}
}
