import java.io.*;
import java.util.*;
import net.sf.json.*;

/**.
 * This is abstract class used as the base class 
 * for different game controller. The function of
 * a game controller is used to handle the received
 * information from the clients.
 * @author mcgrady
 *
 */
public abstract class GameController {

	Game game;

	GameController(Game game) {
		this.game  = game;
	}

	/**.
	 * To handle the received information
	 * @param info The received information
	 */
	public abstract void dealInfo(ReceivedInfo info);

	/**.
	 * To check whether the game is over
	 * @return true when the game is over else false
	 */
	public abstract boolean checkGameOver();

	/**.
	 * Make the result of a certain game
	 * @return a JSONObject stands for the result of a game
	 */
	public abstract JSONObject generateResult();
	public abstract JSONObject generateResultForHall();


	/**.
	 * Close the socket when a game is over.
	 */
	public void takeCareOfGame() {

		for (int i = 0; i < game.userNumbers; i++) {
		if (game.users.get(i).isOnline) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("type", "null");
				game.infos.put(
				new ReceivedInfo(new Date().getTime(), obj));
				game.users.get(i).is.close();
				game.users.get(i).os.close();
				game.users.get(i).socket.close();
				game.users.get(i).sock.close();
			} catch (Exception e) {
				System.out.println(
			"");
			}
		}
		}
	}

}



