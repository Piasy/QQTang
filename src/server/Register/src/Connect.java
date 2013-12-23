import java.util.*;
import java.io.*;
import java.net.*;

import net.sf.json.*;

/**class: use for connecting*/
public class Connect implements Runnable{
	//private Socket socket;
	private Thread thread;
	private BufferedReader is;
	private PrintWriter os;
	/**variable: use for record time*/
	private Timer timer = new Timer();
	/**variable: a flag to check if the thread is running*/
	private boolean runFlag = true;
	/**constant:  connecting time limit*/
	private final static int LIMIT_TIME_FOR_CONNECTION = 15 * 1000;
	
	/**function: add a new thread*/
	public static void add(Socket socket){
		Connect connect = new Connect();
		System.out.println("connect from client");
		connect.start(socket);
	}
	
	/**function: start the thread*/
	public void start(Socket socket){
		try{
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
			runFlag = true;
			thread = new Thread(this);
			thread.start();
		}catch(Exception e){
			System.out.println((String.format("Socket Start Error: %s", e)));
		}
	}
	/**function: disconnect*/
	private void stop(){
		timer.cancel();
		runFlag = false;
	}
	
	/**class: timer task*/
	private class Task extends TimerTask{
		public void run(){
			stop();
		}
	}
	
	public void run(){
		try{
			//record the connecting time
			timer.schedule(new Task(), LIMIT_TIME_FOR_CONNECTION);
			if (!runFlag) return;
			String text = is.readLine();
			System.out.println("receive from client: " + text);
			JSONObject data = JSONObject.fromObject(text);
			Register register = new Register();
			JSONObject obj = register.checkUser(data);
			System.out.println(obj);
			os.println(obj);
			os.flush();
			stop();
		}catch(Exception e){
			System.out.println((String.format("Connection Error: %s", e)));
		}
	}
}
