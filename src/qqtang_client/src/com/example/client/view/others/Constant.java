package com.example.client.view.others;

public class Constant {
	public static int SCREEN_WIDTH;//ÆÁÄ»µÄ¿í
	public static int SCREEN_HEIGHT;//ÆÁÄ»µÄ¸ß
	public static float BLOCK_WIDTH;
	public static float BLOCK_HEIGHT;
	
	public static int ROW = 5;
	public static int COL = 10;
	
	public static int GAME_ROW = 8;
	public static int GAME_COL = 12;
	
	public static float GAME_BLOCK_WIDTH;
	public static float GAME_BLOCK_HEIGHT;
	
	public static void setGameRowCol(int row, int col){
		GAME_ROW = row;
		GAME_COL = col;
	}
	
	public static void changeRadio(){
		BLOCK_WIDTH =  SCREEN_WIDTH / COL;
		BLOCK_HEIGHT = SCREEN_HEIGHT / ROW;
		
		GAME_BLOCK_WIDTH = ((SCREEN_WIDTH) / GAME_COL);
		GAME_BLOCK_HEIGHT = SCREEN_HEIGHT / GAME_ROW;
	}

}
