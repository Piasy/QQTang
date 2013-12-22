import java.io.*;
import java.net.*;
import java.util.*;

public class Recieve implements Runnable
{
	BufferedReader is;
	Recieve(BufferedReader _is)
	{
		is = _is;
		Thread thread = new Thread(this);
		thread.start();
	}
	public void run()
	{
		try
		{
			while (true)
			{
				String s = is.readLine();
				System.out.println(s);
			}
		}
		catch (Exception e) {}
	}
}
