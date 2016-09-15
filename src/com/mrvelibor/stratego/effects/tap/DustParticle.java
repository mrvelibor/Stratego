package com.mrvelibor.stratego.effects.tap;

import java.awt.Graphics2D;
import java.awt.Image;

import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.sound.SoundFile;

public class DustParticle extends Particle {
	
	private static final int SIZE = 64;
	private static final int CENTER = SIZE / 2;
	
	private static final Image[] ANIMATION = new Image[20];
	static {
		SpriteSheet animation = new SpriteSheet("effects/dust_sheet.png");
		for(int i = 0; i < ANIMATION.length; i++) {
			ANIMATION[i] = animation.getSprite(SIZE, i, 0);
		}
	}
	
	private static final SoundFile[] SOUND = new SoundFile[3];
	static {
		SOUND[0] = new SoundFile("effects/earth_tap1.wav", SoundFile.TYPE_EFFECT);
		SOUND[1] = new SoundFile("effects/earth_tap2.wav", SoundFile.TYPE_EFFECT);
		SOUND[2] = new SoundFile("effects/earth_tap3.wav", SoundFile.TYPE_EFFECT);
	}
	
	public DustParticle(TapEffect parent, int x, int y) {
		super(parent, x - CENTER, y - CENTER);
		SOUND[rand.nextInt(SOUND.length)].play();
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(ANIMATION[mAnim], mX, mY, null);
		mAnim += rand.nextInt(2) + 1;
		if(mAnim >= ANIMATION.length) remove();
	}
	
}