import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;

/**class: store all not environmental-related data*/
public class Constant
{
	/**constant: limit time for client connection*/
	public final static long TIME_LIMIT_FOR_CLIENT = 5*1000;
	/**constant: time for hall to refresh*/
	public final static long REFRESH_TIME_FOR_HALL = 500;
	/**constant: limit time for hall connection*/
	public final static int TIME_LIMIT_FOR_HALL = 20*1000;
	
	/**function: print log information*/
	public final static void log(final String log)
	{
		System.out.println(log);
	}
	/**variable: key for user to connect to the hall server*/
	public final static int KEY_LENGTH = 64;
	/**function:¡¡generate the key*/
	public final static String getKey()
	{
		String key = "";
		for (int i=0; i<KEY_LENGTH; i++)
			key = key + new Integer((int)(Math.random()*10 - 0.001)).toString();
		return key;
	}
	/**function: return hall server info*/
	static JSONObject hallServerInfo(String password)
	{
		JSONObject info = new JSONObject();
		info.put("password", password);
		info.put("status", "offline");
		return info;
	}	
}
