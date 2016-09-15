package com.mrvelibor.stratego.effects.tap;

import java.util.Random;

import com.mrvelibor.stratego.graphics.Drawable;

public abstract class Particle implements Drawable {
	
	protected static final Random rand = new Random();
	
	private final TapEffect mParent;
	
	protected final int mX, mY;
	
	protected int mAnim = 0;
	
	protected Particle(TapEffect parent, int x, int y) {
		mParent = parent;
		
		mX = x;
		mY = y;
		
		mParent.mParticles.add(this);
	}
	
	protected final void remove() {
		mParent.mParticles.remove(this);
	}
	
}
