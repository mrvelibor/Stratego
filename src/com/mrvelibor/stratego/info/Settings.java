package com.mrvelibor.stratego.info;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.sound.Volume;

public class Settings extends Properties {
	
	private static final long serialVersionUID = 1L;
	
	private final String fileName;
	
	public Settings(String fileName) {
		this.fileName = fileName;
		try {
			load(new FileInputStream(fileName));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString(), "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return getProperty("name", "Player");
	}
	
	public Volume getGameVolume() {
		return Volume.get(getProperty("gamevol", "LOW"));
	}
	
	public Volume getEffectVolume() {
		return Volume.get(getProperty("effectsvol", "MEDIUM"));
	}
	
	public Volume getNotifVolume() {
		return Volume.get(getProperty("notifsvol", "MEDIUM"));
	}
	
	public Volume getMusicVolume() {
		return Volume.get(getProperty("musicvol", "LOW"));
	}
	
	public synchronized void setName(String name) {
		setProperty("name", name);
		saveFile();
	}
	
	public synchronized void setGameVolume(Volume volume) {
		setProperty("gamevol", volume.name());
		saveFile();
	}
	
	public synchronized void setEffectVolume(Volume volume) {
		setProperty("effectsvol", volume.name());
		saveFile();
	}
	
	public synchronized void setNotifVolume(Volume volume) {
		setProperty("notifsvol", volume.name());
		saveFile();
	}
	
	public synchronized void setMusicVolume(Volume volume) {
		setProperty("musicvol", volume.name());
		saveFile();
	}
	
	@Override
	public synchronized void clear() {
		super.clear();
		saveFile();
	}
	
	private void saveFile() {
		try {
			store(new FileOutputStream(fileName), "Please keep this file in the same folder as Stratego.jar");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
