package com.example.client.view.myView;

import com.example.client.model.Config;
import com.example.client.view.others.Constant;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


/**.
 * 游戏控制Activity类
 */
public class GameActivity extends Activity {
	GameView gameView;
	/**.
	 * GameplayView成员，负责游戏绘图
	 */	
	GameplayView gameplayView; //游戏界面的引用
	public boolean backgroundsoundFlag = true; //背景音乐开启的标志位，默认为开启
	/**.
	 * 构造函数，控制横屏
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(
         ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //强制横屏    	
        super.onCreate(savedInstanceState); //游戏过程中只允许调整多媒体音量，而不允许调整通话音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); //去掉标头

        Bundle bundle = this.getIntent().getExtras();
        int type = bundle.getInt("type");
        if (type == 0) {
            goToGameView();
        } else {
        	goToGameView2();
        }
    }

    public void goToLogin() {
		Intent intent = new Intent();
		intent.setClass(GameActivity.this, MainActivity.class);
		finish();
		startActivity(intent);
    }
    /**.
     * 进入测试用游戏界面
     */
	public void goToGameView() {
		if (gameView == null) {
			gameView = new GameView(this);
    	}
    	setContentView(gameView);
    	gameView.requestFocus(); //获取焦点
    	gameView.setFocusableInTouchMode(true); //设为可触控
	}
    /**.
     * 进入游戏界面
     */
	public void goToGameView2() {
		if (gameplayView == null) {
			gameplayView = new GameplayView(this);
    	}
    	setContentView(gameplayView);
    	gameplayView.requestFocus(); //获取焦点
    	gameplayView.setFocusableInTouchMode(true); //设为可触控
	}
    /**.
     * 	进入大厅界面
     */
	public void goToHall() {
		Intent intent = new Intent();
		intent.setClass(GameActivity.this, HallActivity.class);
		Log.d(Config.LOG_TAG, "go to hall activity from game activity");
		finish();
		startActivity(intent);
	}

    /**.
     * 	进入房间界面
     */
	public void goToRoom() {
		Intent intent = new Intent();
		intent.setClass(GameActivity.this, RoomActivity.class);
		finish();
		startActivity(intent);
	}
	
	
    /**.
     * 	处理各个SurfaceView发送的消息
     */
	Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
        	switch(msg.what) {
        	case 0:
        		goToGameView(); //进入游戏结束界面
        		break;
        	}
        }
	};
}