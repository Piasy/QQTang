import java.util.*;
import net.sf.json.*;
	
/**.
 * 这个类存放的是一些与环境无关的参数，以及一些常用的方法
 */
public class Constant {
	/**.
	 * 向登陆服务器刷新用户资料的频率
	 */
	public final static int SUBMIT_PERIOD_FOR_LOGINSERVER = 10*1000;

	/**.
	 * 对大厅事件的发送频率（监听频率）
	 */
	public final static int SUBMIT_PERIOD_FOR_CLIENT = 1*1000;

	/**.
	 * 对房间聊天事件的发送频率（监听频率）
	 */
	public final static int SUBMIT_PERIOD_FOR_CLIENT_TALK = 500;

	/**.
	 * 对游戏开始事件的发送频率（监听频率）
	 */
	public final static int SUBMIT_PERIOD_FOR_GAME_START = 200;

	/**.
	 * 对客户端断线的判断标准
	 */
	public final static int TIME_LIMIT_FOR_CLIENT = 20*1000;

	/**.
	 * 对游戏服务器断线的判断标准
	 */
	public final static int TIME_LIMIT_FOR_GAMESERVER = 20*1000;

	/**.
	 * 大厅容量限制
	 */
	public final static int HALL_LIMIT = 50;
	/**.
	 * 房间容量限制
	 */
	public final static int ROOM_LIMIT = 4;

	/**.
	 * 一次性密码的长度
	 */
	public final static int KEY_LENGTH = 64;
	/**.
	 * 获取一个一次性密码
	 */
	public final static String getKey() {
		String key = "";
		for (int i = 0; i < KEY_LENGTH; i++) {
			key = key + new Integer((int)(Math.random()*10 - 0.001)).toString();
		}
		return key;
	}

	/**.
	 * .
	 */
	public static JSONObject getGameServerConnection() {
		JSONObject game = new JSONObject();
		game.put("key", Config.KEY_WITH_GAME_SERVER);
		return game;
	}

	/**.
	 * 根据输赢情况改变经验值
	 */
	public static int getExp(int exp, String result) {
		if (result.equals("win")) {
			return exp + 10;
		}
		if (result.equals("lose")) {
			return exp + 1;
		}
		if (result.equals("break")) {
			if (exp - 10 >= 0) {
				return exp - 10;
			} else {
				return 0;
			}
		}
		return exp;
	}


	/**.
	 * 根据经验值给出等级
	 */
	public static int getLevel(int exp)
	{
		if (exp > 10000) return 15;
		if (exp > 9100) return 14;
		if (exp > 7800) return 13;
		if (exp > 6600) return 12;
		if (exp > 5500) return 11;
		if (exp > 4500) return 10;
		if (exp > 3600) return 9;
		if (exp > 2800) return 8;
		if (exp > 2100) return 7;
		if (exp > 1500) return 6;
		if (exp > 1000) return 5;
		if (exp > 600) return 4;
		if (exp > 300) return 3;
		if (exp > 100) return 2;
		if (exp > 0) return 1;
		return 0;
	}
}
