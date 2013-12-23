import java.util.*;
import java.net.*;
import java.io.*;
	
/**.
 * This class is record for a certain user.
 * @author mcgrady
 *
 */
public class User{

	int bubblex,bubbley;
	/**.
	 * The name of the user.
	 */
	public String name;
	/**.
	 * The id of the User.
	 */
	public int id;
	
	/**.
	 * The port which the user is expected to connect
	 */
	public int port;
	/**.
	 * The key used to verify identification
	 */
	public String key;
	/**.
	 * The socket used to communicate with the user
	 */
	public ServerSocket sock;
	
	public Socket socket;
	public BufferedReader is;
	public PrintWriter os;
	/**.
	 * Whether the user is online
	 */
	public boolean isOnline;
	/**.
	 * The apperence of the user
	 */
	public int model;

	User() {
		status = 0;
		isOnline = true;
		direction = 5;
	}

	/**.
	 * The dodge rate of the user
	 */
	public int dodgeRate;
	/**.
	 * The power of the bubble
	 */
	public int power;
	/**.
	 * The maximum number of the bubble 
	 * which the user can deploy
	 */
	public int maxBubbleNum;
	/**.
	 * The speed of the user.
	 */
	public int speed;
	/**.
	 * The position of the user.
	 */
	public UserPos pos;

	/**.
	 * The status of the user.
	 * 0 is alive.
	 * 1 - 100 means that the user is caught by bubble
	 */
	public int status;
	// 0 is alive
	// 1 - 100 is caught by the bomb 

	/**.
	 * The time when the last refresh happens
	 */
	public long lastRefreshTime;
	/**.
	 * The direction which the user head for.
	 */
	public int direction;

	//{1:up, 2:down, 3:left, 4:right
	// 5:upstop, 6:downStop, 7 leftStop 8:rightStop}
	//

	/**.
	 * How many bubbles can the user can deploy now.
	 */
	public int bubbleRemains;


	/////////////////////////////////////////////////////////////////////////////////
	public boolean collisionDetection;

	////////////////////////////////////////////////////////////////////////////------week10

	/**. This function is used to print
	 *  the information about the user */
	public void print(){

		System.out.println("Name : " + name);
		System.out.println("speed: " + speed);
		System.out.println("power: " + power);
		System.out.println("status: " + status);
		System.out.println("number: " + maxBubbleNum);
		System.out.println();
	}


}

