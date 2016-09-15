package com.mrvelibor.stratego.effects.fireworks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class BubbleSpark extends Spark {
	
	private static final int DIAMETER = 20;
	
	public BubbleSpark(FireworksEffect parent, float x, float y, float direction, long lifespan, float maxSpeed, Color color) {
		super(parent, x, y, direction, lifespan, maxSpeed, DIAMETER, color);
	}
	
	@Override
	public void draw(Graphics2D g) {
		step();
		g.setColor(mColor);
		
		double loops = 70;
		double scale;
		AffineTransform at;
		for(int i = (int) loops; i > 0; i--) {
			scale = Math.sin(Math.toRadians(loops / i)) * 2;
			at = AffineTransform.getTranslateInstance(mX, mY);
			at.scale(scale, scale);
			g.fill(at.createTransformedShape(mSpark));
		}
	}
}