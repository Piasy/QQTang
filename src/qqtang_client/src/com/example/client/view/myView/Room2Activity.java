package com.example.client.view.myView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.R;
import com.example.client.R.id;
import com.example.client.R.layout;
import com.example.client.controller.Controller;
import com.example.client.util.SystemUiHider;
import com.example.client.view.myView.RoomView.DrawThread;
import com.example.client.view.others.Constant;
import com.example.client.view.others.PicLoadUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 房间的Activity版本
 * 实现功能与第一版相同
 * 
 * @see SystemUiHider
 */
public class Room2Activity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	Bitmap person_pic[];
	EditText input, msglist, personinfo[]=new EditText[4];
	Button person[]=new Button[4], ready, exit, send;
	Controller controller;
	int roomnumber;
	
	int isready=0;
	String person_name[] = new String[]{"player 1", "player 2", "player 3", "player 4"};
	String person_player[] = new String[]{"play","play","play","play"};
	String person_state[] = new String[]{"empty","empty","empty","empty"};
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			String message = (String)msg.obj;
			if (message.equals("refresh")){
	            getSnapShot();
	            updateRoomInfo();
	            handleTalk();
	            msglist.setText(talk);
			}
		}
	};
	public Handler getHandler(){
		return this.handler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_room2);
		
		
		//person = new Button [4];
		
		person_pic = new Bitmap[]{
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.p11), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.t11), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.s11), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(BitmapFactory.decodeResource(this.getResources(), R.drawable.v11), 
						Constant.BLOCK_WIDTH, Constant.BLOCK_HEIGHT)
			};
		
		controller = Controller.getController();
		controller.setHandler(roomHandler);
		roomnumber = controller.getMyRoomNum();
		
			
		person[0] = (Button) findViewById(R.id.button1);
		person[1] = (Button) findViewById(R.id.button2);
		person[2] = (Button) findViewById(R.id.button3);
		person[3] = (Button) findViewById(R.id.button4);
		ready = (Button) findViewById(R.id.button5);
		exit = (Button) findViewById(R.id.button6);
		send = (Button) findViewById(R.id.button7);
		input = (EditText) findViewById(R.id.editText2);
		msglist = (EditText) findViewById(R.id.editText1);
		personinfo[0] = (EditText) findViewById(R.id.editText3);
		personinfo[1] = (EditText) findViewById(R.id.editText4);
		personinfo[2] = (EditText) findViewById(R.id.editText5);
		personinfo[3] = (EditText) findViewById(R.id.editText6);
		
		ready.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
    			if (isready == 0)
    				ready();
    			else
    				unready();
    			ready.setEnabled(false);
			}
		}
		);
		
		exit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (exit.isEnabled() == true)
					leave();
				exit.setEnabled(false);
			}
		}
		);
		
		send.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				controller.sendTalkMessage(input.getText().toString());
    			input.setText("");
			}
		}
		);
		
		person[0].setOnClickListener(new Button.OnClickListener() {
		@Override
			public void onClick(View arg0) {
				String info = "person level:" + person_player[0];
		       	Toast.makeText(Room2Activity.this, info, Toast.LENGTH_SHORT).show();
			}
		}
		);
		person[1].setOnClickListener(new Button.OnClickListener() {
		@Override
			public void onClick(View arg0) {
				String info = "person level:" + person_player[1];
		       	Toast.makeText(Room2Activity.this, info, Toast.LENGTH_SHORT).show();
			}
		}
		);
		person[2].setOnClickListener(new Button.OnClickListener() {
		@Override
			public void onClick(View arg0) {
				String info = "person level:" + person_player[2];
		       	Toast.makeText(Room2Activity.this, info, Toast.LENGTH_SHORT).show();
			}
		}
		);
		person[3].setOnClickListener(new Button.OnClickListener() {
		@Override
			public void onClick(View arg0) {
				String info = "person level:" + person_player[3];
		       	Toast.makeText(Room2Activity.this, info, Toast.LENGTH_SHORT).show();
			}
		}
		);
		
		createAllThreads();
		startAllThreads();
		
//		final View controlsView = findViewById(R.id.fullscreen_content_controls);
//		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.


		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
//		findViewById(R.id.dummy_button).setOnTouchListener(
//				mDelayHideTouchListener);
	}
	
    public void ready()
    {
        controller.ready();
        Log.d("fuck", "readyyes");
    }
    
    public void unready(){
    	controller.unready();
    }
    
    public void leave(){
    	controller.leave();
    }
    
	String talk;
	void handleTalk(){
		String msg = controller.getCurrentMessage(6);
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
	

	
	
	void getSnapShot(){
		JSONObject snapShot;
		try {
			snapShot = new JSONObject(controller.getHallSnapShot(roomnumber, roomnumber));		
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
					this.person_name[j]="player " + (j+1);
					this.person[j].setText(person_name[j]);
					this.person_state[j] = "empty";
					this.person_player[j] = "empty";
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}		
	
	}
	
	
    public void gotoGame(){ //
		Intent intent = new Intent();
		intent.setClass(Room2Activity.this, GameActivity.class);			
		Bundle bundle = new Bundle();
		bundle.putInt("type", 1);
		intent.putExtras(bundle);
		stopAllThreads();
		//close the activity before jump to the next one;
		finish();
		startActivity(intent);   
    }
    
    public void gotoChooseView(){
    	Intent intent = new Intent();
    	intent.setClass(Room2Activity.this, HallActivity.class);
    	stopAllThreads();
    	finish();
    	startActivity(intent);
    	
    }
	
	
	@SuppressLint("HandlerLeak")
	Handler roomHandler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
		    JSONObject hallStatus;
			try {
				hallStatus = new JSONObject((String) msg.obj);
			
				if (hallStatus.getString("type").equals("ready"))
				{
					String status = "";
					isready = 1;
				
				    if (hallStatus.has("status"))
				    	status = hallStatus.getString("status");
				    if (status != null && status.equals("Success"))
				    {
				    	Toast.makeText(Room2Activity.this, "successfuly ", Toast.LENGTH_SHORT).show();
				    	ready.setText("unready");
				    	ready.setEnabled(true);
				    }
				    else
				    {
				    	Toast.makeText(Room2Activity.this, "failed", Toast.LENGTH_SHORT).show(); 
				    }
				}
				else if (hallStatus.getString("type").equals("unready")){
					String status = "";
					isready = 0;
					
				    if (hallStatus.has("status"))
				    	status = hallStatus.getString("status");
				    if (status != null && status.equals("Success"))
				    {
				    	Toast.makeText(Room2Activity.this, "successfuly", Toast.LENGTH_SHORT).show();
				    	ready.setText("ready");
				    	ready.setEnabled(true);
				    }
				    else
				    {
				    	Toast.makeText(Room2Activity.this, "failed", Toast.LENGTH_SHORT).show(); 
				    }
				}
				else if (hallStatus.getString("type").equals("leave")){
					String status = "";
					
				    if (hallStatus.has("status"))
				    	status = hallStatus.getString("status");
				    if (status != null && status.equals("Success"))
				    {
				    	Toast.makeText(Room2Activity.this, "successfuly ", Toast.LENGTH_SHORT).show();
				    	gotoChooseView();
				    }
				    else
				    {
				    	Toast.makeText(Room2Activity.this, "failed", Toast.LENGTH_SHORT).show(); 
				    }
				}
				else if (hallStatus.getString("type").equals("game")){
					String status = "";
					if (hallStatus.has("status"))
						status = hallStatus.getString("status");
					if (status != null && status.equals("prepare")){
						gotoGame();
					}
				}
			}
			catch(JSONException e)
			{
			//that means signin fail			
			}
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
			
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			
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
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			case AlertDialog.BUTTON_NEGATIVE:
				break;
			default:
				break;
			}
		}
	};	
	
	@SuppressWarnings("deprecation")
	public void updateRoomInfo()
    {
		if (isready == 1){
			ready.setText("unready");
		}
		else{
			ready.setText("ready");
		}
        for(int i = 0; i < 4; i++){
        	person[i].setText("");
        	personinfo[i].setText(person_name[i] + '\n' + person_state[i]);
        	if (person_state[i].equals("empty")){
        		person[i].setBackgroundDrawable(null);
        	}
        	else
        	{
				Drawable temp = new BitmapDrawable(person_pic[i]);
        		person[i].setBackgroundDrawable(temp);
        	}
        }
    }
	DrawThread drawThread;
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
    
	public class DrawThread extends Thread{
		private boolean flag = true;	
		private int sleepSpan = 500;
		private Room2Activity ctx;
		public DrawThread(Room2Activity fatherView){
			ctx = fatherView;
		}
		public void setFlag(boolean b) {
			flag = b;
		}
		public void run(){
	        while (this.flag) {
	            try{
	            	Thread.sleep(sleepSpan);
	            	String str = "refresh";
	            	Message msg = Message.obtain();
	            	msg.obj = str;
	            	ctx.handler.sendMessage(msg);
	            }
	            catch(Exception e){
	            	e.printStackTrace();
	            }

	        }
		}
	};
	
}
