package com.mrvelibor.stratego.effects.fireworks;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

public class PerfectCircleSpark extends Spark {
	
	private static final int DIAMETER = 10;
	
	private Color clrGlowInnerHi;
	private Color clrGlowInnerLo;
	private Color clrGlowOuterHi;
	private Color clrGlowOuterLo;
	
	public PerfectCircleSpark(FireworksEffect parent, float x, float y, float direction, long lifespan, float maxSpeed, Color color) {
		super(parent, x, y, direction, lifespan, maxSpeed, DIAMETER, color);
		mSpark.x = x - 5;
		mSpark.y = y - 5;
		
		Color c2 = color.brighter().brighter();
		clrGlowInnerHi = new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 120);
		clrGlowInnerLo = color;
		clrGlowOuterHi = new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 100);
		clrGlowOuterLo = c2;
	}
	
	private Color getMixedColor(Color c1, float pct1, Color c2, float pct2) {
		float[] clr1 = c1.getComponents(null);
		float[] clr2 = c2.getComponents(null);
		for(int i = 0; i < clr1.length; i++) {
			clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
		}
		return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
	}
	
	@Override
	public void draw(Graphics2D g) {
		step();
		
		int gw = mMaxDiameter * 6;
		for(int i = gw; i >= 4; i -= 4) {
			float pct = (float) (gw - i) / (gw - 1);
			Color mixHi = getMixedColor(clrGlowInnerHi, pct, clrGlowOuterHi, 1.0f - pct);
			Color mixLo = getMixedColor(clrGlowInnerLo, pct, clrGlowOuterLo, 1.0f - pct);
			g.setPaint(new GradientPaint(0.0f, mMaxDiameter * 0.25f, mixHi, 0.0f, mMaxDiameter, mixLo));
			g.draw(mSpark);
		}
	}
}
