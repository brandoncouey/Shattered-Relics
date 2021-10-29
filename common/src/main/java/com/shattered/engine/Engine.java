package com.shattered.engine;

import com.shattered.engine.threads.SlowThreadFactory;
import lombok.Data;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * @author JTlr Frost 9:43PM 10/23/2018
 */
@Data
public abstract class Engine {


	/**
	 * Starts the grand exchange thread
	 */
	protected Timer fastExecutor;

	/**
	 * The 'slow' executor responsible for repetitive .6 milisecond ticks
	 */
	protected ScheduledExecutorService slowExecutor;

	/**
	 * If a system update is in progress
	 */
	protected boolean shuttingDown;

	/**
	 * initializes this game engine and returns said initialized engine
	 * 
	 * @return The engine future
	 */
	public void run() {
		fastExecutor(new Timer("Fast Executor"));
		setSlowExecutor(Executors.newSingleThreadScheduledExecutor(new SlowThreadFactory()));
		fastExecutor(new Timer());

	}

	/**
	 * @return fastExecutor
	 */
	public Timer fastExecutor() {
		return fastExecutor;
	}

	/**
	 * @param fastExecutor
	 * @return
	 */
	public Engine fastExecutor(Timer fastExecutor) {
		this.fastExecutor = fastExecutor;
		return this;
	}

	/**
	 *
	 * @param delay
	 * @param restart
	 */
	public void initializeSystemUpdate(int delay, boolean restart) {
		setShuttingDown(true);
		System.err.println("Initializing system update in " + delay
				+ " seconds. RESTART: " + restart);
	}

	/**
	 * Shuts down this game engine
	 */
	public void shutdown() {//so it will shutdown the whole gameengine? idk lol lol well test it
		getSlowExecutor().shutdownNow();
		fastExecutor().cancel();
	}


}
