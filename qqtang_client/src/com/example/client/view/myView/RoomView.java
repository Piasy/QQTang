package com.example.client.view.myView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.R;
import com.example.client.R.drawable;
import com.example.client.controller.Controller;
import com.example.client.model.Config;
import com.example.client.view.myWidgets.MyButton;
import com.example.client.view.myWidgets.MyPerson;
import com.example.client.view.myWidgets.MyRoom;
import com.example.client.view.myWidgets.MyText;
import com.example.client.view.others.Constant;
import com.example.client.view.others.PicLoadUtil;
import com.example.client.view.myWidgets.*;
import com.example.client.controller.Controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


/**
 * The View of Room
 * contains the paint of the room, exhibits the status of each person, inform the controller of ready(), unready(), leave()
 * information. Receive message from controller by handler.
 * 
 */


@SuppressLint("HandlerLeak")
public class RoomView extends SurfaceView implements SurfaceHolder.Callback {
	RoomActivity activity;
	DrawThread drawThread;
	Paint paint;
	
	

	/**
	 * ���浱ǰ������
	 */
	int roomnumber;

	/**
	 * ���÷����ڵİ�ť������
	 */

	MyButton ready, exit, chat, send, express;
    MyPerson[] person = new MyPerson[4];
	
	int pointx;   
	int pointy;  
	
	Bitmap person_pic;
	Bitmap back_pic;
	Bitmap btn_pic;
	
	String person_name[] = new String[]{"player 1", "player 2", "player 3", "player 4"};
	String person_player[] = new String[]{"play","play","play","play"};
	String person_state[] = new String[]{"empty","empty","empty","empty"};
    /**
     * Controller���ã����ڵ��ÿͻ���model���ֽӿ�
     */	
	Controller controller;
    /**
     * ���캯��
     */
	
	InputMethodManager input;
//	MyText myEdit;
	public RoomView(RoomActivity activity, MyText edit) {
		super(activity);
//		myEdit = edit;
		this.activity = activity;
		getHolder().addCallback(this);
		input=(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		
	}
	
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        // TODO Auto-generated method stub
        return new MyInputConnection(this,false);//super.onCreateInputConnection(outAttrs);
    }
    String inputString = "";
    public class MyInputConnection extends BaseInputConnection{

//        String inputString="";

        public MyInputConnection(View targetView, boolean fullEditor) { 
            super(targetView, fullEditor); 
            // TODO Auto-generated constructor stub 
        } 
        public boolean commitText(CharSequence text, int newCursorPosition){ 
            inputString=inputString+(String) text; 
            return true; 
        } 
        
    }

	
    /**
     * �����ʼ������ʼ����ť�����䣬ˢ֡�߳�
     */
	public void surfaceCreated(SurfaceHolder arg0) {
		paint=new Paint();
		paint.setAntiAlias(true);
		
		Constant.changeRadio();
		initBitmap();
        controller = Controller.getController();
        controller.setHandler(roomHandler);
        
        roomnumber = controller.getMyRoomNum();
        
        Log.d(Config.LOG_TAG, "get controller in room view : " + controller + " " + roomnumber);
        //Log.d(Config.LOG_TAG, "hall model in room view : " + controller.hallModel + " " + controller.hallModel.users);
        
		ready = new MyButton(btn_pic, btn_pic, Constant.BLOCK_WIDTH * 2, Constant.BLOCK_HEIGHT * 4.2f, "ready");
		exit = new MyButton(btn_pic, btn_pic, Constant.BLOCK_WIDTH * 4, Constant.BLOCK_HEIGHT * 4.2f, "exit");
		send = new MyButton(btn_pic, btn_pic, Constant.BLOCK_WIDTH * 6, Constant.BLOCK_HEIGHT * 4.2f, "send");
		chat = new MyButton(btn_pic, btn_pic, Constant.BLOCK_WIDTH * 5, Constant.BLOCK_HEIGHT * 4.2f, "");
		express = new MyButton(btn_pic, btn_pic, Constant.BLOCK_WIDTH * 6, Constant.BLOCK_HEIGHT * 3f, "");
		float location[][] = {{2.0f, 1.0f},{2.0f, 2.5f},{4.0f, 1.0f},{4.0f, 2.5f}};

		for(int i = 0; i < 4; i++)
		{
		    person[i] = new MyPerson(person_pic, person_pic, Constant.BLOCK_WIDTH * location[i][0], Constant.BLOCK_HEIGHT * location[i][1]);
		}
		Log.d(Config.LOG_TAG, "create thread here");
		createAllThreads();
		startAllThreads();

    }
	
    /**
     * �����û������¼�
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		pointx=(int) event.getX();
		pointy=(int) event.getY();
    	switch(event.getAction())
    	{
    	case MotionEvent.ACTION_DOWN:
    		if(ready.isPointInRect(pointx, pointy)){
    			if (ready.isOnflag() == true) {
    			//	input.showSoftInput(this,0);
    				if (ready.getInfo() == "ready")
    					ready();
    				else
    					unready();
    				
    			}
    			ready.setOnflag(false);
    			
    		}
    		else if(exit.isPointInRect(pointx, pointy)){
    			if (exit.isOnflag() == true) leave();
    			exit.setOnflag(false);
    		}
    		else if(chat.isPointInRect(pointx, pointy)){
    			input.showSoftInput(this, 0);
    		}
    		else if(send.isPointInRect(pointx, pointy)){
    			controller.sendTalkMessage(inputString);
    			inputString = "";
    		}
    		else
    		{
    			
    		    for(int i = 0; i < 4; i ++)
    		    {
    		        if(person[i].isPointInRect(pointx, pointy))
    		        {
    		        	
    		        	
    		        	String info = "person level:" + person_player[i];
    		        	Toast.makeText(activity, info, Toast.LENGTH_SHORT).show();
    		        }
    		    }	
    		}
    		break;
    	case MotionEvent.ACTION_UP:
		    for(int i = 0; i < 4; i ++)
		    {
		    //    person[i].setOnflag(false);
		    }	
    		break;    	
    	}
		return true;
	}	

/**
 * �滭������ư�ť������
 */

    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawColor(Color.WHITE);
		
		canvas.drawBitmap(back_pic, 0, 0, paint);
		
		ready.drawSelf(canvas, paint);
		exit.drawSelf(canvas, paint);
		chat.drawSelf(canvas, paint);
		send.drawSelf(canvas, paint);
		express.drawSelf(canvas, paint);
		for(int i = 0; i < 4; i++)
		{
		    person[i].drawSelf(canvas, paint);
		}
	}
    /**
     * ���·�����Ϣ
     */
    public void updateRoomInfo()
    {
        for(int i = 0; i < 4; i++){
        	person[i].SetInfo(person_name[i], person_state[i]);
        }
    }
    
/**
 * ����Controller�ӿڣ�׼����ȡ��׼�����˳�
 *
 */

    public void ready()
    {
        controller.ready();
    }
    
    public void unready(){
    	controller.unready();
    }
    
    public void leave(){
    	controller.leave();
    }
    
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}


	public void surfaceDestroyed(SurfaceHolder arg0) {
	}
/**
 * ��������ٻ滭�߳�
 *
 */
    void createAllThreads()
	{
		drawThread = new DrawThread(this);
	}
	void startAllThreads()
	{
		drawThread.setFlag(true);     
		drawThread.start();
	}
	void stopAllThreads()
	{
		drawThread.setFlag(false);       
	}

    /**
     * ��ʼ��λͼ��Ϣ
     */
	public void initBitmap(){

		back_pic = PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.chooseground), 
				Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
		person_pic = PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.room), 
				Constant.BLOCK_WIDTH * 2f, Constant.BLOCK_HEIGHT * 1f);
		btn_pic = PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.btn_button), 
				Constant.BLOCK_WIDTH * 1.2f, Constant.BLOCK_HEIGHT * 0.5f);
		
	}
	
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
		}
		finally
		{
			if(canvas!=null)
			{
				this.getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}
	
	
/**
 * ��÷����ڵĿ��գ���ʾ�����Ϣ
 *
 */
	void getSnapShot(){
		JSONObject snapShot;
		try {
			Log.d(Config.LOG_TAG, "roomnum " + roomnumber);
			String snap = controller.getHallSnapShot(roomnumber, roomnumber);
			if (snap != null)
			{
				//Log.d(Config.LOG_TAG, snap);
				snapShot = new JSONObject(snap);		
				//Log.d(tag, msg)
				System.out.println(snapShot);
				@SuppressWarnings("rawtypes")
				JSONArray rooms = snapShot.getJSONArray ("rooms");
				
				for (int i = 0; i < rooms.length(); i ++)
				{
					JSONObject room = rooms.getJSONObject(i);
					String status = room.getString("status");
					JSONArray users = room.getJSONArray("users");
					String players = "";
					for (int j = 0; j < 4; j++){
						this.person_name[j] = "player" + j;
						this.person_state[j] = "empty";
					}
					for(int j = 0; j < users.length(); j++)
					{
						Log.v("RoomSnap", "room");
						JSONObject player = users.getJSONObject(j);
						String playername = player.getString("user");
						JSONObject playerdetails = player.getJSONObject("details");
						String playerinfo = playerdetails.getString("level");
						String playerstate = player.getString("ready");
						int pos = player.getInt("pos");
						this.person_name[pos] = playername;
						this.person_player[pos] = playerinfo;
						this.person_state[pos] = playerstate;
					}
					
				}
			}
			else
			{
				//Log.d(Config.LOG_TAG, "null pointer");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}		
	
	}
	String talk;
	void handleTalk(){
		String msg = controller.getCurrentMessage(3);
		try{
			JSONArray arr = new JSONArray(msg);
			talk = "";
			for (int i = 0; i < arr.length(); i++){
				JSONObject obj = arr.getJSONObject(i);
				String user = ""; String content = "";
				user = obj.getString("speaker");
				content = obj.getString("message");
				talk = talk + user + ": " + content + '\n';
			}
		}
		catch(Exception e){
			
		}
		
	}
	
	public void openEditAgent(){

//		myEdit.requestFocus();

//		InputMethodManager input  = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE); 

//		input.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

		}


	
/**
 * ����viewer�յ���handler��Ϣ��׼����ȡ��׼�����˳��ĳɹ���ʧ��
 */
	Handler roomHandler = new Handler()
	{
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) 
		{
		    JSONObject hallStatus;
			try {
				hallStatus = new JSONObject((String) msg.obj);
			
				if (hallStatus.getString("type").equals("ready"))
				{
					String status = "";
				
				    if (hallStatus.has("status"))
				    	status = hallStatus.getString("status");
				    if (status != null && status.equals("Success"))
				    {
				    	Toast.makeText(activity, "successfuly ", Toast.LENGTH_SHORT).show();
				    	ready.setInfo("unready");
				    	ready.setOnflag(true);
				    }
				    else
				    {
				    	Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show(); 
				    }
				}
				else if (hallStatus.getString("type").equals("unready")){
					String status = "";
					
				    if (hallStatus.has("status"))
				    	status = hallStatus.getString("status");
				    if (status != null && status.equals("Success"))
				    {
				    	Toast.makeText(activity, "successfuly ", Toast.LENGTH_SHORT).show();
				    	ready.setInfo("ready");
				    	ready.setOnflag(true);
				    }
				    else
				    {
				    	Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show(); 
				    }
				}
				else if (hallStatus.getString("type").equals("leave")){
					String status = "";
					Log.v("myinfo", "in roomview get leave info");
				    if (hallStatus.has("status"))
				    	status = hallStatus.getString("status");
				    if (status != null && status.equals("Success"))
				    {
				    	Toast.makeText(activity, "successfuly ", Toast.LENGTH_SHORT).show();
				    	stopAllThreads();
				    	activity.gotoChooseView();
				    }
				    else
				    {
				    	Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show(); 
				    }
				}
				else if (hallStatus.getString("type").equals("game")){
					String status = "";
					if (hallStatus.has("status"))
						status = hallStatus.getString("status");
					if (status != null && status.equals("prepare")){
						stopAllThreads();
						activity.gotoGame();
					}
				}
			}
			catch(JSONException e)
			{
			//that means signin fail			
			}
		}
	 };
	 
	 private static int drawThreadInstanceCount = 0;
	 
	DrawThread getDrawThreadInstance()
	{
		DrawThread instance = null;
		if (drawThreadInstanceCount == 0)
		{
			drawThreadInstanceCount ++;
			instance = new DrawThread(this);
		}
		return instance;
	}
	 
	/**
	 * �滭�̣߳�ÿ500msˢ��һ��
	 */
	public class DrawThread extends Thread{
		private boolean flag = true;	
		private int sleepSpan = 500;
		
		public DrawThread(RoomView fatherView){
		}
		public void run(){
	        while (this.flag) {
	            try{
	            	Thread.sleep(sleepSpan);
	            }
	            catch(Exception e){
	            	e.printStackTrace();
	            }
	            
	            chat.setInfo(inputString);
	            getSnapShot();
	            updateRoomInfo();
	            handleTalk();
	            express.setInfo(talk);
	            repaint();
	        }
	        drawThreadInstanceCount --;
		}
		public void setFlag(boolean flag) {
			Log.d(Config.LOG_TAG, "flag: " + flag);
			this.flag = flag;
		}
	}
   
	
	@SuppressWarnings("deprecation")
    /**
     * ���?�ؼ��¼���Ӧ
     */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
			
			AlertDialog isExit = new AlertDialog.Builder(activity).create();
			
			isExit.setTitle("退出");	
			isExit.setMessage("是否退出游戏？");	
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);
			
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
			case AlertDialog.BUTTON_POSITIVE:
				System.exit(0);
				break;
			case AlertDialog.BUTTON_NEGATIVE:
				break;
			default:
				break;
			}
		}
	};	
		
}
