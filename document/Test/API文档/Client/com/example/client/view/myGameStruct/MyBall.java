package com.example.client.view.myGameStruct;

public class MyBall {
	public int row, col;
	public int type;  //泡泡种类
	public int state;  //0--尚未爆炸，1~5表示泡泡爆炸的不同阶段，6表示已经爆炸
	public int splash[] = {0,0,0,0}; //下、右、上、左水花长度
    public static int index = 0; //人物移动时状态变化0~1
    public static int  MAXINDEX = 9;
	public MyBall() {
    	row = 0; col = 0;
    	type = 0;
    	state = 0;
    }
	public MyBall(int _row, int _col, int _type, int _state, int[] spla) {
    	row = _row;
    	col = _col;
    	type = _type;
    	state = _state;
    	for (int i = 0; i < 4; i++) {
    		splash[i] = spla[i];
    	}
    }
	public void set(int _row, int _col, int _type, int _state, int[] spla){
    	row = _row;
    	col = _col;
    	type = _type;
    	state = _state;
    	for (int i = 0; i < 4; i++) {
    		splash[i] = spla[i];
    	}
    }
	public void set(MyBall ball) {
    	row = ball.row;
    	col = ball.col;
    	type = ball.type;
    	state = ball.state;
    	for (int i = 0; i < 4; i++) {
    		splash[i] = ball.splash[i];
    	}
    }
	public static void changeIndex() {
		index++;
		index = index % MAXINDEX;
	}
}
