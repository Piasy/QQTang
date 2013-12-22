import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;
	
/**.
 * 这个类负责通信。它是一个纯虚类，派生类有：
 * 		1） LoginTalk   负责与登录服务器通信
 * 		2） GameTalk    负责与游戏服务器通信
 * 		3） GlientTalk  负责与客户端通信
 */
public abstract class Talk implements Runnable {
	/**.
	 * 与游戏服务器通信的相关信息
	 */
	protected static JSONObject gameServerConnection = Constant.getGameServerConnection();

	/**.
	 * 与客户端通信的相关信息
	 */
	protected static JSONObject clientConnection = new JSONObject();

	/**.
	 * 大厅
	 */
	protected static HallInfo hall = HallInfo.getInstance();

	/**.
	 * 通信的socket
	 */
	protected Socket socket;

	/**.
	 * 监听线程
	 */
	protected Thread thread;

	/**.
	 * 读入缓存
	 */
	protected BufferedReader is;

	/**.
	 * 输出缓存
	 */
	protected PrintWriter os;

	/**.
	 * 经常用到的计时器
	 */
	protected Timer timer = new Timer();

	/**.
	 * 线程是否还在运行的flag
	 */
	protected boolean runFlag = true;

	/**.
	 * 使用工厂方法构造一个通信线程
	 */
	public static void add(String type, Socket socket, BufferedReader is, PrintWriter os) {
		Talk talk = null;
		if (type.equals("client")) talk = new ClientTalk();
		if (type.equals("loginServer")) talk = new LoginTalk();
		if (type.equals("gameServer")) talk = new GameTalk();
		if (talk != null) talk.start(socket, is, os);
	}

	/**.
	 * 入口函数，配置好各种信息
	 */
	public void start(Socket _socket, BufferedReader _is, PrintWriter _os) {
		try {
			socket = _socket;
			is = _is;
			os = _os;
			if (is == null) {
				is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			if (os == null) {
				os = new PrintWriter(socket.getOutputStream());
			}
			runFlag = true;
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
		}
	}

	/**.
	 * 线程运行的函数
	 */
	public abstract void run();

	/**.
	 * 停止线程的方法
	 */
	protected void stop() {
		timer.cancel();
		runFlag = false;
		thread = null;
	}
}