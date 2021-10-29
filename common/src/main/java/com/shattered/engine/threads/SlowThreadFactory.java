package com.shattered.engine.threads;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SlowThreadFactory implements ThreadFactory {

	/**
	 * Represents the Pool Number
	 */
	private static final AtomicInteger poolNumber = new AtomicInteger(1);

	/**
	 * Represents the Thread Group
	 */
	private final ThreadGroup group;

	/**
	 * Represents the Thread number
	 */
	private final AtomicInteger threadNumber = new AtomicInteger(1);

	/**
	 * Represents the Thread Name
	 */
	private final String name;

	/**
	 * Creates a new SlowThreadFactory
	 */
	public SlowThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
				.getThreadGroup();
		name = "Slow Pool-" + poolNumber.getAndIncrement() + "-thread-";
	}

	/**
	 *
	 * @param r
	 * @return
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, name
				+ threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.MIN_PRIORITY)
			t.setPriority(Thread.MIN_PRIORITY);
		return t;
	}

}
