import java.io.*;
import java.util.*;
import net.sf.json.*;
	
public class SimpleGameController extends GameController{

	int count;

	UserPos[] startPos = new UserPos[10];


	SimpleGameController(Game game) {
		super(game);
		count = 0;
		startPos[0] = new UserPos(1, 1);
		startPos[1] = new UserPos(Config.MAP_LENGTH - 2, 1);
		startPos[2] = new UserPos(1, Config.MAP_WIDTH - 2);
		startPos[3] = new UserPos(
			Config.MAP_LENGTH - 2, Config.MAP_WIDTH - 2);
	}

	@Override
	public boolean checkGameOver() {
		++count;
		//System.out.println(game.onlineUsers);
		if (game.onlineUsers <= 0) {
			return true;
		}


		if (count > 3330) {
			return true;
		}

		int alive = 0;
		for (int i = 0; i < game.userNumbers; i++) {
			if (game.users.get(i).isOnline) {
				if (game.users.get(i).status < 100) {
					alive++;
				}
			}
		}

		if (alive <= 1) {
			return true;
		}


		return false;
	}

	@Override
	public JSONObject generateResult(){

		JSONObject obj = game.jsonMaker.makeResult();
		return obj;
	}


	@Override
	public JSONObject generateResultForHall(){
		JSONObject obj = game.jsonMaker.makeResultForHall();
		return obj;

	}




	@Override
	public void dealInfo(ReceivedInfo info) {

		//System.out.println(game.controller);
		//System.out.println(this);

		String type;
		JSONObject obj = info.obj;
		type = obj.getString("type");

		if (type.equals("init")) {
			dealInit(info);
		} else if (type.equals("refresh")) {
			dealRefresh(info);
		} else if (type.equals("move")) {
			dealMove(info);
		} else if (type.equals("deploy")) {
			dealDeploy(info);
		} else if (type.equals("prop")) {
			dealProp(info);
		}
	}

	private void dealInit(ReceivedInfo info) {

		for (int i = 0; i < game.userNumbers; i++) {
			if (game.users.get(i).isOnline == true) {
				game.users.get(i).status = 0;
				game.users.get(i).lastRefreshTime = info.time;
				game.users.get(i).pos
				= new UserPos(startPos[i]);
			}
		}

	}

	private void dealRefresh(ReceivedInfo info) {

		for (int i = 0; i < game.userNumbers; i++) {
			if (game.users.get(i).isOnline == true) {
				game.gameMap.updateUser(
				game, game.users.get(i), info.time);
			}
		}
/*
		for (int i = 0 ; i < game.bubbles.size(); i ++){
			game.gameMap.updateBubble( game.bubbles.get(i) , info.time);
		}
*/

		//System.out.println("update bubbles");
		game.gameMap.updateBubbles(game.bubbles, info.time);
		//System.out.println("end update bubbles");


		for (int i = 0; i < game.bubbles.size(); i++) {
			if (game.bubbles.get(i).getStatus()
					>= Config.BUBBLE_ED) {
				game.users.get(game.bubbles.get(i).getBelongsTo()).bubbleRemains++;
				game.bubbles.remove(i);
			}
		}




		//System.out.println("update game map");


		for (int i = 0; i < game.gameMap.width; i++) {
			for (int j = 0; j < game.gameMap.length; j++) {
				if (game.gameMap.isBox(i, j)) {
					game.gameMap.updateBox(
				i, j, info.time - game.gameMap.lastRefreshTime);
				}
			}
		}

		game.gameMap.lastRefreshTime = info.time;

	}

	private void dealDeploy(ReceivedInfo info) {

		Bubble aBubble = new Bubble();

		//aBubble.belongsTo = info.id;
		
		aBubble.setBelongsTo(info.id);

		//aBubble.power = game.users.get(info.id).power;
		
		int pp = game.users.get(info.id).power;
		aBubble.setPower(pp);

		//aBubble.lastRefreshTime = info.time;
		
		aBubble.setLastRefreshTime(info.time);

		game.users.get(info.id).bubbleRemains--;

	//	System.out.println(info.obj.toString());

		System.out.println(info.obj.getJSONObject("pos"));
		//aBubble.pos.x = info.obj.getJSONObject("pos").getInt("x");
		//aBubble.pos.y = info.obj.getJSONObject("pos").getInt("y");

		int id = info.id;

		//aBubble.pos.x = (int)game.users.get(id).pos.x;
		//aBubble.pos.y = (int)game.users.get(id).pos.y;

		int temp1 = (int) (game.users.get(id).pos.getXX() + 0.5);
		int temp2 = (int) (game.users.get(id).pos.getYY() + 0.5);

		game.users.get(id).bubblex = temp1;
		game.users.get(id).bubbley = temp2;

		aBubble.getPos().setX(temp1);
		aBubble.getPos().setY(temp2);
		game.bubbles.add(aBubble);

	}

////////////////////////////////////////////////////////////////////////////////////
	private void dealProp(ReceivedInfo info) {

		String str = info.obj.getString("category");
		int id = info.id;
		dealRefresh(info);

		int xx = info.obj.getJSONObject("pos").getInt("x");
		int yy = info.obj.getJSONObject("pos").getInt("y");

		if (str.equals("speed")) {
			if ( game.gameMap.prop[yy][xx] == 5) { 
				game.users.get(id).speed++;
				game.gameMap.prop[yy][xx] = 0;
			}
		}
		else if (str.equals("power")) {
			if ( game.gameMap.prop[yy][xx] == 6) { 
				game.users.get(id).power++;
				game.gameMap.prop[yy][xx] = 0;
			}

		}
		else if (str.equals("number")) {

			if ( game.gameMap.prop[yy][xx] == 7) { 
				game.users.get(id).maxBubbleNum++;
				game.gameMap.prop[yy][xx] = 0;
			}
		}
		else if (str.equals("dodge")) {

			if ( game.gameMap.prop[yy][xx] == 8) { 
				game.users.get(id).dodgeRate++;
				game.gameMap.prop[yy][xx] = 0;
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////

	private void dealMove(ReceivedInfo info) {

		//System.out.println("DSDFDSF");
		dealRefresh(info);
		///System.out.println("DSDFDSF");
		String str = info.obj.getString("direction");
		int id = info.id;


			if (str.equals("up")) {
				game.users.get(id).direction = 1;
			} else if (str.equals("down")) {
				game.users.get(id).direction = 2;
			} else if (str.equals("left")) {
				game.users.get(id).direction = 3;
			} else if (str.equals("right")) {
				game.users.get(id).direction = 4;
			} else if (str.equals("stop")) {	
				if (game.users.get(id).direction <= 4) {
					game.users.get(id).direction += 4;
				}
			}
	/////////////////////////////////////////////////// ---week10
		ReceivedInfo tempInfo = new ReceivedInfo(info.time + 10, new JSONObject());////////0.01s
		dealRefresh(tempInfo);
	///////////////////////////////////////////////////
	
		//System.out.println("DSDFDSF");
	}
}


