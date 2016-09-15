package com.mrvelibor.stratego.game;

import java.awt.Graphics2D;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.sound.SoundFile;
import com.mrvelibor.stratego.units.Unit;

public class OnlinePlayPhase extends PlayPhase {
	
	public final SoundFile SOUND_WARNING = new SoundFile("notifs/warning.wav", SoundFile.TYPE_NOTIF),
			SOUND_TIME_UP = new SoundFile("notifs/time_up.wav", SoundFile.TYPE_NOTIF);
	
	private static final int TURN_TIME = 60, WARNING_DELAY = 10;
	
	private String mWarningString = null;
	
	@Override
	protected void setOnTurn(boolean onTurn) {
		if(onTurn) {
			StrategoGame.setTimer(TURN_TIME, WARNING_DELAY);
		}
		else {
			StrategoGame.cancelTimer();
			mWarningString = null;
		}
		super.setOnTurn(onTurn);
	}
	
	@Override
	protected void play() {
		setOnTurn(false);
		StrategoGame.sWindow.client.play(mStartField, mMoveField, mAttackField);
	}
	
	@Override
	public boolean serverCommand(String line) {
		if(line.startsWith(COMMAND_PLAY)) {
			opponentPlay(line.substring(COMMAND_PLAY.length()));
		}
		else if(line.startsWith(COMMAND_CONFIRM_MOVE)) {
			final Unit target = mAttackField != null ? mAttackField.getUnit() : null;
			if(target != null) {
				animateYourAttack(mSelected, target);
			}
			mLastMove.saveLastMove(mStartField, mMoveField, mSelected, mAttackField != null ? mAttackField.getUnit() : null);
			clearSelection();
		}
		else if(line.startsWith(COMMAND_DENY_MOVE)) {
			selectUnit(null);
			setOnTurn(true);
		}
		else if(line.startsWith(COMMAND_WIN)) {
			setGameOver(GameOverPhase.WIN);
		}
		else if(line.startsWith(COMMAND_LOSE)) {
			setGameOver(GameOverPhase.LOSE);
		}
		else return false;
		return true;
	}
	
	@Override
	protected void onTurnOver() {}
	
	private void opponentPlay(String line) {
		String[] commands = line.split(";");
		if(commands.length < 2) return;
		
		final Unit unit = (commands[0].length() == 2) ? sBoard.getField(Character.getNumericValue(commands[0].charAt(0)),
				Character.getNumericValue(commands[0].charAt(1))).getUnit() : null;
		final Field move = (commands[1].length() == 2) ? sBoard.getField(Character.getNumericValue(commands[1].charAt(0)),
				Character.getNumericValue(commands[1].charAt(1))) : null;
		final Unit target = (commands.length > 2 && commands[2].length() == 2) ? sBoard.getField(Character.getNumericValue(commands[2].charAt(0)),
				Character.getNumericValue(commands[2].charAt(1))).getUnit() : null;
		
		if(unit == null || move == null && target == null) return;
		
		animateOpponentPlay(unit, move, target);
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		Screen.renderText(g, mWarningString);
	}
	
	public void warning(int secs) {
		if(secs == WARNING_DELAY) SOUND_WARNING.play();
		mWarningString = secs + " seconds remaining!";
	}
	
	public void timeUp() {
		SOUND_TIME_UP.play();
		if(mMoveField == null && mAttackField == null) {
			randomAction();
		}
		play();
	}
	
	@Override
	protected void onGameOver() {
		StrategoGame.setPhase(new GameOverPhase(mVictory, false));
	}
	
}
