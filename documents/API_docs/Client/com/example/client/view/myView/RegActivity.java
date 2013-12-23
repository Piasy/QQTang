package com.example.client.view.myView;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.R;
import com.example.client.R.id;
import com.example.client.R.layout;
import com.example.client.R.menu;
import com.example.client.R.string;
import com.example.client.controller.Controller;
import com.example.client.model.Config;
import com.example.client.view.others.Constant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**.
 *The activity of register
 *includes painting of register,
 * inform the controller of user information
 *  and transfer between the activities.
 */
public class RegActivity extends Activity {
	EditText name, nickname, password, repassword;
	Button register, regreturn;
	Controller controller;
	String username, nicknametxt, pass, repass;
	/**.
	 * 绘制注册页面，设置文本信息
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		name = (EditText) findViewById(R.id.reg_username);
		nickname = (EditText) findViewById(R.id.nickname);
		password = (EditText) findViewById(R.id.reg_password);
		repassword = (EditText) findViewById(R.id.reg_repassword);
		register = (Button) findViewById(R.id.reg_button);
		//regreturn = (Button) findViewById(R.id.reg_return);
		controller = Controller.getController();
		controller.setHandler(regHandler);
		register.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			    username = name.getText().toString();
			    nicknametxt = nickname.getText().toString();
			    pass = password.getText().toString();
			    repass = repassword.getText().toString();
				controller.signup(username,
						pass, repass, nicknametxt);
			}
		}
		);
		regreturn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gotoMainAct();
			}
		}
		);
	}
	/**.
	 * 转到大厅界面
	 */
    public void gotoChooseView() {
		Intent intent = new Intent();
		intent.setClass(RegActivity.this, HallActivity.class);
		Log.d(Config.LOG_TAG, "go to hall activity from reg activity");
		finish();
		startActivity(intent);
    }
    
    public void gotoMainAct(){
    	Intent intent = new Intent();
    	intent.setClass(RegActivity.this, MainActivity.class);
    	finish();
    	startActivity(intent);
    }
/**.
 * 控制游戏界面菜单
 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	/**.
	 * 注册后登入游戏
	 */
	public void signin() {
		Log.v("myinfo2", "ok0");
		controller.signin(username, pass, "hall-server1");
	}
	/**.
	 * 设置接收消息Handler
	 */
	Handler regHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg)  {
			Log.v("myinfo2", (String)msg.obj);
		    JSONObject signupStatus = null;
			try {
				signupStatus = new JSONObject((String) msg.obj);
				if (signupStatus.getString("type").equals("signup")) {
					String status = "";
					Log.v("myinfo2", "signup");
				    if (signupStatus.has("status")) {
				    	status =
				    	signupStatus.getString("status");
				    }
				    if (status != null
				    		&& status.equals("success")) {
				    	Log.v("myinfo2", "ok");
				    	Toast.makeText(
				    			RegActivity.this,
				    			status,
				    			Toast.LENGTH_SHORT
				    			).show();
				    	Log.v("myinfo2", "okk");
				    	signin();
				    	Log.v("myinfo2", "okkk");
				    } else {
				    	Toast.makeText(RegActivity.this, status, Toast.LENGTH_SHORT).show(); 
				    }
				}
				if (signupStatus.getString("type").equals("signin")) {
					String status = "";
					if (signupStatus.has("status")) {
						status = signupStatus.getString("status");
					}
					if (status != null && status.equals("Success")) {
						Toast.makeText(RegActivity.this,
								status,
								Toast.LENGTH_SHORT
								).show();
						gotoChooseView();
					}
					else{
						Toast.makeText(
								RegActivity.this,
								status,
								Toast.LENGTH_SHORT
								).show();
					}
				}
			}
			catch(JSONException e)
			{
				Toast.makeText(RegActivity.this, "execption", Toast.LENGTH_SHORT).show();
			}
		}
	 };
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
