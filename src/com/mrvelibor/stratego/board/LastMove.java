package com.mrvelibor.stratego.board;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.mrvelibor.stratego.graphics.Drawable;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.units.Unit;

public class LastMove implements Drawable {
	
	private final Image BORDER, KILLED, ARROW_UP, ARROW_LEFT, ARROW_DOWN, ARROW_RIGHT;
	
	private final int BORDER_X1 = Field.SIZE / 4, BORDER_X2 = BORDER_X1 + Field.SIZE * 2, BORDER_Y = BORDER_X1;
	
	private BufferedImage mImage;
	private Graphics2D mGraphics;
	
	private boolean mShowAttack = true;
	
	private Image mArrow = null;
	
	private Unit mUnit = null, mTarget = null;
	private Field mStart = null, mMove = null;
	
	public LastMove() {
		Image border = null;
		try {
			border = SpriteSheet.loadImage("board/last_move.png");
			mImage = SpriteSheet.loadImage("board/last_move.png");
			mGraphics = mImage.createGraphics();
		} catch(IOException e) {
			e.printStackTrace();
		}
		BORDER = border;
		KILLED = SpriteSheet.MISC.getSprite(Field.SIZE, 3, 0);
		ARROW_UP = SpriteSheet.MISC.getSprite(Field.SIZE, 0, 1);
		ARROW_LEFT = SpriteSheet.MISC.getSprite(Field.SIZE, 1, 1);
		ARROW_DOWN = SpriteSheet.MISC.getSprite(Field.SIZE, 2, 1);
		ARROW_RIGHT = SpriteSheet.MISC.getSprite(Field.SIZE, 3, 1);
	}
	
	public void saveLastMove(Field start, Field move, Unit unit, Unit target) {
		mUnit = unit;
		mStart = start;
		mMove = move;
		mTarget = target;
		
		if(move != null) {
			if(start.x > move.x) mArrow = ARROW_LEFT;
			else if(start.x < move.x) mArrow = ARROW_RIGHT;
			else if(start.y > move.y) mArrow = ARROW_UP;
			else if(start.y < move.y) mArrow = ARROW_DOWN;
		}
		else {
			mArrow = null;
		}
		
		if(target != null) {
			int outcome = mUnit.calculateAttack(mTarget);
			
			mGraphics.drawImage(BORDER, 0, 0, null);
			mGraphics.drawImage(mUnit.getImage(), BORDER_X1, BORDER_Y, null);
			if(outcome < 1) mGraphics.drawImage(KILLED, BORDER_X1, BORDER_Y, null);
			
			mGraphics.drawImage(mTarget.getImage(), BORDER_X2, BORDER_Y, null);
			if(outcome > -1) mGraphics.drawImage(KILLED, BORDER_X2, BORDER_Y, null);
		}
	}
	
	public void setAttackShown(boolean shown) {
		mShowAttack = shown;
	}
	
	public void toggleAttackShown() {
		mShowAttack = !mShowAttack;
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(mStart != null) {
			Screen.renderOverlay(g, mStart, Screen.COLOR_TRANSPARENT_WHITE);
			Screen.renderBorder(g, mStart, Screen.COLOR_UNIT_SELECTED);
		}
		if(mMove != null) {
			Screen.renderOverlay(g, mMove, Screen.COLOR_TRANSPARENT_WHITE);
			Screen.renderBorder(g, mMove, Screen.COLOR_UNIT_READY);
			g.drawImage(mArrow, mStart.drawX, mStart.drawY, null);
		}
		if(mTarget != null) {
			if(mShowAttack) g.drawImage(mImage, 0, 0, null);
			Screen.renderOverlay(g, mTarget.getField(), Screen.COLOR_TRANSPARENT_BLACK);
			Screen.renderBorder(g, mTarget.getField(), Screen.COLOR_UNIT_WAITING);
		}
	}
	
}
