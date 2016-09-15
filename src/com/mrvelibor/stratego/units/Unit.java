package com.mrvelibor.stratego.units;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.exceptions.FieldNotPassableExteption;
import com.mrvelibor.stratego.game.GamePhase;
import com.mrvelibor.stratego.graphics.Drawable;
import com.mrvelibor.stratego.graphics.SpriteSheet;

public abstract class Unit implements Drawable {
	
	public static final int SIZE = Field.SIZE;
	
	public static final Color[] TEAM_COLOR = new Color[] { new Color(0x9C0B07), new Color(0x0B1F6A) };
	
	protected static final BufferedImage[] TEAM_BACKGROUND = new BufferedImage[] { SpriteSheet.BACKGROUNDS.getSprite(SIZE, 0, 0),
			SpriteSheet.BACKGROUNDS.getSprite(SIZE, 1, 0) };
	
	private static final Image SPRITE_HIDDEN[] = new Image[] {
			new BufferedImage(TEAM_BACKGROUND[0].getColorModel(), TEAM_BACKGROUND[0].copyData(null), TEAM_BACKGROUND[0].isAlphaPremultiplied(), null),
			new BufferedImage(TEAM_BACKGROUND[1].getColorModel(), TEAM_BACKGROUND[1].copyData(null), TEAM_BACKGROUND[1].isAlphaPremultiplied(), null) };
	static {
		Image imageHidden = SpriteSheet.MISC.getSprite(SIZE, 0, 0);
		SPRITE_HIDDEN[0].getGraphics().drawImage(imageHidden, 0, 0, null);
		SPRITE_HIDDEN[1].getGraphics().drawImage(imageHidden, 0, 0, null);
	}
	
	public final int team;
	
	protected final Board mBoard;
	
	private Field mField;
	
	private boolean mRevealed = false;
	
	protected Unit(int team, Board board, int x, int y) throws FieldNotPassableExteption {
		this.team = team;
		
		mShown = team == GamePhase.getTeam();
		
		mBoard = board;
		if(mBoard.place(this, x, y)) {
			mField = mBoard.getField(x, y);
		}
		else throw new FieldNotPassableExteption();
	}
	
	public abstract int getRank();
	
	public int getTeam() {
		return team;
	}
	
	public Field getField() {
		return mField;
	}
	
	public boolean isRevealed() {
		return mRevealed;
	}
	
	public ArrayList<Field> getMoves() {
		ArrayList<Field> moves = new ArrayList<Field>();
		
		ArrayList<Field> adjectent = mField.getAdjecent();
		for(Field field : adjectent) {
			if(field.isPassable()) moves.add(field);
		}
		
		return moves;
	}
	
	public ArrayList<Unit> getTargets() {
		ArrayList<Unit> targets = new ArrayList<Unit>();
		
		ArrayList<Field> adjectent = mField.getAdjecent();
		for(Field field : adjectent) {
			Unit unit = field.getUnit();
			if(unit != null && unit.team != team) targets.add(unit);
		}
		
		return targets;
	}
	
	public boolean move(Field field) {
		if(!field.isPassable()) return false;
		if(mField != null) mField.setUnit(null);
		
		mField = field;
		mField.setUnit(this);
		return true;
	}
	
	public void swap(Unit unit) {
		Field field = mField;
		
		mField = unit.mField;
		unit.mField.setUnit(this);
		
		unit.mField = field;
		field.setUnit(unit);
	}
	
	public int calculateAttack(Unit target) {
		return getRank() - target.getRank();
	}
	
	public void attack(Unit target) {
		mRevealed = true;
		target.mRevealed = true;
		
		int outcome = calculateAttack(target);
		
		if(outcome > 0) {
			onKill(target);
		}
		else if(outcome == 0) {
			onDeath();
			target.onDeath();
		}
		else {
			onDeath();
		}
	}
	
	protected void onDeath() {
		mBoard.remove(this);
		mField.removeUnit();
	}
	
	protected void onKill(Unit killed) {
		killed.onDeath();
		move(killed.mField);
	}
	
	private boolean mHidden = false, mShown;
	
	public boolean isHidden() {
		return mHidden;
	}
	
	public void setHidden(boolean hidden) {
		mHidden = hidden;
		mBoard.redraw(mField);
	}
	
	public void setShown(boolean shown) {
		mShown = shown;
		mBoard.redraw(mField);
	}
	
	public Image getSprite() {
		return mShown ? getImage() : SPRITE_HIDDEN[team];
	}
	
	public abstract Image getImage();
	
	@Override
	public final void draw(Graphics2D g) {
		g.drawImage(getSprite(), mField.drawX, mField.drawY, null);
	}
	
	public static Unit create(int rank, int team, Board board, int x, int y) throws FieldNotPassableExteption {
		switch(rank)
			{
			case Flag.RANK:
				return new Flag(team, board, x, y);
			case Marshal.RANK:
				return new Marshal(team, board, x, y);
			case General.RANK:
				return new General(team, board, x, y);
			case Colonel.RANK:
				return new Colonel(team, board, x, y);
			case Major.RANK:
				return new Major(team, board, x, y);
			case Captain.RANK:
				return new Captain(team, board, x, y);
			case Liutenant.RANK:
				return new Liutenant(team, board, x, y);
			case Sergeant.RANK:
				return new Sergeant(team, board, x, y);
			case Miner.RANK:
				return new Miner(team, board, x, y);
			case Scout.RANK:
				return new Scout(team, board, x, y);
			case Spy.RANK:
				return new Spy(team, board, x, y);
			case Bomb.RANK:
				return new Bomb(team, board, x, y);
				
			default:
				return null;
			}
	}
	
	public static Image getImage(int rank) {
		switch(rank)
			{
			case Flag.RANK:
				return Flag.IMAGE;
			case Marshal.RANK:
				return Marshal.IMAGE;
			case General.RANK:
				return General.IMAGE;
			case Colonel.RANK:
				return Colonel.IMAGE;
			case Major.RANK:
				return Major.IMAGE;
			case Captain.RANK:
				return Captain.IMAGE;
			case Liutenant.RANK:
				return Liutenant.IMAGE;
			case Sergeant.RANK:
				return Sergeant.IMAGE;
			case Miner.RANK:
				return Miner.IMAGE;
			case Scout.RANK:
				return Scout.IMAGE;
			case Spy.RANK:
				return Spy.IMAGE;
			case Bomb.RANK:
				return Bomb.IMAGE;
				
			default:
				return null;
			}
	}
	
	public static String getName(int rank) {
		switch(rank)
			{
			case Flag.RANK:
				return "Flag";
			case Marshal.RANK:
				return "Marshal";
			case General.RANK:
				return "General";
			case Colonel.RANK:
				return "Colonel";
			case Major.RANK:
				return "Major";
			case Captain.RANK:
				return "Captain";
			case Liutenant.RANK:
				return "Liutenant";
			case Sergeant.RANK:
				return "Sergeant";
			case Miner.RANK:
				return "Miner";
			case Scout.RANK:
				return "Scout";
			case Spy.RANK:
				return "Spy";
			case Bomb.RANK:
				return "Bomb";
				
			default:
				return "Unknown";
			}
	}
}
