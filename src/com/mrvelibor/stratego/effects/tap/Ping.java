package com.mrvelibor.stratego.effects.tap;

import java.awt.Graphics2D;
import java.awt.Image;

import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.sound.SoundFile;

public class Ping extends Particle {
	
	private static final int SIZE = 150;
	private static final int CENTER = SIZE / 2;
	
	private static final Image[][] ANIMATION = new Image[2][20];
	static {
		SpriteSheet animation;
		
		animation = new SpriteSheet("effects/ping1_sheet.png");
		for(int i = 0; i < ANIMATION[0].length; i++) {
			ANIMATION[0][i] = animation.getSprite(SIZE, i, 0);
		}
		
		animation = new SpriteSheet("effects/ping2_sheet.png");
		for(int i = 0; i < ANIMATION[1].length; i++) {
			ANIMATION[1][i] = animation.getSprite(SIZE, i, 0);
		}
	}
	
	private static final SoundFile[] SOUND = new SoundFile[2];
	static {
		SOUND[0] = new SoundFile("effects/ping1.wav", SoundFile.TYPE_GAME);
		SOUND[1] = new SoundFile("effects/ping2.wav", SoundFile.TYPE_GAME);
	}
	
	private final int mType;
	
	public Ping(TapEffect parent, int x, int y) {
		super(parent, x - CENTER, y - CENTER);
		mType = rand.nextInt(ANIMATION.length);
		SOUND[mType].play();
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(ANIMATION[mType][mAnim], mX, mY, null);
		if(++mAnim >= ANIMATION[mType].length) remove();
	}
	
}