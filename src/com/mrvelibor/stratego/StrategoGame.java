package com.mrvelibor.stratego;

import java.util.ArrayList;

import com.mrvelibor.stratego.game.GamePhase;
import com.mrvelibor.stratego.game.MenuPhase;
import com.mrvelibor.stratego.game.OnlinePlayPhase;
import com.mrvelibor.stratego.info.Settings;
import com.mrvelibor.stratego.input.Mouse;

public class StrategoGame implements Runnable {
	
	public static final String TITLE = "Stratego";
	
	public static final int TARGET_UPS = 30;
	
	private static boolean sRunning = false;
	
	private StrategoGame() {}
	
	private static synchronized void start() {
		if(sRunning) throw new IllegalStateException("Already running.");
		sRunning = true;
	}
	
	public static synchronized void stop() {
		sRunning = false;
	}
	
	public static final Settings sSettings = new Settings("strategoprefs.cfg");
	
	public static final Window sWindow = new Window(StrategoGame.TITLE);
	
	private static GamePhase sGame;
	
	private static boolean sUnlimitedUPS = false;
	
	private static long sTotalUpdates = 0;
	private static int sUpdatesPerSecond = 0;
	
	public static long getTotalUpdates() {
		return sTotalUpdates;
	}
	
	public static int getUpdatesPerSecond() {
		return sUpdatesPerSecond;
	}
	
	private static boolean sTimer = false;
	private static long sTimeoutTime, sWarningTime;
	
	public synchronized static void setTimer(int timeoutInSecs, int warnBeforeSecs) {
		sTimeoutTime = sTotalUpdates + timeoutInSecs * TARGET_UPS;
		sWarningTime = sTimeoutTime - warnBeforeSecs * TARGET_UPS;
		sTimer = true;
	}
	
	public static void cancelTimer() {
		sTimer = false;
	}
	
	public synchronized static void setPhase(GamePhase phase) {
		cancelTimer();
		sUnlimitedUPS = phase.unlimitedFrames;
		sWindow.controls.setPhase(phase);
		sWindow.screen.setDrawable(phase);
		phase.onSet(sGame);
		sGame = phase;
		System.gc();
	}
	
	private static boolean sActionButton = false, sOtherButton = false;
	
	private static ArrayList<String> sServerLine = new ArrayList<String>();
	
	public static void actionButton() {
		sActionButton = true;
	}
	
	public static void otherButton() {
		sOtherButton = true;
	}
	
	public static void serverResponse(String line) {
		sServerLine.add(line);
	}
	
	public static void update() {
		if(!sServerLine.isEmpty()) {
			sGame.serverResponse(sServerLine.get(0));
			sServerLine.remove(0);
		}
		if(Mouse.leftClick()) {
			sGame.leftClick();
		}
		else if(Mouse.rightClick()) {
			sGame.rightClick();
		}
		else if(sActionButton) {
			sGame.actionButton();
			sActionButton = false;
		}
		else if(sOtherButton) {
			sGame.otherButton();
			sOtherButton = false;
		}
		Mouse.clear();
		
		if(sTimer && sGame instanceof OnlinePlayPhase) {
			if(sTotalUpdates == sTimeoutTime) {
				((OnlinePlayPhase) sGame).timeUp();
				sTimer = false;
			}
			else if(sTotalUpdates == sWarningTime) {
				((OnlinePlayPhase) sGame).warning((int) (sTimeoutTime - sTotalUpdates) / TARGET_UPS);
				sWarningTime += TARGET_UPS;
			}
		}
		++sTotalUpdates;
	}
	
	public static void render() {
		sWindow.screen.repaint();
	}
	
	private static final double NANO_TIMER = 1000000000.0 / TARGET_UPS;
	
	@Override
	public void run() {
		start();
		
		double delta = 0;
		long lastTime = System.nanoTime();
		long now;
		int lastSecond = (int) (lastTime / 1000000000);
		int updatesLastSecond = 0;
		
		while(sRunning) {
			now = System.nanoTime();
			delta += sUnlimitedUPS ? 1 : (now - lastTime) / NANO_TIMER;
			lastTime = now;
			
			while(delta >= 1) {
				update();
				render();
				++updatesLastSecond;
				--delta;
			}
			
			int thisSecond = (int) (lastTime / 1000000000);
			if(thisSecond > lastSecond) {
				sUpdatesPerSecond = updatesLastSecond;
				updatesLastSecond = 0;
				lastSecond = thisSecond;
			}
			
			while(!sUnlimitedUPS && now - lastTime < NANO_TIMER) {
				// Thread.yield();
				try {
					Thread.sleep(1);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				now = System.nanoTime();
			}
		}
		
		stop();
	}
	
	public static void main(String[] args) {
		sWindow.controls.setPreferredSize(sWindow.controls.getSize());
		sWindow.client.setPreferredSize(sWindow.client.getSize());
		
		StrategoGame.setPhase(new MenuPhase());
		new Thread(new StrategoGame(), TITLE).start();
	}
	
}