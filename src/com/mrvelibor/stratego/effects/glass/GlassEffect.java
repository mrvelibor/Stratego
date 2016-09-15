package com.mrvelibor.stratego.effects.glass;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.effects.Effect;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.sound.SoundFile;

public class GlassEffect extends Effect {
	
	private static final Image[] IMAGE = new Image[3];
	private static final Image[] ALPHA = new Image[3];
	private static final int[] WIDTH = new int[IMAGE.length];
	private static final int[] HEIGHT = new int[IMAGE.length];
	static {
		try {
			IMAGE[0] = SpriteSheet.loadImage("effects/glass_small.png");
			ALPHA[0] = SpriteSheet.loadImage("effects/glass_small_alpha.png");
			WIDTH[0] = IMAGE[0].getWidth(null) / 2;
			HEIGHT[0] = IMAGE[0].getHeight(null) / 2;
			
			IMAGE[1] = SpriteSheet.loadImage("effects/glass_large.png");
			ALPHA[1] = SpriteSheet.loadImage("effects/glass_large_alpha.png");
			WIDTH[1] = IMAGE[1].getWidth(null) / 2;
			HEIGHT[1] = IMAGE[1].getHeight(null) / 2;
			
			IMAGE[2] = SpriteSheet.loadImage("effects/glass_mini.png");
			ALPHA[2] = null;
			WIDTH[2] = IMAGE[2].getWidth(null) / 2;
			HEIGHT[2] = IMAGE[2].getHeight(null) / 2;
		} catch(IOException e) {
			JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString(), "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private static final SoundFile[] SOUND = new SoundFile[IMAGE.length];
	static {
		SOUND[0] = new SoundFile("effects/glass_small.wav", SoundFile.TYPE_EFFECT);
		SOUND[1] = new SoundFile("effects/glass_normal.wav", SoundFile.TYPE_EFFECT);
		SOUND[2] = new SoundFile("effects/glass_mini.wav", SoundFile.TYPE_EFFECT);
	}
	
	private static final Color GLASS_COLOR = new Color(182, 209, 225, 50);
	private static final int ALPHA_COLOR = 0xff000000;
	
	private final BufferedImage mImage, mAlpha;
	private final Graphics2D mGraphics, mAlphaGraphics;
	
	private final Composite mComposite;
	
	private final Random rand = new Random();
	
	private boolean mIntact = true;
	
	public GlassEffect() {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		mImage = gc.createCompatibleImage(Screen.WIDTH, Screen.HEIGHT, Transparency.TRANSLUCENT);
		mGraphics = mImage.createGraphics();
		mComposite = mGraphics.getComposite();
		
		mAlpha = gc.createCompatibleImage(Screen.WIDTH, Screen.HEIGHT, Transparency.TRANSLUCENT);
		mAlphaGraphics = mAlpha.createGraphics();
	}
	
	private void createGlass() {
		mGraphics.setColor(GLASS_COLOR);
		mGraphics.fillRect(0, 0, Screen.WIDTH, Screen.HEIGHT);
		mIntact = false;
	}
	
	@Override
	public boolean effect(int x, int y) {
		if(mIntact) createGlass();
		else {
			if(mAlpha.getRGB(x, y) == ALPHA_COLOR) return false;
		}
		
		int size = rand.nextInt(IMAGE.length);
		x -= WIDTH[size];
		y -= HEIGHT[size];
		Image crack = IMAGE[size], alpha = ALPHA[size];
		
		mAlphaGraphics.drawImage(alpha, x, y, null);
		
		mGraphics.drawImage(crack, x, y, null);
		mGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
		mGraphics.drawImage(mAlpha, 0, 0, null);
		mGraphics.setComposite(mComposite);
		
		SOUND[size].play();
		
		return true;
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(mImage, 0, 0, null);
	}
	
}
