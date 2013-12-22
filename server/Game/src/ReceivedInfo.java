import net.sf.json.*;
import java.util.*;
/**.
 * This class is a record for the received information from users.
 * @author mcgrady
 *
 */
public class ReceivedInfo{

	/**.
	 * the time when the information received.
	 */
	public long time;
	/**.
	 * the content of the information
	 */
	JSONObject obj;
	
	/*.
	 * which user does the information belongs to 
	 */
	int id;

	ReceivedInfo(long time, JSONObject obj){
		this.time = time;
		this.obj = obj;
		this.id = 0;
	}

	ReceivedInfo(long time, JSONObject obj,int id){
		this.time = time;
		this.obj = obj;
		this.id = id;
	}

}
