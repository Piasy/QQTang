import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.*;
import javax.xml.crypto.Data;

/**.
 * The class game extending Thread is used to handle all the 
 * events happens in a single game. For each game, a new instance
 * of the class Game will be created.
 * @author mcgrady
 *
 */
public class Game extends Thread{

	GameServer gameServer;

	int id; /// Game id

	String receivedInfo;

	BlockingQueue<ReceivedInfo> infos;

	private byte[] lock;
	GameController controller;
	ArrayList<User> users;
	ArrayList<Bubble> bubbles;

	Map gameMap;

	/*
	int getGameId(){
		return id;
	}

	GameServer getGameServer()
	{
		return gameServer;
	}

	*/



	boolean gameStartFlag;

	Timer timerForCompute;
	Timer timerForSend;

	int connectedNum;
	int onlineUsers;
	int userNumbers;


	JSONObjectMaker jsonMaker;


	Game(GameServer g, String obj) {

		gameServer = g;
		connectedNum = 0;
		lock = new byte[0];
		this.receivedInfo = obj;
		infos  = new LinkedBlockingQueue<ReceivedInfo>(10000);
		gameStartFlag = false;
		gameMap = new Map();
		onlineUsers = Config.CLIENTS_NUM;

		jsonMaker = new JSONObjectMaker(this);
	}



	/**.
	 * This function is used to initialize the
	 * game status which includes the status of
	 * the Users, Map.
	 */
	void initialize() {

		users = new ArrayList<User>();
		// To store all the infomation of the users
		bubbles = new ArrayList<Bubble>();

		JSONObject obj = JSONObject.fromObject(receivedInfo);

		///////////////////////////////////////////////////////////////////////////
		gameMap.loadMap(1);
		controller = new SimpleGameController(this);
		///////////////////////////////////////////////////////////////////////////
		//
		id = Integer.parseInt(obj.getString("id"));

		System.out.println("Game Id: " + id);

		JSONArray objArray = obj.getJSONArray("users");

		JSONObject objTemp;

		User aUser;

		userNumbers = objArray.size();
		onlineUsers = userNumbers;

		for (int i = 0; i < userNumbers; i++) {

			aUser = new User();

			objTemp = objArray.getJSONObject(i);
			aUser.name = objTemp.getString("user");
			aUser.id = i;
			aUser.key =  objTemp.getString("key");
			aUser.model = i;

			aUser.pos = new UserPos(
				Config.MAP_LENGTH, Config.MAP_WIDTH);

			System.out.println(aUser.pos.getXX());


			objTemp = objTemp.getJSONObject("details");

			aUser.speed = objTemp.getInt("velocity");
			aUser.maxBubbleNum = objTemp.getInt("number") + 3;
			aUser.power = objTemp.getInt("power") + 1 ;
			aUser.bubbleRemains = aUser.maxBubbleNum;
			aUser.dodgeRate = objTemp.getInt("dodge");

			users.add(aUser);
		}

		for (int i = 0; i < userNumbers; i++) {
			users.get(i).print();
		}



		System.out.println("finish initialization ... ");
	}


	/**.
	 * After receiving a request from the hall which
	 * ask for a new game, the game server will send 
	 * some information of the users to the hall server
	 * including the Ip and Port which the users should
	 * connect*/

	void sendInfoToHall() {

		JSONObject info = new JSONObject();
		info.put("type", "start");
		info.put("id", id);

		JSONArray clients = new JSONArray();

		for (int i = 0; i < userNumbers; i++) {
			ServerSocket sock = gameServer.getAvailableSocket();

			int port = sock.getLocalPort();
			System.out.println(sock);

			System.out.println("User: " + i + " " + port);

			JSONObject aClient = new JSONObject();

			aClient.put("user", users.get(i).name);
			aClient.put("ip", Config.GAME_SERVER_IP);
			aClient.put("port", port);

			users.get(i).sock = sock;
			users.get(i).port = port;

			clients.add(aClient);
		}

		info.put("users", clients);
		gameServer.os.println(info.toString());
		gameServer.os.flush();
	}

	/**.
	 * The main method of the class, in this method
	 * the game server will control the 
	 * execution order of the event , handle the 
	 * received informations, when this method finish,
	 * it means that a game is over
	 * */
	public void run() {

		System.out.println("A new game ...... ");
		initialize();
		sendInfoToHall();

		for (int i = 0; i < userNumbers; i++) {
			CommunicateWithClient waitClients
			= new CommunicateWithClient(users.get(i));
			waitClients.start();
		}

		try {
			infos.put(new ReceivedInfo(
					new Date().getTime(), makeInitInfo()));
		} catch (Exception e) {
			System.out.println("In Game Error : " + e);
		}

		while (true) {
			if (gameStartFlag == true) {
				break;
			}
			try {
				sleep(50);
			} catch (Exception e) {
				System.out.println(
				"During waiting game start flag Error: " + e);
			}
		}

		ComputeInfo computeInfo = new ComputeInfo(controller);
		computeInfo.start();

		timerForCompute = new Timer();
		timerForCompute.schedule(new SendInfoToClients(), 1000, 100);

		timerForSend = new Timer();
		timerForSend.schedule(new Refresh(), 1000, 50);

		try {
			while (true) {
				if (controller.checkGameOver()) {
					timerForSend.cancel();
					timerForCompute.cancel();
					break;
				}
				sleep(100);
			}

		} catch (Exception e) {
			System.out.println("In juding game state Error: " + e);
		}

		ComputeInfo.interrupted();

		String res = controller.generateResult().toString();

		String res2 = controller.generateResultForHall().toString();

		gameServer.send(res2);

		for (int i = 0; i < userNumbers; i++) {
			if (users.get(i).isOnline) {
				users.get(i).os.println(res);
				System.out.println(res);
				users.get(i).os.flush();
			}
		}

		try {
			sleep(1000);
		} catch (Exception e) {
			System.out.println(
					"Send result to Clients Error: " + e);
		}
		controller.takeCareOfGame();
		System.out.println("Game over......");
	}
    /**.
     * To make a JSONObject of the initial information, this one
     * is used to reminder the game sever it is the first information
     * @return a JSONObject 
     */
	JSONObject makeInitInfo() {

		JSONObject obj = new JSONObject();
		obj.put("type", "init");
		return obj;
	}

	/**.
	 * For each user, a new thread will be created to communicate 
	 * with the users.
	 * */
	class CommunicateWithClient extends Thread {

		User user;
		BufferedReader is;
		PrintWriter os;

		ServerSocket server = null;
		Socket socket = null;

		CommunicateWithClient(User user) {
			this.user = user;
		}

		/**.
		 * Send some information to the user
		 * @param str The information try to send
		 */
		public void sendInfo(String str) {
			os.println(str);
			os.flush();
		}

		/**.
		 * The main method of the class, the first task of the 
		 * method is to establish a new connection with the user
		 * and then the method will receive and send messages to
		 * clients
		 */
		public void run() {
			System.out.println("Thread for user: "
					+ user.name + "	start!");
			try {
				try {
					server = user.sock;

				} catch (Exception e) {
					System.out.println(
					"In CommunicateWithClient Error: " + e);
				}

				try {
					socket = server.accept();
					System.out.println(socket);

					JSONObject obj = new JSONObject();
					obj.put("type" , "accept");

					is = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
					os = new PrintWriter(
				socket.getOutputStream());
					String str;

					user.socket = socket;
					user.is = is;
					user.os = os;

					str = obj.toString();
					os.println(str);
					os.flush();

					synchronized (lock) {
						connectedNum++;
					}

					obj = new JSONObject();
					obj.put("type", "start");
					obj.put("delay", 3000);

					while (connectedNum != userNumbers) {
						try {
							sleep(10);
						} catch (Exception e) {
							System.out.println(
					"In waiting clients Error: " + e);
						}
					}

					sendInfo(obj.toString());

					gameStartFlag = true;
					System.out.println("Game Start......");

					while (true) {
						str = is.readLine();
						if (!str.equals("")) {
							try {
								infos.put(new ReceivedInfo
								(new Date().getTime(),JSONObject.fromObject(str),user.id));

							} catch (Exception e) {
								System.out.println(
										"In adding elements Error : " + e);
							}
						}
					}

				} catch (Exception e) {
					onlineUsers--;
					user.isOnline = false;
					System.out.println(
							"Socket Error: " + e);
				}

			} catch (Exception e) {
				System.out.println("Sockets Error: " + e);
			}
		}
	}


	/**.
	 * This class is created to handle the ReceivedInfo specifically
	 * */
	class ComputeInfo extends Thread {

		GameController controller;
		ComputeInfo(GameController controller) {
			this.controller = controller;
		}


		private void dealInfo(ReceivedInfo aInfo) {
			controller.dealInfo(aInfo);
		}

		/**. This is the main method of the class, the method will keep on
		 * trying to take a ReceivedInfo for the queue and then call the
		 * GameController to handle the infomation.*/
		public void run() {
			ReceivedInfo aInfo;
			try {
				while (true) {

					aInfo = infos.take();
					if(! aInfo.obj.getString("type").equals("refresh")) {
						System.out.println(	 "ReceivedInfos: " + aInfo.obj.toString());
					}
					if (aInfo.obj.getString("type").equals("null")) {
						break;
					}
					dealInfo(aInfo);
				}
			} catch (Exception e) {
				System.out.println(
						"In compute info Error : " + e);
			}
		}
	}


	/**.
	 * This class implements a TimerTask, the method run() will
	 * be executed every 100ms to send the current game infomation
	 * to every users to ensure the consistency of the game*/
	class SendInfoToClients extends TimerTask{

		public void run() {

			//gameMap.print();
			//System.out.println();
			//
			JSONObject obj =
					jsonMaker.makeConcurrentInfoForClient();
			for (int i = 0; i < userNumbers; i++) {
				if (users.get(i).isOnline == true) {
					users.get(i).os.println(obj.toString());
					users.get(i).os.flush();
				}
			}
		}
	}

	/**.
	 * This class implements a TimerTask, the method run() will 
	 * be executed every 50ms to interpose some refresh infomation
	 * to the queue to simulate the users, which is used to 
	 * ensure the consistency of the game*/
	class Refresh extends TimerTask{

		public void run(){
			JSONObject obj = new JSONObject();
			obj.put("type", "refresh");
			try {
				infos.put(new
				ReceivedInfo(new Date().getTime(), obj));
			} catch (Exception e) {
				System.out.println("in Refresh Error :" + e);
			}
		}
	}
}


