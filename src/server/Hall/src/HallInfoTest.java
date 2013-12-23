import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.io.*;
import java.net.*;

import net.sf.json.*;


public class HallInfoTest {

	/**.
	 * 测试获取实例函数
	 * 应返回一个非空值
	 */
	@Test
	public void testGetInstance() {
		HallInfo hall = HallInfo.getInstance();
		assertEquals(hall == null, false);
	}
	
	/**.
	 * 测试进入大厅函数
	 * 数据合法
	 * 应返回true
	 */
	@Test
	public void testExecuteLoginSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			boolean flag = hall.execute(getCorrectLoginData("testname"));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();
		}
	}

	/**.
	 * 测试进入大厅函数
	 * 数据格式不合法
	 * 应抛出异常
	 */
	@Test
	public void testExecuteLoginException() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			boolean flag = hall.execute(getWrongLoginData("testname"));
			fail();
		} catch (Exception e) {
		}
	}	

	/**.
	 * 测试进入大厅函数
	 * 数据格式合法但内容不合法。本测例中，进入大厅的用户是原先大厅中存在的用户
	 * 应返回false
	 */
	@Test
	public void testExecuteLoginFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectLoginData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
	}	
	
	
	/**.
	 * 测试进入房间函数
	 * 数据合法，包含3个测例：
	 * 		1、普通进入，指定房间和座位
	 * 		2、随机房间随机座位
	 * 		3、指定房间随机座位
	 * 应返回true
	 */
	@Test
	public void testExecuteEnterSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", 1, 1));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();			
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", 999, 999));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();			
		}

		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", 1, 999));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();			
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname1"));
			hall.execute(getCorrectEnterData("testname1", 0, 0));
			hall.execute(getCorrectLoginData("testname2"));
			hall.execute(getCorrectEnterData("testname2", 0, 1));
			hall.execute(getCorrectLoginData("testname3"));
			hall.execute(getCorrectEnterData("testname3", 0, 2));
			hall.execute(getCorrectLoginData("testname4"));
			hall.execute(getCorrectEnterData("testname4", 0, 3));
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", 999, 999));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();			
		}
	}
	
	
	/**.
	 * 测试进入房间函数
	 * 测试数据格式合法但内容不合法，包含以下几个测例：
	 * 		1、用户不在大厅
	 * 		2、用户已经在房间中
	 * 		3、进入房间下标向上溢出
	 * 		4、进入房间下标向下溢出
	 * 		5、进入座位下标向上溢出
	 * 		6、进入座位下标向下溢出
	 * 		7、该座位已经有人
	 * 		8、进入指定房间随机座位，但该房间已满
	 * 应返回false
	 */
	@Test
	public void testExecuteEnterFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();

		try {
			boolean flag = hall.execute(getCorrectEnterData("testname", 1, 1));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}
	
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			boolean flag = hall.execute(getCorrectEnterData("testname", 1, 1));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", 101, 1));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}

		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", -10, 1));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}

		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", 1, 4));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectEnterData("testname", 1, -1));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}

		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname1"));
			hall.execute(getCorrectLoginData("testname2"));
			hall.execute(getCorrectEnterData("testname1", 1, 1));
			boolean flag = hall.execute(getCorrectEnterData("testname2", 1, 1));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}
	}
	
	/**.
	 * 测试进入房间函数
	 * 测试数据格式不合法，没有room和pos
	 * 应抛出异常
	 */
	@Test
	public void testExecuteEnterException() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();

		try {
			boolean flag = hall.execute(getWrongEnterData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();			
		}
	}

	/**.
	 * 测试离开房间函数
	 * 测试数据合法
	 * 应返回true
	 */
	@Test
	public void testExecuteLeaveSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();

		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			boolean flag = hall.execute(getCorrectLeaveData("testname"));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();			
		}
	}

	/**.
	 * 测试离开房间函数
	 * 测试数据格式合法但内容不合法，包含以下几个测例：
	 * 		1、用户不在大厅中
	 * 		2、用户在大厅中但不在房间中
	 * 应返回false
	 */
	@Test
	public void testExecuteLeaveFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			boolean flag = hall.execute(getCorrectLeaveData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectLeaveData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试用户准备函数
	 * 测试数据合法
	 * 应返回true
	 */
	@Test
	public void testExecuteReadySuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			boolean flag = hall.execute(getCorrectReadyData("testname"));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();			
		}
	}

	/**.
	 * 测试用户准备函数
	 * 测试数据格式合法但内容不合法，包含以下几个测例：
	 * 		1、用户不在房间中
	 * 		2、用户已经准备
	 * 应返回false
	 */
	@Test
	public void testExecuteReadyFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectReadyData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			hall.execute(getCorrectReadyData("testname"));
			boolean flag = hall.execute(getCorrectReadyData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试用户取消准备函数
	 * 测试数据合法
	 * 应返回true
	 */
	@Test
	public void testExecuteUnreadySuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			hall.execute(getCorrectReadyData("testname"));
			boolean flag = hall.execute(getCorrectUnreadyData("testname"));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();			
		}
	}

	/**.
	 * 测试用户取消准备函数
	 * 测试数据格式合法但内容不合法，包含以下几个测例：
	 * 		1、用户不在房间中
	 * 		2、用户还未准备
	 * 应返回false
	 */
	@Test
	public void testExecuteUnreadyFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectUnreadyData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			boolean flag = hall.execute(getCorrectUnreadyData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试不合法的execute
	 * 测试数据格式合法但内容不合法，包含以下几个测例：
	 * 		1、用户不在房间中
	 * 		2、用户还未准备
	 * 应返回false
	 */
	@Test
	public void testExecuteOthers() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			boolean flag = hall.execute(getOtherData("testname"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
	}	
	
	/**.
	 * 测试发送消息函数
	 * 数据合法
	 * 应返回true
	 */
	@Test
	public void testExecuteMessageSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			boolean flag = hall.execute(getCorrectMessageData("testname", "hahaha"));
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();
		}
	}

	/**.
	 * 测试发送消息函数
	 * 数据格式不合法，缺失message
	 * 应抛出异常
	 */
	@Test
	public void testExecuteMessageException() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 1, 1));
			boolean flag = hall.execute(getWrongMessageData("testname"));
			fail();
		} catch (Exception e) {
		}
	}

	/**.
	 * 测试发送消息函数
	 * 数据格式合法但内容不合法。用户不在房间内。
	 * 应返回false
	 */
	@Test
	public void testExecuteMessageFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.execute(getCorrectMessageData("testname", "hahaha"));
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
	}	
	
	/**.
	 * 测试离开大厅函数
	 * 数据合法，有两种情况：
	 * 		1）在房间中
	 * 		2）不在房间中
	 * 应返回true
	 */
	@Test
	public void testExecuteLogoutSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			boolean flag = hall.logout("testname");
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();
		}
		
		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 999, 999));
			boolean flag = hall.logout("testname");
			assertEquals(flag, true);
		} catch (Exception e) {
			fail();
		}
	}

	/**.
	 * 测试发送消息函数
	 * 数据格式合法但内容不合法。用户不在大厅内。
	 * 应返回false
	 */
	@Test
	public void testExecuteLogoutFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			boolean flag = hall.logout("testname");
			assertEquals(flag, false);
		} catch (Exception e) {
			fail();
		}
	}
		
	
	/**.
	 * 测试获取大厅状态
	 * 应返回包含users、rooms、count的字典
	 */
	@Test
	public void testGetStatusSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 999, 999));
			JSONObject status = hall.getStatus();
			assertEquals(status.has("users"), true);
			assertEquals(status.has("rooms"), true);
			assertEquals(status.has("count"), true);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试获取大厅事件
	 * 应返回包含events、rooms、count的字典
	 */
	@Test
	public void testGetEventsSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 999, 999));
			JSONObject events = hall.getEvents(0);
			assertEquals(events.has("events"), true);
			assertEquals(events.has("rooms"), true);
			assertEquals(events.has("count"), true);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试获取用户资料
	 * 输入数据合法
	 * 应返回完整的用户资料
	 */
	@Test
	public void testGetDetailsSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 999, 999));
			JSONObject details = hall.getDetails("testname");
			assertEquals(details, getDetails("testname"));
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试获取用户资料
	 * 输入数据不合法  大厅中没有这个用户
	 * 应抛出异常
	 */
	@Test
	public void testGetDetailsException() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname"));
			hall.execute(getCorrectEnterData("testname", 999, 999));
			JSONObject details = hall.getDetails("testname2");
			fail();
		} catch (Exception e) {
		}
	}

	/**.
	 * 测试获取用户聊天记录
	 * 输入数据合法
	 * 应返回用户聊天记录
	 */
	@Test
	public void testGetMessagesSuccess() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname1"));
			hall.execute(getCorrectEnterData("testname1", 0, 0));
			hall.execute(getCorrectLoginData("testname2"));
			hall.execute(getCorrectEnterData("testname2", 0, 1));
			hall.execute(getCorrectMessageData("testname2", "hahaha"));
			JSONArray messages = hall.getMessages("testname1");
			assertEquals(messages.size(), 1);
		} catch (Exception e) {
			fail();
		}
	}

	/**.
	 * 测试获取用户聊天记录
	 * 输入数据格式正确但内容错误，有以下几种情况：
	 * 		1）用户不在房间中
	 * 		2）用户不在大厅中
	 * 应返回空JSONArray
	 */
	@Test
	public void testGetMessagesFail() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();
		try {
			hall.execute(getCorrectLoginData("testname1"));
			JSONArray messages = hall.getMessages("testname1");
			assertEquals(messages.size(), 0);
		} catch (Exception e) {
			fail();
		}

		HallInfo.clear();
		hall = HallInfo.getInstance();
		try {
			JSONArray messages = hall.getMessages("testname1");
			assertEquals(messages.size(), 0);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试设置游戏开始信息
	 * 给定参数合法
	 * 过获取游戏房间状态验证正确性，对应房间的状态应变为Playing
	 */
	@Test
	public void testSetGameStartInfo() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();

		try {
			hall.execute(getCorrectLoginData("testname1"));
			hall.execute(getCorrectEnterData("testname1", 1, 0));
			hall.execute(getCorrectReadyData("testname1"));
			hall.execute(getCorrectLoginData("testname2"));
			hall.execute(getCorrectEnterData("testname2", 1, 1));
			hall.execute(getCorrectReadyData("testname2"));
			JSONArray infos = new JSONArray();
			infos.add(getCorrectGameStartInfo("testname1", "1.2.3.4", 10001));
			infos.add(getCorrectGameStartInfo("testname2", "1.2.3.4", 10002));
			hall.setGameStartInfo(1, infos);
			JSONObject status = hall.getStatus();
			JSONArray rooms = status.getJSONArray("rooms");
			assertEquals(rooms.get(1), 1);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试获取游戏开始信息
	 * 参数分为以下三类：
	 * 		1）该用户的房间已经准备好游戏，应返回游戏开始信息
	 * 		2）该用户的房间还没有准备好游戏，应返回空
	 * 		3）该用户不在房间中，应返回空
	 * 		4）该用户不在大厅中，应返回空
	 */
	@Test
	public void testGetGameStartInfo() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();

		try {
			hall.execute(getCorrectLoginData("testname1"));
			hall.execute(getCorrectEnterData("testname1", 1, 0));
			hall.execute(getCorrectReadyData("testname1"));

			hall.execute(getCorrectLoginData("testname2"));
			hall.execute(getCorrectEnterData("testname2", 1, 1));
			hall.execute(getCorrectReadyData("testname2"));
			
			hall.execute(getCorrectLoginData("testname3"));
			hall.execute(getCorrectEnterData("testname3", 2, 0));
			
			hall.execute(getCorrectLoginData("testname4"));			
			JSONArray infos = new JSONArray();
			infos.add(getCorrectGameStartInfo("testname1", "1.2.3.4", 10001));
			infos.add(getCorrectGameStartInfo("testname2", "1.2.3.4", 10002));
			hall.setGameStartInfo(1, infos);
			
			JSONObject info1 = hall.getGameStartInfo("testname2");
			assertEquals(info1 != null, true);
			
			JSONObject info2 = hall.getGameStartInfo("testname3");
			assertEquals(info2 == null, true);
			
			JSONObject info3 = hall.getGameStartInfo("testname4");
			assertEquals(info3 == null, true);
			
			JSONObject info4 = hall.getGameStartInfo("testname5");
			assertEquals(info4 == null, true);
		} catch (Exception e) {
			fail();
		}
	}
	
	/**.
	 * 测试设置游戏结果函数
	 */
	@Test
	public void testGetGameFinishInfo() {
		HallInfo.clear();
		HallInfo hall = HallInfo.getInstance();

		try {
			hall.execute(getCorrectLoginData("testname1"));
			hall.execute(getCorrectEnterData("testname1", 1, 0));
			hall.execute(getCorrectReadyData("testname1"));

			hall.execute(getCorrectLoginData("testname2"));
			hall.execute(getCorrectEnterData("testname2", 1, 1));
			
			hall.execute(getCorrectLoginData("testname3"));
			hall.execute(getCorrectEnterData("testname3", 1, 2));

			hall.execute(getCorrectReadyData("testname2"));
			hall.execute(getCorrectReadyData("testname3"));
			
			JSONArray infos = new JSONArray();
			infos.add(getCorrectGameStartInfo("testname1", "1.2.3.4", 10001));
			infos.add(getCorrectGameStartInfo("testname2", "1.2.3.4", 10002));
			infos.add(getCorrectGameStartInfo("testname3", "1.2.3.4", 10003));
			hall.setGameStartInfo(1, infos);
			
			JSONObject info1 = hall.getGameStartInfo("testname1");
			JSONObject info2 = hall.getGameStartInfo("testname2");
			JSONObject info3 = hall.getGameStartInfo("testname3");
			
			JSONArray infoss = new JSONArray();
			infoss.add(getCorrectGameFinishInfo("testname1", "win"));
			infoss.add(getCorrectGameFinishInfo("testname2", "lose"));
			infoss.add(getCorrectGameFinishInfo("testname3", "break"));
			hall.setGameFinishedInfo(1, infoss);
			
		} catch (Exception e) {
			fail();
		}
	}

	JSONObject getDetails(String user)
	{
		JSONObject details = new JSONObject();
		details.put("user", user);
		details.put("exp", 1);
		details.put("level", 1);
		details.put("success", 0);
		details.put("fail", 0);
		details.put("break", 0);		
		return details;
	}
	
	JSONObject getCorrectLoginData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "login");
		data.put("user", user);
		data.put("details", getDetails(user));
		return data;
	}

	JSONObject getWrongLoginData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "login");
		data.put("user", user);
		return data;
	}

	JSONObject getCorrectEnterData(String user, int room, int pos) {
		JSONObject data = new JSONObject();
		data.put("type", "enter");
		data.put("user", user);
		data.put("room", room);
		data.put("pos", pos);
		return data;
	}

	JSONObject getWrongEnterData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "enter");
		data.put("user", user);
		return data;
	}

	JSONObject getCorrectLeaveData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "leave");
		data.put("user", user);
		return data;
	}
	JSONObject getCorrectReadyData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "ready");
		data.put("user", user);
		return data;
	}
	JSONObject getCorrectUnreadyData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "unready");
		data.put("user", user);
		return data;
	}
	JSONObject getOtherData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "other");
		data.put("user", user);
		return data;
	}
	JSONObject getCorrectMessageData(String user, String msg) {
		JSONObject data = new JSONObject();
		data.put("type", "message");
		data.put("message", msg);
		data.put("user", user);
		return data;
	}
	JSONObject getWrongMessageData(String user) {
		JSONObject data = new JSONObject();
		data.put("type", "message");
		data.put("user", user);
		return data;
	}
	JSONObject getCorrectGameStartInfo(String user, String ip, int port) {
		JSONObject data = new JSONObject();
		data.put("user", user);
		data.put("ip", ip);
		data.put("port", port);
		return data;		
	}
	JSONObject getCorrectGameFinishInfo(String user, String result) {
		JSONObject data = new JSONObject();
		data.put("user", user);
		data.put("result", result);
		return data;		
	}
}
