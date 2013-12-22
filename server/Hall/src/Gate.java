import java.util.*;
import java.io.*;
import java.net.*;
	
/**.
 * 这个类负责监听端口，接受来自游戏服务器和客户端的连接，并创建对应的通信类
 */
public class Gate implements Runnable {
	/**.
	 * 监听端口的线程
	 */
	private Thread thread = null;
	/**.
	 * 需要监听的端口
	 */
	private int port;

	/**.
	 * 这个线程的名称。仅仅用于区分不同的线程，方便跟踪错误
	 */
	private String name;

	/**.
	 * 监听端口的种类
	 * game表示这个端口接受来自游戏服务器的连接
	 * client表示这个端口接受来自客户端的连接
	 */
	private String type;

	/**.
	 * 启动线程，监听端口
	 */
	public void start(String _type, String _name, int _port) {
		if (thread == null) {
			type = _type;
			name = _name;
			port = _port;
			Log.log(String.format("ServerSocket for %s start! name: %s", type, name));
			thread = new Thread(this, name);
			thread.start();
		}
	}

	/**. 
	 * 监听端口
	 */
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				try {
					Socket now = serverSocket.accept();
					Talk.add(type, now, null, null);
				} catch (Exception e) {
					Log.log(String.format("ServerSocket for %s fail! name: %s. exception: %s", type, name, e));
				}
			}
		} catch (Exception e) {
		}
	}
}
