package com.example.client.view.myView;

import com.example.client.R;
import com.example.client.R.layout;
import com.example.client.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.R;
import com.example.client.R.id;
import com.example.client.R.layout;
import com.example.client.R.menu;
import com.example.client.controller.Controller;
import com.example.client.model.Config;
import com.example.client.view.myWidgets.MyText;
import com.example.client.view.others.Constant;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The activity class of room.
 * Set the RoomView and start.
 * Responsible for the transfer between the activities.
 * 
 */

public class RoomActivity extends Activity {
/**
 * RoomView成员，负责房间绘图
 */
	RoomView roomview = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	       
        //设置在游戏中音量调整变为媒体音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
        
        Log.d(Config.LOG_TAG, "create room activity here");
        gotoRoomView();//打开RoomView
		
	}

	/**
	 * 切换到游戏界面
	 */
    public void gotoGame(){ //
		Intent intent = new Intent();
		intent.setClass(RoomActivity.this, GameActivity.class);			
		Bundle bundle = new Bundle();
		bundle.putInt("type", 1);
		intent.putExtras(bundle);
		//close the activity before jump to the next one;
		finish();
		startActivity(intent);   
    }
    
    public void gotoChooseView(){
    	Intent intent = new Intent();
    	intent.setClass(RoomActivity.this, HallActivity.class);
    	Log.d(Config.LOG_TAG, "go to hall activity from room activity");
    	finish();
    	startActivity(intent);
    	
    }
        
	/**
	 * 绘制界面菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	/**
	 * 返回房间界面
	 */
	MyText mytext;
	public void gotoRoomView()
	{
		if(roomview==null){
			Log.d(Config.LOG_TAG, "new room view here");
			roomview=new RoomView(this, mytext);
    	}
//		setContentView(mytext);
		Log.d(Config.LOG_TAG, "set content view room view here");
    	setContentView(roomview);
        roomview.requestFocus();//获得焦点
    	roomview.setFocusableInTouchMode(true);//焦点可触
	}
  
}
