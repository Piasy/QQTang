import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**.
 * 大厅的主类
 * @author qianqiao
 */

public class Main {
	/**.
	 * 启动大厅的入口函数
	 */
	public static void main(String args[]) {
		Log.log("Hello world! I'm Hall Server!");

		Gate gameServerGate = new Gate();
		gameServerGate.start("gameServer", Config.THREAD_FOR_GAMESERVER, Config.MY_PORT_FOR_GAMESERVER);

		LoginTalk.connectLoginServer(Config.MY_USERNAME, Config.MY_PASSWORD, Config.MY_ADDR, Config.MY_PORT_FOR_CLIENT);

		Gate clientGate = new Gate();
		clientGate.start("client", Config.THREAD_FOR_CLIENT, Config.MY_PORT_FOR_CLIENT);

		manage();
	}
	/**.
	 * 通过该函数可以手工控制大厅。具体地讲，该函数支持屏幕输入以下命令来管理大厅：
	 * 1） client close 停止输出与客户端之间的通信内容
	 * 2） client open  输出与客户端的通信内容
	 * 3） login close  停止输出与登录服务器之间的通信内容
	 * 4） login open   输出与客户端之间的通信内容
	 * 5） game close   停止输出与游戏服务器之间的通信内容
	 * 6） game open    输出与游戏服务器之间的通信内容
	 * 7） status       查看当前大厅状态
	 * 8） {JSONObject} 执行一个用户操作。具体格式详见大厅的接口函数。
	 */
	private static void manage() {
		HallInfo hall = HallInfo.getInstance();
		Scanner scanner = new Scanner(System.in);
		int count = 0;
		while (true) {
			try {
				String text = scanner.nextLine();
				if (text.equals("status")) {
					JSONObject ans = hall.getStatus();
					System.out.println(ans);
				}
				
				if (text.equals("events")) {
					JSONObject ans = hall.getEvents(count);
					count = ans.getInt("count");
					System.out.println(String.format("count = %d", count));
					JSONArray result = ans.getJSONArray("events");
					if (result != null) System.out.println(result.toString());
				}

				if (text.equals("game open")) Log.setGameInfo(true);
				if (text.equals("game close")) Log.setGameInfo(false);
				if (text.equals("client open")) Log.setClientInfo(true);
				if (text.equals("client close")) Log.setClientInfo(false);
				if (text.equals("login open")) Log.setLoginInfo(true);
				if (text.equals("login close")) Log.setLoginInfo(false);

				if (text.equals("game err open")) Log.setGameErr(true);
				if (text.equals("game err close")) Log.setGameErr(false);
				if (text.equals("client err open")) Log.setClientErr(true);
				if (text.equals("client err close")) Log.setClientErr(false);
				if (text.equals("login err open")) Log.setLoginErr(true);
				if (text.equals("login err close")) Log.setLoginErr(false);
				
				if (text.startsWith("{") && text.endsWith("}")) {
					JSONObject json = JSONObject.fromObject(text);
					hall.execute(json);
				}
			} catch (Exception e) {
				System.out.println("Manage error -- " + e);
			}
		}
	}
}