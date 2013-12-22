import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;
	
/**.
 * 这个类负责与游戏服务器通信
 */
public class GameTalk extends Talk {
	/**.
	 * GameTalk的唯一事例
	 */
	private static GameTalk talk = null;

	/**.
	 * 获取GameTalk的实例
	 */
	public static GameTalk getInstance() {
		return talk;
	}

	/**.
	 * 新线程开始运行的函数
	 */
	public void run() {
		try {
			if (talk != null) {
				Log.log("GameServer is already online!");
				return;
			}
			String text = is.readLine();
			Log.game("Receive: " + text);
			JSONObject data = JSONObject.fromObject(text);
			if (!data.getString("key").equals(gameServerConnection.getString("key"))) {
				Log.log("GameServer send wrong key!");
				return;
			}

			gameServerConnection.put("timestamp", System.currentTimeMillis());
			//timer.schedule(new CheckConnectionTask(), 0, Constant.TIME_LIMIT_FOR_GAMESERVER);
			talk = this;
			if (talk != null) {
				Log.log("GameServer connect Success");
			}
			listen();
		} catch (Exception e) {
			Log.gameErr("Connection fail", e);
		}
		stop();
	}

	/**.
	 * 向游戏服务器发送数据
	 */
	public void send (String s) {
		synchronized (os) {
			Log.game(s);
			os.println(s);
			os.flush();
		}
	}

	/**
	 * 负责监听游戏服务器发来的消息并作出相应
	 */
	void listen() {
		int errCount = 0;
		while (runFlag) {
			try {
				String text = is.readLine();
				Log.game("Receive: " + text);
				JSONObject data = JSONObject.fromObject(text);
				if (data.getString("type").equals("heartbeat")) {
					gameServerConnection.put("timestamp", System.currentTimeMillis());
					continue;
				}
				if (data.getString("type").equals("start")) {
					int id = data.getInt("id");
					JSONArray users = data.getJSONArray("users");
					hall.setGameStartInfo(id, users);
					continue;
				}
				if (data.getString("type").equals("finished")) {
					int id = data.getInt("id");
					JSONArray result = data.getJSONArray("result");
					hall.setGameFinishedInfo(id, result);
					continue;
				}
				throw new Exception();
			} catch (Exception e) {
				Log.gameErr("Talk listener", e);
				if (errCount++ > 2) {
					break;
				}
			}
		}
		stop();
	}

	/**.
	 * 这个类负责检测游戏服务器是否断线
	 */
	class CheckConnectionTask extends TimerTask {
		public void run() {
			try {
				synchronized (clientConnection) {
					long now = System.currentTimeMillis();
					long timestamp = gameServerConnection.getLong("timestamp");
					if (timestamp + Constant.TIME_LIMIT_FOR_GAMESERVER < now) {
						Log.log("GameServerTalk timeout");
						stop();
					}
				}
			} catch (Exception e) {
				Log.gameErr("CheckConnectionTask", e);
				stop();
			}
		}
	}

	/**.
	 * 停止该线程
	 */
	protected void stop() {
		timer.cancel();
		runFlag = false;
		talk = null;
		Log.log("GameServerTalk Finished.");
		thread = null;
	}
}
