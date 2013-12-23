package com.example.client.model;

/**
 * <p>This class contains all the constants
 * which will be used in all classes. That is design
 * for change!</p>
 *  
 * <p>author: 	Piasy Xu</p>
 * <p>date:		21:25 2013/12/11</p>
 * <p>email:	xz4215@gmail.com</p>
 * <p>github:	Piasy</p>
 * */
public class Config
{
	/**
	 * The LoginServer's ip address
	 * */
	public static final String loginIP = "166.111.134.210";
	/**
	 * The LoginServer's port
	 * */
	public static final int loginPort = 10001;
	/**
	 * The RegisterServer's port
	 * */
	public static final int SIGNUP_PORT = 10000;
	
	/**
	 * The hall size of this software
	 * */
	public static final int HALL_SIZE = 50;
	/**
	 * The room size of this software
	 * */
	public static final int ROOM_SIZE = 4;
	/**
	 * The number of rooms contained in one page of this software
	 * */
	public static final int ROOMS_PER_PAGE = 4;
	/**
	 * The map width during the game
	 * */
	public static final int MAP_WIDTH = 12;
	/**
	 * The map height during the game
	 * */
	public static final int MAP_HIGHT = 8;
	
	/**
	 * The current position code, this stand for in sign up view
	 * */
	public static final int CUR_POS_SIGN_UP = 1;
	/**
	 * The current position code, this stand for in sign in view
	 * */
	public static final int CUR_POS_SIGN_IN = 2;
	/**
	 * The current position code, this stand for in hall view
	 * */
	public static final int CUR_POS_HALL = 3;
	/**
	 * The current position code, this stand for in room view
	 * */
	public static final int CUR_POS_ROOM = 4;
	/**
	 * The current position code, this stand for in game view
	 * */
	public static final int CUR_POS_GAME = 5;
	
	
	/**
	 * The hall server response time out limit
	 * */
	public static final int HALL_RESPONSE_TIMEOUT = 20 * 1000;
	/**
	 * The login server response time out limit
	 * */
	public static final int  LOGIN_RESPONSE_TIMEOUT = 20 * 1000;
	/**
	 * The heart beat packet delay send to hall server
	 * */
	public static final int  HEART_BEAT_DELAY = 8 * 1000;
	 /**
	  * The game server response time out limit
	 * */
	public static final int  GAME_RESPONSE_TIMEOUT = 3 * 1000;
	
	
	/**
	 * The number stand for the least free seat
	 * */
	public static final int LEAST_FREE_SEAT = 999;
	/**
	 * The number stand for the least free room
	 * */
	public static final int LEAST_FREE_ROOM = 999;
	
	/**
	 * The initial string value
	 * */
	public static final String INIT_STR = "INITSTR";
	/**
	 * The logcat tag
	 * */
	public static final String LOG_TAG = "myinfo";
	/**
	 * The directions string which will be sent to game server
	 * */
	public static final String DIRECTION[] = {"up", "down", "left", "right", "stop"};
	/**
	 * The prop category string which will be sent to game server
	 * */
	public static final String CATEGORY[] = {"speed", "power", "number", "dodge"};
	
	/**
	 * The distance threshold that will be judge as bump
	 * */
	public static final double TOUCH_DIST = 0.3;
	/**
	 * The brake disappear speed
	 * */
	public static final double BRAKE_DIS_SPEED = 100;
	/**
	 * The bomb expand speed
	 * */
	public static final double BOMB_EXPAND_SPEED = 5;
}
