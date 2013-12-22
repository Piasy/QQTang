import java.util.*;
	
/** .
 * @author mcgrady
 *
 */

/**.
 * The class Bubble records some information of a bubble.
 * For each bubble, a new instance will be created.
 */
public class Bubble {
	/**.
	 * The position of the bubble
	 */
	private Pos pos;
	/**.
	 * The power of the bubble
	 */
	private int power;
	/**.
	 * The last refresh time of the bubble.
	 */
	private long lastRefreshTime;
	/**.
	 * Which client the bubble belongs to
	 */
	private int belongsTo;
	/**.
	 * The status of the bubble
	 */
	private int status;
	/**.
	 * The explosion range of the bubble
	 */
	private int up, down, left, right;
	/**.
	 */
	Bubble() {
		pos = new Pos();
		status = Config.BUBBLE_ST;
		up = 0;
		down = 0;
		left = 0;
		right = 0;
	}

	/**.
	 * return the position of the bubble
	 * @return Pos
	 */
	public final Pos getPos() {
		return pos;
	}

	/**.
	 * return the power of bubble
	 * @return void
	 */
	public final int getPower() {
		return power;
	}

	/**.
	 * return the last refresh time
	 * @return the newest time
	 */
	public final long getLastRefreshTime() {
		return lastRefreshTime;
	}

	/**.
	 * return the user which the bubble belongs to
	 * @return the parameter belongsTo
	 */
	public final int getBelongsTo() {
		return belongsTo;
	}

	/**.
	 * return the status of the bubble
	 * @return the status
	 */
	public final int getStatus() {
		return status;
	}

	/**.
	 * return the up explosion range of the bubble
	 * @return up
	 */
	public final int getUp() {
		return up;
	}
	/**.
	 * return the down explosion range of the bubble
	 * @return down
	 */
	public final int getDown() {
		return down;
	}

	/**.
	 * return the right explosion range of the bubble
	 * @return right
	 */
	public final int getRight() {
		return right;
	}
	/**.
	 * return the left explosion range of the bubble
	 * @return left
	 */
	public final int getLeft() {
		return left;
	}

	/**.
	 * set the position of the bubble
	 * @param pos1 is pos
	 */
	public final void setPos(Pos pos1) {
		pos = pos1;
	}

	/**.
	 * set the power of the bubble
	 * @param power1 is power
	 */
	public final void setPower(final int power1) {
		power = power1;
	}

	/**.
	 * set the last refresh time of the bubble
	 * @param lastRefreshTime1 is lastRefreshTime
	 */
	public final void setLastRefreshTime(final long lastRefreshTime1) {
		lastRefreshTime = lastRefreshTime1;
	}

	/**.
	 * set the user which the bubble belongs to
	 * @param belongsTo1 is belongsTo
	 * 
	 */
	public final void setBelongsTo(final int belongsTo1) {
		belongsTo = belongsTo1;
	}

	/**.
	 * set the status of the bubble
	 * @param status1 is status
	 */
	public final void setStatus(final int status1) {
		status = status1;
	}
	/**.
	 * set the up explosion range of the bubble
	 * @param up1 is up
	 */
	public final void setUp(final int up1) {
		up = up1;
	}

	/**.
	 * set the down explosion range of the bubble
	 * @param down1 is down
	 */
	public final void setDown(final int down1) {
		down = down1;
	}
	/**.
	 * set the left explosion range of the bubble
	 * @param left1 is left
	 */
	public final void setLeft(final int left1) {
		left = left1;
	}
	/**.
	 * set the right explosion range of the bubble
	 * @param right1 is right
	 */
	public final void setRight(final int right1) {
		right = right1;
	}
}



