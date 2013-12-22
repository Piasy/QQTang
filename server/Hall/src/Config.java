import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;
	
/**.
 * 这个类存放的是与环境相关的参数
 */
public class Config {
	/**.
	 * 验证大厅身份的名称
	 */
	public final static String MY_USERNAME = "hall-server2";
	/**.
	 * 验证大厅身份的密码
	 */
	public final static String MY_PASSWORD = "bigtwos";

	/**.
	 * 运行环境的IP地址
	 */
	public final static String MY_ADDR = "101.5.113.43";

	/**.
	 * 登陆服务器的IP地址
	 */
	public final static String LOGIN_ADDR = "166.111.134.210";

	/**.
	 * 登陆服务器的端口号
	 */
	public final static int LOGIN_PORT = 10002;

	/**.
	 * 监听客户端连接的端口号
	 */
	public final static int MY_PORT_FOR_CLIENT = 10011;

	/**.
	 * 监听客户端连接的线程名称
	 */
	public final static String THREAD_FOR_CLIENT = "Hall-of-Client";

	/**.
	 * 监听游戏服务器的端口号
	 */
	public final static int MY_PORT_FOR_GAMESERVER = 10012;

	/**.
	 * 监听游戏服务器的线程名称
	 */
	public final static String THREAD_FOR_GAMESERVER = "Hall-of-GameServer";

	/**.
	 * 游戏服务器连接时发送的口令
	 */
	public final static String KEY_WITH_GAME_SERVER = "606B606B";
}
