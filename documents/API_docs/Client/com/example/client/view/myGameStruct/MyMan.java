package com.example.client.view.myGameStruct;

public class MyMan {
    public float row, col;
    public int type;
    public int state;
                 // 0-人物朝前静止（后面记为前静）， 1--前动， 2--右静 ，
                //3--右动， 4--上静 ，5--上动， 6--左静，7--左动，8--人物被炸，
                //9--人物死亡（或不存在）
    public static int index = 0; //人物移动时状态变化0~1
    public static int  MAXINDEX = 2;
	public MyMan() {
		row = 0f;
		col = 0f;
		type = 0;
		state = 9;
	}
	public MyMan(float _row, float _col, int _type, int _state) {
		row = _row;
		col = _col;
		type = _type;
		state = _state;
	}
	public void set(float _row, float _col, int _type, int _state) {
		row = _row;
		col = _col;
		type = _type;
		state = _state;
	}
	public void set(MyMan man) {
		row = man.row;
		col = man.col;
		type = man.type;
		state = man.state;
	}
	public static void changeIndex() {
		index++;
		index = index % MAXINDEX;
	}
}
