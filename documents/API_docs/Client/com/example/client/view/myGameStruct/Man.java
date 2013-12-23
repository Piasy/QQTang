package com.example.client.view.myGameStruct;

import com.example.client.view.myThread.ManFootThread;
import com.example.client.view.others.Constant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 单机版游戏测试类,之后不再使用
 */
public class Man {
	public float row;
	public float col;
	MapPic map;
	//int mapData[][];
	float x;//图片的x坐标
	float y;//图片的y坐标 
	Bitmap man[];//位图
	public int dir; //方向0:下  1：右  2上  3：左
	public int i;//控制行走的3张图片


	public Man(Bitmap man[], MapPic map)
	{
		this.map = map;
		this.row = this.col = 0.0f;
		this.man = man;
		this.dir = 0;
		this.i=0;
		
	}
	
	//绘制的方法
	public void drawSelf(Canvas canvas,Paint paint)//绘制的方法，继承该类的方法相同
	{
		y = row * Constant.BLOCK_HEIGHT;
		x = col * Constant.BLOCK_WIDTH;
		Matrix m2=new Matrix();//图片移动一定的距离
		m2.setTranslate(x, y);
		canvas.drawBitmap(man[i], m2, paint);//绘制旋转后的图片
	}
	
	public void manGo(int dir)//鱼走的方法,鱼的步进不同
	{
		int nrow = (int) row;
		int ncol = (int) col;
		this.dir = dir;
		if(dir == 0){
			i = 0;
			//System.out.println("row,col:" +nrow + ncol + row + col);
			if(nrow < Constant.ROW - 1 && map.mapdata[nrow + 1][ncol] < 1 )
			{
				
				ManFootThread foot = new ManFootThread(this, nrow + 1, ncol);
				foot.start();
			    //row ++;
			}

		}
		else if(dir == 1 ){
			i = 3;
			if(ncol < Constant.COL - 1 && map.mapdata[nrow][ncol + 1] < 1)
			{
				ManFootThread foot = new ManFootThread(this, nrow , ncol + 1);
				foot.start();
			    //col ++;
			}
		}
		else if(dir == 2){
			i = 6;
			if( nrow > 0 && map.mapdata[nrow - 1][ncol] < 1)
			{
				
				ManFootThread foot = new ManFootThread(this, nrow - 1, ncol);
				foot.start();
			    //row --;
			}
		}
		else if(dir == 3 ){
			i = 9;
			if(ncol > 0 && map.mapdata[nrow][ncol - 1] < 1 )
			{
				ManFootThread foot = new ManFootThread(this, nrow, ncol - 1);
				foot.start();
				//col --;
			}

		}
	}
}