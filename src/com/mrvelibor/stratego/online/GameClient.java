package com.mrvelibor.stratego.online;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.game.GamePhase;
import com.mrvelibor.stratego.game.MenuPhase;
import com.mrvelibor.stratego.game.PlacementPhase;
import com.mrvelibor.stratego.sound.SoundFile;

public class GameClient extends JPanel implements ServerCommands, GameCommands, Runnable {
	
	private static final long serialVersionUID = 1L;
	
	public static final SoundFile SOUND_NEW_MESSAGE = new SoundFile("notifs/chat.wav", SoundFile.TYPE_NOTIF);
	
	private static Socket sSocket = null;
	
	private static PrintStream sOutput = null;
	private static BufferedReader sInput = null;
	
	private static boolean sConnected = false;
	
	public final GameServer server;
	
	private String mName;
	
	private StyledDocument mText;
	
	JTextPane mTextPane;
	JTextField mIpField, mPortField, mNameField, mMessageField;
	JButton mConnectButton, mSendButton;
	
	private static final SimpleAttributeSet STYLE_RECEIVED, STYLE_SENT, STYLE_CONFIRMED, STYLE_BUG, STYLE_ERROR, STYLE_SERVER;
	static {
		STYLE_RECEIVED = new SimpleAttributeSet();
		StyleConstants.setForeground(STYLE_RECEIVED, Color.BLACK);
		StyleConstants.setBold(STYLE_RECEIVED, false);
		StyleConstants.setAlignment(STYLE_RECEIVED, StyleConstants.ALIGN_LEFT);
		StyleConstants.setSpaceBelow(STYLE_RECEIVED, 0);
		StyleConstants.setSpaceAbove(STYLE_RECEIVED, 0);
		StyleConstants.setLeftIndent(STYLE_RECEIVED, 0);
		StyleConstants.setRightIndent(STYLE_RECEIVED, 0);
		
		STYLE_SENT = new SimpleAttributeSet();
		StyleConstants.setForeground(STYLE_SENT, Color.LIGHT_GRAY);
		StyleConstants.setBold(STYLE_SENT, false);
		StyleConstants.setAlignment(STYLE_SENT, StyleConstants.ALIGN_LEFT);
		StyleConstants.setSpaceBelow(STYLE_SENT, 0);
		StyleConstants.setSpaceAbove(STYLE_SENT, 0);
		StyleConstants.setLeftIndent(STYLE_SENT, 0);
		StyleConstants.setRightIndent(STYLE_SENT, 0);
		
		STYLE_CONFIRMED = new SimpleAttributeSet();
		StyleConstants.setForeground(STYLE_CONFIRMED, Color.DARK_GRAY);
		StyleConstants.setBold(STYLE_CONFIRMED, false);
		StyleConstants.setAlignment(STYLE_CONFIRMED, StyleConstants.ALIGN_LEFT);
		StyleConstants.setSpaceBelow(STYLE_CONFIRMED, 0);
		StyleConstants.setSpaceAbove(STYLE_CONFIRMED, 0);
		StyleConstants.setLeftIndent(STYLE_CONFIRMED, 0);
		StyleConstants.setRightIndent(STYLE_CONFIRMED, 0);
		
		STYLE_BUG = new SimpleAttributeSet();
		StyleConstants.setForeground(STYLE_BUG, Color.YELLOW);
		StyleConstants.setBold(STYLE_BUG, false);
		StyleConstants.setAlignment(STYLE_BUG, StyleConstants.ALIGN_LEFT);
		StyleConstants.setSpaceBelow(STYLE_BUG, 3);
		StyleConstants.setSpaceAbove(STYLE_BUG, 3);
		StyleConstants.setLeftIndent(STYLE_BUG, 0);
		StyleConstants.setRightIndent(STYLE_BUG, 0);
		
		STYLE_ERROR = new SimpleAttributeSet();
		StyleConstants.setForeground(STYLE_ERROR, Color.RED);
		StyleConstants.setBold(STYLE_ERROR, false);
		StyleConstants.setAlignment(STYLE_ERROR, StyleConstants.ALIGN_LEFT);
		StyleConstants.setSpaceBelow(STYLE_ERROR, 3);
		StyleConstants.setSpaceAbove(STYLE_ERROR, 3);
		StyleConstants.setLeftIndent(STYLE_ERROR, 0);
		StyleConstants.setRightIndent(STYLE_ERROR, 0);
		
		STYLE_SERVER = new SimpleAttributeSet();
		StyleConstants.setForeground(STYLE_SERVER, Color.BLUE);
		StyleConstants.setBold(STYLE_SERVER, true);
		StyleConstants.setAlignment(STYLE_SERVER, StyleConstants.ALIGN_CENTER);
		StyleConstants.setSpaceBelow(STYLE_SERVER, 3);
		StyleConstants.setSpaceAbove(STYLE_SERVER, 3);
		StyleConstants.setLeftIndent(STYLE_SERVER, 6);
		StyleConstants.setRightIndent(STYLE_SERVER, 6);
	}
	
	public GameClient() {
		super();
		setPreferredSize(new Dimension(320, 240));
		setLayout(new BorderLayout());
		
		JPanel pan = new JPanel();
		pan.setLayout(new GridBagLayout());
		pan.setBorder(new EmptyBorder(6, 6, 6, 6));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		ActionListener connectListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sConnected) {
					disconnect();
				}
				else {
					String ip = mIpField.getText();
					int port = Integer.parseInt(mPortField.getText());
					connect(ip, port);
				}
			}
		};
		
		JLabel title = new JLabel("NAME", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(17f));
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.gridwidth = 2;
		pan.add(title, c);
		c.gridwidth = 1;
		
		mNameField = new JTextField(StrategoGame.sSettings.getName());
		mNameField.setFont(mNameField.getFont().deriveFont(17f));
		mNameField.addActionListener(connectListener);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 3;
		pan.add(mNameField, c);
		c.gridwidth = 1;
		
		title = new JLabel("JOIN GAME", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(17f));
		c.gridx = 1;
		c.weightx = 0;
		c.gridy = 2;
		pan.add(new JPanel(), c);
		c.gridy = 3;
		c.gridwidth = 2;
		pan.add(title, c);
		c.gridwidth = 1;
		
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		pan.add(new JLabel(" IP: ", SwingConstants.RIGHT), c);
		
		mIpField = new JTextField("localhost");
		mIpField.addActionListener(connectListener);
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 0.6;
		pan.add(mIpField, c);
		
		c.gridx = 2;
		c.gridy = 4;
		c.weightx = 0;
		pan.add(new JLabel(" port: ", SwingConstants.RIGHT), c);
		
		mPortField = new JTextField("2209");
		mPortField.addActionListener(connectListener);
		c.gridx = 3;
		c.gridy = 4;
		c.weightx = 0.4;
		pan.add(mPortField, c);
		
		mConnectButton = new JButton("Connect");
		mConnectButton.addActionListener(connectListener);
		c.gridx = 4;
		c.gridy = 4;
		c.weightx = 0;
		pan.add(mConnectButton, c);
		
		title = new JLabel("CREATE GAME", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(17f));
		c.gridx = 1;
		c.weightx = 0;
		c.gridy = 5;
		pan.add(new JPanel(), c);
		c.gridy = 6;
		c.gridwidth = 2;
		pan.add(title, c);
		c.gridwidth = 1;
		
		server = new GameServer(this);
		
		c.gridx = 0;
		c.gridy = 7;
		c.weightx = 0;
		pan.add(new JLabel(" IP: ", SwingConstants.RIGHT), c);
		
		c.gridx = 1;
		c.gridy = 7;
		c.weightx = 0.6;
		pan.add(server.mIpText, c);
		
		c.gridx = 2;
		c.gridy = 7;
		c.weightx = 0;
		pan.add(new JLabel(" port: ", SwingConstants.RIGHT), c);
		
		c.gridx = 3;
		c.gridy = 7;
		c.weightx = 0.4;
		pan.add(server.mPortText, c);
		
		c.gridx = 4;
		c.gridy = 7;
		c.weightx = 0;
		pan.add(server.mOpen, c);
		
		add(pan, BorderLayout.PAGE_START);
		
		mText = new DefaultStyledDocument();
		mTextPane = new JTextPane(mText);
		mTextPane.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(mTextPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll, BorderLayout.CENTER);
		
		pan = new JPanel();
		pan.setLayout(new GridBagLayout());
		
		ActionListener sendAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!sConnected) return;
				say(mMessageField.getText());
				mMessageField.setText("");
			}
		};
		
		mMessageField = new JTextField();
		mMessageField.addActionListener(sendAction);
		mMessageField.setEnabled(false);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		pan.add(mMessageField, c);
		
		mSendButton = new JButton("Send");
		mSendButton.addActionListener(sendAction);
		mSendButton.setEnabled(false);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		pan.add(mSendButton, c);
		
		add(pan, BorderLayout.PAGE_END);
	}
	
	public void display(String text, SimpleAttributeSet attributes) {
		try {
			int length = mText.getLength();
			mText.setParagraphAttributes(length, 0, attributes, false);
			mText.insertString(length, text + '\n', attributes);
			
			mTextPane.setCaretPosition(mText.getLength());
		} catch(BadLocationException e2) {
			e2.printStackTrace();
		}
	}
	
	public void connect(int port) {
		connect("localhost", port);
		mConnectButton.setEnabled(false);
		server.mOpen.setEnabled(true);
	}
	
	public void connect(String ip, int port) {
		if(sConnected) return;
		
		mName = mNameField.getText();
		if(mName.isEmpty()) {
			display("Name empty.", STYLE_ERROR);
			return;
		}
		else {
			StrategoGame.sSettings.setName(mName);
		}
		try {
			sSocket = new Socket(ip, port);
			
			sOutput = new PrintStream(sSocket.getOutputStream());
			sInput = new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
			
			new Thread(this, "Client").start();
			
			mIpField.setEnabled(false);
			mPortField.setEnabled(false);
			mNameField.setEnabled(false);
			mMessageField.setEnabled(true);
			mSendButton.setEnabled(true);
			server.mOpen.setEnabled(false);
			server.mPortText.setEnabled(false);
			
			mConnectButton.setText("    D/C    ");
		} catch(IOException e) {
			display(e.getLocalizedMessage(), STYLE_ERROR);
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		if(!sConnected) return;
		
		if(sOutput != null) sOutput.println(COMMAND_QUIT);
		
		mIpField.setEnabled(true);
		mPortField.setEnabled(true);
		mNameField.setEnabled(true);
		mConnectButton.setEnabled(true);
		mMessageField.setEnabled(false);
		mSendButton.setEnabled(false);
		server.mOpen.setEnabled(true);
		server.mPortText.setEnabled(true);
		
		mConnectButton.setText("Connect");
		
		StrategoGame.setPhase(new MenuPhase());
		display("Disconnected.", STYLE_SERVER);
		sConnected = false;
	}
	
	public void wrongLine(String line) {
		display(line, STYLE_BUG);
	}
	
	private int mLastMessageStart = -1, mLastMessageLength = -1;
	
	private void say(String message) {
		mLastMessageStart = mText.getLength();
		mLastMessageLength = message.length() + 9;
		
		display("< YOU >: " + message, STYLE_SENT);
		
		sOutput.println(COMMAND_SAY + message);
	}
	
	private void confirm() {
		if(mLastMessageStart < 0 || mLastMessageLength <= 0) return;
		
		mText.setCharacterAttributes(mLastMessageStart, mLastMessageLength, STYLE_CONFIRMED, false);
		
		mLastMessageStart = mLastMessageLength = -1;
	}
	
	public void place(Board board, int team) {
		String line = COMMAND_PLACE + board.getString(team);
		sOutput.println(line);
	}
	
	public void play(Field start, Field move, Field attack) {
		StringBuilder sb = new StringBuilder(COMMAND_PLAY);
		
		sb.append(start.x).append(start.y).append(';');
		if(move != null) sb.append(move.x).append(move.y);
		sb.append(';');
		if(attack != null) sb.append(attack.x).append(attack.y);
		
		sOutput.println(sb.toString());
	}
	
	public void ping(int x, int y) {
		if(!sConnected) return;
		
		StringBuilder sb = new StringBuilder(COMMAND_PING);
		sb.append(x).append(';').append(y);
		sOutput.println(sb.toString());
	}
	
	@Override
	public void run() {
		String line;
		try {
			sConnected = true;
			while((line = sInput.readLine()) != null) {
				if(line.startsWith(COMMAND_CONFIRM_MESSAGE)) {
					confirm();
				}
				else if(line.startsWith(COMMAND_SAY)) {
					display(line.substring(COMMAND_SAY.length()), STYLE_RECEIVED);
					SOUND_NEW_MESSAGE.play();
				}
				else if(line.startsWith(COMMAND_MESSAGE)) {
					display(line.substring(COMMAND_MESSAGE.length()), STYLE_SERVER);
				}
				else if(line.startsWith(COMMAND_GAME)) {
					StrategoGame.serverResponse(line);
				}
				else if(line.startsWith(COMMAND_NAME)) {
					int team = Character.getNumericValue(line.charAt(COMMAND_NAME.length()));
					sOutput.println(COMMAND_NAME + mName);
					GamePhase.setTeam(team);
					StrategoGame.setPhase(new PlacementPhase(false));
				}
				else if(line.startsWith(COMMAND_CLOSE)) {
					break;
				}
				else wrongLine(line);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			disconnect();
			
			if(sOutput != null) {
				sOutput.close();
				sOutput = null;
			}
			try {
				if(sInput != null) {
					sInput.close();
					sInput = null;
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
			try {
				if(sSocket != null) {
					sSocket.close();
					sSocket = null;
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
