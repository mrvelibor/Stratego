package com.mrvelibor.stratego.effects.tap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.sound.SoundFile;

public class LeavesParticle extends Particle {
	
	private static final int SIZE = 64;
	private static final int CENTER = SIZE / 2;
	
	private static final Image[] ANIMATION = new Image[27];
	static {
		SpriteSheet animation = new SpriteSheet("effects/leaves_sheet.png");
		for(int i = 0; i < ANIMATION.length; i++) {
			ANIMATION[i] = animation.getSprite(SIZE, i, 0);
		}
	}
	
	private static final SoundFile[] SOUND = new SoundFile[2];
	static {
		SOUND[0] = new SoundFile("effects/leaves_rustling1.wav", SoundFile.TYPE_EFFECT);
		SOUND[1] = new SoundFile("effects/leaves_rustling2.wav", SoundFile.TYPE_EFFECT);
	}
	
	private static final Shape[] BOUNDS = new Shape[34];
	static {
		BOUNDS[0] = new Polygon(new int[] { 28, 26, 68, 126, 94, 113, 156, 174, 113 }, new int[] { 30, 295, 234, 216, 159, 92, 150, 127, 27 }, 9);
		BOUNDS[1] = new Polygon(new int[] { 295, 341, 390, 395, 424, 446, 507, 549, 597, 622, 606, 629, 630, 675, 676, 645, 629, 676, 673, 637, 600, 597 }, new int[] {
				25, 104, 94, 43, 45, 108, 89, 146, 139, 179, 224, 220, 275, 257, 200, 181, 121, 107, 27, 76, 24, 25 }, 21);
		BOUNDS[2] = new Polygon(new int[] { 25, 27, 238, 209, 165, 106, 64, 101, 204, 249, 330, 402, 304, 200, 138 }, new int[] { 473, 677, 677, 624, 652,
				661, 649, 616, 573, 581, 673, 674, 576, 531, 557 }, 15);
		BOUNDS[3] = new Polygon(new int[] { 542, 530, 459, 531, 682, 682 }, new int[] { 462, 508, 554, 669, 682, 561 }, 6);
		BOUNDS[4] = new Ellipse2D.Float(139, 312, 18, 18);
		BOUNDS[5] = new Ellipse2D.Float(49, 377, 18, 18);
		BOUNDS[6] = new Ellipse2D.Float(177, 447, 18, 18);
		BOUNDS[7] = new Ellipse2D.Float(295, 458, 21, 21);
		BOUNDS[8] = new Ellipse2D.Float(329, 539, 18, 18);
		BOUNDS[9] = new Ellipse2D.Float(377, 562, 18, 18);
		BOUNDS[10] = new Ellipse2D.Float(372, 590, 18, 18);
		BOUNDS[11] = new Ellipse2D.Float(410, 613, 18, 18);
		BOUNDS[12] = new Ellipse2D.Float(417, 565, 18, 18);
		BOUNDS[13] = new Ellipse2D.Float(545, 322, 18, 18);
		BOUNDS[14] = new Ellipse2D.Float(547, 263, 18, 18);
		BOUNDS[15] = new Ellipse2D.Float(261, 53, 18, 18);
		BOUNDS[16] = new Ellipse2D.Float(422, 142, 18, 18);
		BOUNDS[17] = new Ellipse2D.Float(619, 412, 18, 18);
		BOUNDS[18] = new Ellipse2D.Float(516, 450, 20, 20);
		BOUNDS[19] = new Ellipse2D.Float(395, 448, 18, 18);
		BOUNDS[20] = new Ellipse2D.Float(489, 111, 18, 18);
		BOUNDS[21] = new Ellipse2D.Float(372, 186, 20, 20);
		BOUNDS[22] = new Ellipse2D.Float(406, 501, 18, 18);
		BOUNDS[23] = new Ellipse2D.Float(98, 489, 18, 18);
		BOUNDS[24] = new Ellipse2D.Float(533, 143, 18, 18);
		BOUNDS[25] = new Ellipse2D.Float(280, 201, 18, 18);
		BOUNDS[26] = new Ellipse2D.Float(273, 536, 18, 18);
		BOUNDS[27] = new Ellipse2D.Float(619, 264, 18, 18);
		BOUNDS[28] = new Ellipse2D.Float(598, 213, 18, 18);
		BOUNDS[29] = new Ellipse2D.Float(397, 499, 18, 18);
		BOUNDS[30] = new Ellipse2D.Float(527, 223, 17, 17);
		BOUNDS[31] = new Ellipse2D.Float(224, 162, 18, 18);
		BOUNDS[32] = new Ellipse2D.Float(447, 544, 18, 18);
		BOUNDS[33] = new Ellipse2D.Float(127, 202, 18, 18);
	}

	
	public static void drawBounds(Graphics2D g) {
		Font font = g.getFont();
		g.setFont(font.deriveFont(13f));
		g.setColor(Color.PINK);
		
		for(int i = 0; i < BOUNDS.length; i++) {
			g.draw(BOUNDS[i]);
			Rectangle2D bounds = BOUNDS[i].getBounds2D();
			g.drawString(Integer.toString(i), (int) bounds.getX(), (int) bounds.getY());
		}
		
		g.setFont(font);
	}
	
	public static boolean contains(int x, int y) {
		for(Shape shape : BOUNDS) {
			if(shape.contains(x, y)) return true;
		}
		return false;
	}
	
	public LeavesParticle(TapEffect parent, int x, int y) {
		super(parent, x - CENTER, y - CENTER);
		SOUND[rand.nextInt(SOUND.length)].play();
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(ANIMATION[mAnim], mX, mY, null);
		if(++mAnim >= ANIMATION.length) remove();
	}
	
}