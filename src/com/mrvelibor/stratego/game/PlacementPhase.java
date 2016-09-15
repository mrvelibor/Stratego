package com.mrvelibor.stratego.game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;

import com.mrvelibor.stratego.StrategoGame;
import com.mrvelibor.stratego.board.Board;
import com.mrvelibor.stratego.board.Field;
import com.mrvelibor.stratego.exceptions.FieldNotPassableExteption;
import com.mrvelibor.stratego.graphics.Screen;
import com.mrvelibor.stratego.input.Mouse;
import com.mrvelibor.stratego.sound.MusicFile;
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

public class PlacementPhase extends GamePhase {
	
	private int mRank = 0;
	private Image mImage = Flag.IMAGE;
	
	private Unit mSelected = null;
	
	private int[] mRemaining = new int[] { Flag.AVAILABLE, Spy.AVAILABLE, Scout.AVAILABLE, Miner.AVAILABLE, Sergeant.AVAILABLE, Liutenant.AVAILABLE,
			Captain.AVAILABLE, Major.AVAILABLE, Colonel.AVAILABLE, General.AVAILABLE, Marshal.AVAILABLE, Bomb.AVAILABLE };
	
	private ArrayList<Field> mEmpty = new ArrayList<Field>();
	
	private boolean mPlaced = false;
	
	private final boolean mSinglePlayer;
	
	public PlacementPhase(boolean singlePlayer) {
		mSinglePlayer = singlePlayer;
		
		sBoard.clear();
		
		for(int j = sTeam == 0 ? 0 : 6, k = sTeam == 0 ? 4 : Board.SIZE; j < k; j++) {
			for(int i = 0; i < Board.SIZE; i++) {
				mEmpty.add(sBoard.getField(i, j));
			}
		}
		
		if(mSinglePlayer) {
			fillBoard((sTeam + 1) % 2);
		}
		
		setPlaced(false);
		sBoard.redraw();
	}
	
	@Override
	public void onSet(GamePhase previous) {
		StrategoGame.sWindow.controls.setOtherButton(true, "Random", Screen.COLOR_BUTTON_BG);
		StrategoGame.sWindow.controls.setColors(Unit.TEAM_COLOR[sTeam], Screen.COLOR_TEXT);
		MusicFile.SONG_PREPARATION.play();
	}
	
	private void setPlaced(boolean placed) {
		mPlaced = placed;
		StrategoGame.sWindow.controls.setActionButton(mPlaced, !mPlaced ? "START" : "<html><center>WAITING<br/>ON OPPONENT</center></html>", !mPlaced
				? Screen.COLOR_ACTION : Screen.COLOR_OPPONENT_TURN);
	}
	
	private void fillBoard(int team) {
		for(int i = 0, k = 0; i < mRemaining.length; i++) {
			for(int j = 0; j < mRemaining[i]; j++, k++) {
				int x = k % 10;
				int y = k / 10;
				if(team == 1) y += 6;
				try {
					Unit.create(i, team, sBoard, x, y);
				} catch(FieldNotPassableExteption e) {
					e.printStackTrace();
				}
			}
		}
		
		Random rand = new Random();
		for(int i = 0; i < 200; i++) {
			int x1 = rand.nextInt(Board.SIZE);
			int y1 = rand.nextInt(4);
			int x2 = rand.nextInt(Board.SIZE);
			int y2 = rand.nextInt(4);
			if(team == 1) {
				y1 += 6;
				y2 += 6;
			}
			
			Unit unit1 = sBoard.getField(x1, y1).getUnit();
			Unit unit2 = sBoard.getField(x2, y2).getUnit();
			
			unit1.swap(unit2);
		}
	}
	
	private void updateRank() {
		for(mRank = 0; mRank < mRemaining.length; mRank++) {
			if(mRemaining[mRank] > 0) {
				selectUnit(mRank);
				return;
			}
		}
		selectUnit(-1);
	}
	
	@Override
	public void leftClick() {
		int x = Mouse.getFieldX(), y = Mouse.getFieldY();
		if(x != -1 && y != -1) {
			if(!clickAction(x, y)) {
				Field field = sBoard.getField(x, y);
				Unit unit = (field != null) ? unit = field.getUnit() : null;
				
				if(unit == null) sTap.effect(Mouse.getX(), Mouse.getY());
			}
		}
	}
	
	private boolean clickAction(int x, int y) {
		if(mPlaced) return false;
		
		Field field = sBoard.getField(x, y);
		Unit unit = field.getUnit();
		
		if(mSelected == null) {
			if(unit == null) {
				if(mRank >= 0 && mRank < mRemaining.length && mRemaining[mRank] > 0) try {
					Unit.create(mRank, sTeam, sBoard, x, y);
					mEmpty.remove(field);
					StrategoGame.sWindow.controls.updateRemaining(mRank, --mRemaining[mRank]);
					if(mRemaining[mRank] <= 0) updateRank();
				} catch(FieldNotPassableExteption e) {
					return false;
				}
				else return false;
			}
			else {
				if(unit.team == sTeam) selectUnit(unit);
			}
		}
		else {
			if(unit == null) {
				Field previous = mSelected.getField();
				if(sBoard.place(mSelected, field.x, field.y)) {
					mEmpty.add(previous);
					mEmpty.remove(field);
				}
			}
			else {
				mSelected.swap(unit);
			}
			selectUnit(null);
		}
		return true;
	}
	
	@Override
	public void rightClick() {
		if(mSelected != null) selectUnit(null);
		else sendPing(Mouse.getX(), Mouse.getY());
	}
	
	@Override
	public void actionButton() {
		if(mSinglePlayer) {
			StrategoGame.setPhase(new SingleplayerPlayPhase());
		}
		else {
			StrategoGame.sWindow.client.place(sBoard, sTeam);
			StrategoGame.sWindow.controls.setActionButton(false, null, null);
		}
	}
	
	@Override
	public void otherButton() {
		if(mPlaced) return;
		
		updateRank();
		while(!mEmpty.isEmpty()) {
			Field empty = mEmpty.get(0);
			clickAction(empty.x, empty.y);
		}
		
		Random rand = new Random();
		for(int i = 0; i < 200; i++) {
			int x = rand.nextInt(Board.SIZE);
			int y = rand.nextInt(4);
			if(sTeam == 1) y += 6;
			clickAction(x, y);
		}
	}
	
	public void selectUnit(int rank) {
		if(rank < 0 || rank >= mRemaining.length) {
			mRank = -1;
			mImage = null;
		}
		else if(mRemaining[rank] > 0) {
			mRank = rank;
			mImage = Unit.getImage(rank);
		}
	}
	
	private void selectUnit(Unit unit) {
		if(unit != null) {
			mImage = unit.getSprite();
			unit.setHidden(true);
		}
		else {
			if(mSelected != null) mSelected.setHidden(false);
			mImage = Unit.getImage(mRank);
		}
		mSelected = unit;
	}
	
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
		Screen.renderFieldBorders(g, mEmpty);
		if(unit != null && unit.team == sTeam) {
			Screen.renderBorder(g, field, mPlaced ? Screen.COLOR_UNIT_WAITING : Screen.COLOR_UNIT_READY);
		}
		else if(mEmpty.contains(field)) {
			Screen.renderOverlay(g, field, Screen.COLOR_TRANSPARENT_WHITE);
		}
		if(mSelected != null) {
			Screen.renderOverlay(g, mSelected.getField(), Screen.COLOR_TRANSPARENT_BLACK);
			Screen.renderBorder(g, mSelected.getField(), Screen.COLOR_UNIT_SELECTED);
		}
		if(!mPlaced) {
			g.drawImage(mImage, Mouse.getX(), Mouse.getY(), null);
		}
		
		sTap.draw(g);
	}
	
	@Override
	public boolean serverCommand(String line) {
		if(line.startsWith(COMMAND_READY)) {
			if(sBoard.place(line.substring(COMMAND_READY.length()), (sTeam + 1) % 2)) StrategoGame.setPhase(new OnlinePlayPhase());
		}
		else if(line.startsWith(COMMAND_CONFIRM_MOVE)) {
			setPlaced(true);
		}
		else if(line.startsWith(COMMAND_DENY_MOVE)) {
			setPlaced(false);
		}
		else return false;
		return true;
	}
	
	public int[] getRemaining() {
		return mRemaining;
	}
}
