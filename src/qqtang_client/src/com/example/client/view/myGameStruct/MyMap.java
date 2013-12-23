package com.example.client.view.myGameStruct;

import com.example.client.R;
import com.example.client.view.myView.GameActivity;
import com.example.client.view.others.Constant;
import com.example.client.view.others.PicLoadUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class MyMap extends View {
	GameActivity activity;
    int ROW, COL;
    int mapdata[][];
	Bitmap[] back_pic; //±≥æ∞Õº∆¨
	public MyMap(GameActivity _activity) {
		super(_activity);
		activity = _activity;
		ROW = Constant.GAME_ROW;
		COL = Constant.GAME_COL;
		mapdata = new int[ROW][COL];
		initBitmap();
	}
	public void drawSelf(Canvas canvas,Paint paint) { //ªÊ÷∆±≥æ∞Õº∆¨
		for (int i = 0; i < ROW; i ++) {
			for (int j = 0; j < COL; j ++) {
				if (mapdata[i][j] >= 0 && mapdata[i][j] < 10) {
					canvas.drawBitmap(
						back_pic[0],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 10 && mapdata[i][j] < 20) {
					canvas.drawBitmap(
						back_pic[1],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 20 && mapdata[i][j] < 30) {
					canvas.drawBitmap(
						back_pic[2],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 30 && mapdata[i][j] < 40) {
					canvas.drawBitmap(
						back_pic[3],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 40 && mapdata[i][j] < 50) {
					canvas.drawBitmap(
						back_pic[4],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 50 && mapdata[i][j] < 60) {
					canvas.drawBitmap(
						back_pic[5],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 60 && mapdata[i][j] < 70) {
					canvas.drawBitmap(
						back_pic[6],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 70 && mapdata[i][j] < 80) {
					canvas.drawBitmap(
						back_pic[7],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 80 && mapdata[i][j] < 90) {
					canvas.drawBitmap(
						back_pic[8],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 90 && mapdata[i][j] < 100) {
					canvas.drawBitmap(
						back_pic[9],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 100 && mapdata[i][j] < 110) {
					canvas.drawBitmap(
						back_pic[10],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else if (mapdata[i][j] >= 110 && mapdata[i][j] < 120) {
					canvas.drawBitmap(
						back_pic[11],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
				else {
					canvas.drawBitmap(
						back_pic[0],
						j * Constant.GAME_BLOCK_WIDTH,
						i * Constant.GAME_BLOCK_HEIGHT,
						paint);
				}
			}
		}
	}

	public void set(int[][] _map) {	
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				mapdata[i][j] = _map[i][j];
			}
		}
	}

	public void initBitmap(){
		//±≥æ∞Õº∆¨
		back_pic=new Bitmap[]{
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.grass0),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.grass1),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.grass2),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.grass3),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground4),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground5),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground6),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground7),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground8),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground9),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),									
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground10),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground11),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground11),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),
			PicLoadUtil.scaleToFit(
					BitmapFactory.decodeResource(
					this.getResources(),
					R.drawable.ground11),
					Constant.GAME_BLOCK_WIDTH,
					Constant.GAME_BLOCK_HEIGHT),									
		};
	}
}