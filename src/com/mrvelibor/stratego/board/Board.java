package com.mrvelibor.stratego.board;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.exceptions.FieldNotPassableExteption;
import com.mrvelibor.stratego.graphics.Drawable;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.units.Unit;

public class Board implements Drawable {
	
	public static final int SIZE = 10;
	
	public final Image BACKGROUND;
	
	private BufferedImage mImage;
	private Graphics2D mGraphics;
	
	private final Field[][] mField = new Field[SIZE][SIZE];
	
	@SuppressWarnings("unchecked")
	private List<Unit>[] mUnits = new List[] { new ArrayList<Unit>(), new ArrayList<Unit>() };
	
	public Board(boolean simple) {
		if(!simple) {
			BufferedImage image = null;
			try {
				image = SpriteSheet.loadImage(simple ? "board/background_simple.png" : "board/background_normal.png");
			} catch(IOException e) {
				JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString(), "", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			BACKGROUND = image;
			Graphics2D g = image.createGraphics();
			g.setStroke(new BasicStroke(2));
			g.setColor(Screen.COLOR_GRID);
			
			for(int i = 0; i < mField.length; i++) {
				for(int j = 0; j < mField[i].length; j++) {
					mField[i][j] = new Field(this, i, j);
					if(mField[i][j].isPassable()) g.drawRect(mField[i][j].drawX, mField[i][j].drawY, Field.SIZE, Field.SIZE);
				}
			}
			
			g.dispose();
			
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			
			mImage = gc.createCompatibleImage(Screen.WIDTH, Screen.HEIGHT, Transparency.OPAQUE);
			mGraphics = mImage.createGraphics();
			
			redraw();
		}
		else {
			for(int i = 0; i < mField.length; i++) {
				for(int j = 0; j < mField[i].length; j++) {
					mField[i][j] = new Field(this, i, j);
				}
			}
			BACKGROUND = null;
			mImage = null;
			mGraphics = null;
		}
	}
	
	public Board() {
		this(false);
	}
	
	public Field getField(int x, int y) {
		return mField[x][y];
	}
	
	public List<Unit> getUnits(int team) {
		return mUnits[team];
	}
	
	public Unit getUnit(int team, Random rand) {
		return mUnits[team].get(rand.nextInt(mUnits[team].size()));
	}
	
	public boolean place(Unit unit, int x, int y) {
		if(unit.team == 0 && y > 3 || unit.team == 1 && y < 6) return false;
		if(mField[x][y].isPassable()) {
			mUnits[unit.team].add(unit);
			unit.move(mField[x][y]);
			return true;
		}
		return false;
	}
	
	public boolean place(String line, int team) {
		for(int i = 0; i < 40; i++) {
			int rank = Character.getNumericValue(line.charAt(i));
			int x = i % 10, y = i / 10;
			if(team == 1) y += 6;
			try {
				Unit.create(rank, team, this, x, y);
			} catch(FieldNotPassableExteption e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public String getString(int team) {
		StringBuilder sb = new StringBuilder();
		for(int j = team == 0 ? 0 : 6, k = team == 0 ? 4 : Board.SIZE; j < k; j++) {
			for(int i = 0; i < Board.SIZE; i++) {
				sb.append(Integer.toHexString(mField[i][j].getUnit().getRank()));
			}
		}
		return sb.toString();
	}
	
	public void remove(Unit unit) {
		mUnits[unit.team].remove(unit);
	}
	
	public void redraw() {
		if(mImage == null) return;
		
		mGraphics.drawImage(BACKGROUND, 0, 0, null);
		for(List<Unit> list : mUnits) {
			for(int i = 0; i < list.size(); i++) {
				list.get(i).draw(mGraphics);
			}
		}
	}
	
	public void redraw(Field field) {
		if(mImage == null) return;
		
		field.draw(mGraphics);
	}
	
	@Override
	public final void draw(Graphics2D g) {
		g.drawImage(mImage, 0, 0, null);
	}
	
	public void clear() {
		for(Field[] fields : mField)
			for(Field field : fields)
				field.removeUnit();
		for(List<Unit> list : mUnits) {
			list.clear();
		}
		redraw();
	}
	
	public boolean hasMoves(int team) {
		for(Unit unit : mUnits[team]) {
			if(!unit.getMoves().isEmpty()) return true;
			if(!unit.getTargets().isEmpty()) return true;
		}
		return false;
	}
}
