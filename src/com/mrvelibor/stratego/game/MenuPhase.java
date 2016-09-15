package com.mrvelibor.stratego.game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.input.Mouse;
import com.mrvelibor.stratego.units.Unit;

public class MenuPhase extends GamePhase {
	
	public static final Image BACKGROUND, LOGO;
	static {
		BufferedImage back = null;
		BufferedImage logo = null;
		try {
			back = SpriteSheet.loadImage("board/background_normal.png");
			logo = SpriteSheet.loadImage("logo.png");
		} catch(IOException e) {
			JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString().toString(), "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		BACKGROUND = back;
		LOGO = logo;
	}
	
	private static final int LOGO_X = Screen.WIDTH / 4, LOGO_Y = Screen.HEIGHT / 5;
	
	@Override
	public void onSet(GamePhase previous) {
		StrategoGame.sWindow.controls.setColors(Screen.COLOR_BUTTON_BG, Screen.COLOR_BUTTON_FG);
		StrategoGame.sWindow.controls.setActionButton(true, "Play vs Computer", Screen.COLOR_BUTTON_BG);
		StrategoGame.sWindow.controls.setOtherButton(true, "Select team", Unit.TEAM_COLOR[sTeam]);
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(BACKGROUND, 0, 0, null);
		sTap.draw(g);
		g.drawImage(LOGO, LOGO_X, LOGO_Y, null);
		// TapEffect.drawBounds(g);
	}
	
	@Override
	public void leftClick() {
		// System.out.println(Mouse.getX() + ", " + Mouse.getY());
		sTap.effect(Mouse.getX(), Mouse.getY());
	}
	
	@Override
	public void rightClick() {}
	
	@Override
	public void actionButton() {
		StrategoGame.setPhase(new PlacementPhase(true));
	}
	
	@Override
	public void otherButton() {
		setTeam((sTeam + 1) % 2);
		StrategoGame.sWindow.controls.setOtherButton(true, "Select team", Unit.TEAM_COLOR[sTeam]);
	}
	
	@Override
	protected boolean serverCommand(String line) {
		return false;
	}
	
}
