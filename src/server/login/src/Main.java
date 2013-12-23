import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;

/**class: main*/
public class Main 
{
	/**function: entry*/
	public static void main(String args[])
	{
		Constant.log("Hello world! I'm Login Server!");
		Gate gateHallServer = new Gate();
		gateHallServer.start("hallServer", Config.FOR_HALLSERVER_NAME, Config.FOR_HALLSERVER_PORT);
		Gate gateClient = new Gate();
		gateClient.start("client", Config.FOR_CLIENT_NAME, Config.FOR_CLIENT_PORT);
	}
}