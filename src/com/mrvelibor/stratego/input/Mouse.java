package com.mrvelibor.stratego.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.graphics.Screen;

public class Mouse implements MouseListener, MouseMotionListener {
	
	private static int sPosX = -1, sPosY = -1;
	private static int sFieldX = -1, sFieldY = -1;
	
	private static boolean sMoved = false;
	
	private static boolean[] sButtons = new boolean[5];
	
	public static boolean pressed() {
		for(int i = 0; i < sButtons.length; i++)
			if(sButtons[i]) return true;
		return false;
	}
	
	public static boolean leftClick() {
		return sButtons[0];
	}
	
	public static boolean rightClick() {
		return sButtons[2];
	}
	
	public static boolean middleClick() {
		return sButtons[1];
	}
	
	public static boolean button4() {
		return sButtons[3];
	}
	
	public static boolean button5() {
		return sButtons[4];
	}
	
	private static final int OFFSET = Field.SIZE / 2, X_START = OFFSET, X_END = Screen.WIDTH - OFFSET, Y_START = OFFSET, Y_END = Screen.HEIGHT - OFFSET;
	
	private static void calculateFieldCoordinates() {
		if(sPosX < X_START || sPosX >= X_END) sFieldX = -1;
		else sFieldX = (sPosX - OFFSET) / Field.SIZE;
		
		if(sPosY < Y_START || sPosY >= Y_END) sFieldY = -1;
		else sFieldY = (sPosY - OFFSET) / Field.SIZE;
		
		sMoved = false;
	}
	
	public static int getFieldX() {
		if(sMoved) calculateFieldCoordinates();
		return sFieldX;
	}
	
	public static int getFieldY() {
		if(sMoved) calculateFieldCoordinates();
		return sFieldY;
	}
	
	public static int getX() {
		return sPosX;
	}
	
	public static int getY() {
		return sPosY;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		sButtons[e.getButton() - 1] = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		sPosX = e.getX();
		sPosY = e.getY();
		sMoved = true;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
	
	public static void clear() {
		for(int i = 0; i < sButtons.length; sButtons[i++] = false);
	}
}
