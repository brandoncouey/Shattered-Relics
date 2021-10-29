package com.shattered.engine.logic;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Taylor Moon
 * @since Jan 23, 2014
 */
public abstract class LogicEvent implements Runnable {

	/**
	 * Represents the Scheduled Future
	 */
	@Getter
	@Setter
	protected ScheduledFuture<?> future;

	/**
	 * Returns the delay between successful cycles.
	 * @return The interval delay.
	 */
	public abstract long getIntervalDelay();

	/**
	 * Represents the initial delay before the logic is executed.
	 * @return The initial delay.
	 */
	public abstract long getInitialDelay();

	/**
	 * Returns if this logic event should execute once.
	 * @return True if so; false otherwise.
	 */
	public abstract boolean singleShotEvent();

}
