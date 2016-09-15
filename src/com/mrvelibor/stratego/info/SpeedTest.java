package com.mrvelibor.stratego.info;

public final class SpeedTest extends Thread {
	
	/**
	 * Runs on the caller's thread.
	 * 
	 * @param action
	 *            The action to test
	 * @param repeats
	 *            Number of times to repeat the action
	 * @return
	 *         Time it took for the action to complete, in miliseconds
	 */
	public static final long testAction(final Action action, final int repeats) {
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < repeats; ++i) {
			action.action();
		}
		return System.currentTimeMillis() - startTime;
	}
	
	/**
	 * Runs on a new thread.
	 * 
	 * @param action
	 *            The action to test
	 * @param repeats
	 *            Number of times to repeat the action
	 * @param listener
	 *            The listener to which to report the time it took for the action to complete
	 */
	public static final void testAction(final Action action, final int repeats, final Listener listener) {
		new SpeedTest(action, repeats, listener).start();
	}
	
	private final Action mAction;
	private final int mRepeats;
	
	private final Listener mListener;
	
	private SpeedTest(final Action action, final int repeats, final Listener listener) {
		mAction = action;
		mRepeats = repeats;
		mListener = listener;
	}
	
	@Override
	public void run() {
		mListener.onSpeedCalculated(testAction(mAction, mRepeats));
	}
	
	public static abstract class Action {
		public abstract void action();
	}
	
	public static interface Listener {
		public void onSpeedCalculated(long runTimeMilis);
	}
	
}
