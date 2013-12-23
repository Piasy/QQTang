import java.util.*;
import java.lang.*;

/**.
 * This class records all the information of 
 * the map of the game as well as providing some
 * method to update the status of the bubbles,
 * boxes, and users.
 * @author mcgrady
 *
 */
public class Map{

	public int length;
	public int width;
	long lastRefreshTime;

	public boolean[][] isBlast;
	public boolean[][] blastZone;

	int[][] maps;
	int[][] prop;
	
	int[][] bubbles;

	/////prop status
	// 0 no prop exists
	// 1-4 : speed power number dodge  hide
	// 5-8 : speed power number dodge  emerge



	Map() {

		length = Config.MAP_LENGTH;
		width = Config.MAP_WIDTH;
		maps = new int[width + 2][length + 2];
		//////////////////////////////////////////////////////////// ----week10
		prop = new int[width + 2][length + 2];
		bubbles = new int[width + 2][length + 2];
		//////////////////////////////////////////////////////////// ----week10
		isBlast = new boolean[width + 2][length + 2];
		blastZone = new boolean[width + 2][length + 2];
	}

	/**.
	 * To create a new map
	 * @param id the id of the map
	 */
	public void loadMap(int id) {

		for (int i = 0; i < length; i++) {

			if (i % 2 ==0) {
				for (int j = 0; j < width; j++){
					maps[j][i] = 0;
				}
			} else {
				for (int j = 0; j < width; j++){
					maps[j][i] = 20;
				}
			}
		}

		maps[8][8] = 30;
		maps[7][7] = 40;


		for (int i = 0; i < width; i++) {
			maps[i][length / 2] = 80;
			maps[width / 2][length / 2] = 0;
			maps[width / 2+1][length /2] = 100;
			maps[width / 2-1][length /2 ] = 100;
		}



		maps[3][4] = 100;
		maps[4][4] = 100;
		maps[7][9] = 100;
		maps[6][8] = 100;

		//////////////////////////////////////////////////////////// ----week10
		for(int i = 0; i < length; i ++){
			for (int j = 0; j < width; j ++){
				prop[j][i] = 0;
			}
		}

		prop[3][4] = 1;
		prop[4][4] = 2;
		prop[7][9] = 1;
		prop[6][8] = 2;
		//////////////////////////////////////////////////////////// ----week10
	
	}

	/**. Print the map information */
	public void print(){

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < length; j++) {
				if (maps[i][j] == 100) {
					System.out.print('*');
				} else if (maps[i][j] > 100) {
					System.out.print('&');
				} else {
					System.out.print('0');
				}
			}

			System.out.println();
		}
	}

	/**. To judge whether the
	 * cetain spot is a box*/
	public boolean isBox(int i, int j) {
		if ((maps[i][j] >= Config.BOX_ST)
				&& (maps[i][j] <= Config.BOX_ED)) {
			return true;
		}
		return false;
	}


	/**. This function is used to update
	 *  a certain box int map[i][j] and the parameter
	 * deltaTime refers to the time interval the update lasts*/

	public void updateBox(int i , int j , long deltaTime) {
		int frame = (int) (deltaTime) / 10;

		if (maps[i][j] > Config.BOX_ST) {
			if (maps[i][j] + frame <= Config.BOX_ED) {
				maps[i][j] += frame;
			} else {
				maps[i][j] = 0;
////////////////////////////////////////////////////////////////////// --week10
				if (prop[i][j] > 0 && prop[i][j] <= 4) {
					prop[i][j] += 4;
				}
/////////////////////////////////////////////////////////////////////
			}
		}
	}


	/**.This function is used to compute
	 *  the blasr range for a certion bubble*/

	public void computeBombRange(Bubble bubble) {

		int x0 = bubble.getPos().getX();
		int y0 = bubble.getPos().getY();
		int x, y;
		int count;

		y = y0 + 1;
		count = 0;
		while (true) {
			if (count >= bubble.getPower()) {
				break;
			}
			if (y >= width) {
				break;
			}

			if (maps[y][x0] > Config.WALKABLE_ED) {
				if (maps[y][x0] == Config.BOX_ST) {
					maps[y][x0]++;
				}
				break;
			}

			isBlast[y][x0] = true;

			++count;
			++y;
		}

		//bubble.up = count;
		bubble.setUp(count);

		y = y0 - 1;
		count = 0;
		while (true) {
			if (count >= bubble.getPower()) {
				break;
			}
			if (y < 0) {
				break;
			}
			if (maps[y][x0] >= Config.WALKABLE_ED) {
				if (maps[y][x0] == Config.BOX_ST) {
					maps[y][x0]++;
				}
				break;
			}

			isBlast[y][x0] = true;

			count++;
			y--;
		}
		//bubble.down = count;
		bubble.setDown(count);

		x = x0 + 1;
		count = 0;
		while (true) {
			if (count >= bubble.getPower()) {
				break;
			}
			if (x >= length) {
				break;
			}
			if (maps[y0][x] > Config.WALKABLE_ED) {
				if (maps[y0][x] == Config.BOX_ST) {
					maps[y0][x]++;
				}
				break;
			}

			isBlast[y0][x] = true;

			count++;
			x++;
		}
		//bubble.right = count;
		bubble.setRight(count);
		x = x0 - 1;
		count = 0;
		while (true) {
			if (count >= bubble.getPower()) {
				break;
			}
			if (x < 0) {
				break;
			}
			if (maps[y0][x] > Config.WALKABLE_ED) {
				if (maps[y0][x] == Config.BOX_ST) {
					maps[y0][x]++;
				}
				break;
			}

			isBlast[y0][x] = true;

			count++;
			x--;
		}
		//bubble.left = count;
		bubble.setLeft(count);
	}


	/**. This function is used to update the bubble
	 *  status. The parameter bubbles refers to the
	 * bubbles which needs update and the time refers
	 *  to the newest the timestamp
	 *  @param bubbles is the target bubbles
	 *  @param time is the refresh time*/

	public void updateBubbles(
			ArrayList<Bubble> bubbles, long time) {

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < length; j++) {
				isBlast[i][j] = false;
			}
		}

		int firstBomb = -1;
		int minDelta = 10000000;
		int temp;

		for (int i = 0; i < bubbles.size(); i++) {
			if (bubbles.get(i).getStatus() < Config.BUBBLE_BLAST) {
				temp = (int)(time - bubbles.get(i).getLastRefreshTime());

				//System.out.println(temp);
				//System.out.println(time - bubbles.get(i).lastRefreshTime);
				if (temp / 10  + bubbles.get(i).getStatus() >= Config.BUBBLE_BLAST) {

					//bubbles.get(i).status += temp/10 ;
					bubbles.get(i).setStatus(bubbles.get(i).getStatus() + temp / 10);
					computeBombRange(bubbles.get(i));
					if (temp < minDelta) {
						firstBomb = i;
						minDelta = temp;
					}
				}
			}
		}
		//System.out.println("during update bubbles 1");

		boolean flag = false;

		while (true) {

			Bubble aBubble;
			flag = false;
			for (int i = 0; i < bubbles.size(); i++) {
				aBubble = bubbles.get(i);
				if ((aBubble.getStatus() < Config.BUBBLE_BLAST)
						&&(isBlast[aBubble.getPos().getY()][aBubble.getPos().getX()])) {
					flag = true;
					//aBubble.status = Config.BUBBLE_BLAST;
					aBubble.setStatus(Config.BUBBLE_BLAST);
					computeBombRange(aBubble);
				}
			}
			if (!flag) {
				break;
			}

		}

		for (int i = 0; i < bubbles.size(); i++) {
			//bubbles.get(i).status += (int)(time - bubbles.get(i).lastRefreshTime)/10;
			//bubbles.get(i).lastRefreshTime = time;
			int temp2 = (int)(time - bubbles.get(i).getLastRefreshTime()) / 10;
			temp2 += bubbles.get(i).getStatus();
			bubbles.get(i).setStatus(temp2);
			bubbles.get(i).setLastRefreshTime(time);
		}

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < length; j++) {
				blastZone[i][j] = false;
			}
		}

		for (int i = 0; i < bubbles.size(); i++) {
			Bubble aBubble = bubbles.get(i);
			if (aBubble.getStatus() >= Config.BUBBLE_BLAST) {
				int x0 = aBubble.getPos().getX();
				int y0 = aBubble.getPos().getY();
				for (int j = 0; j <= aBubble.getUp();  j++) {
					blastZone[y0 + j][x0] = true;
				}
				for (int j = 0; j <= aBubble.getDown();  j++) {
					blastZone[y0 - j][x0] = true;
				}
				for (int j = 0; j <= aBubble.getLeft();  j++) {
					blastZone[y0][x0 - j] = true;
				}
				for (int j = 0; j <= aBubble.getRight();  j++) {
					blastZone[y0][x0 + j] = true;
				}
			}
		}


	/////////////////////////////////////////////////////////////////////////////////////
	for (int i = 0 ; i < width; i ++) {
		for (int j = 0; j < length ; j ++) {
			this.bubbles[i][j] = 0;
		}
	}

	for(int i = 0 ; i < bubbles.size(); i ++) {
		Bubble aBubble = bubbles.get(i);
		int x0 = aBubble.getPos().getX();
		int y0 = aBubble.getPos().getY();

		if (aBubble.getStatus() < Config.BUBBLE_BLAST) {
			this.bubbles[y0][x0] = 1;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////--end wee12
		/*
		for (int i = 0 ; i < width; i ++)
		{
			for (int j = 0; j < length ; j ++)
				System.out.print(blastZone[i][j]);
			System.out.println();
		}
		*/

	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**.
	 * To judge whether a client can walk in the certain position
	 * @param x the x coordinate of the user
	 * @param y the y coordinate of the user
	 * @return true when can walk else false
	 */
	public boolean canWalk(double x ,double y) {

		if (x <0 || y < 0) {
			return false;
		}

		int xFloor = (int)Math.floor(x); 
		int xCeil = (int)Math.ceil(x);
		int yFloor = (int)Math.floor(y); 
		int yCeil = (int)Math.ceil(y);

		double deltax1 = x - (double)(xFloor);
		double deltax2 = (double)(xCeil) - x; 
		double deltay1 = y - (double)(yFloor);
		double deltay2 = (double)(yCeil) - y;

		if ( xFloor >=0 && yFloor>=0 
				&& ( bubbles[yFloor][xFloor] == 1 
					|| (maps[yFloor][xFloor] >= Config.UNWALKABLE_ST && maps[yFloor][xFloor] <= Config.UNWALKABLE_ED)
					|| (maps[yFloor][xFloor] == Config.BOX_ST))) {
			if ( ! (deltax1 > (1 - Config.THRESHOLD) || deltay1 > (1- Config.THRESHOLD))) {
				return false;
			}
		}

		if ( yFloor>=0 
				&& ( bubbles[yFloor][xCeil] == 1 
					|| (maps[yFloor][xCeil] >= Config.UNWALKABLE_ST && maps[yFloor][xCeil] <= Config.UNWALKABLE_ED)
					|| (maps[yFloor][xCeil] == Config.BOX_ST))) {
			if ( ! (deltax2 > (1 - Config.THRESHOLD) || deltay1 > (1- Config.THRESHOLD))) {
				return false;
			}
		}

		if ( xFloor>=0 
				&& ( bubbles[yCeil][xFloor] == 1 
					|| (maps[yCeil][xFloor] >= Config.UNWALKABLE_ST && maps[yCeil][xFloor] <= Config.UNWALKABLE_ED)
					|| (maps[yCeil][xFloor] == Config.BOX_ST))) {
			if ( ! (deltax1 > (1 - Config.THRESHOLD) || deltay2 > (1- Config.THRESHOLD))) {
				return false;
			}
		}

		if ( ( bubbles[yCeil][xCeil] == 1 
					|| (maps[yCeil][xCeil] >= Config.UNWALKABLE_ST && maps[yCeil][xCeil] <= Config.UNWALKABLE_ED)
					|| (maps[yCeil][xCeil] == Config.BOX_ST))) {
			if ( ! (deltax2 > (1 - Config.THRESHOLD) || deltay2 > (1- Config.THRESHOLD))) {
				return false;
			}
		}

	//if ((maps[y][x] >= Config.UNWALKABLE_ST && maps[y][x] <= Config.UNWALKABLE_ED) 
	//		|| (maps[y][x] == Config.BOX_ST)) {
	//		return false;
	//	}

		return true;

	}

	public boolean judgeBlast(double x, double y) {

		int xFloor = (int)Math.floor(x); 
		int xCeil = (int)Math.ceil(x);

		int yFloor = (int)Math.floor(y); 
		int yCeil = (int)Math.ceil(y);

		double deltax1 = x - (double)(xFloor);
		double deltax2 = (double)(xCeil) - x; 
		double deltay1 = y - (double)(yFloor);
		double deltay2 = (double)(yCeil) - y;

		if ( xFloor >=0 && yFloor>=0 && blastZone[yFloor][xFloor] ) {
			if ( ! (deltax1 > (1 - Config.THRESHOLD) || deltay1 > (1- Config.THRESHOLD))) {
				return true;
			}
		}
		if ( yFloor >=0 && blastZone[yFloor][xCeil]) {
			if ( ! (deltax2 > (1 - Config.THRESHOLD) || deltay1 > (1- Config.THRESHOLD))) {
				return true;
			}
		}

		if ( xFloor >=0 && blastZone[yCeil][xFloor]) {
			if ( ! (deltax1 > (1 - Config.THRESHOLD) || deltay2 > (1- Config.THRESHOLD))) {
				return true;
			}
		}

		if ( blastZone[yCeil][xCeil]) {
			if ( ! (deltax2 > (1 - Config.THRESHOLD) || deltay2 > (1- Config.THRESHOLD))) {
				return true;
			}
		}

		return false;
	}

	///////////////////////////////////////////////////////// ----week10

	/**. This function is used to update the user
	 * information in a certain game.
	 *  The parameter game refers to the game in
	 *   which the users are. The parameter
	 *  time refers to time which ,after the
	 *   update, the user's timestamp will be */

	public void updateUser(Game game, User user, long time) {


		//System.out.println("update User phase1");
		int deltaTime = (int)(time - user.lastRefreshTime);

		////////////////////////////////////////////////////////////////////
		//if (blastZone[(int) user.pos.getYY()][(int) user.pos.getXX()]) {
		if (judgeBlast(user.pos.getXX(),user.pos.getYY())) {
			user.status = 101;
		}
		//////////////////////////////////////////////////////////////////// --week12

		if (user.status > 30) {
			user.status += deltaTime / 50;
			return;
		}

		//System.out.println("update User phase2");

		if (user.direction == 1) {

			double temp1 = Config.DELTA_SPEED 
					* (double) user.speed*((double) deltaTime / (double)(1000));
			temp1 += user.pos.getYY();
	///////////////////////////////////////////////////////// ----week10
			
			boolean tempFlag = false;
			boolean tempFlag2 = false;
			boolean tempFlag1 = false;

			int x1 = (int)Math.ceil(user.pos.getXX());
			int x2 = (int)Math.floor(user.pos.getXX());
			int yy = (int)Math.ceil(temp1);

			//tempFlag = canWalk(x1,yy) && canWalk(x2, yy);
			
			tempFlag2 = canWalk(user.pos.getXX(), user.pos.getYY());
			tempFlag1 = canWalk(user.pos.getXX() , temp1);

			if ( !tempFlag2) {
			////////////////////////////////////////////////--week13
				if ( !tempFlag1) {
					//if ( (int)(user.pos.getYY()+0.5) == (int)(temp1 + 0.5)) {
					if ( Math.abs(user.bubbley - temp1) < (1-Config.THRESHOLD)) {
						tempFlag = true;
					} else {
						tempFlag = false;
					}
				} else {
					tempFlag = true;
				}
			////////////////////////////////////////////////--end week13
				
			} else if ( tempFlag1 == true) {
				tempFlag = true;
			}

			if (! tempFlag) {
				temp1 = Math.floor(temp1);
				user.collisionDetection = true;

				if (user.direction <= 4) {
					user.direction += 4;
				}

			} else {
				user.collisionDetection = false;
			}

	///////////////////////////////////////////////////////// 
	//
			user.pos.setYY(temp1);
			if (user.pos.getYY() >= Config.MAP_WIDTH - 1) {
				user.pos.setYY(Config.MAP_WIDTH - 1);
			}
		} else if (user.direction == 2) {
			double temp1 = user.pos.getYY();
			temp1 -= Config.DELTA_SPEED *(double)user.speed*((double)deltaTime/(double)(1000));
	///////////////////////////////////////////////////////// ----week10
			
			boolean tempFlag = false;
			boolean tempFlag2 = false;
			boolean tempFlag1 = false;

			int x1 = (int)Math.ceil(user.pos.getXX());
			int x2 = (int)Math.floor(user.pos.getXX());

			int yy =(int) Math.floor(temp1);

			tempFlag2 = canWalk(user.pos.getXX(), user.pos.getYY());
			tempFlag1 = canWalk(user.pos.getXX(),temp1);

			if ( !tempFlag2) {

			////////////////////////////////////////////////--week13
				if ( !tempFlag1) {
					//if ( (int)(user.pos.getYY()+0.5) == (int)(temp1 + 0.5)) {
					if ( Math.abs(user.bubbley - temp1) < (1-Config.THRESHOLD)) {
						tempFlag = true;
					} else {
						tempFlag = false;
					}
				} else {
					tempFlag = true;
				}
			////////////////////////////////////////////////--end week13
				
			} else if ( tempFlag1 == true) {
				tempFlag = true;
			}

			//tempFlag = canWalk(x1,yy) && canWalk(x2, yy);
			
			if (! tempFlag) {

				temp1 = Math.ceil(temp1);
				user.collisionDetection = true;

				if (user.direction <= 4) {
					user.direction += 4;
				}
			} else {
				user.collisionDetection = false;
			}

	///////////////////////////////////////////////////////// 
			user.pos.setYY(temp1);
			if (user.pos.getYY() <= 0) {
				user.pos.setYY(0);
			}
		} else if (user.direction == 3) {
			double temp1 = user.pos.getXX();
			temp1 -= Config.DELTA_SPEED *(double)user.speed*((double)deltaTime/(double)(1000));
	///////////////////////////////////////////////////////// ----week10
			
			boolean tempFlag = false;
			boolean tempFlag2 = false;
			boolean tempFlag1 = false;

			int y1 = (int)Math.ceil(user.pos.getYY());
			int y2 = (int)Math.floor(user.pos.getYY());

			int xx =(int) Math.floor(temp1);

			//tempFlag = canWalk(xx,y1) && canWalk(xx, y2);

			tempFlag2 = canWalk(user.pos.getXX(), user.pos.getYY());
			tempFlag1 = canWalk(temp1,user.pos.getYY());

			if ( !tempFlag2) {

			////////////////////////////////////////////////--week13
				if ( !tempFlag1) {
					//if ( (int)(user.pos.getXX()+0.5) == (int)(temp1 + 0.5)) {
					if ( Math.abs(user.bubblex - temp1) < (1-Config.THRESHOLD)) {
						tempFlag = true;
					} else {
						tempFlag = false;
					}
				} else {
					tempFlag = true;
				}
			////////////////////////////////////////////////--end week13
			} else if ( tempFlag1 == true) {
				tempFlag = true;
			}

			if (! tempFlag) {
				temp1 = Math.ceil(temp1);
				user.collisionDetection = true;

				if (user.direction <= 4) {
					user.direction += 4;
				}

			} else {
				user.collisionDetection = false;
			}

	/////////////////////////////////////////////////////////
			user.pos.setXX(temp1);
			if (user.pos.getXX() <= 0) {
				user.pos.setXX(0);
			}

		} else if (user.direction == 4) {

			double temp1 = Config.DELTA_SPEED *(double)user.speed*((double)deltaTime/(double)(1000)); 
			temp1 += user.pos.getXX();
	///////////////////////////////////////////////////////// ----week10
	//
			boolean tempFlag = false;
			boolean tempFlag2 = false;
			boolean tempFlag1 = false;

			int y1 = (int)Math.ceil(user.pos.getYY());
			int y2 = (int)Math.floor(user.pos.getYY());


			int xx =(int) Math.ceil(temp1);
			//tempFlag = canWalk(xx,y1) && canWalk(xx, y2);

			tempFlag2 = canWalk(user.pos.getXX(), user.pos.getYY());
			tempFlag1 = canWalk(temp1,user.pos.getYY());

			if ( !tempFlag2) {

			////////////////////////////////////////////////--week13
				if ( !tempFlag1) {
					//if ( (int)(user.pos.getXX()+0.5) == (int)(temp1 + 0.5)) {
					if ( Math.abs(user.bubblex - temp1) < (1-Config.THRESHOLD)) {
						tempFlag = true;
					} else {
						tempFlag = false;
					}
				} else {
					tempFlag = true;
				}
			////////////////////////////////////////////////--end week13

			} else if ( tempFlag1 == true) {
				tempFlag = true;
			}


			//tempFlag = canWalk(xx,y1) && canWalk(xx, y2);


			if (! tempFlag) {
				temp1 = Math.floor(temp1);
				user.collisionDetection = true;

				if (user.direction <= 4) {
					user.direction += 4;
				}
			} else {
				user.collisionDetection = false;
			}
	///////////////////////////////////////////////////////// 
	//
			user.pos.setXX(temp1);
			if (user.pos.getXX() >= Config.MAP_LENGTH - 1) {
				user.pos.setXX(Config.MAP_LENGTH - 1);
			}
		}

		user.lastRefreshTime = time;
	}
}

