package com.mrvelibor.stratego.effects.tap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.mrvelibor.stratego.effects.Effect;

public class TapEffect extends Effect {
	
	private static final int START_BOUND = 32, END_BOUND = 672;
	
	public static void drawBounds(Graphics2D g) {
		LeavesParticle.drawBounds(g);
		SplashParticle.drawBounds(g);
		RockParticle.drawBounds(g);
		
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(START_BOUND, START_BOUND, END_BOUND - START_BOUND, END_BOUND - START_BOUND);
	}
	
	ArrayList<Particle> mParticles = new ArrayList<Particle>();
	
	@Override
	public boolean effect(int x, int y) {
		if(checkBounds(x, y)) return false;
		
		if(RockParticle.contains(x, y)) {
			new RockParticle(this, x, y);
		}
		else if(SplashParticle.contains(x, y)) {
			new SplashParticle(this, x, y);
		}
		else if(LeavesParticle.contains(x, y)) {
			new LeavesParticle(this, x, y);
		}
		else {
			new DustParticle(this, x, y);
		}
		
		return true;
	}
	
	public boolean ping(int x, int y) {
		if(checkBounds(x, y)) return false;
		
		new Ping(this, x, y);
		return true;
	}
	
	private boolean checkBounds(int x, int y) {
		return x < START_BOUND || x > END_BOUND || y < START_BOUND || y > END_BOUND;
	}
	
	@Override
	public void draw(Graphics2D g) {
		Particle[] particles = mParticles.toArray(new Particle[mParticles.size()]);
		for(Particle p : particles) {
			p.draw(g);
		}
	}
	
}
