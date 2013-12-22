import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;

/**class: create two different login server*/
public abstract class Talk implements Runnable
{
	protected static JSONObject hallServerConnection = Config.getHallServer();
	protected static JSONObject clientConnection = new JSONObject();
	
	protected Socket socket;
	protected Thread thread;
	protected BufferedReader is;
	protected PrintWriter os;
	/**variable: use to record connecting time*/
	protected Timer timer = new Timer();
	/**variable: a flag to chack if the thread is running or not*/
	protected boolean runFlag = true;

	/**function: use to create new threads*/
	public static void add(String type, Socket socket)
	{
		Talk talk = null;
		if (type.equals("client")) talk = new ClientTalk();
		if (type.equals("hallServer")) talk = new HallTalk();
		if (talk != null) talk.start(socket);
	}
	
	/**function: start the thread*/
	public void start(Socket _socket)
	{
		try
		{
			socket = _socket;
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
			runFlag = true;
			thread = new Thread(this);
			thread.start();
		}
		catch (Exception e) {}
	}
	
	public abstract void run();
	
	/**class: timer task*/
	protected class TalkTask extends TimerTask
	{
		public void run()
		{
			task();
		}
	}
	
	abstract void task();
	
	/**function: disconnect*/
	protected void stop()
	{
		timer.cancel();
		runFlag = false;
	}
}