package com.example.client.view.myThread;

import com.example.client.view.myGameStruct.Ball;
/**
 * 单机版游戏测试类,之后不再使用
 */
public class BallThread extends Thread {
    Ball ball;

	private int sleepSpan = 60;//线程休息的时间
	
	public BallThread(Ball ball)//构造方法
	{
		this.ball = ball;
	
	}
	@Override
	public void run()
	{	
		
		try {
			for(int i = 0; i < 30; i++){
				ball.i = ball.i;
				sleep(sleepSpan);//休息2000毫秒		
			}
	
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		ball.explode();
		
	}
	
	
}
