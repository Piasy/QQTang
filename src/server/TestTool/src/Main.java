import java.io.*;
import java.net.*;
import java.util.*;

public class Main
{
    public static void main(String args[]) 
    {
        try
        {
        	Socket socket = new Socket("59.66.132.139", 10012);
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            os.println("{\"key\":\"606B606B\"}");
            os.flush();
            
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Recieve recieve = new Recieve(is);            
            Scanner scanner = new Scanner(System.in);
            
            while (true)
            {
            	String s = scanner.nextLine();
            	os.println(s);
            	os.flush();
            	System.out.println(s);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
