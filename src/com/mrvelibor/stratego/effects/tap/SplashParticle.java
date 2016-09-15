package com.mrvelibor.stratego.effects.tap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.sound.SoundFile;

public class SplashParticle extends Particle {
	
	private static final int SIZE = 100;
	private static final int CENTER = SIZE / 2;
	
	private static final Image[] ANIMATION = new Image[23];
	static {
		SpriteSheet animation = new SpriteSheet("effects/water_sheet.png");
		for(int i = 0; i < ANIMATION.length; i++) {
			ANIMATION[i] = animation.getSprite(SIZE, i, 0);
		}
	}
	
	private static final SoundFile[] SOUND = new SoundFile[3];
	static {
		SOUND[0] = new SoundFile("effects/water_splash1.wav", SoundFile.TYPE_EFFECT);
		SOUND[1] = new SoundFile("effects/water_splash2.wav", SoundFile.TYPE_EFFECT);
		SOUND[2] = new SoundFile("effects/water_splash3.wav", SoundFile.TYPE_EFFECT);
	}
	
	private static final Shape[] BOUNDS = new Shape[3];
	static {
		BOUNDS[0] = new Polygon(new int[] { 169, 172, 187, 202, 200, 190, 187, 169, 170, 184, 216, 200, 214, 232, 244, 260, 284, 259, 284, 283, 284, 265, 261,
				227, 230 }, new int[] { 291, 312, 325, 326, 338, 339, 331, 350, 392, 413, 408, 376, 376, 387, 402, 408, 402, 378, 361, 337, 303, 308, 294, 291,
				306 }, 25);
		BOUNDS[1] = new Polygon(new int[] { 436, 436, 424, 426, 440, 456, 462, 494, 495, 506, 519, 533, 526, 537, 533, 520, 504, 506, 529, 538, 534, 512, 479,
				461, 460, 478, 495, 478, 459 }, new int[] { 296, 338, 340, 395, 400, 389, 405, 406, 393, 388, 399, 392, 379, 370, 356, 364, 353, 338, 330, 313,
				295, 305, 348, 349, 342, 339, 301, 294, 302 }, 29);
		BOUNDS[2] = new Rectangle2D.Float(471, 340, 7, 7);
	}
	
	public static void drawBounds(Graphics2D g) {
		Font font = g.getFont();
		g.setFont(font.deriveFont(13f));
		g.setColor(Color.ORANGE);
		
		for(int i = 0; i < BOUNDS.length; i++) {
			g.draw(BOUNDS[i]);
			Rectangle2D bounds = BOUNDS[i].getBounds2D();
			g.drawString(Integer.toString(i), (int) bounds.getX(), (int) bounds.getY());
		}
		
		g.setFont(font);
	}
	
	public static boolean contains(int x, int y) {
		for(Shape s : BOUNDS) {
			if(s.contains(x, y)) return true;
		}
		return false;
	}
	
	public SplashParticle(TapEffect parent, int x, int y) {
		super(parent, x - CENTER, y - CENTER);
		SOUND[rand.nextInt(SOUND.length)].play();
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(ANIMATION[mAnim], mX, mY, null);
		if(++mAnim >= ANIMATION.length) remove();
	}
	
}