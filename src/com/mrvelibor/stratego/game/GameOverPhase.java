package com.mrvelibor.stratego.game;

import java.awt.Graphics2D;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.effects.Effect;
import com.mrvelibor.stratego.effects.fireworks.FireworksEffect;
import com.mrvelibor.stratego.effects.glass.GlassEffect;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.input.Mouse;
import com.mrvelibor.stratego.units.Unit;

public class GameOverPhase extends GamePhase {
	
	public static final int LOSE = -1, DRAW = 0, WIN = 1;
	
	private final String mMessage;
	
	private final Effect mEffect;
	
	private final boolean mSinglePlayer;
	
	public GameOverPhase(int victory, boolean singlePlayer) {
		super();
		mSinglePlayer = singlePlayer;
		
		switch(victory)
			{
			case WIN:
				mMessage = "VICTORY!";
				mEffect = new FireworksEffect();
				break;
			
			case DRAW:
				mMessage = "DRAW...";
				mEffect = new Effect() {
					@Override
					public void draw(Graphics2D g) {}
					
					@Override
					public boolean effect(int x, int y) {
						return false;
					}
				};
				break;
			
			case LOSE:
				mMessage = "YOU LOST!";
				mEffect = new GlassEffect();
				break;
				
			default:
				throw new IllegalArgumentException();
			}
		
	}
	
	@Override
	public void onSet(GamePhase previous) {
		StrategoGame.sWindow.controls.setActionButton(true, "BACK", Screen.COLOR_ACTION);
		StrategoGame.sWindow.controls.setColors(Unit.TEAM_COLOR[sTeam], Screen.COLOR_TEXT);
		StrategoGame.sWindow.controls.setOtherButton(true, mMessage, Screen.COLOR_BUTTON_BG);
	}
	
	@Override
	public void draw(Graphics2D g) {
		sBoard.draw(g);
		sTap.draw(g);
		mEffect.draw(g);
	}
	
	@Override
	public void leftClick() {
		int x = Mouse.getX(), y = Mouse.getY();
		if(!mEffect.effect(x, y)) sTap.effect(x, y);;
	}
	
	@Override
	public void rightClick() {}
	
	@Override
	public void actionButton() {
		StrategoGame.setPhase(mSinglePlayer ? new MenuPhase() : new PlacementPhase(false));
	}
	
	@Override
	public void otherButton() {}

	@Override
	protected boolean serverCommand(String line) {
		return false;
	}
	
}
