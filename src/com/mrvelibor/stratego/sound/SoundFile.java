package com.mrvelibor.stratego.sound;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import com.mrvelibor.stratego.StrategoGame;

public class SoundFile {
	
	public static final int TYPE_NOTIF = 0, TYPE_EFFECT = 1, TYPE_GAME = 2;
	
	public static void playTest(int type) {
		new SoundFile("test.wav", type).play();
	}
	
	private static Volume[] sVolume = new Volume[3];
	static {
		sVolume[0] = StrategoGame.sSettings.getNotifVolume();
		sVolume[1] = StrategoGame.sSettings.getEffectVolume();
		sVolume[2] = StrategoGame.sSettings.getGameVolume();
	}
	
	public static void setVolume(Volume volume, int type) {
		sVolume[type] = volume;
		switch(type)
			{
			case TYPE_NOTIF:
				StrategoGame.sSettings.setNotifVolume(volume);
				break;
			case TYPE_EFFECT:
				StrategoGame.sSettings.setEffectVolume(volume);
				break;
			case TYPE_GAME:
				StrategoGame.sSettings.setGameVolume(volume);
				break;
			}
	}
	
	public static Volume getVolume(int type) {
		return sVolume[type];
	}
	
	private final int mType;
	
	private final String mFileName;
	
	public SoundFile(String fileName, int type) {
		mFileName = "/sounds/" + fileName;
		mType = type;
	}
	
	private LineListener mListener = null;
	
	public void addLineListener(LineListener listener) {
		mListener = listener;
	}
	
	public void play() {
		if(Volume.OFF.equals(sVolume[mType]) || (mType != TYPE_NOTIF && !StrategoGame.sWindow.isFocused())) return;
		try {
			URL url = getClass().getResource(mFileName);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			
			FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(sVolume[mType].floatValue);
			
			clip.setFramePosition(0);
			if(mListener != null) {
				clip.addLineListener(mListener);
				mListener = null;
			}
			
			clip.start();
		} catch(UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString(), "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
}
