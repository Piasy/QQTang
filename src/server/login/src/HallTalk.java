import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;

/**class: communicate with Hall Server*/
public class HallTalk extends Talk
{
	JSONObject connection = null;
	
	/**function: receive user data from Hall Server*/
	public void run() 
	{
		try
		{
			String text = is.readLine();
			System.out.println("Receive from HallServer: " + text);
			JSONObject data = JSONObject.fromObject(text);
			String user = data.getString("user");
			String password = data.getString("password");
			synchronized (hallServerConnection)
			{
				if (hallServerConnection.has(user))
				{
					connection = hallServerConnection.getJSONObject(user);
					if (connection.getString("password").equals(password) && connection.getString("status").equals("offline"))
					{
						Constant.log(String.format("HallServer Connected  name: %s", user));
						connection.put("user", user);
						connection.put("status", "online");
						connection.put("timestamp", System.currentTimeMillis());
						connection.put("events", new JSONArray());
						connection.put("limit", 0);
						connection.put("ip", data.getString("ip"));
						connection.put("port", data.getInt("port"));
					}
				}
				else
					throw new Exception();
			}
		}
		catch (Exception e) 
		{
			Constant.log("HallServer's connection fail " + e);
			stop();
		}
		if (!runFlag) return;

		timer.schedule(new TalkTask(), 0, Constant.REFRESH_TIME_FOR_HALL);
		
		int errCount = 0;
		while (runFlag)
		{
			try
			{
				String text = is.readLine();
				if (!runFlag) return;
				System.out.println("Receive from HallServer: " + text);
				JSONObject data = JSONObject.fromObject(text);
				JSONArray userData = data.getJSONArray("userData");
				for (int i=0; i<userData.size(); i++)
					User.execute(connection.getString("user"), JSONObject.fromObject(userData.get(i)));
				synchronized (connection)
				{
					connection.put("limit", data.getInt("limit"));
					connection.put("timestamp", System.currentTimeMillis());
				}
			}
			catch (Exception e)
			{
				Constant.log("the thread of HallServer is failed! exception: " + e);
				if (errCount++ > 20) break;
			}
		}
		stop();
	}

	/**function: send user data to Hall Server*/
	void task()
	{
		try
		{
			synchronized (connection)
			{
				JSONArray events = connection.getJSONArray("events");
				for (int i=0; i<events.size(); i++)
				{
					String s = JSONObject.fromObject(events.get(i)).toString();
					os.println(s);
					System.out.println("Send to HallServer: " + s);
				}
				os.flush();
				connection.put("events", new JSONArray());
				if (connection.getLong("timestamp") + Constant.TIME_LIMIT_FOR_HALL < System.currentTimeMillis()) stop();
			}
		}
		catch (Exception e) 
		{
			Constant.log("Task error: " + e);
		}
	}
	
	/**function: disconnect the Hall Server*/
	protected void stop()
	{
		User.logout(connection.getString("user"));
		connection.put("status", "offline");
		runFlag = false;
		timer.cancel();
	}
}
