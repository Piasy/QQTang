package com.example.client.view.myGameStruct;

import java.util.Vector;

import com.example.client.R;
import com.example.client.view.myThread.ManFootThread;
import com.example.client.view.myView.GameActivity;
import com.example.client.view.others.Constant;
import com.example.client.view.others.PicLoadUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;


public class MyPropCollect extends View {
	GameActivity activity;
	Vector<MyProp>  prop_vec = new Vector<MyProp>();
	Bitmap prop_pic[]; //位图
    int timer = 0;
	public MyPropCollect(GameActivity _activity) {
       super(_activity);
       activity = _activity;
	   initBitmap();
	}

	//绘制的方法
	public void drawSelf(Canvas canvas,Paint paint) { //绘制的方法，继承该类的方法相同
		float x, y;
		int type = 0;
		int pic_index;
		pic_index = MyProp.index;

		timer++;
		if (timer >= 5) {
		    timer = 0;
		    MyProp.changeIndex();
		}
        for (int i = 0; i < prop_vec.size(); i++) {
			x = prop_vec.elementAt(i).col
					* Constant.GAME_BLOCK_WIDTH;
			y = prop_vec.elementAt(i).row
					* Constant.GAME_BLOCK_HEIGHT;

            type = prop_vec.elementAt(i).type;
            if(type > 4) type = 4;
			
			canvas.drawBitmap(
			prop_pic[2 * type + MyProp.index],
			x, y, paint);
			
        }
	}

	public void set(Vector<MyProp> _prop_vec) {
		prop_vec = _prop_vec;
	}

	public void initBitmap() {
		prop_pic = new Bitmap[]{
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop_1),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop1_0),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop_2),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop2_0),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop_3),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop3_0),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop_4),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop4_0),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop_5),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT),
						PicLoadUtil.scaleToFit(
								BitmapFactory.decodeResource(
								this.getResources(),
								R.drawable.prop5_0),
								Constant.GAME_BLOCK_WIDTH,
								Constant.GAME_BLOCK_HEIGHT)
		};
	}
}