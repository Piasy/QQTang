package com.example.client.view.myWidgets;

import com.example.client.view.myView.GameActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 按钮类，绘制按钮
 */
public class MyButton {
	float x;//图片的左上点x坐标
	float y;//图片的左上点y坐标
	int width;//虚拟按钮的宽
	int height;//虚拟按钮的高
	Bitmap onBitmap;//按下图片
	Bitmap offBitmap;//抬起图片
	String info;
	boolean isOn=true;//按下状态为false
	
	public MyButton(Bitmap onBitmap,Bitmap offBitmap, float x,float y,String info)
	{
		this.isOn = false;
		this.onBitmap = onBitmap;
		this.offBitmap = offBitmap;
		this.info = info;
		this.x = x;
		this.y = y;
		this.width = onBitmap.getWidth();
		this.height= onBitmap.getHeight();
	}
	/**
	 * 构造函数
	 */
	public void drawSelf(Canvas canvas,Paint paint)//绘制虚拟按钮
	{
		if(isOn)
		{
			canvas.drawBitmap(onBitmap, x, y, paint);
			paint.setTextSize(15.0f);
			canvas.drawText(info,x + width/4, y + height/3, paint);
		}
		else
		{
			canvas.drawBitmap(offBitmap, x, y, paint);
			paint.setTextSize(15.0f);
			canvas.drawText(info,x + width/4, y + height/2, paint);
			
		}
	}
	/**
	 * 设置按钮文本内容
	 */
    public void setInfo(String _info)
    {
    	info = _info;
    }
	/**
	 * 得到文本
	 */
    public String getInfo(){
    	return info;
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
		if( pointx>=x&&pointx<=x+width&&
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
