import java.util.*;
import net.sf.json.*;

/**.
 * This class is used to generate some JSONObject which
 * is used to communicate with clients and the hall server
 * @author mcgrady
 *
 */
public class JSONObjectMaker{

	Game game;

	JSONObjectMaker(Game game) {
		this.game = game;
	}


	/**. This function is used to generate the
	 *  result for hall */
	JSONObject makeResultForHall() {

		JSONObject obj = new JSONObject();
		obj.put("type", "finished");
		obj.put("id", game.id);
		JSONArray arr = new JSONArray();

		JSONObject obj2;
		for (int i = 0; i < game.userNumbers; i++) {
			obj2 = new JSONObject();
			obj2.put("user", game.users.get(i).name);
			if (game.users.get(i).isOnline) {
				if (game.users.get(i).status == 0) {
					obj2.put("result", "win");
				} else {
					obj2.put("result", "lose");
				}
			} else {
				obj2.put("result", "break");
			}

			arr.add(obj2);
		}

		obj.put("result", arr);
		return obj;

	}

	/**. This function is used to generate
	 *  the result for clients */
	JSONObject makeResult() {

		JSONObject obj = new JSONObject();
		obj.put("type", "result");
		for (int i = 0; i < game.userNumbers; i++) {
			if (game.users.get(i).isOnline) {
				if (game.users.get(i).status == 0) {
					obj.put(game.users.get(i).name, "win");
				} else {
					obj.put(game.users.get(i).name, "lose");
				}
			} else {
				obj.put(game.users.get(i).name, "break");
			}
		}
		return obj;
	}

	/**. This function is used to generate the
	 *  map information in the concurrent information*/
	JSONArray makeMapInfo() {
		JSONArray arr1 = new JSONArray();
		JSONArray arr2;
		JSONObject obj;
		for (int i = 0; i < Config.MAP_WIDTH; i++) {
			arr2 = new JSONArray();
			for (int j = 0; j < Config.MAP_LENGTH; j++) {
				obj = new JSONObject();
				obj.put("status", game.gameMap.maps[i][j]);
				arr2.add(obj);
			}
			arr1.add(arr2);
		}
		return arr1;
	}

	//////////////////////////////////////////////////////////---week10
	JSONArray makePropInfo() {
		JSONArray arr1 = new JSONArray();
		JSONArray arr2;
		JSONObject obj;
		for (int i = 0; i < Config.MAP_WIDTH; i ++) {
			arr2 = new JSONArray();
			for (int j = 0; j < Config.MAP_LENGTH; j ++) {
				obj = new JSONObject();
				obj.put("status", game.gameMap.prop[i][j]);
				arr2.add(obj);
			}
			arr1.add(arr2);
		}
		return arr1;
	}

	//////////////////////////////////////////////////////////
	
	/**. This function is used to generate the player
	 *  information in the concurrent information*/
	JSONObject makePlayerInfo() {
		JSONObject player = new JSONObject();

		JSONObject obj;

		for (int i = 0; i < game.userNumbers; i++) {

			obj = new JSONObject();
			if (game.users.get(i).isOnline) {
				obj.put("isOline", "true");
			} else {
				obj.put("isOline", "false");
			}
			obj.put("model", game.users.get(i).model);


			obj.put("x", game.users.get(i).pos.getXX());
			obj.put("y", game.users.get(i).pos.getYY());

			int status = -1;

			User aUser = game.users.get(i);
			if ( aUser.status > 30) {
				status = aUser.status;
			} else {
				if (aUser.direction == 5) {
					status = 0;
				}
				if (aUser.direction == 1) {
					status = 1;
				}
				if (aUser.direction == 8) {
					status = 2;
				}
				if (aUser.direction == 4) {
					status = 3;
				}
				if (aUser.direction == 6) {
					status = 4;
				}
				if (aUser.direction == 2) {
					status = 5;
				}
				if (aUser.direction == 7) {
					status = 6;
				}
				if (aUser.direction == 3) {
					status = 7;
				}
			}

			obj.put("status", status);


			obj.put("speed", (double) game.users.get(i).speed
					* Config.DELTA_SPEED);

			obj.put("remain", game.users.get(i).bubbleRemains);

			player.put(game.users.get(i).name, obj);
		}

		return player;
	}


	/**. This function is used to generate
	 *  the bubble information in the concurrent information*/
	JSONArray makeBombInfo() {
		JSONArray arr = new JSONArray();
		JSONObject obj;
		JSONObject obj2;
		for (int i = 0; i < game.bubbles.size(); i++) {
			obj = new JSONObject();
			obj.put("x", game.bubbles.get(i).getPos().getX());
			obj.put("y", game.bubbles.get(i).getPos().getY());
			obj.put("model", 0);

			int ttt = game.bubbles.get(i).getStatus();
			if (ttt < Config.BUBBLE_BLAST) {
				ttt = 0;
			}	else {
				ttt = ttt - Config.BUBBLE_BLAST;
			}

			obj.put("status", ttt);
			//obj.put("status", game.bubbles.get(i).getStatus());

			obj.put("power", game.bubbles.get(i).getPower());
			obj2 = new JSONObject();

			obj2.put("l", game.bubbles.get(i).getLeft());
			obj2.put("r", game.bubbles.get(i).getRight());
			obj2.put("u", game.bubbles.get(i).getUp());
			obj2.put("d", game.bubbles.get(i).getDown());

			obj.put("range", obj2);
			//System.out.println(obj2.toString());
			arr.add(obj);
		}
		return arr;
	}

	/**. This function is used to generate the concurrent
	 * information for the clients.   */

	JSONObject makeConcurrentInfoForClient() {
		JSONObject obj = new JSONObject();
		obj.put("type", "info");
		obj.put("map", makeMapInfo());
		obj.put("users", makePlayerInfo());

		//System.out.println(obj.get("users").toString());

		obj.put("bubbles", makeBombInfo());
		System.out.println(obj.get("bubbles").toString());
		obj.put("prop",makePropInfo());
		//System.out.println(obj.get("prop").toString());
		return obj;
	}

}
