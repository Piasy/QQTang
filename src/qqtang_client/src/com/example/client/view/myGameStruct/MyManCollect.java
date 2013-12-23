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


public class MyManCollect extends View {
	GameActivity activity;
	Vector<MyMan>  man_vec = new Vector<MyMan>();
	Bitmap man_pic[][]; //位图
    int timer = 0;
	public MyManCollect(GameActivity _activity) {
       super(_activity);
       activity = _activity;
	   initBitmap();
	}
	//绘制的方法
	public void drawSelf(Canvas canvas,Paint paint) { //绘制的方法，继承该类的方法相同
		float x, y;
		int state;
		int type = 0;
		int pic_index;
		timer++;
		if (timer >= 5) {
		    timer = 0;
		    MyMan.changeIndex();
		}
        for (int i = 0; i < man_vec.size(); i ++) {
			x = man_vec.elementAt(i).col
					* Constant.GAME_BLOCK_WIDTH;
			y = man_vec.elementAt(i).row
					* Constant.GAME_BLOCK_HEIGHT;
			state = man_vec.elementAt(i).state;
			if (state < 100) {
				type = man_vec.elementAt(i).type;
				if(type > 3) type = 0;
	            pic_index = getPicIndex(i, state);
				//type = 1;
				//pic_index = 1;
				if (pic_index >= 0) {
					canvas.drawBitmap(
					man_pic[type][pic_index],
					x, y, paint);
				}
			}
        }
	}
	public void set(Vector<MyMan> _man_vec) {
		man_vec = _man_vec;
	}

	public int getPicIndex(int i, int state){
		int pic_index = 0;
		if (state == 0) {
			pic_index = 0;
			//pic_index = 1 + man_vec.elementAt(i).index;
		} else if (state == 1) {
			pic_index = 1 + man_vec.elementAt(i).index;
		} else if(state == 2 ) {
			pic_index = 3;
		} else if(state == 3) {
			pic_index = 4 + man_vec.elementAt(i).index;	
		} else if(state == 4) {
			pic_index = 6;
		} else if(state == 5) {
			pic_index = 7 + man_vec.elementAt(i).index;
		} else if(state == 6) {
			pic_index = 9;
		} else if(state == 7) {
			pic_index = 10 + man_vec.elementAt(i).index;
			//man_vec.elementAt(i).changeIndex();
		} else {
			pic_index = -1;
		}
		return pic_index;
	}
	public void initBitmap(){
		man_pic=new Bitmap[][]{
			{PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t11),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
				    R.drawable.t12),
				    Constant.GAME_BLOCK_WIDTH,
				    Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t13),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t21),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t22),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t23),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t31),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t32),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t33),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t41),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t42),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.t43),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT)},
			{PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u11),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u12),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u13),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u21),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u22),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u23),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u31),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u32),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u33),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u41),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u42),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.u43),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT)},
			{PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v11),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v12),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v13),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v21),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v22),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v23),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v31),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v32),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v33),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v41),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v42),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.v43),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT)},
			{PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s11),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s12),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s13),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s21),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s22),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s23),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s31),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s32),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s33),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s41),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s42),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.s43),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT)}
			};
	}
}