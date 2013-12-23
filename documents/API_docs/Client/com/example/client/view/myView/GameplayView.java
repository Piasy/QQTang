package com.example.client.view.myView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.R;
import com.example.client.R.drawable;
import com.example.client.controller.Controller;
import com.example.client.model.Config;
import com.example.client.view.myGameStruct.Ball;
import com.example.client.view.myGameStruct.Man;
import com.example.client.view.myGameStruct.MapPic;
import com.example.client.view.myGameStruct.MyBall;
import com.example.client.view.myGameStruct.MyBallCollect;
import com.example.client.view.myGameStruct.MyMan;
import com.example.client.view.myGameStruct.MyManCollect;
import com.example.client.view.myGameStruct.MyMap;
import com.example.client.view.myGameStruct.MyProp;
import com.example.client.view.myGameStruct.MyPropCollect;
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerProperties;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**.
  *负责游戏界面的显示和捕捉用户操作
  */
public class GameplayView extends SurfaceView
    implements SurfaceHolder.Callback {
    /**.
      *  GameActivity的引用
      */
    GameActivity activity;
    /**.
      * Controller引用，调用客户端model的接口
      */
    Controller controller;
    Paint paint; //画笔
    /**.
      * 游戏的行数，列数
      */
    int ROW = Constant.GAME_ROW;
    int COL = Constant.GAME_COL;
    /**.
      *刷帧线程
      */
	GameKeyThread keythread;
    /**.
      *  地图类，负责地图绘制处理
      */
	MyMap map;
    /**.
      *人物类，负责人物的绘制
      */
	MyManCollect manCollect;
    /**.
     * 炸弹类, 负责炸弹的绘制
     */

	MyBallCollect ballCollect;
	/**.
	 * 道具类, 负责道具的绘制
	 */
	MyPropCollect propCollect;

	int mapdata[][] = new int[ROW][COL]; //地图数据
	Vector<MyBall>  ball_vec;
	Vector<MyMan>  man_vec;
	Vector<MyProp> prop_vec;
    MyBall tempBall;
    MyMan tempMan;
    MyProp tempProp;
    /**.
     * 方向移动按钮及放置炸弹按钮
     */
	MyButton[] dir_btn = new MyButton[4];
	MyButton btn_ball;
	Bitmap dir_pic[];  //上下左右按钮图片
    Bitmap btn_ball_pic[];  //放泡泡按钮图片
    Bitmap load_pic, win_pic, lose_pic;
    boolean start_flag = false;
    int game_result = 0;
    
	int ballstate = 0;
	
	int[][] tempMap ={
			{0,10,100,80,0,0,100,70,0,0,0,100,0,0,0,0,0,100,0,0},
			{0,0,70,0,0,0,100,100,0,0,0,0,100,0,0,0,0,0,0,0},
			{0,0,40,0,40,40,30,90,0,0,60,0,0,0,0,0,0,0,0,0},
			{100,80,40,40,100,50,0,0,0,0,100,0,0,0,0,100,100,0,0,10},
			{0,0,0,0,10,10,0,0,0,0,100,100,10,0,0,10,10,0,0,10},
			{0,0,100,80,0,0,100,100,100,0,0,10,0,0,0,0,10,10,0,0},
			{0,0,0,0,0,0,10,10,10,0,0,0,10,0,0,0,10,10,10,10},
			{0,0,0,0,0,0,0,0,0,0,10,10,0,0,0,0,0,0,0,0},
			{0,0,10,10,10,0,0,0,0,0,10,10,0,0,0,10,10,0,0,0},
			{0,0,10,10,10,0,0,0,0,0,10,10,10,0,0,10,10,0,0,0},
			{0,0,10,10,10,0,0,0,0,0,10,10,0,0,0,10,10,0,0,0},
			{0,0,10,10,10,0,0,0,0,0,10,10,10,0,0,10,10,0,0,0}
	};
    /**.
     * 构造函数，负责创建所需类
     */
	public GameplayView(GameActivity activity) {
		super(activity);
		this.activity=activity;
		getHolder().addCallback(this); //注册回调接口
		manCollect = new MyManCollect(activity);
		ballCollect = new MyBallCollect(activity);
		propCollect = new MyPropCollect(activity);
		map = new MyMap(activity);
		ball_vec = new Vector<MyBall>();
		man_vec = new Vector<MyMan>();
		prop_vec = new Vector<MyProp>();
		
		///////control//////////
        controller = Controller.getController();
        controller.setHandler(gameHandler);
	}
    /**.
     * 	界面初始化类，初始化位图资源，创建刷帧线程
     */
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("view", "created");
		paint = new Paint(); //创建画笔
		paint.setAntiAlias(true); //打开抗锯齿
		initBitmap(); //初始化位图资源
		keythread = new GameKeyThread(); //刷帧线程
    	for (int i = 0; i < this.ROW; i++) {
    		for (int j = 0; j < this.COL; j++) {
    			this.mapdata[i][j] = tempMap[i][j];
    		}
    	}
    	//按钮位置数组col, row

    	float [][] btn_location = {
        		{2f, 4.7f},
        		{3f, 3.7f},
        		{2f, 2.7f},
        		{1f, 3.7f}};
        for (int i = 0; i < 4; i ++) {
        	dir_btn[i] = new MyButton(dir_pic[i], dir_pic[i + 4],
        			Constant.BLOCK_HEIGHT * btn_location[i][0],
        			Constant.BLOCK_WIDTH * btn_location[i][1], "");
        }
	    btn_ball = new MyButton(btn_ball_pic[0],
	    		btn_ball_pic[1], Constant.BLOCK_HEIGHT * 6,
	    		Constant.BLOCK_WIDTH * 4, "");
		startAllThreads();
	}
    /**.
     * 	onDraw方法，负责游戏界面的绘制
     */
	@Override
	public void onDraw(Canvas canvas) {
		Log.d("view", "draw");
		super.onDraw(canvas);
		canvas.clipRect(new Rect(0, 0, Constant.SCREEN_WIDTH,
				Constant.SCREEN_HEIGHT)); //只在屏幕范围内绘制图片
		canvas.drawColor(Color.GREEN); //界面设置为白色
		
		
		if (this.start_flag) {
			map.drawSelf(canvas, paint);
			ballCollect.drawSelf(canvas, paint);
			manCollect.drawSelf(canvas, paint);
			propCollect.drawSelf(canvas, paint);
			btn_ball.drawSelf(canvas, paint);
	        for(int i = 0; i < 4; i ++) {
	        	dir_btn[i].drawSelf(canvas, paint);
	        }
		}
		else {
			if(game_result == 0) {
				canvas.drawBitmap(load_pic, 0, 0, paint);
			}
			else if(game_result == 1) {
				canvas.drawBitmap(win_pic, 0, 0, paint);
			}
			else {
				canvas.drawBitmap(lose_pic, 0, 0, paint);
			}
		}
	}
    /**.
     * 	负责捕捉处理用户的触摸操作
     */
	@Override
	public boolean onTouchEvent(MotionEvent me) {

        if (!start_flag) {
			return true;
		}

        float pointx, pointy;
        float pointx2, pointy2;
        switch(me.getActionMasked())                //me.getActionMasked()  获取当前动作类型      
        {  
            case MotionEvent.ACTION_DOWN:           //如果是第一个触点被按下是执行  
                pointx = me.getX(me.getActionIndex());            //获取第一个触点的 X 坐标  
                pointy = me.getY(me.getActionIndex());            //获取第一个触点的 Y 坐标  
				for (int i = 0; i < 4; i ++) {
					if (dir_btn[i].isPointInRect(pointx,
							pointy)) {
						dir_btn[i].setOnflag(true);
						manGoStart(i);
					}
				}
			    if (btn_ball.isPointInRect(pointx,
			    		pointy)) {
			    	btn_ball.setOnflag(true);
					setBall();
				}              
                break;  
            case MotionEvent.ACTION_UP:             //如果第一个触点弹起时被触发(手指离开屏幕时)  
                pointx = me.getX(me.getActionIndex());            //获取第一个触点的 X 坐标  
                pointy = me.getY(me.getActionIndex());            //获取第一个触点的 Y 坐标  
				for (int i = 0; i < 4; i ++) {
					if (dir_btn[i].isPointInRect(pointx,
							pointy)) {
						dir_btn[i].setOnflag(false);
						manGoStop();
					}
				}
			    if (btn_ball.isPointInRect(pointx,
			    		pointy)) {
			    	btn_ball.setOnflag(false);
				}    
                break;  
            case MotionEvent.ACTION_POINTER_DOWN:           //除第一个触点外，其他触点被按下时该方法被触发。  
            	pointx2 = me.getX(me.getActionIndex());    //获取第二个触点的坐标  
            	pointy2 = me.getY(me.getActionIndex());    //获取第二个触点的坐标  
				for (int i = 0; i < 4; i ++) {
					if (dir_btn[i].isPointInRect(pointx2,
							pointy2)) {
						dir_btn[i].setOnflag(true);
						manGoStart(i);
					}
				}
			    if (btn_ball.isPointInRect(pointx2,
			    		pointy2)) {
			    	btn_ball.setOnflag(true);
					setBall();
				}               
                break;  
            case MotionEvent.ACTION_POINTER_UP:             //除第一个触点外，其他触点弹起时该方法被触发。  
            	pointx2 = me.getX(me.getActionIndex());  
            	pointy2 = me.getY(me.getActionIndex());  
				for (int i = 0; i < 4; i ++) {
					if (dir_btn[i].isPointInRect(pointx2,
							pointy2)) {
						dir_btn[i].setOnflag(false);
						manGoStop();
					}
				}
			    if (btn_ball.isPointInRect(pointx2,
			    		pointy2)) {
			    	btn_ball.setOnflag(false);
				}    
				break;
            case MotionEvent.ACTION_MOVE:                   //当任意一个触点在屏幕移动时被触发  
                
                break;  
        }

		return true;
	}

    /**.
     * 	调用controller接口改变人物移动状态
     */
	void manGoStart(int i) {
		Log.d("gameview", "go" + i);
		if (i == 0) {
			controller.changeMoveStatus(0);
		} else if (i == 1) {
			controller.changeMoveStatus(3);
		} else if (i == 2) {
			controller.changeMoveStatus(1);
		} else if (i == 3) {
			controller.changeMoveStatus(2);
		}
		//Toast.makeText(activity, "go", Toast.LENGTH_SHORT).show();
	}
    void manGoStop() {
    	controller.changeMoveStatus(4);
    	//Toast.makeText(activity, "stop", Toast.LENGTH_SHORT).show();
    }
    /**.
     * 	调用controller接口放置炸弹
     */
    void setBall() {
    	controller.placeBomb();
    	//Toast.makeText(activity, "set", Toast.LENGTH_SHORT).show();
    }
    /**.
     * 	调用controller接口实时获取游戏信息
     */
    public void getGameStatus() {
    	//this.start_flag = true;
		JSONObject snapShot;
		try {
			Log.d("gameview", "ok1");
			snapShot = new JSONObject(controller.getGameStatus());
			Log.d("gameview", snapShot.toString());
			//System.out.println(snapShot);
		    String game_type = snapShot.getString("type");
		    if (game_type.equals("info")){
		    	this.start_flag = true;
		    	game_result = 0;
		    } else if (game_type.equals("win")) {
		    	start_flag = false;
		    	game_result = 1;
		    	return;
		    } else if (game_type.equals("lose")) {
		    	start_flag = false;
		    	game_result = 2;
		    	return;
		    } else if (game_type.equals("prepare")) {
		    	start_flag = false;
		    	return;
		    } else if (game_type.equals("error")) {
		    	start_flag = false;
		    	game_result = 0;
		    	
		    	this.activity.goToRoom();
		    	
		    	return;
		    }
		    Log.d("gameview", "ok2");
			JSONArray  jmap = snapShot.getJSONArray ("map");
			for (int i = 0; i < jmap.length(); i ++) {
				JSONArray tjmap = jmap.getJSONArray(i);
				for (int j = 0; j < tjmap.length(); j++) {
					this.tempMap[i][j] =
					tjmap.getJSONObject(j).getInt("status");
				}
			}
	    	for (int i = 0; i < this.ROW; i ++) {
	    		for (int j = 0; j < this.COL; j++) {
	    			this.mapdata[i][j] = tempMap[i][j];
	    		}
	    	}
	    	ball_vec.clear();
	    	man_vec.clear();
	    	prop_vec.clear();
	    	
	    	
	    	JSONObject jman_vec = snapShot.getJSONObject("player");
	        Iterator it = jman_vec.keys();
	        while (it.hasNext()) {
	        	String key = (String) it.next();
	        	JSONObject jman = jman_vec.getJSONObject(key);
	        	float x, y;
	        	int model, status;
	    		x = (float) jman.getDouble("x") + 0.01f;
	    		y = (float) jman.getDouble("y") + 0.01f;
	    		model = jman.getInt("model");
	    		status = jman.getInt("status");
	            tempMan = new MyMan(y, x, model, status);
	            man_vec.add(tempMan);
	        }
	       
	    	JSONArray jball_vec = snapShot.getJSONArray("bomb");
	    	
	    	for (int i = 0; i < jball_vec.length(); i++) {
	    		JSONObject jball = jball_vec.getJSONObject(i);
	    		int x, y, model, status;
	    		int splash[] = {0,0,0,0};
	    		x = jball.getInt("x");
	    		y = jball.getInt("y");
	    		model = jball.getInt("model");
	    		status = jball.getInt("status");
	    		JSONObject jsplash = jball.getJSONObject("range");
	    		/*splash[0] = jsplash.getInt("d");
	    		splash[1] = jsplash.getInt("r");
	    		splash[2] = jsplash.getInt("u");
	    		splash[3] = jsplash.getInt("l");*/
	    		splash[0] = jsplash.getInt("u");
	    		splash[1] = jsplash.getInt("r");
	    		splash[2] = jsplash.getInt("d");
	    		splash[3] = jsplash.getInt("l");
	            tempBall = new MyBall(y, x, model, status, splash);
	            
	            ball_vec.add(tempBall);
	    	}
	    	Log.d("gameview", "prop");
	    	//JSONArray jprop_vec = snapShot.getJSONArray("prop");

			JSONArray  jprop_vec = snapShot.getJSONArray("prop");
			for (int i = 0; i < jmap.length(); i ++) {
				JSONArray tjprop_vec = jprop_vec.getJSONArray(i);
				for (int j = 0; j < tjprop_vec.length(); j++) {
					int propStatus = 
				    tjprop_vec.getJSONObject(j).getInt("status");
				    if(propStatus >= 5) {
			            tempProp = new MyProp(i, j, propStatus - 5);
			            prop_vec.add(tempProp);	
				    }
				}
			}
	    	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("view", "JSONexception");
			e.printStackTrace();
			
		}
    
    	
    	////////debug/////////////
    	/*    	
    	for(int i = 0; i < this.ROW; i ++){
    		for(int j = 0; j < this.COL; j++){
    			this.mapdata[i][j] = tempMap[i][j];
    		}
    	}
    	
        ball_vec.clear();
    	man_vec.clear();
    	prop_vec.clear();
    	Random rand = new Random();
    	int ran_row, ran_col;
    	//ran_row = rand.nextInt() % 8;
    	//ran_col = rand.nextInt() % 16;
    	ran_row = 5;
    	ran_col = 5;
        int tsplash[] = {1,1,1,1};
        ballstate += 5;
        ballstate = (ballstate) % 100;
        tempBall = new MyBall(ran_row, ran_col, 0, ballstate, tsplash);
        ball_vec.add(tempBall);
        tempBall = new MyBall(4, 4, 0, 0, tsplash);
        ball_vec.add(tempBall);
        float rand_row, rand_col;
    	rand_row = 0;
    	rand_col = 0;
        //rand_row = (float) (Math.random() * 8);
        //rand_col = (float) (Math.random() * 16);
        tempMan = new MyMan(rand_row, rand_col, 0, 1);
        man_vec.add(tempMan);
        tempMan = new MyMan(1, 0, 0, 3);
        man_vec.add(tempMan);
        tempMan = new MyMan(2, 0, 0, 5);
        man_vec.add(tempMan);

        //rand_row = (float) (Math.random() * 8);
        //rand_col = (float) (Math.random() * 16);
    	rand_row = 0;
    	rand_col =10;
        tempMan = new MyMan(rand_row, rand_col, 1, 1);
        man_vec.add(tempMan);
        tempMan = new MyMan(1, 10, 1, 3);
        man_vec.add(tempMan);
        tempMan = new MyMan(1, 11, 1, 5);
        man_vec.add(tempMan);

        //rand_row = (float) (Math.random() * 8);
        //rand_col = (float) (Math.random() * 16);
    	rand_row = 7;
    	rand_col = 0;
        tempMan = new MyMan(rand_row, rand_col, 2, 1);
        man_vec.add(tempMan);    
        tempMan = new MyMan(5, 0, 2, 3);
        man_vec.add(tempMan);
        tempMan = new MyMan(6, 0, 2, 5);
        man_vec.add(tempMan);

    	rand_row = 7;
    	rand_col = 10;
        tempMan = new MyMan(rand_row, rand_col, 3, 1);
        man_vec.add(tempMan);    
        tempMan = new MyMan(6, 10, 3, 3);
        man_vec.add(tempMan);
        tempMan = new MyMan(7, 9, 3, 5);
        man_vec.add(tempMan);

        //ran_row = rand.nextInt() % 5;
        tempProp = new MyProp(2, 5, 0);
        prop_vec.add(tempProp);
        tempProp = new MyProp(3, 7, 1);
        prop_vec.add(tempProp);
        tempProp = new MyProp(1, 5, 2);
        prop_vec.add(tempProp);
        tempProp = new MyProp(4, 7, 3);
        prop_vec.add(tempProp);
        tempProp = new MyProp(5, 7, 4);
        prop_vec.add(tempProp);*/
        ///////////////////////
        
		Log.d("view", "ok5");
        this.map.set(mapdata);
        this.ballCollect.set(ball_vec);
        this.manCollect.set(man_vec);
        this.propCollect.set(prop_vec);
    }
    /**.
     * 	界面销毁时调用
     */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		stopAllThreads(); //停止所有线程
	    while (retry) {
	        try {
	        	keythread.join();
	            retry = false;
	        }
	        catch (InterruptedException e) {
	        	e.printStackTrace();
	        } //不断地循环，直到其它线程结束
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder,
			int format, int width, int height) {
	}

    /**.
     * 	初始化位图信息
     */
	public void initBitmap() { //背景图片
		load_pic = PicLoadUtil.scaleToFit(
				BitmapFactory.decodeResource(
				this.getResources(),R.drawable.wellcome1), 
				Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
		win_pic = PicLoadUtil.scaleToFit(
				BitmapFactory.decodeResource(
				this.getResources(),R.drawable.win), 
				Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
		lose_pic = PicLoadUtil.scaleToFit(
				BitmapFactory.decodeResource(
				this.getResources(),R.drawable.lose), 
				Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
		dir_pic = new Bitmap[] {
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir00),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir10),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir20),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir30),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir01),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir11),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir22),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
			  this.getResources(), R.drawable.dir33),
			  Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT)
		};
		btn_ball_pic = new Bitmap[] {
				PicLoadUtil.scaleToFit(
				BitmapFactory.decodeResource(
				this.getResources(),
				R.drawable.btn_ball),
				Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
				BitmapFactory.decodeResource(
				this.getResources(),
				R.drawable.btn_ball2),
				Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT)
        };
	}
    /**.
     * 	结束游戏的方法
     */
	public void overGame() {
		stopAllThreads(); //停止所有线程	
		activity.goToHall(); //发送消息，进入游戏结束界面
	}
    /**.
     * 	开启所有线程
     */
	public void startAllThreads() {
		keythread.setKeyFlag(true); //刷帧线程的标志位设为true
		keythread.start(); //开启刷帧线程

	}

	public void stopAllThreads() {
		keythread.setKeyFlag(false);//关闭刷帧线程
	}
    /**.
     * 	重绘函数，调用onDraw进行绘制
     */
	@SuppressLint("WrongCall")
	public void repaint() {
		Canvas canvas = this.getHolder().lockCanvas();
		try {
			synchronized (canvas) {
				onDraw(canvas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (canvas != null) {
				this.getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
    /**.
     * 	游戏刷帧线程类
     */
	private class GameKeyThread extends Thread {
		private boolean keyFlag = true;
		private int sleeptime = 30;
		public GameKeyThread() {
		}
		@Override
		public void run() {
			while(isKeyFlag()) {
				try {
					Thread.sleep(sleeptime);
				} catch(Exception e) {
					e.printStackTrace();
				}
				///////controller////////
				//int POS = controller.getCurPos();
				int POS = 5;
				if(POS == 5) {
					getGameStatus();
					repaint();					
				}
				else {
					switch(POS) {
					case 1:
		        	case 2:
		        		activity.goToLogin(); //进入游戏结束界面
		        		break;
		        	case 3:
		        		activity.goToHall();
		        		break;
		        	case 4:
		        		activity.goToRoom();
		        		break;
		        	}
					
				}

			}
		}
		public boolean isKeyFlag() {
			return keyFlag;
		}
		public void setKeyFlag(boolean keyFlag1) {
			this.keyFlag = keyFlag1;
		}
	}

	Handler gameHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {

		}
	 };
	 
    /**.
     * 	处理退出按钮响应
     */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			// 创建退出对话框
			AlertDialog isExit =
				new AlertDialog.Builder(activity).create();
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

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
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
