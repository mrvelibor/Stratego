package com.mrvelibor.stratego.effects.fireworks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import com.mrvelibor.stratego.effects.Effect;
import com.mrvelibor.stratego.sound.SoundFile;

public class FireworksEffect extends Effect {
	
	private static final SoundFile EXPLOSION1 = new SoundFile("effects/fireworks1.wav", SoundFile.TYPE_EFFECT),
			EXPLOSION2 = new SoundFile("effects/fireworks2.wav", SoundFile.TYPE_EFFECT),
			FIZZ1 = new SoundFile("effects/fireworks_fizz1.wav", SoundFile.TYPE_EFFECT),
			FIZZ2 = new SoundFile("effects/fireworks_fizz2.wav", SoundFile.TYPE_EFFECT);
	
	private ArrayList<Spark> mSparks = new ArrayList<Spark>();
	
	private final Random rand = new Random();
	
	public boolean removeSpark(Spark s) {
		return mSparks.remove(s);
	}
	
	@Override
	public void draw(Graphics2D g) {
		Spark[] sparks = mSparks.toArray(new Spark[mSparks.size()]);
		for(Spark s : sparks) {
			s.draw(g);
		}
	}
	
	@Override
	public boolean effect(int x, int y) {
		int sparkCount = 50 + rand.nextInt(20);
		Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 50);
		long lifespan = 1000 + rand.nextInt(1000);
		
		switch(rand.nextInt(5))
			{
			case 0:
				createBubbleSpark(x, y, sparkCount, color, lifespan);
				break;
			case 1:
				createCircleSpark(x, y, sparkCount, color, lifespan);
				break;
			case 2:
				createMovingSpark(x, y, sparkCount, color, lifespan);
				break;
			case 3:
				createPerfectCircleSpark(x, y, sparkCount, color, lifespan);
				break;
			case 4:
				createTrigSpark(x, y, sparkCount, color, lifespan);
				break;
			case 5:
				createGiantSpark(x, y, sparkCount, color, lifespan);
				break;
			}
		
		return true;
	}
	
	private void createBubbleSpark(int x, int y, int sparkCount, Color color, long lifespan) {
		for(int i = 0; i < sparkCount; i++) {
			float direction = 360 * rand.nextFloat();
			float speed = 10 * rand.nextFloat() + 5;
			mSparks.add(new BubbleSpark(this, x, y, direction, lifespan, speed, color));
		}
		EXPLOSION1.play();
	}
	
	private void createCircleSpark(int x, int y, int sparkCount, Color color, long lifespan) {
		for(int i = 0; i < sparkCount; i++) {
			float direction = 360 * rand.nextFloat();
			float speed = 10 * rand.nextFloat() + 5;
			mSparks.add(new CircleSpark(this, x, y, direction, lifespan, speed, color));
		}
		FIZZ2.play();
	}
	
	private void createGiantSpark(int x, int y, int sparkCount, Color color, long lifespan) {
		for(int i = 0; i < sparkCount; i++) {
			float direction = 360 * rand.nextFloat();
			float speed = 10 * rand.nextFloat() + 5;
			mSparks.add(new GiantSpark(this, x, y, direction, lifespan, speed, color));
		}
		EXPLOSION1.play();
	}
	
	private void createMovingSpark(int x, int y, int sparkCount, Color color, long lifespan) {
		for(int i = 0; i < sparkCount; i++) {
			float direction = 360 * rand.nextFloat();
			float speed = 10 * rand.nextFloat() + 5;
			mSparks.add(new MovingSpark(this, x, y, direction, lifespan, speed, color));
		}
		FIZZ1.play();
	}
	
	private void createPerfectCircleSpark(int x, int y, int sparkCount, Color color, long lifespan) {
		sparkCount *= 2;
		lifespan /= 2;
		
		float speed = 20 * rand.nextFloat() + 5;
		for(int i = 0; i < sparkCount; i++) {
			float direction = 360 * rand.nextFloat();
			mSparks.add(new PerfectCircleSpark(this, x, y, direction, lifespan, speed, color));
		}
		EXPLOSION2.play();
	}
	
	private void createTrigSpark(int x, int y, int sparkCount, Color color, long lifespan) {
		for(int i = 0; i < sparkCount; i++) {
			float direction = 360 * rand.nextFloat();
			float speed = 10 * rand.nextFloat() + 5;
			mSparks.add(new TrigSpark(this, x, y, direction, lifespan, speed, color));
		}
		FIZZ2.play();
	}
}
