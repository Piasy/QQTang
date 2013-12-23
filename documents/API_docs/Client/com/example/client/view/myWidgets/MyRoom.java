package com.example.client.view.myWidgets;

import com.example.client.view.myView.GameActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 房间类,储存房间信息,绘制房间
 */
public class MyRoom {
	/**
	 * 房间左上角位置
	 */
	float x;
	float y;
	/**
	 * 房间宽高
	 */
	int width;
	int height;
	/**
	 * 房间位图
	 */
	Bitmap onBitmap; 
	Bitmap offBitmap;
	boolean isOn = false; //按下为false
	/**
	 * 房间信息
	 */
	String info;
	String roomName;
	String players;
	String state;
	/**
	 * 构造函数
	 */
	public MyRoom(Bitmap onBitmap,Bitmap offBitmap, float x,float y)
	{	
		this.info = "room";
		this.onBitmap=onBitmap;
		this.offBitmap=offBitmap;
		this.x=x;
		this.y=y;
		this.width=offBitmap.getWidth();
		this.height=offBitmap.getHeight();		
	}
	/**
	 * 设置房间信息
	 */
	public void setRoomInfo(String _roomName, String _players, String _state)
	{
		roomName = _roomName;
		players = _players;
		state = _state;
	}
	/**
	 * 绘制房间
	 */
	public void drawSelf(Canvas canvas,Paint paint)//绘制虚拟按钮
	{
		if(!isOn)
		{
			canvas.drawBitmap(onBitmap, x, y, paint);
			canvas.drawText(roomName, x + width/4, y + 2*height/6, paint);
			
			canvas.drawText(players, x + width/4, y + 3*height/6, paint);
			
			canvas.drawText(state, x + width/4, y + 5*height/6, paint);
		}
		else
		{
			//info = roomName + "\n" + players + "\n" + state;
			info = "enter";
			canvas.drawBitmap(offBitmap, x, y, paint);
			canvas.drawText(info, x + width/5, y + 2*height/5, paint);
		}
	}
	/**
	 * 是否处于激活（按下）状态
	 */
	public boolean isOnflag()//返回isOn的状态
	{
		return isOn;
	}
	/**
	 * 设置激活状态
	 */
	public void setOnflag(boolean flag)
	{
	    isOn = flag;
	}
	/**
	 * 一个点是否在矩形内（包括边界）
	 */
	public boolean isPointInRect(float pointx,float pointy)
	{
		if(//如果在矩形内，返回true
				pointx>=x&&pointx<=x+width&&
				pointy>=y&&pointy<=y+height
		  )
		  {
			  isOn = true;
			  return true;//如果在，返回true
		  }
		isOn = false;
		return false;//否则返回false
	}	
}
