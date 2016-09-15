package com.mrvelibor.stratego.units;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.exceptions.FieldNotPassableExteption;
import com.mrvelibor.stratego.graphics.SpriteSheet;

public class Marshal extends Unit {
	
	public static final Image IMAGE = SpriteSheet.UNITS.getSprite(SIZE, 1, 0);
	public static final Image SPRITE[] = new Image[] { new BufferedImage(TEAM_BACKGROUND[0].getColorModel(), TEAM_BACKGROUND[0].copyData(null), TEAM_BACKGROUND[0].isAlphaPremultiplied(), null), new BufferedImage(TEAM_BACKGROUND[1].getColorModel(), TEAM_BACKGROUND[1].copyData(null), TEAM_BACKGROUND[1].isAlphaPremultiplied(), null) };
	static {
		SPRITE[0].getGraphics().drawImage(IMAGE, 0, 0, null);
		SPRITE[1].getGraphics().drawImage(IMAGE, 0, 0, null);
	}
	
	public static final int AVAILABLE = 1, RANK = 10;
	
	public Marshal(int team, Board board, int x, int y) throws FieldNotPassableExteption {
		super(team, board, x, y);
	}
	
	@Override
	public int getRank() {
		return RANK;
	}
	
	@Override
	public Image getImage() {
		return SPRITE[team];
	}
	
}