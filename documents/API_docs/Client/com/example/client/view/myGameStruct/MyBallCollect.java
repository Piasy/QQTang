package com.example.client.view.myGameStruct;

import java.util.Vector;

import com.example.client.R;
import com.example.client.view.myThread.BallExplodeThread;
import com.example.client.view.myThread.BallThread;
import com.example.client.view.myView.GameActivity;
import com.example.client.view.others.Constant;
import com.example.client.view.others.PicLoadUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;


public class MyBallCollect extends View {
	GameActivity activity;
	Bitmap ball_pic[];//位图
	Bitmap splash_pic[];
	Vector<MyBall> ball_vec;
	int timer = 0;
	public MyBallCollect(GameActivity _activity) {
		super(_activity);
		activity = _activity;
		initBitmap();
		ball_vec = new Vector<MyBall>();
	}

	//绘制的方法
	public void drawSelf(Canvas canvas,Paint paint) {
		float x, y;
		int state;
		int bsplash[] = {0,0,0,0};
		timer++;
		if (timer >= 3) {
		    timer = 0;
		    MyBall.changeIndex();
		}
		for (int i = 0; i < ball_vec.size(); i++) {
			int bcol = ball_vec.elementAt(i).col;
			int brow = ball_vec.elementAt(i).row;
			for (int j = 0; j < 4; j++) {
				bsplash[j] = ball_vec.elementAt(i).splash[j];
			}
			int nbcol = bcol - bsplash[3];
			int nbrow = brow;
			for (int j = 0; j < (bsplash[1] + bsplash[3] + 1);
					j++) {
				x = nbcol * Constant.GAME_BLOCK_WIDTH;
				y = nbrow * Constant.GAME_BLOCK_HEIGHT;
				if (nbrow == brow && nbcol == bcol) {
					nbcol++;
					continue;
				}
				canvas.drawBitmap(splash_pic[0], x, y, paint);
				nbcol++;
			}
			nbcol = bcol;
			nbrow = brow - bsplash[2];
			for (int j = 0; j < (bsplash[0] + bsplash[2] + 1);
					j++) {

				x = nbcol * Constant.GAME_BLOCK_WIDTH;
				y = nbrow * Constant.GAME_BLOCK_HEIGHT;
				if (nbrow == brow && nbcol == bcol) {
					nbrow++;
					continue;
				}
				canvas.drawBitmap(splash_pic[1], x, y, paint);
				nbrow++;

			}
			x = bcol * Constant.GAME_BLOCK_WIDTH;
			y = brow * Constant.GAME_BLOCK_HEIGHT;
			state = ball_vec.elementAt(i).state;
			int index = 0;
			if (state == 0) {
				index = MyBall.index;
			}
			else {
				index = (9 + state / 10);
				if(index > 16) index = 16;
			}
			
			canvas.drawBitmap(ball_pic[index], x, y, paint);
		}

	}
    public void set(Vector<MyBall> _ball_vec) {
    	ball_vec = _ball_vec;
    }
	public void initBitmap() {
		ball_pic = new Bitmap[]{
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball0),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball01),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball02),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball03),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
					    R.drawable.ball04),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball03),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball02),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball01),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball0),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball05),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
					    R.drawable.ball05),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball06),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball06),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball07),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
					    R.drawable.ball07),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball08),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.ball08),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT)
		};

		splash_pic = new Bitmap[]{
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.splash),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT),
				PicLoadUtil.scaleToFit(
						BitmapFactory.decodeResource(
						this.getResources(),
						R.drawable.splash2),
						Constant.GAME_BLOCK_WIDTH,
						Constant.GAME_BLOCK_HEIGHT)
		};
	}
}