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
 * the sign up model. It takes charge of all communication
 *  with login server, send sign up user information, and 
 *  receive sign up result, and give the result to
 *  controller, all of these data are transmitted in 
 *  json format. For more information, please see
 *  the wiki page {Register}.</p>
 *  
 * <p>We assume that TCP provide very reliable service,
 * once receive server's message, it must be accurate.</p>
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class SignupModel
{
	Socket loginSocket;
	BufferedReader is;
	PrintWriter os;
	String signupIP = "";
	int signupPort;
	
	String username = "", password = "", passwordConfirm = "", name = "";
	JSONObject signupResponse = null;
	
	/**
	 * public constructor, set the server info and user info
	 * @param server info and user info
	 * @return none
	 * */
	public SignupModel(String signupIP, int signupPort, String username, String password, String passwordConfirm, String name)
	{
		this.signupIP = signupIP;
		this.signupPort = signupPort;
		this.username = username;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
		this.name = name;
	}
	
	/**
	 * Communicate with server, set signup result
	 *  This method will send sign up user information, and 
	 *  receive sign up result, and give the result to
	 *  controller, all of these data are transmitted in 
	 *  json format. For more information, please see
	 *  the wiki page {Register}.
	 * @return signup result
	 * */
	public String signup()
	{
		String ret = "InternetError";
		JSONObject signupResult = new JSONObject();
		try
		{
			signupResult.put("type", "signup");
			
			//Log.d(Config.LOG_TAG, "signup " + username + " " + password + " " + passwordConfirm);
			if (username == null)
			{
				signupResult.put("status", "EmptyUsernameError");
				return signupResult.toString();
			}
			boolean legalPass = password != null && passwordConfirm != null && password.equals(passwordConfirm);
			if (!legalPass)
			{
				signupResult.put("status", "IllegalPasswordError");
				return signupResult.toString();
			}
			if (!init())
			{
				signupResult.put("status", "InternetError");
				return signupResult.toString();
			}
			
			
			//Log.v("myinfo", "signup : " + username + " " + password);
			JSONObject signupInfo = new JSONObject();
			
			signupInfo.put("user", username);
			signupInfo.put("password", password);
			signupInfo.put("name", name);
			
			os.println(signupInfo.toString());
			os.flush();
			Log.v("myinfo", "send : " + signupInfo.toString());
			
			String signupResponseStr = is.readLine();
			Log.v("myinfo", "receive : " + signupResponseStr);
			if (signupResponseStr != null)
			{
				signupResponse = new JSONObject(signupResponseStr);
				if (signupResponse != null && signupResponse.has("result"))
				{
					String res = signupResponse.getString("result");
					if (res != null) 
					{
						if (res.equals("success")) //success
							ret = "success";
						else
							ret = signupResponse.getString("result");
					}
						
				}
				
			}
			os.close();
			is.close();
			loginSocket.close();
			signupResult.put("status", ret);
		} 
		catch (SocketTimeoutException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "signup SocketTimeoutException");
		}
		catch (UnknownHostException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "UnknownHostException");
		} 
		catch (IOException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "IOException");
		} 
		catch (JSONException e)
		{
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "JSONException");
		}
		
		return signupResult.toString();
	}
	
	protected boolean init()
	{
		boolean ret = false;
		try
		{
			loginSocket = new Socket(signupIP, signupPort);
			loginSocket.setSoTimeout(Config.LOGIN_RESPONSE_TIMEOUT);
			is = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
			os = new PrintWriter(loginSocket.getOutputStream());
			ret = true;
			Log.v("myinfo", "signup : " + "init : ok");
		} 
		catch (UnknownHostException e)
		{
			ret = false;
			Log.v("myinfo", "signup : " + "init : UnknownHostException");
		} 
		catch (IOException e)
		{
			ret = false;
			if (e.getMessage() != null)
				Log.d(Config.LOG_TAG, e.getMessage());
			else
				Log.d(Config.LOG_TAG, "IOException");
		}
		Log.v("myinfo", "signup : " + "init : " + ret);
		return ret;
	}
}
