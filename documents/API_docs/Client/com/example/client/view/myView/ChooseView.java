package com.example.client.view.myView;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.R;
import com.example.client.R.drawable;
import com.example.client.controller.Controller;
import com.example.client.model.Config;
import com.example.client.view.myWidgets.MyButton;
import com.example.client.view.myWidgets.MyRoom;
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;
	
/**.
 * 处理大厅房间选择界面的显示和处理用户操作
 */
public class ChooseView extends SurfaceView implements SurfaceHolder.Callback {
    /**.
     * HallActivity的引用
     */
    public HallActivity activity = null;
    /**.
     * 刷帧线程
     */
    DrawThread drawThread;
	Paint paint; //画笔

	MyButton incPage, decPage;
    MyRoom room[] = new MyRoom[4];

	int pointx;   //触控到屏幕的位置
	int pointy;   //触控到屏幕的位置

	Bitmap room_pic; //房间的图片
	Bitmap back_pic; //背景图片
	Bitmap btn_pic;
    /**.
     * 页数
     */
	int page = 0;
	String room_name[] = new String[]{"room", "room", "room", "room"};
	String room_player[] = new String[]{"play","play","play","play"};
	String room_state[] = new String[]{"empty","empty","empty","empty"};
    /**.
     * Controller引用，用于调用客户端model部分接口
     */
	Controller controller;
	
	public void IncPage(){
		if (page < 12)
			page += 1;
		else
			Toast.makeText(activity, "page out of range", Toast.LENGTH_SHORT).show();
	}
	
	public void DecPage(){
		if (page > 0)
			page -= 1;
		else
			Toast.makeText(activity, "page out of range", Toast.LENGTH_SHORT).show();
	}
	
    /**.
     * 构造函数
     */
	public ChooseView(HallActivity activity) {
		
		super(activity);
		
		Log.d(Config.LOG_TAG, "before assign ref of hall activity, null? " + (this.activity == null));
		if (this.activity != null)
			Log.d(Config.LOG_TAG, "before assign ref of hall activity: " + this.activity);
		Log.d(Config.LOG_TAG, "new choose view");
		this.activity = activity;
		Log.d(Config.LOG_TAG, "after assign ref of hall activity");
		getHolder().addCallback(this); //注册回调接口
		Log.d(Config.LOG_TAG, "after add callback");
        controller = Controller.getController();
        controller.setHandler(chooseHandler);		
	}
    /**.
     * 界面初始化，初始化按钮，房间，刷帧线程
     */
	public void surfaceCreated(SurfaceHolder arg0) {
		paint = new Paint(); //创建画笔
		paint.setAntiAlias(true); //打开抗锯齿
		page = 0;
		Constant.changeRadio(); //调用常量类中常量赋值的方法
		initBitmap(); //初始化位图资源

		incPage = new MyButton(btn_pic, btn_pic, Constant.BLOCK_WIDTH * 8, Constant.BLOCK_HEIGHT * 4.2f, "=>");
		decPage = new MyButton(btn_pic, btn_pic, Constant.BLOCK_WIDTH * 5, Constant.BLOCK_HEIGHT * 4.2f, "<=");
		float location[][] = {{2.0f, 1.0f},{6.0f, 1.0f},{2.0f, 2.5f},{6.0f, 2.5f}};

		for (int i = 0; i < 4; i++) {
		    room[i] = new MyRoom(room_pic, room_pic, Constant.BLOCK_WIDTH * location[i][0], Constant.BLOCK_HEIGHT * location[i][1]);
		}
		
		Log.d(Config.LOG_TAG, "create choose view");
		createAllThreads();
		startAllThreads();

    }
    /**.
     * 处理用户触摸事件
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		pointx = (int) event.getX();
		pointy = (int) event.getY();
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		if (incPage.isPointInRect(pointx, pointy)) {
    			IncPage();
    	    } else if (decPage.isPointInRect(pointx, pointy)) {
    			DecPage();
    		} else {
    		    for (int i = 0; i < 4; i++) {
    		        if (room[i].isPointInRect(pointx, pointy)) {
    		        	Integer roomNum = i + 4 * page;
    		        	String info = "room:" + roomNum.toString();
    		        	Toast.makeText(activity, info,
    		        			Toast.LENGTH_SHORT).show();
    		        	enterRoom(roomNum);
    		        }
    		    }
    		}
    		break;
    	case MotionEvent.ACTION_UP:
		    for (int i = 0; i < 4; i ++) {
		        room[i].setOnflag(false);
		    }
    		break;
    	}
		return true;
	}


    /**.
     * 选择界面绘制方法，绘制按钮，房间
     */
    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//绘制背景
		canvas.drawColor(Color.WHITE);
		//绘制背景图片
		canvas.drawBitmap(back_pic, 0, 0, paint);
		incPage.drawSelf(canvas, paint);
		decPage.drawSelf(canvas, paint);
		for (int i = 0; i < 4; i++) {
		    room[i].drawSelf(canvas, paint);
		}
	}
    /**.
     * 更新房间信息
     */
    public void updateRoomInfo() {
        for (int i = 0; i < 4; i++) {
        	room[i].setRoomInfo(room_name[i],
        			room_player[i], room_state[i]);
        }
    }
    /**.
     * 调用controller接口进入房间
     */
    public void enterRoom(int roomNum) {
    	controller.enter(roomNum);
    }

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}


	public void surfaceDestroyed(SurfaceHolder arg0) {
	}

    void createAllThreads() {
		drawThread = new DrawThread(this);//创建绘制线程		
	}
	void startAllThreads() {
		drawThread.setFlag(true);
		drawThread.start();
	}
	void stopAllThreads() {
		drawThread.setFlag(false);
	}

    /**.
     * 初始化位图信息
     */
	public void initBitmap() {
		back_pic = PicLoadUtil.scaleToFit(
				BitmapFactory.decodeResource(
				this.getResources(),R.drawable.chooseground), 
				Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
		room_pic = PicLoadUtil.scaleToFit(
				BitmapFactory.decodeResource(
				this.getResources(), R.drawable.room),
				Constant.BLOCK_WIDTH * 2f,
				Constant.BLOCK_HEIGHT * 1f);
		btn_pic = PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.btn_button),
				Constant.BLOCK_WIDTH * 1.2f,
				Constant.BLOCK_HEIGHT * 0.5f);
	}

	@SuppressLint("WrongCall")
	public void repaint() {
		Canvas canvas = this.getHolder().lockCanvas();
		try {
			synchronized (canvas) {
				Log.d(Config.LOG_TAG,
					"canvas null? "
				+ (canvas == null));
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
     * 调用controller接口获取大厅快照
     */
	void getSnapShot() {
		JSONObject snapShot;
		try {
			snapShot = new JSONObject(controller.getHallSnapShot(page * 4, 3 + page * 4));		
			//System.out.println(snapShot);
			@SuppressWarnings("rawtypes")
			//int pageNum = snapShot.getInt("pageNum");
			JSONArray rooms = snapShot.getJSONArray ("rooms");
			for (int i = 0; i < rooms.length(); i ++ ) {
				JSONObject room = rooms.getJSONObject(i);
				String status = room.getString("status");
				JSONArray users = room.getJSONArray("users");
				String players = "";
				for(int j = 0; j < users.length(); j++) {
					JSONObject player =
							users.getJSONObject(j);
					String playinfo =
							player.getString("user");
					players += playinfo + "\n";
				}
				Integer roomNum = i+page*4;
				this.room_name[i] = "room "
				    + roomNum.toString();
				this.room_player[i] = players;
				this.room_state[i] = status;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    /**.
     * 大厅handler, 处理controller传递的信息
     */
	Handler chooseHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			Log.d(Config.LOG_TAG, "in hallview " + (String)msg.obj);
		    JSONObject hallStatus;
			try {
				hallStatus = new JSONObject((String) msg.obj);
			if (hallStatus.getString("type").equals("enter")){
			    String status = "";
				    if (hallStatus.has("status")) {
			    	    status = hallStatus.getString("status");
				    }
				    Log.d(Config.LOG_TAG, "in hallview status "
				    + status);
					if (status != null
						&& status.equals("Success")) {
						Toast.makeText(activity,
						"successfuly ",
						Toast.LENGTH_SHORT).show();
					    activity.gotoRoom();
					}
					else {
					 Toast.makeText(activity, "failed",
					 Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
			//that means signin fail…
			}
		}
	 };
    /**.
     * 刷帧线程
     */
	private class DrawThread extends Thread {
		private boolean flag = true;
		private int sleepSpan = 500;
		public DrawThread(ChooseView fatherView) {
		}
		public void run() {
	        while (this.flag) {
	            try {
	            	Thread.sleep(sleepSpan); //睡眠指定毫秒数
	            } catch (Exception e) {
	            	e.printStackTrace(); //打印堆栈信息
	            }
	            getSnapShot();
	            updateRoomInfo();
	            repaint();
	        }
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}

    /**.
     * 处理返回键事件响应
     */
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			// 创建退出对话框
			AlertDialog isExit = new
					AlertDialog.Builder(activity).create();
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

	DialogInterface.OnClickListener listener =
			new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				System.exit(0);
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};
}
