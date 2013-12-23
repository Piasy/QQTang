import java.util.*;
import java.io.*;
	

/**.
 * The entry of the program, a new instance
 * of game server will be created.
 * @author mcgrady
 *
 */
public class Main{

	public static void main(String args[]) {

		System.out.println("Hello world! I'm Game Server!");
		GameServer gameServer = new GameServer();
		gameServer.start(
		Config.HALL_SERVER_IP, Config.HALL_SERVER_PORT, Config.KEY);

	}
}

