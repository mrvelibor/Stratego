package com.mrvelibor.stratego.graphics;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.mrvelibor.stratego.StrategoGame;

public class SpriteSheet {
	
	public static final SpriteSheet UNITS = new SpriteSheet("sprites/sheet_units.png"), BACKGROUNDS = new SpriteSheet("sprites/sheet_backs.png"),
			MISC = new SpriteSheet("sprites/sheet_misc.png");
	
	private final BufferedImage mImage;
	
	public SpriteSheet(String path) {
		BufferedImage image;
		try {
			image = loadImage(path);
		} catch(IOException e) {
			image = null;
			JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString(), "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		mImage = image;
	}
	
	public static BufferedImage loadImage(String fileName) throws IOException {
		String path = "/images/" + fileName;
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
		if(image.getColorModel().equals(gc.getColorModel())) return image;
		
		BufferedImage newImage = gc.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
		Graphics2D g2d = (Graphics2D) newImage.getGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		
		return newImage;
	}
	
	public BufferedImage getSprite(int width, int height, int x, int y) {
		int xSize = x * width, ySize = y * height;
		
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		BufferedImage image = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		Graphics2D g2d = image.createGraphics();
		g2d.drawImage(mImage, 0, 0, width, height, xSize, ySize, xSize + width, ySize + height, null);
		g2d.dispose();
		
		return image;
	}
	
	public BufferedImage getSprite(int size, int x, int y) {
		return getSprite(size, size, x, y);
	}
}
