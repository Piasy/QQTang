import java.io.*;
import java.net.*;
import java.util.*;
import net.sf.json.*;
	
/**.
 * 这个类负责与客户端通信
 */
public class ClientTalk extends Talk {
	/**.
	 * 记录大厅内发生事件的编号
	 */
	private int hallCount = 0;

	/**.
	 * 记录通信的客户端的用户名
	 */
	private String user = null;

	/**.
	 * 记录通信的客户端的一次性密码
	 */
	private String key = null;

	/**.
	 * 记录通信的客户端的用户资料
	 */
	private JSONObject userData = null;

	/**.
	 * 线程运行函数入口，负责验证客户端的身份和一次性密码
	 */
	public void run() {
		try {
			String loginText = is.readLine();
			Log.client("Receive: " + loginText);
			JSONObject loginData = JSONObject.fromObject(loginText);
			user = loginData.getString("user");
			key = loginData.getString("key");
			synchronized (clientConnection) {
				userData = clientConnection.getJSONObject(user);
				if (userData == null) {
					return;
				}
				if (userData.getString("key").equals(key)) {
					userData.put("key", Constant.getKey());
					userData.put("timestamp", System.currentTimeMillis());
				} else {
					return;
				}
			}

			hall.execute(loginQuery(userData.getJSONObject("details")));
			userData.remove("details");
			sendSuccess();
			timer.schedule(new SendBeginTask(), 0, Constant.SUBMIT_PERIOD_FOR_GAME_START);
			timer.schedule(new SendHallInfoTask(), 0, Constant.SUBMIT_PERIOD_FOR_CLIENT);
			timer.schedule(new SendRoomMessagesTask(), 0, Constant.SUBMIT_PERIOD_FOR_CLIENT_TALK);
			timer.schedule(new CheckConnectionTask(), 0, Constant.TIME_LIMIT_FOR_CLIENT);
			listen();
		} catch (Exception e) {
			Log.clientErr("Connection fail", e);
		}
		stop();
	}

	/**.
	 * 监听函数，负责接收客户端发来的消息并作出处理
	 */

	private void listen() throws Exception {
		int errCount = 0;
		while (runFlag) {
			try {
				String text = is.readLine();
				if (!runFlag) {
					break;
				}
				Log.client("Receive: " + text);

				JSONObject data = JSONObject.fromObject(text);
				if (data.getString("type").equals("logout")) {
					break;
				}
				if (data.getString("type").equals("heartbeat")) {
					synchronized (clientConnection) {
						userData.put("timestamp", System.currentTimeMillis());
					}
					continue;
				}
				data.put("user", user);
				hall.execute(data);
			} catch (Exception e) {
				Log.clientErr("Talk listener", e);
				if (errCount++ > 2) {
					return;
				}
			}
		}
	}

	/**.
	 * 向客户端发送连接成功
	 */
	void sendSuccess() throws Exception {
		JSONObject ans = null;
		ans = hall.getStatus();
		hallCount = ans.getInt("count");

		JSONObject result = new JSONObject();
		result.put("type", "success");
		result.put("users", ans.getJSONObject("users"));
		result.put("rooms", ans.getJSONArray("rooms"));
		Log.client("Send: " + result);
		os.println(result.toString());
		os.flush();
	}

	/**.
	 * 这个子类负责向客户端发送游戏开始的信息
	 */
	class SendBeginTask extends TimerTask {
		public void run() {
			try {
				JSONObject game = hall.getGameStartInfo(user);
				if (game != null) {
					game.put("type", "game");
					Log.client("Send: " + game.toString());
					os.println(game.toString());
					os.flush();
				}
			} catch (Exception e) {
				Log.clientErr("SendBeginTask", e);
				stop();
			}
		}
	}

	/**.
	 * 这个子类负责向客户端发送大厅内的事件
	 */
	class SendHallInfoTask extends TimerTask {
		/**.
		 * ��ͻ��˷��ͽ�1���ڴ����ڷ��͵��¼�
		 */
		public void run() {
			try {
				JSONObject ans = hall.getEvents(hallCount);
				hallCount = ans.getInt("count");
				JSONObject result = new JSONObject();
				result.put("type", "events");
				result.put("events", ans.getJSONArray("events"));
				result.put("rooms", ans.getJSONArray("rooms"));
				Log.client("Send: " + result.toString());
				os.println(result.toString());
				os.flush();

			}
			catch (Exception e)
			{
				Log.clientErr("SendHallInfoTask", e);
				stop();
			}

		}
	}

	/**.
	 * 这个子类负责向客户端发送房间内的聊天内容
	 */
	class SendRoomMessagesTask extends TimerTask {
		/**.
		 * ��ͻ��˷��ͽ�1���ڴ����ڷ��͵��¼�
		 */
		public void run() {
			try {
				JSONArray messages = hall.getMessages(user);
				if (messages.size() > 0)
				{
					JSONObject result = new JSONObject();
					result.put("type", "messages");
					result.put("messages", messages);
				
					Log.client("Send: " + result.toString());
					os.println(result.toString());
					os.flush();
				}
			}
			catch (Exception e)
			{
				Log.clientErr("SendRoomMessagesTask", e);
				stop();
			}

		}
	}	

	/**.
	 * 这个子类负责检测客户端是否断线
	 */
	class CheckConnectionTask extends TimerTask {
		/**.
		 * �������ͻ����Ƿ�Ͽ�����
		 */
		public void run() {
			try {
				synchronized (clientConnection) {
					long now = System.currentTimeMillis();
					long timestamp = userData.getLong("timestamp");
					if (timestamp + Constant.TIME_LIMIT_FOR_CLIENT < now) {
						stop();
						return;
					}
				}
			} catch (Exception e) {
				Log.clientErr("CheckConnectionTask", e);
				stop();
			}
		}
	}

	/**.
	 * 停止监听这个客户端，与其断开连接
	 */
	protected void stop() {
		timer.cancel();
		runFlag = false;
		synchronized (clientConnection) {
			userData.put("timestamp", 0);
		}
		Log.client("ClientTalk Finished.");
		thread = null;
	}

	/**.
	 * 构造一个登陆的信息
	 */
	private JSONObject loginQuery(JSONObject details) {
		JSONObject query = new JSONObject();
		query.put("user", user);
		query.put("type", "login");
		query.put("details", details);
		return query;
	}
}
