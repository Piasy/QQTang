import java.util.*;
import java.io.*;
import java.net.*;

/**class: login server for hall and client*/
public class Gate implements Runnable
{
	Thread thread = null;
	/**variable: port number*/
	int port;
	/**variable: server name*/
	String name;
	/**variable: server type*/
	String type;

	/**function: start a new login server*/
	public void start(String _type, String _name, int _port)
	{
		if (thread == null)
		{
			type = _type;
			name = _name;
			port = _port;
			Constant.log(String.format("ServerSocket for %s start! name: %s", type, name));
			thread = new Thread(this, name);
			thread.start();
		}
	}
	
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(port);
			while (true)
			{
				try
				{
					Socket now = serverSocket.accept();
					Talk.add(type, now);
				}
				catch (Exception e)
				{
					Constant.log(String.format("ServerSocket for %s fail! name: %s. exception: %s", type, name, e));
				}				
			}
		}
		catch (Exception e) {}
	}
}
