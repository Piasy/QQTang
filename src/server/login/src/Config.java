import java.util.*;

import net.sf.json.JSONObject;

/**class: store all environmental related data*/
public class Config
{
	/**constant: database info*/
	public final static String dbName = "jdbc:mysql://166.111.134.210:3306/qqtang";
	public final static String dbUser = "root";
	public final static String dbPassword = "qianqiao";
	
	/**constant: client port number*/
	public final static int FOR_CLIENT_PORT = 10001;
	public final static String FOR_CLIENT_NAME = "Login-for-Client";
	
	/**constant: hall server port number*/
	public final static int FOR_HALLSERVER_PORT = 10002;
	public final static String FOR_HALLSERVER_NAME = "Login-for-HallServer";

	/**function: return hall server*/
	public static JSONObject getHallServer()
	{
		JSONObject hallServer = new JSONObject();
		hallServer.put("hall-server1", Constant.hallServerInfo("smalltwos"));
		hallServer.put("hall-server2", Constant.hallServerInfo("bigtwos"));
		return hallServer;
	}	
	
}
