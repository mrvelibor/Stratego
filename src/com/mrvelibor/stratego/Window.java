package com.mrvelibor.stratego;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import com.mrvelibor.stratego.game.BenchmarkPhase;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.input.ButtonsPanel;
import com.mrvelibor.stratego.input.Mouse;
import com.mrvelibor.stratego.online.GameClient;
import com.mrvelibor.stratego.sound.MusicFile;
import com.mrvelibor.stratego.sound.SoundFile;
import com.mrvelibor.stratego.sound.Volume;

public class Window extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	public final Screen screen;
	public final GameClient client;
	public final ButtonsPanel controls;
	
	public final Mouse mouse;
	
	public Window(String title) {
		super(title);
		setLayout(new BorderLayout());
		
		screen = new Screen();
		add(screen, BorderLayout.CENTER);
		
		mouse = new Mouse();
		screen.addMouseListener(mouse);
		screen.addMouseMotionListener(mouse);
		
		controls = new ButtonsPanel();
		add(controls, BorderLayout.LINE_START);
		
		client = new GameClient();
		add(client, BorderLayout.LINE_END);
		
		addMenu();
		
		try {
			setIconImage(SpriteSheet.loadImage("icon.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setFocusable(true);
		setResizable(false);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void addMenu() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem radio;
		ButtonGroup group;
		
		menuBar = new JMenuBar();
		menuBar.setFocusable(false);
		
		menu = new JMenu("Game");
		menuBar.add(menu);
		{
			menuItem = new JMenuItem("Hello");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// nista
				}
			});
			menuItem.setEnabled(false);
			menu.add(menuItem);
			
			menu.addSeparator();
			
			menuItem = new JMenuItem("Exit");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					StrategoGame.stop();
					client.disconnect();
					client.server.closePort();
					dispose();
					System.exit(0);
				}
			});
			menu.add(menuItem);
		}
		
		menu = new JMenu("Options");
		menuBar.add(menu);
		{
			submenu = new JMenu("Game volume");
			menu.add(submenu);
			group = new ButtonGroup();
			
			Volume volume = SoundFile.getVolume(SoundFile.TYPE_GAME);
			
			radio = new JRadioButtonMenuItem("Off");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.OFF, SoundFile.TYPE_GAME);
					SoundFile.playTest(SoundFile.TYPE_GAME);
				}
			});
			radio.setSelected(volume.equals(Volume.OFF));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Low");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.LOW, SoundFile.TYPE_GAME);
					SoundFile.playTest(SoundFile.TYPE_GAME);
				}
			});
			radio.setSelected(volume.equals(Volume.LOW));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Medium");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.MEDIUM, SoundFile.TYPE_GAME);
					SoundFile.playTest(SoundFile.TYPE_GAME);
				}
			});
			radio.setSelected(volume.equals(Volume.MEDIUM));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("High");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.HIGH, SoundFile.TYPE_GAME);
					SoundFile.playTest(SoundFile.TYPE_GAME);
				}
			});
			radio.setSelected(volume.equals(Volume.HIGH));
			group.add(radio);
			submenu.add(radio);
			submenu = new JMenu("Effects volume");
			menu.add(submenu);
			group = new ButtonGroup();
			
			volume = SoundFile.getVolume(SoundFile.TYPE_EFFECT);
			
			radio = new JRadioButtonMenuItem("Off");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.OFF, SoundFile.TYPE_EFFECT);
					SoundFile.playTest(SoundFile.TYPE_EFFECT);
				}
			});
			radio.setSelected(volume.equals(Volume.OFF));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Low");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.LOW, SoundFile.TYPE_EFFECT);
					SoundFile.playTest(SoundFile.TYPE_EFFECT);
				}
			});
			radio.setSelected(volume.equals(Volume.LOW));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Medium");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.MEDIUM, SoundFile.TYPE_EFFECT);
					SoundFile.playTest(SoundFile.TYPE_EFFECT);
				}
			});
			radio.setSelected(volume.equals(Volume.MEDIUM));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("High");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.HIGH, SoundFile.TYPE_EFFECT);
					SoundFile.playTest(SoundFile.TYPE_EFFECT);
				}
			});
			radio.setSelected(volume.equals(Volume.HIGH));
			group.add(radio);
			submenu.add(radio);
			submenu = new JMenu("Notification volume");
			menu.add(submenu);
			group = new ButtonGroup();
			
			volume = SoundFile.getVolume(SoundFile.TYPE_NOTIF);
			
			radio = new JRadioButtonMenuItem("Off");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.OFF, SoundFile.TYPE_NOTIF);
					SoundFile.playTest(SoundFile.TYPE_NOTIF);
				}
			});
			radio.setSelected(volume.equals(Volume.OFF));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Low");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.LOW, SoundFile.TYPE_NOTIF);
					SoundFile.playTest(SoundFile.TYPE_NOTIF);
				}
			});
			radio.setSelected(volume.equals(Volume.LOW));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Medium");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.MEDIUM, SoundFile.TYPE_NOTIF);
					SoundFile.playTest(SoundFile.TYPE_NOTIF);
				}
			});
			radio.setSelected(volume.equals(Volume.MEDIUM));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("High");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SoundFile.setVolume(Volume.HIGH, SoundFile.TYPE_NOTIF);
					SoundFile.playTest(SoundFile.TYPE_NOTIF);
				}
			});
			radio.setSelected(volume.equals(Volume.HIGH));
			group.add(radio);
			submenu.add(radio);
			
			submenu = new JMenu("Music volume");
			menu.add(submenu);
			group = new ButtonGroup();
			
			volume = MusicFile.getVolume();
			
			radio = new JRadioButtonMenuItem("Off");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicFile.setVolume(Volume.OFF);
					// MusicFile.TEST_SONG.play();
				}
			});
			radio.setSelected(volume.equals(Volume.OFF));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Low");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicFile.setVolume(Volume.LOW);
					// MusicFile.TEST_SONG.play();
				}
			});
			radio.setSelected(volume.equals(Volume.LOW));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("Medium");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicFile.setVolume(Volume.MEDIUM);
					// MusicFile.TEST_SONG.play();
				}
			});
			radio.setSelected(volume.equals(Volume.MEDIUM));
			group.add(radio);
			submenu.add(radio);
			
			radio = new JRadioButtonMenuItem("High");
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicFile.setVolume(Volume.HIGH);
					// MusicFile.TEST_SONG.play();
				}
			});
			radio.setSelected(volume.equals(Volume.HIGH));
			group.add(radio);
			submenu.add(radio);
			
			menu.addSeparator();
			
			menuItem = new JMenuItem("Benchmark");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					StrategoGame.setPhase(new BenchmarkPhase());
				}
			});
			menu.add(menuItem);
		}
		
		menu = new JMenu("Help");
		menuBar.add(menu);
		{
			menuItem = new JMenuItem("How to play");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Window.this, "Hello", "Help", JOptionPane.QUESTION_MESSAGE);
				}
			});
			menu.add(menuItem);
			
			menuItem = new JMenuItem("Troubleshoot");
			menuItem.addActionListener(new ActionListener() {
				String testVersion = "1.7.0_45-b18", activeVersion = System.getProperty("java.runtime.version");
				int status = getStatus();
				
				int getStatus() {
					int comp = activeVersion.compareTo(testVersion);
					if(comp > 0) return JOptionPane.QUESTION_MESSAGE;
					else if(comp < 0) return JOptionPane.ERROR_MESSAGE;
					else return JOptionPane.INFORMATION_MESSAGE;
				}
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// System.out.print(System.getProperty("java.runtime.version"));
					JOptionPane.showMessageDialog(null, "Tested on Windows 7 Ultimate 64-Bit\n" + "with Java Runtime Environment:\n    v" + testVersion
							+ "\n\nYou're using:\n    " + System.getProperty("sun.arch.data.model") + "-Bit JRE\n    v" + activeVersion, "Troubleshoot", status);
				}
			});
			menu.add(menuItem);
			menu.addSeparator();
			
			menuItem = new JMenuItem("About");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(Window.this, "Stratego\n" + "    by Velibor Baèujkov\n" + "    2014.", "About",
							JOptionPane.INFORMATION_MESSAGE);
				}
			});
			menu.add(menuItem);
		}
		
		setJMenuBar(menuBar);
	}
	
}
