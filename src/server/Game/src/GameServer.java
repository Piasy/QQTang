import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**.
 * This class is the game server whose function is
 * to communicate with the hall, receive request from
 * the hall server.
 * @author mcgrady
 *
 */
public class GameServer implements Runnable{


	ArrayList<Game> games;

	Thread thread = null;

	int hallServerPort;

	boolean[] availablePort = new boolean[30000];

	String hallServerIP; 
	String key;

	PrintWriter os;
	BufferedReader is;


	Socket socketWithHallServer; 



	/**.
	 * To start a new thread to run a new game server
	 * @param hallIp The IP of the hall server
	 * @param hallPort The Port of the hall server
	 * @param key The Key used to verify identity with the hall server
	 */
	public void start(String hallIp, int hallPort, String key) {

		for (int i = 0; i < 30000; i++) {
			availablePort[i] = true;
		}
		games = new ArrayList<Game>();

		if (thread == null) {
			hallServerIP = hallIp;
			hallServerPort = hallPort;
			this.key = key;
			thread = new Thread(this);
			thread.start();
		}
	}

	/**.
	 * Try to establish a connection with the hall server
	 */
	public void connectHallserver() {
		try {
			socketWithHallServer = new Socket(
					hallServerIP, hallServerPort);
			os = new PrintWriter(
					socketWithHallServer.getOutputStream());
			is = new BufferedReader(
					new InputStreamReader(
					socketWithHallServer.getInputStream()));
			JSONObject obj = new JSONObject();
			obj.put("key", key);
			String str = obj.toString();
			os.println(str);
			os.flush();
			System.out.println(str);

		} catch (Exception e) {
			System.out.println("In connectHallserver Error" + e);
		}
	}

	/**.
	 * To receive request from the hall server
	 * @author mcgrady
	 *
	 */
	class ReceiveFromHall extends Thread {

		GameServer gameServer;
		ReceiveFromHall(GameServer g) {
			gameServer = g;
		}

		public void run() {
			try{

				System.out.println(gameServer);
				while (true) {
					String str = is.readLine();
					System.out.println(str);
					Game game = new Game(gameServer, str);
					games.add(game);
					game.start();

				}
			} catch (Exception e) {
				System.out.println(
					"In ReceiveFromHall Error : " + e);
			}
		}

	}

	class SendToHall extends Thread {
		String str;

		SendToHall(String str) {
			this.str = str;
		}

		public void run() {
			try {
				os.println(str);
				os.flush();

			} catch (Exception e) {
				System.out.println(
					"In SendTo Hall Error : " + e);
			}
		}
	}


	/**. The body of the thread. In this function,
	 * the server will first connect the
	 * HallServer and then receive users
	 * information from the connected hall server */
	public void run() {

		System.out.println("GameServer Start ......... ");
		System.out.println(this);
		connectHallserver();
		System.out.println("Connect successed......");
		ReceiveFromHall receiveFromHallThread =
				new ReceiveFromHall(this);
		receiveFromHallThread.start();
	}


	/**. Send some information to hall server*/
	public void send(String str) {

		SendToHall sendToHall = new SendToHall(str);
		sendToHall.start();
	}



	/**. Get an available port and set up a socket,
	 *  which is used for communication for a
	 * client*/
	ServerSocket getAvailableSocket() {

		int port = 0;
		ServerSocket sock = null;


		for (int i = 8010; i < 10000; i++) {
			//if (availablePort[i] == true){
			//availablePort[i] = false;
			//port = i ;
			//break;
			//	}
			try {
				sock = new ServerSocket(i);

				return sock;
			} catch (Exception e) {
				//System.out.println("Error: " + e);
			}
		}
		return sock;
	}

}

