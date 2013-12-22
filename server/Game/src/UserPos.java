import java.util.*;
	
public class UserPos extends Pos{

	/**.
	 */
	private double x;
	/**.
	 */
	private double y;

	UserPos() {
	}

	UserPos(int x, int y) {
		this.x = (double) x;
		this.y = (double) y;
	}

	UserPos(double xx, double yy) {
		this.x = xx;
		this.y = yy;
	}

	UserPos(UserPos us) {
		this.x = us.x;
		this.y = us.y;
	}
	UserPos(Pos p) {
		this.x = (double) p.getX();
		this.y = (double) p.getY();
	}

	public final double getXX() {
		return x;
	}

	public final double getYY() {
		return y;
	}

	public final void setXX(final double xx) {
		x = xx;
	}

	public final void setYY(final double yy) {
		y = yy;
	}


}



