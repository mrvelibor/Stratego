package com.mrvelibor.stratego.game;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.effects.MoveAnimation;
import com.mrvelibor.stratego.exceptions.FieldNotPassableExteption;
import com.mrvelibor.stratego.graphics.Drawable;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.input.Mouse;
import com.mrvelibor.stratego.units.Unit;

public class BenchmarkPhase extends GamePhase implements Drawable {
	
	private Board mBoard = new Board();
	private ArrayList<Unit> mUnits = new ArrayList<Unit>();
	
	private ArrayList<Field> mFields = new ArrayList<Field>();
	private Unit mSelected;
	
	private MoveAnimation mAnimation;
	
	private final Random rand = new Random();
	
	private GamePhase mPrevious;
	
	public BenchmarkPhase() {
		super(true);
		
		for(int i = 0; i < 10; i++) {
			try {
				mUnits.add(Unit.create(i, 0, mBoard, i, 1));
				mUnits.add(Unit.create(i, 1, mBoard, i, 8));
			} catch(FieldNotPassableExteption e) {
				e.printStackTrace();
			}
			mFields.add(mBoard.getField(i, rand.nextInt(Board.SIZE)));
		}
		
		newAnimation();
	}
	
	@Override
	public void onSet(GamePhase previous) {
		if(previous instanceof BenchmarkPhase) mPrevious = ((BenchmarkPhase) previous).mPrevious;
		else mPrevious = previous;
		
		StrategoGame.sWindow.controls.setColors(Screen.COLOR_BUTTON_BG, Screen.COLOR_BUTTON_FG);
		StrategoGame.sWindow.controls.setActionButton(true, "BACK", Screen.COLOR_ACTION);
		StrategoGame.sWindow.controls.setOtherButton(true, "calculating", Screen.COLOR_BUTTON_BG);
	}
	
	private void newAnimation() {
		Unit unit;
		do {
			unit = mUnits.get(rand.nextInt(mUnits.size()));
			mFields = unit.getMoves();
		} while(mFields.isEmpty());
		
		mSelected = unit;
		final Field field = mFields.get(rand.nextInt(mFields.size()));
		
		mAnimation = new MoveAnimation(mSelected, field, new MoveAnimation.Action() {
			@Override
			public void doAfter() {
				mSelected.move(field);
				newAnimation();
			}
		});
		mAnimation.start();
	}
	
	private long mTimer = System.currentTimeMillis();
	private int mDraws = 0;
	
	@Override
	public void draw(Graphics2D g) {
		int x = Mouse.getFieldX(), y = Mouse.getFieldY();
		Field field;
		Unit unit;
		if(x != -1 && y != -1) {
			field = mBoard.getField(x, y);
			unit = field.getUnit();
		}
		else {
			field = null;
			unit = null;
		}
		
		mBoard.draw(g);
		
		Screen.renderFieldBorders(g, mFields);
		Screen.renderUnitBorders(g, mUnits);
		Screen.renderBorder(g, mSelected.getField(), Screen.COLOR_UNIT_SELECTED);
		if(unit != null) {
			Screen.renderOverlay(g, field, Screen.COLOR_TRANSPARENT_BLACK);
		}
		else if(field != null) {
			Screen.renderOverlay(g, field, Screen.COLOR_TRANSPARENT_WHITE);
		}
		sTap.draw(g);
		mAnimation.draw(g);
		
		++mDraws;
		if(System.currentTimeMillis() - mTimer >= 1000) {
			mTimer += 1000;
			StrategoGame.sWindow.controls.setOtherButton(true, mDraws + " FPS", Screen.COLOR_BUTTON_BG);
			mDraws = 0;
		}
	}
	
	@Override
	public void leftClick() {
		sTap.effect(Mouse.getX(), Mouse.getY());
	}
	
	@Override
	public void rightClick() {}
	
	@Override
	public void actionButton() {
		StrategoGame.setPhase(mPrevious);
	}
	
	@Override
	public void otherButton() {}
	
	@Override
	protected boolean serverCommand(String line) {
		return mPrevious.serverCommand(line);
	}
	
}
