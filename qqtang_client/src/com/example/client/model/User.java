package com.example.client.model;

import org.json.JSONObject;

/**
 * <p>This class is the user class which encapsulate
 * all data of a user and the management method 
 * to these data, including getting the details,
 * position, and entering a room, leaving a room,
 * ready for a game, etc.</p>
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class User
{
	JSONObject details;
	int roomNum = -1, seatPos = -1;
	boolean ready = false;
	
	/**
	 * default constructor
	 * */
	public User()
	{
		//do nothing
	}
	
	/**
	 * constructor with user details
	 * @param user details
	 * @return none
	 * */
	public User(JSONObject details)
	{
		this.details = details;
	}
	
	/**
	 * set user details
	 * @param user details
	 * @return none
	 * */
	public void setDetails(JSONObject details)
	{
		this.details = details;
	}
	
	/**
	 * set user sit at room roomNum, pos seatPos
	 * @param roomNum, seatPos
	 * @return none
	 * */
	public void sit(int roomNum, int seatPos)
	{
		this.roomNum = roomNum;
		this.seatPos = seatPos;
	}
	
	/**
	 * set user leave room
	 * @param none
	 * @return none
	 * */
	public void leave()
	{
		roomNum = -1;
		seatPos = -1;
	}
	
	/**
	 * set user to ready status
	 * @param none
	 * @return none
	 * */
	public void ready()
	{
		ready = true;
	}
	
	/**
	 * set user to unready status
	 * @param none
	 * @return none
	 * */
	public void unready()
	{
		ready = false;
	}
	
	/**
	 * get current room number of user
	 * @param none
	 * @return current room number, -1 if not in any one
	 * */
	public int getRoomNum()
	{
		return roomNum;
	}
	
	/**
	 * get current seat position of user
	 * @param none
	 * @return current seat position, -1 if not in any one
	 * */
	public int getSeatPos()
	{
		return seatPos;
	}
	
	/**
	 * get current user status
	 * @param none
	 * @return current user status ready or unready
	 * */
	public boolean getStatus()
	{
		return ready;
	}
	
	/**
	 * get current user details
	 * @param none
	 * @return jsonobject, user details
	 * */
	public JSONObject getDetails()
	{
		return details;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() + ", roomnum = " + roomNum + ", seatnum = " + seatPos;
		return ret;
	}
}
