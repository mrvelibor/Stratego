package com.mrvelibor.stratego.sound;

import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.swing.JOptionPane;

import com.mrvelibor.stratego.StrategoGame;

public class MusicFile {
	
	private static final Sequencer sPlayer;
	private static final Synthesizer sSynthesizer;
	// private static final Receiver sReceiver;
	static {
		Sequencer sequencer = null;
		Synthesizer synthesizer = null;
		Receiver receiver = null;
		try {
			sequencer = MidiSystem.getSequencer(true);
			synthesizer = MidiSystem.getSynthesizer();
			
			sequencer.open();
			synthesizer.open();
			
			if(synthesizer.getDefaultSoundbank() == null) {
				receiver = MidiSystem.getReceiver();
			}
			else {
				receiver = synthesizer.getReceiver();
			}
			sequencer.getTransmitter().setReceiver(receiver);
		} catch(MidiUnavailableException e) {
			JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString(), "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		sPlayer = sequencer;
		sSynthesizer = synthesizer;
		// sReceiver = receiver;
	}
	
	private static final Sequence RESET_SEQUENCE = new MusicFile("reset.mid").mSong;
	
	public static final MusicFile TEST_SONG = new MusicFile("test.mid", 0), SONG_PREPARATION = new MusicFile("FromHere.mid"),
			SONG_BATTLE_1 = new MusicFile("Drina.mid"), SONG_BATTLE_2 = new MusicFile("DarkKnight.mid");
	
	public static void stop() {
		sPlayer.stop();
	}
	
	private static Volume sVolume = StrategoGame.sSettings.getMusicVolume();
	
	public static void setVolume(Volume volume) {
		sVolume = volume;
		StrategoGame.sSettings.setMusicVolume(volume);
		
		updateVolume();
	}
	
	public static Volume getVolume() {
		return sVolume;
	}
	
	private static void updateVolume() {
		if(sVolume.equals(Volume.OFF)) {
			stop();
			return;
		}
		
		/*ShortMessage volMessage = new ShortMessage();
		for(int i = 0; i < 16; i++) {
			try {
				volMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, sVolume.intValue);
			} catch(InvalidMidiDataException e) {}
			sReceiver.send(volMessage, -1);
		}*/
		MidiChannel[] channels = sSynthesizer.getChannels();
		for(int i = 0; i < channels.length; i++)
			channels[i].controlChange(7, sVolume.intValue);
	}
	
	private final Sequence mSong;
	
	private int mLoopCount;
	
	public MusicFile(String fileName, int loopCount) {
		String path = "/sounds/music/" + fileName;
		
		Sequence sequence = null;
		try {
			URL url = getClass().getResource(path);
			sequence = MidiSystem.getSequence(url);
		} catch(InvalidMidiDataException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		mSong = sequence;
		mLoopCount = loopCount;
	}
	
	public MusicFile(String fileName) {
		this(fileName, Sequencer.LOOP_CONTINUOUSLY);
	}
	
	public void play() {
		if(sVolume.equals(Volume.OFF) || sPlayer == null || mSong == null) return;
		
		try {
			if(mSong.equals(sPlayer.getSequence())) {
				sPlayer.setSequence(RESET_SEQUENCE);
			}
			sPlayer.setSequence(mSong);
			
			sPlayer.setLoopCount(mLoopCount);
			sPlayer.start();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(200);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					updateVolume();
				}
			}).start();
		} catch(InvalidMidiDataException e) {
			JOptionPane.showMessageDialog(StrategoGame.sWindow, e.toString(), "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
}