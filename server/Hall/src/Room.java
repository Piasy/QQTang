import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;
	
public class Room {
	/**.
	 * 该房间中每个位置的用户id
	 */
	private String[] users;

	/**.
	 * 该房间中每个位置的状态
	 */
	private int[] status;
	public final static int EMPTY = 0;
	public final static int UNREADY = 1;
	public final static int READY = 2;

	/**.
	 * 该房间的状态
	 */
	private int stage;

	public final static int WAITING = 0;
	public final static int ALLREADY = 1;
	public final static int PLAYING = 2;

	/**.
	 * 该房间每个位置的用户加入游戏服务器的一次性口令
	 */
	private String[] key;

	/**.
	 * 该房间每个位置的用户加入游戏服务器的ip地址
	 */
	private String[] ip;

	/**.
	 * 该房间每个位置的用户加入游戏服务器的端口号
	 */
	private int[] port;

	/**.
	 * 该房间的编号
	 */
	private int num;

	/**.
	 * 该房间内的聊天记录
	 */
	private JSONArray[] msg;

	Room(int _num) {
		users =  new String[Constant.ROOM_LIMIT];
		status = new int[Constant.ROOM_LIMIT];
		key = new String[Constant.ROOM_LIMIT];
		ip = new String[Constant.ROOM_LIMIT];
		port = new int[Constant.ROOM_LIMIT];
		num = _num;
		msg = new JSONArray[Constant.ROOM_LIMIT];
		for (int i = 0; i < Constant.ROOM_LIMIT; i++) {
			users[i] = "";
			status[i] = EMPTY;
			msg[i] = new JSONArray();
		}
		stage = WAITING;
    }

	/**.
	 * 获取该房间内的一个空位编号，若没有则返回-1
	 */
	public int getEmpty() {
		for (int i = 0; i < status.length; i++) {
			if (status[i] == EMPTY) {
				return i;
			}
		}
		return -1;
	}

	/**.
	 * 查询该房间内一个位置的状态
	 */
	public int getStatus(int pos) throws Exception {
		return status[pos];
	}

	/**.
	 * 查询该房间内一个位置的用户名称
	 */
	public String getUser(int pos) throws Exception {
		return users[pos];
	}

	/**.
	 * 用户user进入该房间的pos号座位
	 */
	public void enter(int pos, String user) throws Exception {
		users[pos] = user;
		status[pos] = UNREADY;
	}

	/**.
	 * 用户user离开该房间
	 */
	public void leave(int pos) throws Exception {
		users[pos] = "";
		status[pos] = EMPTY;
		msg[pos] = new JSONArray();
		
		boolean flag = true;
		for (int i=0; i<status.length; i++)
			if (status[i] != EMPTY) flag = false;
		if (flag) stage = WAITING;
	}
	
	/**.
	 * 在pos号位置的用户准备
	 */
	public void ready(int pos) throws Exception {
		status[pos] = READY;
	}

	/**.
	 * 在pos号位置的用户取消准备
	 */
	public void unready(int pos) throws Exception {
		status[pos] = UNREADY;
	}

	/**.
	 * 判断房间内所有用户是否都已准备
	 */
	public boolean isReady() {
		int count = 0;
		for (int i = 0; i < Constant.ROOM_LIMIT; i++) {
			if (status[i] == READY) {
				count++;
			}
			if (status[i] == UNREADY) {
				return false;
			}
		}
		return count > 1;
	}

	/**.
	 * 获取房间的状态
	 */
	public int getStage() {
		return stage;
	}

	/**.
	 * 设置房间的状态
	 */
	public void setStage(int _stage) {
		stage = _stage;
	}

	/**.
	 * 获取pos号位置用户的key
	 */
	String getKey(int pos) throws Exception {
		return key[pos];
	}

	/**.
	 * 设置pos号位置用户的key
	 */
	public void setKey(int pos, String _key) throws Exception {
		key[pos] = _key;
	}

	/**.
	 * 获取pos号位置用户的ip
	 */
	String getIp(int pos) throws Exception {
		return ip[pos];
	}

	/**.
	 * 获取pos号位置用户的port
	 */
	int getPort(int pos) throws Exception {
		return port[pos];
	}

	/**.
	 * 设置pos号位置用户的ip和port
	 */
	void setAddr(int pos, String _ip, int _port) throws Exception {
		ip[pos] = _ip;
		port[pos] = _port;
	}

	/**.
	 * 获取房间编号
	 */
	public int getNum() {
		return num;
	}
	
	/**.
	 * 添加聊天记录
	 */
	public void addMessage(int pos, String message) throws Exception
	{
		JSONObject m = new JSONObject();
		m.put("speaker", users[pos]);
		m.put("message", message);
		for (int i=0; i<msg.length; i++)
		{
			if (status[i] != EMPTY)
			{
				msg[i].add(m);
			}
		}
	}
	
	/**.
	 * 获取聊天记录
	 */
	public JSONArray getMessages(int pos) throws Exception
	{
		JSONArray messages = msg[pos];
		msg[pos] = new JSONArray();
		return messages;
	}
}
