public class Pos{
	
	/**.
	 */
	private int x;
	/**.
	 */
	private int y;

	Pos() {

	}

	Pos(final int xx, final int yy) {
		this.x = xx;
		this.y = yy;
	}

	Pos(Pos p) {
		x = p.x;
		y = p.y;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final void setX(final int xx) {
		x = xx;
	}
	public final void setY(final int yy) {
		y = yy;
	}
}
