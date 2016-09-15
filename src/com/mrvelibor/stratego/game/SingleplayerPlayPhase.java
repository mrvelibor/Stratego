package com.mrvelibor.stratego.game;

import java.util.ArrayList;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.units.Flag;
import com.mrvelibor.stratego.units.Unit;

public class SingleplayerPlayPhase extends PlayPhase {
	
	private final int mTeam;
	
	public SingleplayerPlayPhase() {
		super();
		mTeam = (sTeam + 1) % 2;
		if(mTeam == 0) onTurnOver();
	}
	
	@Override
	protected void setOnTurn(boolean onTurn) {
		super.setOnTurn(onTurn);
		if(onTurn) checkVictory();
	}
	
	@Override
	protected void play() {
		setOnTurn(false);
		
		if(mAttackField != null) {
			Unit target = mAttackField.getUnit();
			animateYourAttack(mSelected, target);
			if(target instanceof Flag) setGameOver(GameOverPhase.WIN);
		}
		else onTurnOver();
		
		mLastMove.saveLastMove(mStartField, mMoveField, mSelected, mAttackField != null ? mAttackField.getUnit() : null);
		clearSelection();
	}
	
	@Override
	protected void onTurnOver() {
		if(!checkVictory()) new Thread("AI") {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				while(!aiPlay());
			}
		}.start();
	}
	
	private boolean aiPlay() {
		Unit unit;
		ArrayList<Field> moves;
		ArrayList<Unit> targets;
		do {
			unit = sBoard.getUnit(mTeam, rand);
			moves = unit.getMoves();
			targets = unit.getTargets();
		} while(moves.isEmpty() && targets.isEmpty());
		
		Field move = null;
		Unit target = null;
		if(!targets.isEmpty()) {
			for(Unit u : targets) {
				if(!u.isRevealed()) {
					target = u;
				}
				else if(unit.calculateAttack(u) >= 0) {
					target = u;
				}
			}
		}
		else if(target == null && !moves.isEmpty()) {
			move = moves.get(rand.nextInt(moves.size()));
			
			for(Field f : move.getAdjecent()) {
				Unit u = f.getUnit();
				if(u != null && u.team != mTeam) {
					if(!u.isRevealed()) {
						target = u;
					}
					else if(unit.calculateAttack(u) >= 0) {
						target = u;
					}
				}
			}
		}
		
		if(move == null && target == null) return false;
		
		animateOpponentPlay(unit, move, target);
		if(target instanceof Flag) setGameOver(GameOverPhase.LOSE);
		return true;
	}
	
	private boolean checkVictory() {
		boolean noMovesPlayer = !sBoard.hasMoves(sTeam), noMovesAi = !sBoard.hasMoves(mTeam);
		
		if(noMovesPlayer) {
			setGameOver(noMovesAi ? GameOverPhase.DRAW : GameOverPhase.LOSE);
			return true;
		}
		
		if(noMovesAi) {
			setGameOver(noMovesPlayer ? GameOverPhase.DRAW : GameOverPhase.WIN);
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void onGameOver() {
		StrategoGame.setPhase(new GameOverPhase(mVictory, true));
	}
	
	@Override
	protected boolean serverCommand(String line) {
		return false;
	}
}
