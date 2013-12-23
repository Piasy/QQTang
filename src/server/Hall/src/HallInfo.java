import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;

/**.
 * 游戏大厅类。由于只有一个大厅，所以它是单件模式。
 */
public class HallInfo {
	/**.
	 * 该大厅内的房间。
	 */	
	private Room rooms[];

	/**.
	 * 该大厅内的用户以及他们的信息。
	 * 格式为：{"username1":{...}, "username2":{...}}
	 * 其中省略部分为一个用户的信息，包括：
	 * 		int room 			表示所在房间号，-1表示不在房间中
	 * 		int pos   			表示所在座位号，-1表示不在座位中
	 * 		String ready		表示当前是否准备，只可能是"ready" 或 "unready"
	 * 		JSONObject details	表示用户资料
	 * 之后再提到用户信息，请参考这个说明
	 */	
	private JSONObject users;

	/**.
	 * 该大厅内的玩家操作记录，包括进入/离开大厅，进入/离开房间，准备/取消准备。
	 * 大厅内的事件从0开始顺序编号，事件编号对应着事件在events中的下标。事件的编号在后面还会用到。
	 */	
	private JSONArray events;

	/**.
	 * 大厅的构造函数，主要任务是为每个房间创造实例。
	 */
	private HallInfo() {
		events = new JSONArray();
		users = new JSONObject();
		rooms = new Room[Constant.HALL_LIMIT];
		for (int i = 0; i < rooms.length; i++) {
			rooms[i] = new Room(i);
		}		
	}
	
	private static HallInfo hall = null;

	public static HallInfo getInstance() {
		if (hall == null) {
			hall = new HallInfo();
		}
		return hall;
	}

	public static void clear()
	{
		if (hall != null)
			hall = new HallInfo();
	}

	/**.
	 * 通过该函数，可以得到大厅当前的状态。
	 * 返回的字典包括：
	 * 		1）users  大厅内所有用户的信息
	 * 		2）rooms  大厅内所有房间是否开始游戏
	 * 		3）count  截止到现在，大厅内发生过的事件数量
	 */
	public JSONObject getStatus() throws Exception {
		synchronized (this) {
			JSONObject ans = new JSONObject();
			ans.put("users", users);
			ans.put("rooms", getRoomStage());
			ans.put("count", events.size());
			return ans;
		}
	}

	/**.
	 * 通过该函数，可以得到最近一段时间大厅发生的变化。
	 * 返回的字典包括：
	 * 		1）events 编号从now开始到现在为止大厅内发生的事件。
	 * 		2）rooms  大厅内所有房间是否开始游戏
	 * 		3）count  截止到现在，大厅内发生过的事件数量
	 */
	public JSONObject getEvents(int now) throws Exception {
		synchronized (this) {
			int n = events.size();
			JSONArray result = new JSONArray();
			for (int i = now; i < n; i++) {
				result.add(events.getJSONObject(i));
			}
			JSONObject ans = new JSONObject();
			ans.put("count", n);
			ans.put("events", result);
			ans.put("rooms", getRoomStage());
			return ans;
		}
	}

	/**.
	 * 通过该函数，可以得到一个用户的信息。传入参数user为该用户的用户名。
	 */	
	public JSONObject getDetails(String user) throws Exception {
		synchronized (this) {
			return users.getJSONObject(user).getJSONObject("details");
		}
	}
	
	/**.
	 * 通过这个函数，获取一个用户最近的聊天信息
	 */	
	public JSONArray getMessages(String user) throws Exception{
		if (!users.has(user)) {
			return new JSONArray();
		}
		
		int room = users.getJSONObject(user).getInt("room");
		int pos = users.getJSONObject(user).getInt("pos");
		if (room > -1) {
			synchronized (this){
				return rooms[room].getMessages(pos);
			}
		} else {
			return new JSONArray();
		}
	}	

	/**.
	 * 通过该函数，可以执行一个用户的操作。传入的字典query必须包含type和user，表示用户和操作种类。
	 * 操作种类包括：
	 * 		1）login  	表示进入大厅，需要同时传入details
	 * 		2）enter  	表示进入房间，需要同时传入room与pos表示房间号和座位号
	 * 		3）leave  	表示离开房间
	 * 		4）ready  	表示准备
	 * 		5）unready 	表示取消准备
	 * 		6）message	表示在房间中说一句话，需要同时传入message
	 * 对于每个操作：
	 * 		1） 若格式非法，则会抛出异常
	 * 		2） 若格式合法，但操作行为不合法（例如不在房间但要求离开房间），则什么都不会做，并返回false
	 * 		3） 若格式合法，行为也合法，则会进行该操作，并返回true
	 */	
	public boolean execute(JSONObject query) throws Exception {
		synchronized (this) {
			String type = query.getString("type");
			String user = query.getString("user");
						
			if (type.equals("login")) {
				if (!query.has("details")) throw new Exception();
				
				if (!users.has(user)) {
					users.put(user, newUser(query.getJSONObject("details")));
					events.add(query);
					return true;
				} else {
					return false;
				}
			}
			
			if (!users.has(user)) return false;
			JSONObject userData = users.getJSONObject(user);

			if (type.equals("enter")) {
				int room = query.getInt("room");
				int pos = query.getInt("pos");

				if (room == 999) {
					for (int i = 0; i < rooms.length; i++)
						if (rooms[i].getEmpty() > -1) {
							room = i;
							pos = rooms[i].getEmpty();
							break;
						}
				}
				if (room >= Constant.HALL_LIMIT || room < 0) {
					return false;
				}

				if (pos == 999)
					pos = rooms[room].getEmpty();
				if (pos >= Constant.ROOM_LIMIT || pos < 0) {
					return false;
				}
				
				if (userData.getInt("room") == -1 && rooms[room].getStatus(pos) == Room.EMPTY && rooms[room].getStage() == Room.WAITING)
				{
					userData.put("room", room);
					userData.put("pos", pos);
					rooms[room].enter(pos, user);
					query.put("room", room);
					query.put("pos", pos);
					events.add(query);
					return true;
				} else {
					return false;
				}
			}

			int room = userData.getInt("room");
			int pos = userData.getInt("pos");
			if (room == -1) {
				return false;
			}

			Room userRoom = rooms[room];

			if (type.equals("message")) {
				if (!query.has("message")) throw new Exception();
				String message = query.getString("message");
				userRoom.addMessage(pos, message);
				return true;
			}

			if (type.equals("leave")) {
				userData.put("room", -1);
				userData.put("pos", -1);
				userData.put("ready", "unready");					
				userRoom.leave(pos);
				events.add(query);
				return true;
			}

			if (type.equals("ready")) {
				if (userRoom.getStatus(pos) == Room.UNREADY) {
					userData.put("ready", "ready");
					userRoom.ready(pos);
					events.add(query);
					if (userRoom.isReady()) {
						start(userRoom);
					}
					return true;
				} else {
					return false;
				}
			}

			if (type.equals("unready")) {
				if (userRoom.getStatus(pos) == Room.READY) {
					userData.put("ready", "unready");
					userRoom.unready(pos);
					events.add(query);
					return true;
				} else {
					return false;
				}
			}
			
			return false;
		}
	}	

	/**.
	 * 通过该函数，执行一个用户离开大厅的操作。
	 */	
	public boolean logout(String user) throws Exception {
		synchronized (this) {
			if (users.has(user)) {
				JSONObject data = users.getJSONObject(user);
				int room = data.getInt("room");
				int pos = data.getInt("pos");
				if (room > -1) {
					rooms[room].leave(pos);
				}
				users.remove(user);
				JSONObject query = new JSONObject();
				query.put("user", user);
				query.put("type", "logout");
				events.add(query);
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**.
	 * 通过这个函数，设置一局游戏开始的信息
	 */	
	public void setGameStartInfo(int id, JSONArray infos) throws Exception {
		JSONObject data = new JSONObject();
		for (int i = 0; i < infos.size(); i++) {
			JSONObject info = infos.getJSONObject(i);
			data.put(info.getString("user"), info);
		}
		synchronized (this) {
			Room room = rooms[id];
			if (room.getStage() == Room.ALLREADY) {
				for (int i = 0; i < Constant.ROOM_LIMIT; i++) {
					if (room.getStatus(i) != Room.EMPTY) {
						String user = room.getUser(i);
						room.setAddr(i, data.getJSONObject(user).getString("ip"), data.getJSONObject(user).getInt("port"));
					}
				}
				room.setStage(Room.PLAYING);
			}
		}
	}

	/**.
	 * 通过这个函数，查询一个用户是否开始游戏
	 */	
	public JSONObject getGameStartInfo(String user) throws Exception {
		synchronized (this) {
			if (!users.has(user)) {
				return null;
			}
			int room = users.getJSONObject(user).getInt("room");
			int pos = users.getJSONObject(user).getInt("pos");
			if (room > -1) {
				Room userRoom = rooms[room];
				if (userRoom.getStage() == Room.PLAYING && userRoom.getStatus(pos) == Room.READY) {
					userRoom.unready(pos);
					
					JSONObject result = new JSONObject();
					result.put("key", userRoom.getKey(pos));
					result.put("ip", userRoom.getIp(pos));
					result.put("port", userRoom.getPort(pos));
					return result;
				}				
			}
		}
		return null;
	}

	/**.
	 * 通过这个函数，设置一局游戏结束的信息
	 */	
	public void setGameFinishedInfo(int id, JSONArray infos) throws Exception {
		Room room = rooms[id];
		if (room.getStage() == Room.PLAYING) {
			room.setStage(Room.WAITING);
			for (int i = 0; i < infos.size(); i++) {
				JSONObject info = infos.getJSONObject(i);
				String user = info.getString("user");
				String result = info.getString("result");
				synchronized(this) {
			        if (users.has(user)) {
		        		JSONObject userInfo = users.getJSONObject(user);
	        			userInfo.put("ready", "unready");
        				JSONObject details = userInfo.getJSONObject("details");
    					details.put("exp", Constant.getExp(details.getInt("exp"), result));
				        details.put("level", Constant.getLevel(details.getInt("exp")));
			        	if (result.equals("win")) {
		        			details.put("success", details.getInt("success") + 1);
	        			}
        				if (result.equals("lose")) {
					        details.put("fail", details.getInt("fail") + 1);
				        }
			        	if (result.equals("break")) {
					        details.put("break", details.getInt("break") + 1);
				        }
						//JSONObject unready = new JSONObject();
						//unready.put("user", user);
						//unready.put("type", "unready");
						//events.add(unready);

						JSONObject refresh = new JSONObject();
						refresh.put("user", user);
						refresh.put("type", "refresh");
						refresh.put("details", details);
				        events.add(refresh);
			        }
		        }
			}
		}
	}
		
	private void start(Room room) throws Exception {
		room.setStage(Room.ALLREADY);

		JSONArray infos = new JSONArray();
		for (int i = 0; i < Constant.ROOM_LIMIT; i++) {
			if (room.getStatus(i) != Room.EMPTY) {
				String user = room.getUser(i);
				String key = Constant.getKey();
				room.setKey(i, key);
				JSONObject details = users.getJSONObject(user).getJSONObject("details");

				JSONObject info = new JSONObject();
				info.put("user", user);
				info.put("key", key);
				info.put("details", details);
				infos.add(info);
			}
		}
		JSONObject data = new JSONObject();
		data.put("type", "ready");
		data.put("id", room.getNum());
		data.put("users", infos);
		
		GameTalk gameTalk = GameTalk.getInstance();
		if (gameTalk == null) {
			Log.log("Not find GameServer!");
		} else {
			gameTalk.send(data.toString());
		}
	}
	
	private JSONObject newUser(JSONObject details) {
		JSONObject data = new JSONObject();
		data.put("room", -1);
		data.put("pos", -1);
		data.put("ready", "unready");
		data.put("details", details);
		return data;
	}

	private JSONArray getRoomStage() {
		JSONArray stages = new JSONArray();
		for (int i = 0; i < rooms.length; i++) {
			int stage = rooms[i].getStage();
			if (stage == Room.WAITING) {
				stages.add(0);
			} else {
				stages.add(1);
			}
		}
		return stages;
	}
}
