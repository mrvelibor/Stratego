package com.mrvelibor.stratego.graphics;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import java.util.ArrayList;

import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.units.Unit;

public class Screen extends Canvas {
	
	private static final long serialVersionUID = 1L;
	
	public static final Color COLOR_GRID = Color.BLACK, COLOR_TEXT = new Color(250, 250, 250), COLOR_BUTTON_BG = COLOR_TEXT, COLOR_BUTTON_FG = Color.BLACK,
			COLOR_TRANSPARENT_BLACK = new Color(0, 0, 0, 123), COLOR_TRANSPARENT_WHITE = new Color(255, 255, 255, 85), COLOR_ACTION = new Color(0xE5A514),
			COLOR_ON_TURN = new Color(0x10B41A), COLOR_OPPONENT_TURN = new Color(0xE51914), COLOR_FIELD_AVAILABLE = COLOR_TEXT,
			COLOR_UNIT_SELECTED = COLOR_ON_TURN, COLOR_UNIT_READY = COLOR_ACTION, COLOR_UNIT_WAITING = COLOR_OPPONENT_TURN;
	
	private VolatileImage mBuffer = null;
	private Graphics2D mGraphics;
	
	private Drawable mDrawable = null;
	
	public static final int WIDTH = Field.SIZE * (Board.SIZE + 1), HEIGHT = Field.SIZE * (Board.SIZE + 1);
	
	public Screen() {
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
	}
	
	public void setDrawable(Drawable d) {
		mDrawable = d;
	}
	
	protected void createBuffer() {
		if(mBuffer != null) {
			mGraphics.dispose();
			mBuffer.flush();
		}
		
		// mBuffer = getGraphicsConfiguration().createCompatibleImage(WIDTH, HEIGHT, Transparency.OPAQUE);
		mBuffer = createVolatileImage(WIDTH, HEIGHT);
		
		mGraphics = mBuffer.createGraphics();
		mGraphics.setStroke(new BasicStroke(2));
		mGraphics.setFont(new Font("Arial", Font.BOLD, 37));
	}
	
	@Override
	public void update(Graphics g) {
		if(mBuffer == null) createBuffer();
		do {
			int valid = mBuffer.validate(getGraphicsConfiguration());
			if(valid == VolatileImage.IMAGE_INCOMPATIBLE) createBuffer();
			
			if(mDrawable != null) mDrawable.draw(mGraphics);
			
			g.drawImage(mBuffer, 0, 0, this);
		} while(mBuffer.contentsLost());
	}
	
	public static void renderFieldBorders(Graphics g, ArrayList<Field> fields) {
		g.setColor(COLOR_FIELD_AVAILABLE);
		for(Field field : fields) {
			g.drawRect(field.drawX, field.drawY, Field.SIZE, Field.SIZE);
		}
	}
	
	public static void renderUnitBorders(Graphics g, ArrayList<Unit> units) {
		g.setColor(COLOR_UNIT_READY);
		for(Unit unit : units) {
			Field field = unit.getField();
			g.drawRect(field.drawX, field.drawY, Field.SIZE, Field.SIZE);
		}
	}
	
	public static void renderBorder(Graphics g, Field field, Color color) {
		g.setColor(color);
		g.drawRect(field.drawX, field.drawY, Field.SIZE, Field.SIZE);
	}
	
	public static void renderOverlay(Graphics g, Field field, Color color) {
		g.setColor(color);
		g.fillRect(field.drawX, field.drawY, Field.SIZE, Field.SIZE);
	}
	
	public static void renderText(Graphics g, String text) {
		if(text == null) return;
		
		g.setColor(COLOR_TEXT);
		g.drawString(text, HEIGHT / 4, WIDTH / 2);
	}
	
}
