package com.example.client.model;

/**
 * <p>This class is the room class which encapsulate
 * all data of a room and the management method 
 * to these data, including getting a user at the 
 * specific position, a user and entering the room,
 * leaving the room, get room status, set room to 
 * playing game, etc.</p>
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class Room
{
	
	int size = 0;
	/**
	 * Users are created in other part, here just keep the reference
	 * */
	User users[] = new User[Config.ROOM_SIZE]; 
	/**
	 * Free Playing Full
	 * */
	String status; 
	
	/**
	 * default constructor
	 * */
	public Room()
	{
		for (int i = 0; i < Config.ROOM_SIZE; i ++)
			users[i] = null;
		status = "Free";
	}
	
	/**
	 * enter method
	 * @param user, pos
	 * @return success or not
	 * */
	public boolean enter(User user, int pos)
	{
		boolean ret = false;
		if (size < Config.ROOM_SIZE && 0 <= pos && pos < Config.ROOM_SIZE && users[pos] == null)
		{
			users[pos] = user;
			size ++;
			if (Config.ROOM_SIZE <= size)
				status = "Full";
			ret = true;
		}
		return ret;
	}
	
	/**
	 * enter method
	 * @param pos
	 * @return success or not
	 * */
	public void leave(int pos)
	{
		if (0 <= pos && pos < Config.ROOM_SIZE && users[pos] != null)
		{
			users[pos] = null;
			size --;
			status = "Free";
		}
	}
	
	/**
	 * get room status
	 * @return status
	 * */
	public String getStatus()
	{
		return status;
	}
	
	/**
	 * set status to Playing
	 * */
	public void startGame()
	{
		status = "Playing";
	}
	
	/**
	 * set status out of Playing
	 * */
	public void endGame()
	{
		if (size < Config.ROOM_SIZE)
			status = "Free";
		else
			status = "Full";
	}
	
	/**
	 * get user at pos i
	 * @param seat pos
	 * @return user at pos i
	 * */
	public User userAt(int i)
	{
		if (0 <= i && i < Config.ROOM_SIZE)
			return users[i];
		else
			return null;
	}
	
}
