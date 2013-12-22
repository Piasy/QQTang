package com.example.client.view.myWidgets;

import com.example.client.view.myView.GameActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * �ı���,�����ı�
 */
public class MyText extends EditText{
	public MyText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	void draw(){
		
	}

	SurfaceView sur;
	/**
	 * �ı����Ͻ�λ��
	 */
	float x;
	float y;
	/**
	 * �ı���Ϣ���
	 */
	int width;
	int height;
	/**
	 * �ı���ͼƬ
	 */
	Bitmap onBitmap;
	Bitmap offBitmap;
	String info;
	boolean isOn=true;
	/**
	 * �������뷨
	 */
	class MyInputConnection extends BaseInputConnection{

        String inputString="";

        public MyInputConnection(View targetView, boolean fullEditor) { 
            super(targetView, fullEditor); 
            // TODO Auto-generated constructor stub 
        } 
        public boolean commitText(CharSequence text, int newCursorPosition){ 
            inputString=inputString+(String) text;
            setInfo(inputString);
            return true; 
        } 
        
    }
	/**
	 * ���캯��
	 */
/*	public MyText(Bitmap onBitmap,Bitmap offBitmap, float x,float y,String info, SurfaceView sur)
	{
		this.isOn = false;
		this.onBitmap = onBitmap;
		this.offBitmap = offBitmap;
		this.info = info;
		this.x = x;
		this.y = y;
		this.width = onBitmap.getWidth();
		this.height= onBitmap.getHeight();
	}*/
	public void drawSelf(Canvas canvas,Paint paint)
	{
		if(isOn)
		{
			canvas.drawBitmap(onBitmap, x, y, paint);
			paint.setTextSize(20.0f);
			canvas.drawText(info,x + width/5, y + 2*height/5, paint);
		}
		else
		{
			canvas.drawBitmap(offBitmap, x, y, paint);
			paint.setTextSize(20.0f);
			canvas.drawText(info,x + width/5, y + 2*height/5, paint);
			
		}
	}
	/**
	 * �����ı�����
	 */
    public void setInfo(String _info)
    {
    	info = _info;
    }
	/**
	 * �õ��ı�
	 */
    public String getInfo()
    {
    	return info;
    }
	/**
	 * �Ƿ��ڼ�����£�״̬
	 */
	public boolean isOnflag()
	{
		return isOn;
	}
	/**
	 * ���ü���״̬
	 */
	public void setOnflag(boolean flag)
	{
	    isOn = flag;
	}
	/**
	 * �������뷨
	 */
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new MyInputConnection(sur, false);
		
	}
	
	/**
	 * �ж��û��Ƿ������ı�������
	 */
	public boolean isPointInRect(float pointx,float pointy)
	{
		if( pointx>=x&&pointx<=x+width&&
				pointy>=y&&pointy<=y+height
		  )
		  {
			Context context = sur.getContext();
			InputMethodManager  input=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			input.showSoftInput(sur,0);
			//  isOn = true;
			  return true;
		  }
		isOn = false;
		return false;
	}	
}
