import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;

/** class: communicate with client*/
public class ClientTalk extends Talk 
{
	/**function: receive user login data from clients*/
	public void run() 
	{
		try
		{
			timer.schedule(new TalkTask(), Constant.TIME_LIMIT_FOR_CLIENT);
			String text = is.readLine();
			if (!runFlag) return;
			
			System.out.println("Receive from Client: " + text);
			JSONObject data = JSONObject.fromObject(text);
			User user = User.getUser(data);
			
			if (user.reason != null)
			{
				retBadResult(user.reason);
				stop();
			}
			if (!runFlag) return;
			
			String target = data.getString("target");
			String reason = null;
			String ip = null; int port = 0;
			synchronized (hallServerConnection)
			{
				if (hallServerConnection.has(target) && !hallServerConnection.getJSONObject(target).getString("status").equals("offline"))
				{
					JSONObject connection = hallServerConnection.getJSONObject(target);
					if (connection.getInt("limit") > 0)
					{
						JSONArray events = connection.getJSONArray("events");
						ip = connection.getString("ip");
						port = connection.getInt("port");
						events.add(user.toJSON());
					}
					else
						reason = "Full";
				}
				else reason = "No Hall";
			}
			if (reason != null)
			{
				retBadResult(reason);
			}
			else
			{
				User.login(user.user, target);
				System.out.println(target);
				Thread.sleep(1000);
				retSuccessResult(user.toJSON(), ip, port);
			}
		}
		catch (Exception e) {}
		stop();
	}

	/**function: return success result*/
	void retSuccessResult(JSONObject result, String ip, int port)
	{
		result.put("ip", ip);
		result.put("port", port);
		System.out.println("Send to Client: " + result);
		os.println(result);
		os.flush();		
	}	

	/** return failure result */
	void retBadResult(String reason) throws Exception
	{
		JSONObject result = new JSONObject();
		result.put("reason", reason);
		System.out.println("Send to Client: " + result);
		os.println(result);
		os.flush();
	}
	
	void task() 
	{
		stop();
	}
}
