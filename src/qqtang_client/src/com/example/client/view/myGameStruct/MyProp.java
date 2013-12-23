package com.example.client.view.myGameStruct;


public class MyProp {
    public float row, col;
    public int type;
    public static int index = 0;
    public static int  MAXINDEX = 2;
    public MyProp() {
        row = 0f;
        col = 0f;
        type = 0;
    }
	public MyProp(float _row, float _col, int _type) {
		row = _row;
		col = _col;
		type = _type;
	}
	public void set(float _row, float _col, int _type) {
		row = _row;
		col = _col;
		type = _type;
	}
	public void set(MyProp man) {
		row = man.row;
		col = man.col;
		type = man.type;
	}

	public static void changeIndex() {
		index++;
		index = index % MAXINDEX;
	}
}
