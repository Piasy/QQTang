import java.net.*;


/** register server*/
public class Server implements Runnable{
	Thread thread = null;
	int port;
	
	/**function: start a new register server*/
	public void start(int _port){
		if (thread == null){
			port = _port;
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		try{
			ServerSocket serverSocket = new ServerSocket(port);
			while (true){
				try{
					//if accept a client, then create a new thread to communicate
					Socket socket = serverSocket.accept();
					Connect.add(socket);
				}catch(Exception e){
					System.out.println(String.format("ServerSocket Fail! Exception: %s", e));
				}
			}
		}catch(Exception e){
			System.out.println(String.format("ServerSocket created Fail! Exception: %s", e));
		}
	}
}
