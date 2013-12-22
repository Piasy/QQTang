package com.example.client.view.myView;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.R;
import com.example.client.R.id;
import com.example.client.R.layout;
import com.example.client.R.menu;
import com.example.client.controller.Controller;
import com.example.client.model.Config;
import com.example.client.view.others.Constant;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

/*
 * 登录界面
 */
public class MainActivity extends Activity {
	EditText name, password;
	Button login, reg;
	Controller controller;
	/**.
	 * 绘制登录界面，配置Controller
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //游戏过程中只允许调整多媒体音量，而不允许调整通话音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉标题
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); //去掉标头
        this.setRequestedOrientation(
        		ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //强制横屏
		setContentView(R.layout.activity_main);
		//获取分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //给常量类中的屏幕高和宽赋值
        if(dm.widthPixels > dm.heightPixels) {
        	Constant.SCREEN_WIDTH = dm.widthPixels;
        	Constant.SCREEN_HEIGHT = dm.heightPixels;
        } else {
        	Constant.SCREEN_HEIGHT = dm.widthPixels;
        	Constant.SCREEN_WIDTH = dm.heightPixels;
        }
		name = (EditText) findViewById(R.id.text_name);
		password = (EditText) findViewById(R.id.text_password);
		login = (Button) findViewById(R.id.btn_login);
		reg = (Button) findViewById(R.id.btn_reg);
		//取得controller的连接
		controller = Controller.getController();
		controller.setHandler(signinHandler);
		login.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			    String username = name.getText().toString();
			    String pass = password.getText().toString();
				String target = "hall-server1";
			    controller.signin(username, pass, target);
				//gotoChooseView();
			}
		}
		);
		reg.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gotoReg();
			}
		}
		);
	}
	/*.
	 * 切换大厅界面
	 */
    public void gotoChooseView() {
		Intent intent = new Intent();
		Log.d(Config.LOG_TAG, "go to hall activity from main activity");
		intent.setClass(MainActivity.this, HallActivity.class);
		finish();
		startActivity(intent);
		
    }
    /*.
	 * 切换注册界面
	 */
    public void gotoReg() {
    	Intent intent = new Intent();
    	intent.setClass(MainActivity.this, RegActivity.class);
    	finish();
    	startActivity(intent);
    }
	/**.
	 * 设置界面菜单
	 *
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	/**.
	 * 设置登录界面Handler，接收Controller信息
	 */
	Handler signinHandler = new Handler() {
		public void handleMessage(Message msg) {
		    JSONObject signinStatus;
			try {
				signinStatus = new JSONObject((String) msg.obj);
			if (signinStatus.getString("type").equals("signin")) {
			    String status = "";
				    if (signinStatus.has("status")) {
				        status =
				        signinStatus.getString("status");
				    }
					if (status != null
						&& status.equals("Success")) {
					    Toast.makeText(
					    MainActivity.this,
					    status,
					    Toast.LENGTH_SHORT).show();
					    gotoChooseView();
					} else {
						Toast.makeText(
						MainActivity.this,
						status,
						Toast.LENGTH_SHORT).show();
						//gotoChooseView();
					}
				}
			}  catch (JSONException e) {
				Toast.makeText(
				MainActivity.this,
				"execption",
				Toast.LENGTH_SHORT
				).show();
			}
		}
	 };
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			// 创建退出对话框
			AlertDialog isExit = new
				AlertDialog.Builder(this).create();
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
	/*
	 * 设置返回键对话框的内容
	 *
	 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
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
