package com.shattered.engine;

import lombok.Getter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameTick implements Serializable {

	/**
	 * The serial UID (not needed but since our serialization is messed up we
	 * have to keep this here)
	 */
	private static final long serialVersionUID = 8661046983125870525L;
	/**
	 * The ticks that are allocated in this specific game tick
	 */
	@Getter
	private AtomicInteger ticks;
	/**
	 * If this game tick is running
	 */
	@Getter
	private boolean running;

	/**
	 * Constructs a new {@code GameTick}
	 * 
	 * @param run
	 *            If this game tick should run
	 */
	public GameTick(boolean run) {
		running = run;
		ticks = new AtomicInteger(0);
	}

	/**
	 * Tick called every 600 milliseconds by the parent game engine
	 */
	public abstract void tick();

	/**
	 * Causes this tick to stop ticking
	 */
	public final void shutdown() {
		running = false;
	}

	/**
	 * Causes this tick to resume ticking
	 */
	public final void start() {
		running = true;
	}

}