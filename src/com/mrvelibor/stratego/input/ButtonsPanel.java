package com.mrvelibor.stratego.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.game.GamePhase;
import com.mrvelibor.stratego.game.PlacementPhase;
import com.mrvelibor.stratego.game.PlayPhase;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.units.Bomb;
import com.mrvelibor.stratego.units.Captain;
import com.mrvelibor.stratego.units.Colonel;
import com.mrvelibor.stratego.units.Flag;
import com.mrvelibor.stratego.units.General;
import com.mrvelibor.stratego.units.Liutenant;
import com.mrvelibor.stratego.units.Major;
import com.mrvelibor.stratego.units.Marshal;
import com.mrvelibor.stratego.units.Miner;
import com.mrvelibor.stratego.units.Scout;
import com.mrvelibor.stratego.units.Sergeant;
import com.mrvelibor.stratego.units.Spy;
import com.mrvelibor.stratego.units.Unit;

public class ButtonsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final int[][] KILLS = { {}, { Flag.RANK, Marshal.RANK }, { Flag.RANK, Spy.RANK }, { Flag.RANK, Spy.RANK, Scout.RANK, Bomb.RANK },
			{ Flag.RANK, Spy.RANK, Scout.RANK, Miner.RANK }, { Flag.RANK, Spy.RANK, Scout.RANK, Miner.RANK, Sergeant.RANK },
			{ Flag.RANK, Spy.RANK, Scout.RANK, Miner.RANK, Sergeant.RANK, Liutenant.RANK },
			{ Flag.RANK, Spy.RANK, Scout.RANK, Miner.RANK, Sergeant.RANK, Liutenant.RANK, Captain.RANK },
			{ Flag.RANK, Spy.RANK, Scout.RANK, Miner.RANK, Sergeant.RANK, Liutenant.RANK, Captain.RANK, Major.RANK },
			{ Flag.RANK, Spy.RANK, Scout.RANK, Miner.RANK, Sergeant.RANK, Liutenant.RANK, Captain.RANK, Major.RANK, Colonel.RANK },
			{ Flag.RANK, Spy.RANK, Scout.RANK, Miner.RANK, Sergeant.RANK, Liutenant.RANK, Captain.RANK, Major.RANK, Colonel.RANK, General.RANK },
			{ Flag.RANK, Spy.RANK, Scout.RANK, Sergeant.RANK, Liutenant.RANK, Captain.RANK, Major.RANK, Colonel.RANK, General.RANK, Marshal.RANK } };
	
	private JButton[] mButtons = new JButton[12];
	private JButton mOther, mAction;
	
	private GamePhase mGame;
	
	public ButtonsPanel() {
		super();
		
		setLayout(new BorderLayout());
		
		mOther = new JButton("Other");
		mOther.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				StrategoGame.otherButton();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				if(mGame instanceof PlayPhase) ((PlayPhase) mGame).setLastMoveShown(false);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				if(mGame instanceof PlayPhase) ((PlayPhase) mGame).setLastMoveShown(true);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		mOther.setBackground(Screen.COLOR_BUTTON_BG);
		mOther.setForeground(Screen.COLOR_BUTTON_FG);
		mOther.setBorder(new EmptyBorder(8, 0, 8, 0));
		mOther.setFont(mOther.getFont().deriveFont(17f));
		add(mOther, BorderLayout.PAGE_START);
		
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(6, 2));
		add(pan, BorderLayout.CENTER);
		
		for(int i = 0; i < mButtons.length; i++) {
			final int rank = i;
			mButtons[i] = new JButton(" / ", new ImageIcon(Unit.getImage(rank)));
			mButtons[i].setToolTipText(Unit.getName(rank));
			mButtons[i].addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {
					if(mGame instanceof PlacementPhase) ((PlacementPhase) mGame).selectUnit(rank);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					for(JButton button : mButtons)
						button.setEnabled(true);
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					for(int i : KILLS[rank])
						mButtons[i].setEnabled(false);
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
			mButtons[i].setEnabled(false);
			pan.add(mButtons[i]);
		}
		
		mAction = new JButton("HELLO");
		mAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StrategoGame.actionButton();
			}
		});
		mAction.setEnabled(false);
		mAction.setBorder(new EmptyBorder(16, 0, 16, 0));
		mAction.setFont(mAction.getFont().deriveFont(21f));
		add(mAction, BorderLayout.PAGE_END);
	}
	
	public void updateRemaining(int rank, int remaining) {
		mButtons[rank].setText(Integer.toString(remaining));
		refreshActionButton();
	}
	
	private void refreshActionButton() {
		boolean enabled = true;
		
		for(int i = 0; i < mButtons.length; i++) {
			if(!"0".equals(mButtons[i].getText())) {
				enabled = false;
				break;
			}
		}
		
		mAction.setEnabled(enabled);
	}
	
	public void setActionButton(boolean enabled, String message, Color background) {
		mAction.setEnabled(enabled);
		if(message != null) mAction.setBackground(background);
		if(background != null) mAction.setText(message);
	}
	
	public void setOtherButton(boolean enabled, String message, Color background) {
		mOther.setEnabled(enabled);
		if(message != null) mOther.setBackground(background);
		if(background != null) mOther.setText(message);
	}
	
	public void setPhase(GamePhase phase) {
		mGame = phase;
		if(phase instanceof PlacementPhase) {
			PlacementPhase placement = (PlacementPhase) phase;
			int[] remaining = placement.getRemaining();
			for(int i = 0; i < mButtons.length; i++) {
				mButtons[i].setText(Integer.toString(remaining[i]));
				mButtons[i].setEnabled(remaining[i] > 0);
			}
			mAction.setText("START");
			refreshActionButton();
		}
		else {
			for(int i = 0; i < mButtons.length; i++) {
				mButtons[i].setText("/");
				mButtons[i].setEnabled(true);
			}
		}
	}
	
	public void setColors(Color background, Color foreground) {
		for(JButton button : mButtons) {
			button.setBackground(background);
			button.setForeground(foreground);
		}
	}
	
}
