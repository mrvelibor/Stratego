package com.mrvelibor.stratego.effects.fireworks;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import com.mrvelibor.stratego.graphics.Drawable;

public abstract class Spark implements Drawable {
	
	private final FireworksEffect mParent;
	
	private final long mSpawnTime;
	private final long mLifespan;
	
	private final float mMaxSpeed;
	private final float mAcceleration;
	
	private final float mDirection;
	
	protected final int mMaxDiameter;
	
	protected final Color mColor;
	
	protected final float mX;
	protected final float mY;
	
	protected final Ellipse2D.Float mSpark;
	
	
	protected Spark(FireworksEffect parent, float x, float y, float direction, long lifespan, float maxSpeed, int maxDiameter, Color color) {
		mParent = parent;
		mX = x;
		mY = y;
		mDirection = direction;
		mLifespan = lifespan;
		mMaxSpeed = maxSpeed;
		mMaxDiameter = maxDiameter;
		mColor = color;
		mSpark = new Ellipse2D.Float(0, 0, mMaxDiameter, mMaxDiameter);
		mAcceleration = -1.0f / mLifespan * mMaxSpeed / 1.1f;
		mSpawnTime = System.currentTimeMillis();
	}
	
	protected void step() {
		long currentTime = System.currentTimeMillis();
		long currentLifeLength = currentTime - mSpawnTime;
		
		if(currentLifeLength < mLifespan) {
			double currentSpeed = mMaxSpeed + mAcceleration * currentLifeLength;
			
			double dx = currentSpeed * Math.cos(Math.toRadians(mDirection));
			double dy = currentSpeed * Math.sin(Math.toRadians(mDirection));
			
			mSpark.x += dx;
			mSpark.y += dy;
			
			float shrink = 1 - ((float) currentLifeLength / mLifespan);
			
			mSpark.height = mMaxDiameter * shrink;
			mSpark.width = mMaxDiameter * shrink;
		}
		else {
			mParent.removeSpark(this);
		}
	}
}
