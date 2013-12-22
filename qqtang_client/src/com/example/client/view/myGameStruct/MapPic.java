package com.example.client.view.myGameStruct;

import com.example.client.view.others.Constant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
/**
 * 单机版游戏测试类,之后不再使用
 */
public class MapPic {
	int row, col;
    public int mapdata[][];
	Bitmap[] backpic;//背景图片

	public MapPic(int [][] data, Bitmap[] pic)
	{
		row = Constant.ROW;
		col = Constant.COL;
		
		mapdata = data;
		backpic = pic;
	}
	public void drawSelf(Canvas canvas,Paint paint)//绘制背景图片
	{
		for(int i = 0; i < row; i ++){
			for(int j = 0; j < col; j ++){
				if(mapdata[i][j] == 1)
				    canvas.drawBitmap(backpic[1],j * Constant.BLOCK_WIDTH, i * Constant.BLOCK_HEIGHT,  paint);
				else{
					canvas.drawBitmap(backpic[0],j * Constant.BLOCK_WIDTH, i * Constant.BLOCK_HEIGHT,  paint);
				}
			}
		}
		
	}

}