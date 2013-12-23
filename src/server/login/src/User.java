import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;
import net.sf.json.*;

/**class: use to connect user database and refresh*/
public class User 
{
	/**variable: user info*/
	public String user, key;
	/**variable: user info*/
	public JSONObject details;
	/** variable: store login fail information */
	public String reason = null;
	/** function: check if user login data is valid or not*/
	public static User getUser(JSONObject data)
	{
		try
		{
			User user = new User();
			if (!data.containsKey("user"))
			{
				user.reason = "No User";
				return user;
			}
			if (!data.containsKey("password"))
			{
				user.reason = "No Password";
				return user;
			}
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Config.dbName, Config.dbUser, Config.dbPassword);
			String s = "select * from user where user = ? and password = ?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(s);
			statement.setString(1, data.getString("user"));
			statement.setString(2, data.getString("password"));
			ResultSet result = statement.executeQuery();
			
			if (!result.next())
			{
				user.reason = "Wrong username or password";
				return user;
			}
			if (!result.getString("status").equals("offline"))
			{
				user.reason = "Already Online";
				return user;
			}
			
			user.user = result.getString("user");			
			user.key = Constant.getKey();			
			user.details = setDetails(result);
			return user;
		}
		catch (Exception e)
		{
			Constant.log(String.format("Database Error: %s", e));
			return null;
		}
	}
	
	/**function: set user data*/
	static JSONObject setDetails(ResultSet input) throws Exception
	{
		JSONObject details = new JSONObject();
		details.put("user", input.getString("user"));
		details.put("name", input.getString("name"));
		details.put("exp", input.getInt("exp"));
		details.put("level", input.getInt("level"));
		details.put("gold", input.getInt("gold"));
		details.put("success", input.getInt("success"));
		details.put("fail", input.getInt("fail"));
		details.put("break", input.getInt("break"));
		details.put("velocity", input.getInt("velocity"));
		details.put("power", input.getInt("power"));
		details.put("number", input.getInt("number"));
		details.put("dodge", input.getInt("dodge"));		
		return details;
	}

	/**function: refresh user data*/
	public static void execute(String hall, JSONObject data)
	{
		try
		{
			String user = data.getString("user");
			String status = data.getString("status");
			if (status.equals("online")) status = hall;
			JSONObject details = data.getJSONObject("details");
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Config.dbName, Config.dbUser, Config.dbPassword);
			String s = "update user set status = ?, exp = ?, level = ?, gold = ?, success = ?, fail = ?, break = ?, velocity = ?, power = ?, number = ?, dodge = ? where user = ?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(s);
			statement.setString(1, status);
			statement.setInt(2, details.getInt("exp"));
			statement.setInt(3, details.getInt("level"));
			statement.setInt(4, details.getInt("gold"));
			statement.setInt(5, details.getInt("success"));
			statement.setInt(6, details.getInt("fail"));
			statement.setInt(7, details.getInt("break"));
			statement.setInt(8, details.getInt("velocity"));
			statement.setInt(9, details.getInt("power"));
			statement.setInt(10, details.getInt("number"));
			statement.setInt(11, details.getInt("dodge"));
			statement.setString(12, user);
			statement.executeUpdate();
		}
		catch (Exception e) 
		{
			Constant.log("Execute error: " + e);
		}
	}
	
	/**function: user login*/
	public static void login(String hall, String user)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Config.dbName, Config.dbUser, Config.dbPassword);
			String s = "update user set status = ? where user = ?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(s);
			statement.setString(1, hall);
			statement.setString(2, user);
			statement.executeUpdate();
		}
		catch (Exception e) 
		{
			Constant.log("Execute error: " + e);
		}
	}
	
	/**function: user logout*/
	public static void logout(String hall)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Config.dbName, Config.dbUser, Config.dbPassword);
			String s = "update user set status = ? where status = ?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(s);
			statement.setString(1, "offline");
			statement.setString(2, hall);
			statement.executeUpdate();		
		}
		catch (Exception e)
		{
			Constant.log("Execute error: " + e);			
		}
	}
	
	/**function: data sent to client*/
	public JSONObject toJSON()
	{
		JSONObject now = new JSONObject();
		now.put("key", key);
		now.put("user", user);
		now.put("details", details);
		return now;
	}
}