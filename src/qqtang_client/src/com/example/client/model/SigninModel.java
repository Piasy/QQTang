package com.example.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * <p>This class is the model in the MVC design pattern, 
 * the sign in model. It takes charge of all communication
 *  with login server, send sign in user information, and 
 *  receive sign in result, and give the result to
 *  controller, all of these data are transmitted in 
 *  json format. For more information, please see
 *  the wiki page {Client And Login Server}.</p>
 *  
 * <p>We assume that TCP provide very reliable service,
 * once receive server's message, it must be accurate.</p>
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class SigninModel
{
	Socket loginSocket;
	BufferedReader is;
	PrintWriter os;
	String loginIP = "";
	int loginPort;
	
	String username = "", password = "", target = "";
	JSONObject loginResponse = null;
	
	/**
	 * Public constructor, set the server info and user info
	 * @param server info and user info
	 * @return none
	 * */
	public SigninModel(String loginIP, int loginPort, String username, String password, String target)
	{
		this.loginIP = loginIP;
		this.loginPort = loginPort;
		this.username = username;
		this.password = password;
		this.target = target;
	}
	
	/**
	 * Communicate with server, set signup result.
	 * Send sign in user information, and 
	 *  receive sign in result, and give the result to
	 *  controller, all of these data are transmitted in 
	 *  json format. For more information, please see
	 *  the wiki page {Client And Login Server}.
	 * @param none
	 * @return signup result
	 * */
	public boolean signin()
	{
		boolean ret = false;
		if (!init())
			return ret;
		Log.v("myinfo2", username + " " + password);
		try
		{
			JSONObject loginInfo = new JSONObject();
			
			loginInfo.put("user", username);
			loginInfo.put("password", password);
			loginInfo.put("target", target);
			
			os.println(loginInfo.toString());
			os.flush();
			Log.v("myinfo2", "send : " + loginInfo.toString());
			
			String loginResponseStr = is.readLine();
			Log.v("myinfo2", "receive : " + loginResponseStr);
			if (loginResponseStr != null)
			{
				loginResponse = new JSONObject(loginResponseStr);
				if (loginResponse != null && loginResponse.has("user"))
				{
					String user = loginResponse.getString("user");
					if (user != null && user.equals(username))  //通信成功，此线程的工作基本完成
						ret = true;
				}
			}
			os.close();
			is.close();
			loginSocket.close();
		} 
		catch (SocketTimeoutException e)
		{
			ret = false;
		}
		catch (UnknownHostException e)
		{
			ret = false;
		} 
		catch (IOException e)
		{
			ret = false;
		} 
		catch (JSONException e)
		{
			ret = false;
		}
		return ret;
	}
	
	/**
	 * Get signin detail result
	 *  All of these data are transmitted in 
	 *  json format. 
	 * @param none
	 * @return JSONObject, detail result
	 * */
	public JSONObject getSigninResponse()
	{
		return loginResponse;
	}
	
	protected boolean init()
	{
		boolean ret = false;
		try
		{
			loginSocket = new Socket(loginIP, loginPort);
			loginSocket.setSoTimeout(Config.LOGIN_RESPONSE_TIMEOUT);
			is = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
			os = new PrintWriter(loginSocket.getOutputStream());
			ret = true;
			Log.v("myinfo", "init : ok");
		} 
		catch (UnknownHostException e)
		{
			ret = false;
			Log.v("myinfo", "init : UnknownHostException");
		} 
		catch (IOException e)
		{
			ret = false;
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "init IOException");
		}
		Log.v("myinfo", "init : " + ret);
		return ret;
	}
}
