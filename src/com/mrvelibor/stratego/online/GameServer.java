package com.mrvelibor.stratego.online;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.game.GamePhase;
import com.mrvelibor.stratego.units.Flag;
import com.mrvelibor.stratego.units.Unit;

public class GameServer implements ServerCommands, GameCommands, Runnable {
	
	private boolean mRunning = false;
	
	private ServerThread mClients[] = new ServerThread[2];
	
	private ServerSocket mServer;
	
	JTextField mIpText, mPortText;
	JButton mOpen;
	// JTextArea mMessage;
	
	private final GameClient client;
	
	private Board mBoard = new Board(true);
	
	public GameServer(GameClient client) {
		this.client = client;
		
		ActionListener hostListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mRunning) {
					closePort();
				}
				else {
					int port = Integer.parseInt(mPortText.getText());
					openPort(port);
				}
			}
		};
		
		mIpText = new JTextField("000.000.000.000");
		mIpText.setEditable(false);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL[] urls = new URL[3];
				URI google = null;
				try {
					urls[0] = new URL("http://echoip.com/");
					urls[1] = new URL("http://icanhazip.com/");
					urls[2] = new URL("http://myip.dnsdynamic.com/");
					google = new URL("https://www.google.com/search?q=my%20ip").toURI();
				} catch(MalformedURLException | URISyntaxException e) {
					e.printStackTrace();
				}
				
				for(URL url : urls) {
					Scanner scanner = null;
					try {
						scanner = new Scanner(url.openStream());
						String ip = scanner.next();
						mIpText.setText(ip);
						return;
					} catch(IOException e) {
						// e.printStackTrace();
					} finally {
						if(scanner != null) scanner.close();
					}
				}
				
				final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if(google != null && desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					mIpText.setText("click here");
					
					final URI searchIp = google;
					mIpText.addMouseListener(new MouseListener() {
						@Override
						public void mouseClicked(MouseEvent e) {
							try {
								desktop.browse(searchIp);
							} catch(Exception ex) {
								ex.printStackTrace();
							}
						}
						
						@Override
						public void mousePressed(MouseEvent e) {}
						
						@Override
						public void mouseReleased(MouseEvent e) {}
						
						@Override
						public void mouseEntered(MouseEvent e) {
							mIpText.setText("search on Google");
						}
						
						@Override
						public void mouseExited(MouseEvent e) {
							mIpText.setText("click here");
						}
					});
				}
				else {
					mIpText.setText("unknown");
				}
				
			}
		}, "IP finder").start();
		
		mPortText = new JTextField("2209");
		mPortText.addActionListener(hostListener);
		
		mOpen = new JButton("Host");
		mOpen.addActionListener(hostListener);
	}
	
	@Override
	public void run() {
		Socket client = null;
		while(mRunning) {
			try {
				client = mServer.accept();
				for(int i = 0; i < mClients.length; ++i) {
					if(mClients[i] == null) {
						new ServerThread(client, i);
						if(mSwap) {
							swap();
							mSwap = false;
						}
						break;
					}
				}
			} catch(IOException e) {
				String message = e.getLocalizedMessage();
				if(!"socket closed".equals(message)) display(message, JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		closePort();
	}
	
	private boolean mSwap = false;
	
	private void openPort(int port) {
		try {
			mServer = new ServerSocket(port);
			
			mPortText.setEditable(false);
			mOpen.setText("Close");
			
			mRunning = true;
			new Thread(this, "Server").start();
			
			mSwap = GamePhase.getTeam() == 1;
			client.connect(port);
		} catch(IOException e) {
			display(e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public void closePort() {
		mRunning = false;
		
		try {
			if(mServer != null) mServer.close();
		} catch(IOException e) {
			display(e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		mServer = null;
		
		for(int i = 0; i < mClients.length; i++) {
			if(mClients[i] != null) {
				mClients[i].closeStream();
			}
		}
		
		clearBoard();
		mOpen.setText("Host");
		mPortText.setEditable(true);
	}
	
	private void clearBoard() {
		mBoard.clear();
		for(int i = 0; i < mClients.length; i++) {
			if(mClients[i] != null) mClients[i].mPlaced = false;
		}
	}
	
	public void swap() {
		ServerThread client = mClients[0];
		
		mClients[0] = mClients[1];
		if(mClients[0] != null) {
			mClients[0].mIndex = 0;
			mClients[0].mOtherIndex = 1;
			mClients[0].mOutput.println(COMMAND_TEAM + 0);
		}
		
		mClients[1] = client;
		if(mClients[1] != null) {
			mClients[1].mIndex = 1;
			mClients[1].mOtherIndex = 0;
			mClients[1].mOutput.println(COMMAND_TEAM + 1);
		}
	}
	
	private void display(String text, int messageType) {
		// mMessage.append(text + '\n');
		JOptionPane.showMessageDialog(StrategoGame.sWindow, text, "", messageType);
	}
	
	private void message(String message) {
		for(int i = 0; i < mClients.length; i++)
			if(mClients[i] != null) mClients[i].mOutput.println(COMMAND_MESSAGE + message);
	}
	
	private class ServerThread extends Thread {
		
		private int mIndex, mOtherIndex;
		
		private String mName;
		
		private Socket mSocket = null;
		
		private BufferedReader mInput = null;
		private PrintStream mOutput = null;
		
		public ServerThread(Socket socket, int index) {
			super("ServerThread" + index);
			mSocket = socket;
			
			mIndex = index;
			mOtherIndex = (mIndex + 1) % 2;
			
			mClients[index] = this;			
			start();
		}
		
		private synchronized void openStream() throws IOException {
			mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mOutput = new PrintStream(mSocket.getOutputStream());
			
			mOutput.println(COMMAND_NAME + mIndex);
			String name = mInput.readLine();
			mName = name.substring(COMMAND_NAME.length());
			message(mName + " joined.");
		}
		
		private void closeStream() {
			mOutput.println(COMMAND_CLOSE);
			try {
				mSocket.close();
			} catch(IOException e) {
				display(e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			mClients[mIndex] = null;
			message(mName + " left.");
		}
		
		@Override
		public void run() {
			String line;
			try {
				openStream();
				while((line = mInput.readLine()) != null) {
					// display("Socket " + index + ": " + line);
					if(line.startsWith(COMMAND_QUIT)) break;
					if(line.startsWith(COMMAND_SAY)) {
						say(line.substring(COMMAND_SAY.length()));
					}
					else if(line.startsWith(COMMAND_PING)) {
						if(mClients[mOtherIndex] != null) mClients[mOtherIndex].mOutput.println(line);
					}
					else if(line.startsWith(COMMAND_PLAY)) {
						play(line.substring(COMMAND_PLAY.length()));
					}
					else if(line.startsWith(COMMAND_PLACE)) {
						place(line.substring(COMMAND_PLACE.length()));
					}
				}
			} catch(IOException e) {
				String message = e.getLocalizedMessage();
				if(!"socket closed".equals(message)) display(message, JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} finally {
				closeStream();
			}
		}
		
		private void say(String message) {
			mOutput.println(COMMAND_CONFIRM_MESSAGE);
			for(int i = 0; i < mClients.length; i++)
				if(i != mIndex && mClients[i] != null) mClients[i].mOutput.println(COMMAND_SAY + "< " + mName + " >: " + message);
		}
		
		private boolean mPlaced = false;
		
		private void place(String line) {
			if(mPlaced || line.length() != 40) return;
			
			mPlaced = mBoard.place(line, mIndex);
			
			if(mPlaced) {
				mOutput.println(COMMAND_CONFIRM_MOVE);
				if(mClients[mOtherIndex] != null && mClients[mOtherIndex].mPlaced) {
					mOutput.println(COMMAND_READY + mBoard.getString(mOtherIndex));
					mClients[mOtherIndex].mOutput.println(COMMAND_READY + mBoard.getString(mIndex));
					checkVictory(false);
				}
			}
			else mOutput.println(COMMAND_DENY_MOVE);
		}
		
		private void play(String line) {
			String[] commands = line.split(";");
			if(commands.length < 2) return;
			
			Unit unit = (commands[0].length() == 2) ? mBoard.getField(Character.getNumericValue(commands[0].charAt(0)),
					Character.getNumericValue(commands[0].charAt(1))).getUnit() : null;
			Field move = (commands[1].length() == 2) ? mBoard.getField(Character.getNumericValue(commands[1].charAt(0)),
					Character.getNumericValue(commands[1].charAt(1))) : null;
			Unit target = (commands.length > 2 && commands[2].length() == 2) ? mBoard.getField(Character.getNumericValue(commands[2].charAt(0)),
					Character.getNumericValue(commands[2].charAt(1))).getUnit() : null;
			
			boolean moveRegular = unit != null && (move == null || target == null);
			boolean flagCaptured = false;
			
			if(move != null) {
				moveRegular = unit.move(move);
			}
			if(moveRegular && target != null) {
				unit.attack(target);
				if(target instanceof Flag) {
					flagCaptured = true;
				}
			}
			
			if(moveRegular) {
				mOutput.println(COMMAND_CONFIRM_MOVE);
				mClients[mOtherIndex].mOutput.println(COMMAND_PLAY + line);
				checkVictory(flagCaptured);
			}
			else {
				mOutput.println(COMMAND_DENY_MOVE);
			}
		}
		
		private void checkVictory(boolean flagCaptured) {
			if(flagCaptured) {
				mOutput.println(COMMAND_WIN);
				mClients[mOtherIndex].mOutput.println(COMMAND_LOSE);
				clearBoard();
				swap();
			}
			else {
				if(!mBoard.hasMoves(mOtherIndex)) {
					mClients[mOtherIndex].mOutput.println(COMMAND_LOSE);
					if(mBoard.hasMoves(mIndex)) mOutput.println(COMMAND_WIN);
					clearBoard();
					swap();
				}
				else if(!mBoard.hasMoves(mIndex)) {
					mOutput.println(COMMAND_LOSE);
					if(mBoard.hasMoves(mOtherIndex)) mClients[mOtherIndex].mOutput.println(COMMAND_WIN);
					clearBoard();
					swap();
				}
			}
		}
	}
	
}
