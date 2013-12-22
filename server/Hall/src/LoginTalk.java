import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;
	
/**.
 * 这个类负责与登录服务器通信
 */
public class LoginTalk extends Talk {
	/**.
	 * 一个静态方法，负责创建与登录服务器通信的类
	 */
	public static void connectLoginServer(String user, String password, String addr, int port)
	{
		JSONObject information = new JSONObject();
		information.put("user", user);
		information.put("password", password);
		information.put("ip", addr);
		information.put("port", port);
		try {
			Socket socket = new Socket(Config.LOGIN_ADDR, Config.LOGIN_PORT);
			PrintWriter os = new PrintWriter(socket.getOutputStream());
			Log.login("Send: " + information.toString());
			os.println(information.toString());
			os.flush();
			Talk.add("loginServer", socket, null, os);
		} catch (Exception e) {
			Log.loginErr("Connect to LoginServer fail", e);
		}
	}

	/**.
	 * 新线程开始运行
	 */
	public void run() {
		Log.log("Connected to LoginServer");
		timer.schedule(new SubmitTask(), 0, Constant.SUBMIT_PERIOD_FOR_LOGINSERVER);
		int errCount = 0;
		while (runFlag) {
			try {
				String text = is.readLine();
				if (!runFlag) {
					break;
				}
				Log.login("Receive: " + text);
				JSONObject data = JSONObject.fromObject(text);
				data.put("timestamp", System.currentTimeMillis());
				data.put("status", "online");
				String user = data.getString("user");
				synchronized (clientConnection) {
					if (!clientConnection.has(user) || clientConnection.getJSONObject(user).getString("status").equals("offline")) 
						clientConnection.put(user, data);
				}
			} catch (Exception e) {
				Log.loginErr("Connection", e);
				if (errCount++ > 2) {
					break;
				}
			}
		}
		stop();
	}

	/**.
	 * 这个类负责向登录服务器发送用户资料
	 */
	class SubmitTask extends TimerTask {
		public void run() {
			try {
				JSONArray userData = new JSONArray();
				int count = 0;
				synchronized (clientConnection) {
					Set set = clientConnection.keySet();
					Iterator iter = set.iterator();
					while (iter.hasNext()) {
						String user = (String)iter.next();
						JSONObject connection = clientConnection.getJSONObject(user);
						if (connection.getString("status").equals("online")) {
							long currentTime = System.currentTimeMillis();
							JSONObject data = new JSONObject();
							data.put("user", connection.getString("user"));
							if (connection.getLong("timestamp") + Constant.TIME_LIMIT_FOR_CLIENT < currentTime) {
								connection.put("status", "offline");
								data.put("status", "offline");
								if (connection.has("details")) {
                                                                        data.put("details", connection.getJSONObject("details"));
                                                                } else {
                                                                        data.put("details", hall.getDetails(user));
								        hall.logout(user);
                                                                }
							
                                                        } else {
								count ++;
								data.put("status", "online");
								if (connection.has("details")) {
                                                                        data.put("details", connection.getJSONObject("details"));
                                                                } else {
                                                                        data.put("details", hall.getDetails(user));
                                                                }
							}
							userData.add(data);
						}
					}
				}
				JSONObject result = new JSONObject();
				result.put("userData", userData);
				result.put("limit", Constant.HALL_LIMIT - count);
				os.println(result.toString());
				os.flush();
				Log.login("Send: " + result);
			} catch (Exception e) {
				Log.loginErr("SubmitTask: ", e);
			}
		}
	}

	protected void stop() {
		Log.log("HallServer stop");
		super.stop();
	}
}
