import java.io.*;
import java.net.*;
import java.util.*;

public class Main
{
	static PrintWriter os;
	static BufferedReader is;
    public static void main(String args[]) 
    {
        try
        {
        	Socket socket = new Socket("166.111.134.210", 10001);
            os = new PrintWriter(socket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os.println(Common.getLoginInfo("qianqiao", "qianqiao", "hall-server2"));
            os.flush();
            String login = is.readLine();
            System.out.println(login);
            
                       
            socket = new Socket("192.168.1.103", 10011);
            os = new PrintWriter(socket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Recieve recieve = new Recieve(is);

            os.println(login);
            os.flush();
            Thread.sleep(3000);
            
            os.println(Common.getEnterCommend());
            os.flush();
            Thread.sleep(3000);
            
            Scanner scanner = new Scanner(System.in);

            Timer timer = new Timer();
            timer.schedule(new Main().new SendTask(), 0, 5000);
            while (true)
            {
            	String msg = scanner.nextLine();
            	String commend = String.format("{\"type\":\"message\", \"message\":\"%s\"}", msg);
            	os.println(commend);
            	os.flush();
            	Thread.sleep(1000);
            }
            
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    class SendTask extends TimerTask
    {
    	public void run()
    	{
    		synchronized (os)
    		{
    			os.println(Common.getHeartbeat());
    			os.flush();
    		}
    	}
    }
}

//{"user":"qianqiao", "password":"qianqiao", "target":"hall-server2"}
//{"type":"enter", "room":-2, "pos":1}
//{"type":"heartbeat"}
