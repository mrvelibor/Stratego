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

public class RockParticle extends Particle {
	
	private static final int SIZE = 64;
	private static final int CENTER = SIZE / 2;
	
	private static final Image[] ANIMATION = new Image[20];
	static {
		SpriteSheet animation = new SpriteSheet("effects/rocks_sheet.png");
		for(int i = 0; i < ANIMATION.length; i++) {
			ANIMATION[i] = animation.getSprite(SIZE, i, 0);
		}
	}
	
	private static final SoundFile[] SOUND = new SoundFile[3];
	static {
		SOUND[0] = new SoundFile("effects/rock_tap1.wav", SoundFile.TYPE_EFFECT);
		SOUND[1] = new SoundFile("effects/rock_tap2.wav", SoundFile.TYPE_EFFECT);
		SOUND[2] = new SoundFile("effects/rock_tap3.wav", SoundFile.TYPE_EFFECT);
	}
	
	private static final Shape[] BOUNDS = new Shape[6];
	static {
		BOUNDS[0] = new Polygon(new int[] { 29, 27, 102, 127, 98, 76, 62 }, new int[] { 30, 137, 148, 113, 90, 90, 24 }, 7);
		BOUNDS[1] = new Polygon(new int[] { 93, 172, 208, 235 }, new int[] { 24, 121, 98, 27 }, 4);
		BOUNDS[2] = new Polygon(new int[] { 109, 216, 301, 349, 257, 285, 254, 225, 207, 193, 112 }, new int[] { 633, 577, 666, 669, 570, 492, 488, 538, 540,
				567, 616 }, 11);
		BOUNDS[3] = new Polygon(new int[] { 633, 608, 616, 644, 652, 669, 670 }, new int[] { 416, 509, 608, 604, 549, 552, 410 }, 7);
		BOUNDS[4] = new Rectangle2D.Float(340, 237, 30, 30);
		BOUNDS[5] = new Rectangle2D.Float(393, 465, 20, 13);
	}
	
	public static void drawBounds(Graphics2D g) {
		Font font = g.getFont();
		g.setFont(font.deriveFont(13f));
		g.setColor(Color.YELLOW);
		
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
	
	public RockParticle(TapEffect parent, int x, int y) {
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