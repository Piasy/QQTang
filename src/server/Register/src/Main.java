/**class: main*/
public class Main {
	
	/**constant: port number*/
	private final static int PORT = 10003;
	
	/**function: entry*/
	public static void main(String args[]){
		Server server = new Server();
		server.start(PORT);
	}
}
