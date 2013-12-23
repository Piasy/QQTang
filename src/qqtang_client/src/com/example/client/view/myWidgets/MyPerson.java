package com.example.client.view.myWidgets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * 玩家类,保持玩家信息,绘制玩家
 */
public class MyPerson {
	/**
	 * 玩家信息左上角位置
	 */
	float x;
	float y;
	/**
	 * 玩家信息宽高
	 */
	int width;
	int height;
	/**
	 * 玩家位图
	 */
	Bitmap onBitmap;
	Bitmap offBitmap;
	String info;
	boolean isOn=true;
	/**
	 * 玩家信息
	 */
	public class PersonInfo{
		int id;
	};

	PersonInfo pinfo;
	/**
	 * 构造函数
	 */
	public MyPerson(Bitmap onBitmap,Bitmap offBitmap, float x,float y)
	{
		this.isOn=false;
		this.info = "player";
		this.onBitmap=onBitmap;
		this.offBitmap=offBitmap;
		this.x=x;
		this.y=y;
		this.width=offBitmap.getWidth();
		this.height=offBitmap.getHeight();
	}
	public void SetInfo(String name, String ready){
		this.info = name + '\n' + ready;
	}
	/**
	 * 绘制房间
	 */
	public void drawSelf(Canvas canvas,Paint paint)
	{
		if(isOn)
		{
			canvas.drawBitmap(onBitmap, x, y, paint);
			canvas.drawText(info, x + width/5, y + 2*height/5, paint);
		}
		else
		{
			canvas.drawBitmap(offBitmap, x, y, paint);
			canvas.drawText(info, x + width/5, y + 2*height/5, paint);
		}
	}
	public void setswitch()
	{
		isOn=!isOn;
	}
	public boolean isOnflag()
	{
		return isOn;
	}

	/**
	 * 一个点是否在矩形内（包括边界）
	 */
	
	public boolean isPointInRect(float pointx,float pointy)
	{
		if(
				pointx>=x&&pointx<=x+width&&
				pointy>=y&&pointy<=y+height
		  )
		  {
			  return true;
		  }
		return false;
	}
}
