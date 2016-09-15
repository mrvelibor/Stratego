package com.mrvelibor.stratego.effects;

import java.util.Random;

import com.mrvelibor.stratego.sound.SoundFile;
import com.mrvelibor.stratego.units.Bomb;
import com.mrvelibor.stratego.units.Miner;
import com.mrvelibor.stratego.units.Unit;

public class AttackAnimation extends MoveAnimation {
	
	private static final SoundFile BOMB_FUSE = new SoundFile("game/fuse.wav", SoundFile.TYPE_GAME), BOMB_EXPLODE = new SoundFile("game/explosion.wav", SoundFile.TYPE_GAME);
	private static final SoundFile[] FIGHTING = new SoundFile[3];
	static {
		FIGHTING[0] = new SoundFile("game/stab1.wav", SoundFile.TYPE_GAME);
		FIGHTING[1] = new SoundFile("game/stab2.wav", SoundFile.TYPE_GAME);
		FIGHTING[2] = new SoundFile("game/clash.wav", SoundFile.TYPE_GAME);
	}
	
	/*private ArrayList<SoundFile> mSoundQueue = new ArrayList<SoundFile>();
	
	private class ArrayUpdater implements LineListener {
		
		@Override
		public void update(LineEvent event) {
			Type type = event.getType();
			if(type.equals(Type.STOP)) {
				mSoundQueue.remove(0);
				if(!mSoundQueue.isEmpty()) mSoundQueue.get(0).play();
			}
		}
	}*/
	
	private SoundFile mOnStart, mOnEnd;
	
	public AttackAnimation(Unit moving, Unit target, Action action) {
		super(moving, target.getField(), action);
		Random rand = new Random();
		
		if(target instanceof Bomb) {
			mOnStart = BOMB_FUSE;
			if(!(moving instanceof Miner)) mOnEnd = BOMB_EXPLODE;
		}
		else {
			mOnEnd = FIGHTING[rand.nextInt(FIGHTING.length)];
		}
	}
	
	/*private void addToQueue(SoundFile sound) {
		sound.addLineListener(new ArrayUpdater());
		mSoundQueue.add(sound);
	}*/
	
	@Override
	public void start() {
		// if(!mSoundQueue.isEmpty()) mSoundQueue.get(0).play();
		if(mOnStart != null) mOnStart.play();
		super.start();
	}
	
	@Override
	protected void onEnd() {
		if(mOnEnd != null) mOnEnd.play();
		super.onEnd();
	}
	
}
