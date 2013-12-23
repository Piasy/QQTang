import java.util.*;
import java.math.*;

public class Common 
{
	public static String getLoginInfo(String user, String password, String target)
	{
		return String.format("{\"user\":\"%s\", \"password\":\"%s\", \"target\":\"%s\"}", user, password, target);
	}
	public static String getHeartbeat()
	{
		return "{\"type\":\"heartbeat\"}";
	}
	public static String getRandomCommend()
	{
		int type = (int)(Math.random() * 1000) % 5;
		if (type == 0)
		{
			int x = (int)(Math.random() * 1000) % 5;
			int y = (int)(Math.random() * 1000) % 4;
			return String.format("{\"type\":\"enter\", \"room\":%d, \"pos\":%d}", x, y);
		}
		if (type == 1)
		{
			return "{\"type\":\"enter\", \"room\":999, \"pos\":999}";
		}
		if (type == 2)
		{
			return "{\"type\":\"ready\"}";
		}
		if (type == 3)
		{
			return "{\"type\":\"unready\"}";
		}
		return "{\"type\":\"leave\"}";
	}
	
	public static String getEnterCommend()
	{
		return "{\"type\":\"enter\", \"room\":999, \"pos\":999}";		
	}
}
