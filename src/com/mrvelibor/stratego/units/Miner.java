package com.mrvelibor.stratego.units;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.exceptions.FieldNotPassableExteption;
import com.mrvelibor.stratego.graphics.SpriteSheet;

public class Miner extends Unit {
	
	public static final Image IMAGE = SpriteSheet.UNITS.getSprite(SIZE, 0, 2);
	public static final Image SPRITE[] = new Image[] {
			new BufferedImage(TEAM_BACKGROUND[0].getColorModel(), TEAM_BACKGROUND[0].copyData(null), TEAM_BACKGROUND[0].isAlphaPremultiplied(), null),
			new BufferedImage(TEAM_BACKGROUND[1].getColorModel(), TEAM_BACKGROUND[1].copyData(null), TEAM_BACKGROUND[1].isAlphaPremultiplied(), null) };
	static {
		SPRITE[0].getGraphics().drawImage(IMAGE, 0, 0, null);
		SPRITE[1].getGraphics().drawImage(IMAGE, 0, 0, null);
	}
	
	public static final int AVAILABLE = 5, RANK = 3;
	
	public Miner(int team, Board board, int x, int y) throws FieldNotPassableExteption {
		super(team, board, x, y);
	}
	
	@Override
	public int getRank() {
		return RANK;
	}
	
	@Override
	public int calculateAttack(Unit target) {
		if(target instanceof Bomb) return 1;
		return super.calculateAttack(target);
	}
	
	@Override
	public Image getImage() {
		return SPRITE[team];
	}
	
}
