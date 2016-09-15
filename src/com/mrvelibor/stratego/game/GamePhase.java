package com.mrvelibor.stratego.game;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.effects.tap.TapEffect;
import com.mrvelibor.stratego.graphics.Drawable;
import com.mrvelibor.stratego.online.GameCommands;

public abstract class GamePhase implements GameCommands, Drawable {
	
	protected static final TapEffect sTap = new TapEffect();
	
	protected static Board sBoard = new Board();
	
	protected static int sTeam = 0;
	
	public final boolean unlimitedFrames;
	
	public static void setTeam(int team) {
		sTeam = team;
	}
	
	public static int getTeam() {
		return sTeam;
	}
	
	protected GamePhase(boolean unlimitedFrames) {
		this.unlimitedFrames = unlimitedFrames;
	}
	
	protected GamePhase() {
		this(false);
	}
	
	public abstract void onSet(GamePhase previous);
	
	public abstract void leftClick();
	
	public abstract void rightClick();
	
	public abstract void actionButton();
	
	public abstract void otherButton();
	
	public final void serverResponse(String line) {
		if(line.startsWith(COMMAND_PING)) {
			String[] coords = line.substring(COMMAND_PING.length()).split(";");
			if(coords.length == 2) try {
				int x = Integer.parseInt(coords[0]), y = Integer.parseInt(coords[1]);
				sTap.ping(x, y);
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else if(line.startsWith(COMMAND_TEAM)) {
			char c = line.charAt(COMMAND_TEAM.length());
			setTeam(Character.getNumericValue(c));
		}
		else if(!serverCommand(line)) {
			StrategoGame.sWindow.client.wrongLine(line);
		}
	}
	
	protected abstract boolean serverCommand(String line);
	
	protected void sendPing(int x, int y) {
		if(sTap.ping(x, y)) StrategoGame.sWindow.client.ping(x, y);
	}
	
}
