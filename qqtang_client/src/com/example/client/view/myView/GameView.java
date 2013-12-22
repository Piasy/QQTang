package com.example.client.view.myView;

import java.util.ArrayList;
import java.util.Vector;

import com.example.client.R;
import com.example.client.R.drawable;
import com.example.client.view.myGameStruct.Ball;
import com.example.client.view.myGameStruct.Man;
import com.example.client.view.myGameStruct.MapPic;
import com.example.client.view.myThread.KeyThread;
import com.example.client.view.myWidgets.MyButton;
import com.example.client.view.others.Constant;
import com.example.client.view.others.PicLoadUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 单机版游戏测试类,之后不再使用
 */
public class GameView extends SurfaceView  implements SurfaceHolder.Callback{
	
	GameActivity activity;
	Paint paint;//画笔
	
	int mapdata[][] = {
			{0,0,1,1,0,0,1,1,0,0},
			{0,0,1,1,0,0,1,1,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,1,1,0,0,0,0},
			{0,0,0,0,1,1,0,0,0,0}
	};
	
	Man man;
	MapPic mappic;//背景图片类的引用
	MyButton dir0, dir1, dir2, dir3 ,btn_ball;
	
	int pointx;//触控到屏幕的位置
	int pointy;//触控到屏幕的位置
	
	
	KeyThread keythread;//刷帧线程
	//TimeRunningThread timeruningthread;//负责倒计时的线程
	
	Bitmap man_pic[];//第一组人物的图片
	Bitmap dir_pic[];
	Bitmap back_pic[];//背景图片
    Bitmap ball_pic[];
    Bitmap btn_ball_pic;
	Vector<Ball> vecBall = new Vector<Ball>();
	
	
	
	public GameView(GameActivity activity) {
		super(activity);
		this.activity=activity;
		getHolder().addCallback(this);//注册回调接口
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.clipRect(new Rect(0,0,Constant.SCREEN_WIDTH,Constant.SCREEN_HEIGHT));//只在屏幕范围内绘制图片
		
		canvas.drawColor(Color.WHITE);//界面设置为白色		
		mappic.drawSelf(canvas, paint);//绘制背景
		for(int i = 0; i < vecBall.size(); i++){
			vecBall.elementAt(i).drawSelf(canvas, paint);
			vecBall.elementAt(i).time();
		}
		man.drawSelf(canvas, paint);//后边的0,0为偏移量
        
		dir0.drawSelf(canvas, paint);
		dir1.drawSelf(canvas, paint);
		dir2.drawSelf(canvas, paint);
		dir3.drawSelf(canvas, paint);
		btn_ball.drawSelf(canvas, paint);
	
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		pointx=(int) event.getX();
		pointy=(int) event.getY();
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN://按下
			if(dir0.isPointInRect(pointx, pointy)){
			    man.manGo(0);
			}
			else if(dir1.isPointInRect(pointx, pointy)){
			    man.manGo(1);
			}
			else if(dir2.isPointInRect(pointx, pointy)){
			    man.manGo(2);
			}
			else if(dir3.isPointInRect(pointx, pointy)){
			    man.manGo(3);
			}
			else if(btn_ball.isPointInRect(pointx, pointy)){
				createBall(pointx, pointy);
			}
			break;
		case MotionEvent.ACTION_MOVE://移动
			
			break;
		case MotionEvent.ACTION_UP://抬起
		
			break;
		}
		return true;
	}
	public void createBall(float pointx, float pointy){
		Ball nball = new Ball(mappic, ball_pic, (int)man.row, (int)man.col);
	    vecBall.add(nball);
	    
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		paint=new Paint();//创建画笔
		paint.setAntiAlias(true);//打开抗锯齿
		
		Constant.changeRadio();//调用常量类中常量赋值的方法
		initBitmap();//初始化位图资源
		//背景音乐

		
		keythread=new KeyThread(this);//刷帧线程		
		mappic=new MapPic(mapdata, back_pic);//创建背景图片类的对象
		man = new Man(man_pic, mappic);
		mappic=new MapPic(mapdata, back_pic);//创建背景图片类的对象
		dir0 = new MyButton(dir_pic[0], dir_pic[0], Constant.BLOCK_HEIGHT * 1, Constant.BLOCK_WIDTH * 4.5f,"");
		dir1 = new MyButton(dir_pic[1], dir_pic[1], Constant.BLOCK_HEIGHT * 1.8f, Constant.BLOCK_WIDTH * 3.7f,"");
		dir2 = new MyButton(dir_pic[2], dir_pic[2], Constant.BLOCK_HEIGHT * 1, Constant.BLOCK_WIDTH * 3f,"");
		dir3 = new MyButton(dir_pic[3], dir_pic[3], Constant.BLOCK_HEIGHT * 0.2f, Constant.BLOCK_WIDTH * 3.7f,"");
	    btn_ball = new MyButton(btn_ball_pic, btn_ball_pic, Constant.BLOCK_HEIGHT * 6, Constant.BLOCK_WIDTH * 4, "");
		//开启全部线程	
		startAllThreads();
	}	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		stopAllThreads();//停止所有线程
		
		//关闭所有线程
	    while (retry) {
	        try {
	        	
	        	keythread.join();
	      
	            retry = false;
	        } 
	        catch (InterruptedException e) {e.printStackTrace();}//不断地循环，直到其它线程结束
		}
	    
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	//初始化图片资源
	public void initBitmap(){
		//大鱼的图片
		man_pic=new Bitmap[]{
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t11),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t12),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t13),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t21),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t22),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t23),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t31),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t32),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t33),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t41),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t42),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t43),Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT)
			};


		//背景图片
		back_pic=new Bitmap[]{
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.grass1), 
					Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.grass2), 
					Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT)
		
		};
		dir_pic=new Bitmap[]{
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.dir00), 
					Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.dir10), 
					Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.dir20), 
					Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.dir30), 
					Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT)
		};
		ball_pic = new Bitmap[]{
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.ball), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.ball), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.ball), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.ball), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT)
		};
		btn_ball_pic = PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.btn_ball), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT);

	}
	//结束游戏的方法
	public void overGame()
	{
		stopAllThreads();//停止所有线程
			
		activity.goToHall();//发送消息，进入游戏结束界面
	}
	//开启所有线程
	public void startAllThreads()
	{
		keythread.setKeyFlag(true);//刷帧线程的标志位设为true
		
		keythread.start();//开启刷帧线程

	}
	//停止所有线程
	public void stopAllThreads()
	{
		keythread.setKeyFlag(false);//关闭刷帧线程
		
	}
	
	//重新绘制画面
	@SuppressLint("WrongCall")
	public void repaint()
	{
		Canvas canvas=this.getHolder().lockCanvas();
		try
		{
			synchronized(canvas)
			{
				onDraw(canvas);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(canvas!=null)
			{
				this.getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
			// 创建退出对话框
			AlertDialog isExit = new AlertDialog.Builder(activity).create();
			// 设置对话框标题
			isExit.setTitle("提示");
			// 设置对话框消息
			isExit.setMessage("确定要退出吗");
			// 添加选择按钮并注册监听
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);
			// 显示对话框
			isExit.show();

		}
	return false;
	}

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				activity.finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};	
}
