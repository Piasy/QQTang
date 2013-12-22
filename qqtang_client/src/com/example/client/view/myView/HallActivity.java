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
/**.
 * 大厅控制Activity类
 */
public class HallActivity extends Activity {
    /**.
     * ChooseView引用，负责选择房间界面绘制
     */
	ChooseView chooseView;
    /**.
     * 构造函数
     */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //游戏过程中只允许调整多媒体音量，而不允许调整通话音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); //去掉标头
        this.setRequestedOrientation(
        		ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //强制横屏
        Log.d(Config.LOG_TAG, "create hall activity");
        gotoChooseView(); //进入"欢迎界面"
	}
    /**.
     * 进入房间选择界面
     */
    public void gotoChooseView() {
    	if (chooseView == null) {
			chooseView = new ChooseView(this);
			if (chooseView.activity != null)
				Log.d(Config.LOG_TAG, "hall activity in chooseview null? " + chooseView.activity);
			Log.d(Config.LOG_TAG, "hall activity in chooseview null? " + (chooseView.activity == null));
    	}
    	setContentView(chooseView);
        chooseView.requestFocus(); //获取焦点
    	chooseView.setFocusableInTouchMode(true); //设为可触控
    }
    /**.
     * 跳转至房间Activity
     */
    public void gotoRoom() {
		Intent intent = new Intent();
		intent.setClass(HallActivity.this, RoomActivity.class);
		finish();
		startActivity(intent);
		Log.d(Config.LOG_TAG, "Hall to Room here");
    }

    public void gotoGame() {
		Intent intent = new Intent();
		intent.setClass(HallActivity.this, GameActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("type", 0);
		intent.putExtras(bundle);
		finish();
		startActivity(intent);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
    /**.
     * 处理各个SurfaceView发送的消息
     */
	Handler hallHandler = new Handler() {
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        	case 0:
        		//goToChooseView();
        		break;
        	case 1:
        		//goToRoomView();
        		break;
			default:
				break;
        	}
        }
	};
}