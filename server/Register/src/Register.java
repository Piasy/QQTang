import net.sf.json.JSONObject;
import java.sql.*;

/**class: connect to register database*/
public class Register{
	//database information
	private final static String dbName = "jdbc:mysql://59.66.132.139/qqtang";
	private final static String dbUser = "root";
	//private final static String dbPassword = "lqaz2wsx!@";
	private final static String dbPassword = "qianqiao";
	
	/**function: check the user data is valid or not*/
	public JSONObject checkUser(JSONObject data){
		JSONObject obj = new JSONObject();
		try {
			if (!data.containsKey("user")){
				obj.put("result", "fail");
				obj.put("reason", "user is invalid");
				return obj;
			}
			if (data.getString("user").equals("")){
				obj.put("result", "fail");
				obj.put("reason", "user is empty");
				return obj;
			}
			if (!data.containsKey("password"))
			{
				obj.put("result", "fail");
				obj.put("reason", "password is invalid");
				return obj;
			}
			if (data.getString("password").equals("")){
				obj.put("result", "fail");
				obj.put("reason", "password is empty");
				return obj;
			}
			if (!data.containsKey("name"))
			{
				obj.put("result", "fail");
				obj.put("reason", "name is invalid");
				return obj;
			}
			if (data.getString("name").equals("")){
				obj.put("result", "fail");
				obj.put("reason", "name is empty");
				return obj;
			}
			//connect to the database
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbName, dbUser, dbPassword);
			String s = "select * from user where user = ?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(s);
			statement.setString(1, data.getString("user"));
			ResultSet result = statement.executeQuery();
			System.out.println("connect to database");
			if (!result.next()){
				return appendUser(data);
			}
			obj.put("result", "fail");
			obj.put("reason", "user is existed");
			return obj;
		}catch(Exception e){
			System.out.println((String.format("Database Check Error: %s", e)));
		}
		obj.put("result", "fail");
		obj.put("reason", "connect error");
		return obj;
	}
	
	/**function: append the user data to the database*/
	public JSONObject appendUser(JSONObject data){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbName, dbUser, dbPassword);
			String s = "insert into user set user = ?, password = ?, name = ?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(s);
			statement.setString(1, data.getString("user"));
			statement.setString(2, data.getString("password"));
			statement.setString(3, data.getString("name"));
			statement.executeUpdate();
			System.out.println("append successful");
			JSONObject obj = new JSONObject();
			obj.put("result", "success");
			return obj;
		}catch(Exception e){
			System.out.println((String.format("Database Append Error: %s", e)));
		}
		JSONObject obj = new JSONObject();
		obj.put("result", "fail");
		obj.put("reason", "connect error");
		return obj;
	}
}
