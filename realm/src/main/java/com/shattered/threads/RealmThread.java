package com.shattered.threads;


import com.shattered.BuildRealm;
import com.shattered.realm.GameRealm;
import lombok.Getter;

import java.util.Objects;

/**
 * @author JTlr Frost | Apr 28, 2018 : 12:15:32 PM
 */
public final class RealmThread extends Thread {

	/** Represents the Current Cycle */
	public static long WORLD_CYCLE;

	/**
	 * Represents the time the frame was last updated
	 */
	private static long lastFrameUpdate = System.currentTimeMillis();

	/**
	 * Creates a new World Thread
	 */
	public RealmThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("GameRealm Thread");
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		while (!BuildRealm.getInstance().getEngine().isShuttingDown()) {
			WORLD_CYCLE++;

			final long currentFrame = System.currentTimeMillis();
			final long deltaTime = currentFrame - lastFrameUpdate;

			/**
			 * Loops through all {@link Player}s and Updates
			 */
			GameRealm.getAccounts().stream().filter(Objects::nonNull).forEach(accounts -> {
				try {
					if (accounts.getChannel().isActive()) {
						accounts.onTick(deltaTime);
					} else {
						accounts.onFinish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});



			lastFrameUpdate = System.currentTimeMillis();
			long difference = lastFrameUpdate - currentFrame;
			try {
				if (difference < 100L) {
					Thread.sleep(100L - difference);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
