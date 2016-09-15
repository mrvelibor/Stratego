package com.mrvelibor.stratego.board;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.mrvelibor.stratego.graphics.Drawable;
import com.mrvelibor.stratego.units.Unit;

public class Field implements Drawable {
	
	public static final int SIZE = 64;
	
	protected final Board mBoard;
	
	public final int x, y;
	
	public final int drawX, drawY, drawXend, drawYend;
	
	private final boolean mPassable;
	
	private Unit mUnit = null;
	
	public Field(Board board, int x, int y) {
		mBoard = board;
		this.x = x;
		this.y = y;
		
		drawX = x * SIZE + SIZE / 2;
		drawY = y * SIZE + SIZE / 2;
		drawXend = drawX + SIZE;
		drawYend = drawY + SIZE;
		
		mPassable = !((x == 2 || x == 3 || x == 6 || x == 7) && (y == 4 || y == 5));
	}
	
	public void setUnit(Unit unit) {
		mUnit = unit;
		mBoard.redraw(this);
	}
	
	public void removeUnit() {
		setUnit(null);
	}
	
	public Unit getUnit() {
		return mUnit;
	}
	
	public boolean isPassable() {
		return mPassable && mUnit == null;
	}
	
	public ArrayList<Field> getAdjecent() {
		ArrayList<Field> list = new ArrayList<Field>();
		
		if(x > 0) list.add(mBoard.getField(x - 1, y));
		if(x < Board.SIZE - 1) list.add(mBoard.getField(x + 1, y));
		if(y > 0) list.add(mBoard.getField(x, y - 1));
		if(y < Board.SIZE - 1) list.add(mBoard.getField(x, y + 1));
		
		return list;
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(mUnit != null && !mUnit.isHidden()) {
			mUnit.draw(g);
		}
		else {
			g.drawImage(mBoard.BACKGROUND, drawX, drawY, drawXend, drawYend, drawX, drawY, drawXend, drawYend,
					null);
		}
	}
	
}
