package com.example.client.view.myGameStruct;

import com.example.client.view.myThread.BallExplodeThread;
import com.example.client.view.myThread.BallThread;
import com.example.client.view.others.Constant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
/**
 * 单机版游戏测试类,之后不再使用
 */
public class Ball {
	public MapPic map;
	public int row, col;
	public float x;//图片的x坐标
	public float y;//图片的y坐标 
	Bitmap ball[];//位图
	BallThread ballThread;
	BallExplodeThread explodeThread;
	public boolean exist;
	public int i;
	public int count = 0;

	public Ball(MapPic map, Bitmap ball[],int row, int col)
	{
		this.ball = ball;
		this.map = map;
		this.row = row;
		this.col = col;
        map.mapdata[row][col] = 2;
        i = 0;
        exist = true;

	}
	
	//绘制的方法
	public void drawSelf(Canvas canvas,Paint paint)//绘制的方法，继承该类的方法相同
	{
		if(exist){
			y = row * Constant.BLOCK_HEIGHT;
			x = col * Constant.BLOCK_WIDTH;
			Matrix m2=new Matrix();//图片移动一定的距离
			m2.setTranslate(x, y);
			canvas.drawBitmap(ball[i], m2, paint);//绘制旋转后的图片	
		}
	}
    public void time(){
  
        ballThread = new BallThread(this);
	    ballThread.start();
    }
	public void explode(){
		explodeThread = new BallExplodeThread(this);
		explodeThread.start();
		
	}
	
}