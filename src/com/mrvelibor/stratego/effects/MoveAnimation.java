package com.mrvelibor.stratego.effects;

import java.awt.Graphics2D;
import java.awt.Image;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.graphics.Drawable;
import com.mrvelibor.stratego.units.Unit;

public class MoveAnimation implements Drawable {
	
	private static final int ANIMATION_LENGTH = 2 * StrategoGame.TARGET_UPS;
	private static final float MOVE_DELTA = Unit.SIZE / ANIMATION_LENGTH;
	
	private int mCount = 0;
	
	private boolean mStarted = false;
	
	private Image mImage;
	
	private Unit mMoving;
	private Field mTarget;
	
	private float mX, mY, mDeltaX, mDeltaY;
	
	private Action mAction;
	
	public MoveAnimation(Unit moving, Field target, Action action) {
		mMoving = moving;
		mTarget = target;
		mAction = action;
		mImage = mMoving.getSprite();
	}
	
	public void start() {
		mMoving.setHidden(true);
		
		Field start = mMoving.getField();
		mX = (start.x + 0.5f) * Unit.SIZE;
		mY = (start.y + 0.5f) * Unit.SIZE;
		
		mDeltaX = (mTarget.x - start.x) / MOVE_DELTA;
		mDeltaY = (mTarget.y - start.y) / MOVE_DELTA;
		
		mStarted = true;
	}
	
	protected void onEnd() {
		mMoving.setHidden(false);
		mAction.doAfter();
	}
	
	@Override
	public void draw(Graphics2D g) {
		if(!mStarted) start();
		
		g.drawImage(mImage, (int) mX, (int) mY, null);
		mX += mDeltaX;
		mY += mDeltaY;
		
		if(++mCount > ANIMATION_LENGTH) onEnd();
	}
	
	public static interface Action {
		public abstract void doAfter();
	}
	
}
