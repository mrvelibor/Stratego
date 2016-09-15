package com.mrvelibor.stratego.game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.board.LastMove;
import com.mrvelibor.stratego.effects.AttackAnimation;
import com.mrvelibor.stratego.effects.MoveAnimation;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.graphics.SpriteSheet;
import com.mrvelibor.stratego.input.Mouse;
import com.mrvelibor.stratego.sound.MusicFile;
import com.mrvelibor.stratego.sound.SoundFile;
import com.mrvelibor.stratego.units.Unit;

public abstract class PlayPhase extends GamePhase {
	
	public final SoundFile SOUND_ON_TURN = new SoundFile("notifs/on_turn.wav", SoundFile.TYPE_NOTIF);
	
	protected final Image ACTION_ATTACK = SpriteSheet.MISC.getSprite(Field.SIZE, 1, 0), ACTION_MOVE = SpriteSheet.MISC.getSprite(Field.SIZE, 2, 0);
	
	protected final Random rand = new Random();
	
	private boolean mOnTurn;
	
	protected Unit mSelected = null;
	
	protected Field mStartField = null, mMoveField = null, mAttackField = null;
	
	protected LastMove mLastMove = new LastMove();
	private boolean mShowLastMove = false;
	
	private ArrayList<Field> mMoves;
	private ArrayList<Unit> mTargets;
	
	private boolean mGameOver = false;
	protected int mVictory = 0;
	
	public PlayPhase() {
		mOnTurn = sTeam == 0;
		sBoard.redraw();
	}
	
	@Override
	public void onSet(GamePhase previous) {
		setOnTurn(mOnTurn);
		StrategoGame.sWindow.controls.setColors(Unit.TEAM_COLOR[sTeam], Screen.COLOR_TEXT);
		StrategoGame.sWindow.controls.setOtherButton(true, "Last move", Screen.COLOR_BUTTON_BG);
		MusicFile.SONG_BATTLE_2.play();
	}
	
	protected void setOnTurn(boolean onTurn) {
		mOnTurn = onTurn;
		if(mOnTurn && !StrategoGame.sWindow.isFocused()) SOUND_ON_TURN.play();
		refreshButton();
	}
	
	private void refreshButton() {
		if(mOnTurn) {
			if(mMoveField != null || mAttackField != null) StrategoGame.sWindow.controls.setActionButton(true, "END TURN", Screen.COLOR_ACTION);
			else StrategoGame.sWindow.controls.setActionButton(true, "YOUR TURN", Screen.COLOR_ON_TURN);
		}
		else {
			StrategoGame.sWindow.controls.setActionButton(true, "OPPONENT'S TURN", Screen.COLOR_OPPONENT_TURN);
		}
	}
	
	@Override
	public void leftClick() {
		int x = Mouse.getFieldX(), y = Mouse.getFieldY();
		if(x == -1 || y == -1) return;
		
		Field field = sBoard.getField(x, y);
		Unit unit = field.getUnit();
		
		if(mOnTurn) {
			if(unit != null && unit.team == sTeam) {
				selectUnit(unit);
			}
			else if(mSelected != null) {
				if(mTargets.contains(unit)) {
					attack(unit);
				}
				else if(mMoves.contains(field)) {
					moveSelected(field);
				}
				else if(mStartField.equals(field)) {
					moveSelected(null);
				}
				else {
					selectUnit(null);
				}
			}
			else if(unit == null) {
				sTap.effect(Mouse.getX(), Mouse.getY());
			}
		}
		else if(unit == null) {
			sTap.effect(Mouse.getX(), Mouse.getY());
		}
	}
	
	@Override
	public void rightClick() {
		if(mSelected != null) selectUnit(null);
		else sendPing(Mouse.getX(), Mouse.getY());
	}
	
	@Override
	public void actionButton() {
		if(mOnTurn && (mMoveField != null || mAttackField != null)) play();
	}
	
	protected abstract void play();
	
	@Override
	public void otherButton() {
		mLastMove.toggleAttackShown();
	}
	
	public void setLastMoveShown(boolean shown) {
		mShowLastMove = shown;
		mLastMove.setAttackShown(true);
	}
	
	protected void selectUnit(Unit unit) {
		if(unit == null) {
			moveSelected(null);
			mMoves = null;
			mTargets = null;
			mStartField = null;
			mSelected = null;
		}
		
		if(unit == mSelected || unit.team != sTeam) return;
		
		if(mSelected != null) moveSelected(null);
		
		mSelected = unit;
		mMoves = mSelected.getMoves();
		mTargets = mSelected.getTargets();
		mStartField = mSelected.getField();
	}
	
	protected void clearSelection() {
		mMoveField = null;
		mAttackField = null;
		mMoves = null;
		mTargets = null;
		mStartField = null;
		mSelected = null;
	}
	
	private void moveSelected(Field field) {
		if(mSelected == null) return;
		
		if(field == null) {
			mSelected.move(mStartField);
			mMoves = mSelected.getMoves();
		}
		else {
			mSelected.move(field);
		}
		mTargets = mSelected.getTargets();
		mMoveField = field;
		mAttackField = null;
		refreshButton();
	}
	
	private void attack(Unit unit) {
		if(mSelected == null) return;
		
		if(unit == null || unit.getField() == mAttackField) {
			mAttackField = null;
		}
		else {
			mAttackField = unit.getField();
		}
		refreshButton();
	}
	
	private ArrayList<MoveAnimation> mAnimations = new ArrayList<MoveAnimation>();
	
	protected void animateOpponentPlay(final Unit unit, final Field move, final Unit target) {
		mLastMove.saveLastMove(unit.getField(), move, unit, target);
		
		if(move != null) {
			mAnimations.add(new MoveAnimation(unit, move, new MoveAnimation.Action() {
				@Override
				public void doAfter() {
					unit.move(move);
					mAnimations.remove(0);
					
					if(mAnimations.isEmpty()) {
						setOnTurn(true);
						if(mGameOver) onGameOver();
					}
				}
			}));
		}
		if(target != null) {
			unit.setShown(true);
			mAnimations.add(new AttackAnimation(unit, target, new MoveAnimation.Action() {
				
				final Unit attacker = unit;
				final Unit attacked = target;
				
				@Override
				public void doAfter() {
					attacker.attack(attacked);
					attacker.setShown(false);
					mAnimations.remove(0);
					
					setOnTurn(true);
					if(mGameOver) onGameOver();
				}
			}));
		}
	}
	
	protected void animateYourAttack(final Unit unit, final Unit target) {
		if(target == null) return;
		
		target.setShown(true);
		mAnimations.add(new AttackAnimation(unit, target, new MoveAnimation.Action() {
			@Override
			public void doAfter() {
				unit.attack(target);
				target.setShown(false);
				mAnimations.remove(0);
				
				onTurnOver();
				if(mGameOver) onGameOver();
			}
		}));
	}
	
	protected void setGameOver(int victory) {
		mVictory = victory;
		mGameOver = true;
		if(mAnimations.isEmpty()) onGameOver();
	}
	
	protected abstract void onGameOver();
	
	protected abstract void onTurnOver();
	
	@Override
	public void draw(Graphics2D g) {
		int x = Mouse.getFieldX(), y = Mouse.getFieldY();
		Field field;
		Unit unit;
		if(x != -1 && y != -1) {
			field = sBoard.getField(x, y);
			unit = field.getUnit();
		}
		else {
			field = null;
			unit = null;
		}
		
		sBoard.draw(g);
		
		if(mSelected != null) {
			if(mMoveField != null) {
				Screen.renderOverlay(g, mStartField, Screen.COLOR_TRANSPARENT_BLACK);
				Screen.renderBorder(g, mStartField, Screen.COLOR_FIELD_AVAILABLE);
			}
			if(mAttackField != null) {
				g.drawImage(ACTION_ATTACK, mAttackField.drawX, mAttackField.drawY, null);
			}
			
			if(field != null) {
				if(unit != null) {
					if(unit.team == sTeam) {
						Screen.renderBorder(g, field, mOnTurn ? Screen.COLOR_UNIT_READY : Screen.COLOR_UNIT_WAITING);
					}
					else {
						// if(unit.isRevealed()) g.drawImage(unit.getImage(), field.drawX, field.drawY, null);
						Screen.renderOverlay(g, unit.getField(), Screen.COLOR_TRANSPARENT_BLACK);
					}
				}
				
				if((field != mMoveField && mMoves.contains(field)) || (mMoveField != null && field == mStartField)) {
					Screen.renderOverlay(g, field, Screen.COLOR_TRANSPARENT_WHITE);
					g.drawImage(ACTION_MOVE, field.drawX, field.drawY, null);
				}
				else {
					if(mTargets.contains(unit)) {
						g.drawImage(ACTION_ATTACK, field.drawX, field.drawY, null);
					}
				}
			}
			
			Screen.renderFieldBorders(g, mMoves);
			Screen.renderUnitBorders(g, mTargets);
			Screen.renderBorder(g, mSelected.getField(), Screen.COLOR_UNIT_SELECTED);
		}
		else if(unit != null) {
			if(unit.team == sTeam) {
				Screen.renderBorder(g, field, mOnTurn ? Screen.COLOR_UNIT_READY : Screen.COLOR_UNIT_WAITING);
			}
			else {
				// if(unit.isRevealed()) g.drawImage(unit.getImage(), field.drawX, field.drawY, null);
				Screen.renderOverlay(g, unit.getField(), Screen.COLOR_TRANSPARENT_BLACK);
			}
		}
		
		if(!mAnimations.isEmpty()) {
			mAnimations.get(0).draw(g);
		}
		sTap.draw(g);
		
		if(mShowLastMove) mLastMove.draw(g);
	}
	
	protected void randomAction() {
		do {
			selectUnit(sBoard.getUnit(sTeam, rand));
		} while(mMoves.isEmpty() && mTargets.isEmpty());
		
		if(mTargets.isEmpty()) {
			moveSelected(mMoves.get(rand.nextInt(mMoves.size())));
		}
		if(!mTargets.isEmpty()) {
			attack(mTargets.get(rand.nextInt(mTargets.size())));
		}
	}
	
}
