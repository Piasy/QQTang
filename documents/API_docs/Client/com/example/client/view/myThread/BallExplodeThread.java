package com.example.client.view.myThread;

import com.example.client.view.myGameStruct.Ball;
/**
 * 单机版游戏测试类,之后不再使用
 */
public class BallExplodeThread extends Thread {
    Ball ball;

	private int sleepSpan=60;//线程休息的时间
	
	public BallExplodeThread(Ball ball)//构造方法
	{
		this.ball = ball;
	
	}
	@Override
	public void run()
	{	
		
		try {
			for(int i = 0; i < 3; i ++){
				ball.i = i + 1;
				sleep(sleepSpan);//休息60毫秒	
			}
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		ball.exist = false;
		ball.map.mapdata[ball.row][ball.col] = 0;
		
	}
	
	
}
