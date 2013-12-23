package com.example.client.view.myThread;

import com.example.client.view.myView.GameView;
/**
 * 单机版游戏测试类,之后不再使用
 */
public class KeyThread extends Thread {
	GameView gameView;
	private boolean keyFlag=false;
	
	public KeyThread(GameView gameView)
	{
		this.gameView=gameView;
	}
	@Override
	public void run()
	{
		while(isKeyFlag())
		{
			try
			{
				Thread.sleep(20);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			gameView.repaint();
		}
	}
	
	public boolean isKeyFlag() {
		return keyFlag;
	}
	public void setKeyFlag(boolean keyFlag1) {
		this.keyFlag = keyFlag1;
	}
	
}
