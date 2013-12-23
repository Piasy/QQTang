import java.io.*;
import java.util.*;
	
/**.
 * 这个类负责打印调试信息
 */
public class Log {
	static boolean CLIENT_ERR = true;

	static boolean GAME_ERR = true;

	static boolean LOGIN_ERR = true;

	static boolean CLIENT_INFO = true;

	static boolean GAME_INFO = true;

	static boolean LOGIN_INFO = true;

	public final static void log(final String log) {
		System.out.println(log);
	}

	public final static void clientErr(final String type, final Exception e) {
		if (CLIENT_ERR) {
			System.out.println(String.format("Exception of Client -- %s -- %s", type, e));
		}
	}

	public final static void gameErr(final String type, final Exception e) {
		if (GAME_ERR) {
			System.out.println(String.format("Exception of GameServer -- %s -- %s", type, e));
		}
	}

	public final static void loginErr(final String type, final Exception e) {
		if (LOGIN_ERR) {
			System.out.println(String.format("Exception of LoginServer -- %s -- %s", type, e));
		}
	}

	public final static void client(final String log) {
		if (CLIENT_INFO) {
			System.out.println(String.format("Client -- %s", log));
		}
	}

	public final static void game(final String log) {
		if (GAME_INFO) {
			System.out.println(String.format("GameServer -- %s", log));
		}
	}

	public final static void login(final String log) {
		if (LOGIN_INFO) {
			System.out.println(String.format("LoginServer -- %s", log));
		}
	}

	public static void setClientErr(boolean now) {
		CLIENT_ERR = now;
	}

	public static void setGameErr(boolean now) {
		GAME_ERR = now;
	}

	public static void setLoginErr(boolean now) {
		LOGIN_ERR = now;
	}

	public static void setClientInfo(boolean now) {
		CLIENT_INFO = now;
	}

	public static void setGameInfo(boolean now) {
		GAME_INFO = now;
	}

	public static void setLoginInfo(boolean now) {
		LOGIN_INFO = now;
	}
}