package com.shattered.engine.tasks;

import lombok.Data;

/**
 * @author JTlr Frost 11/6/2019 : 6:45 PM
 */
@Data
public class DelayedTaskInformation {

    /**
     * Represents the Delayed Task to execute.
     */
    private Runnable task;

    /**
     * Represents the Max / Last Tick
     */
    private float delayTime;

    /**
     * Do not Edit -> Represents the time elapsed during cycles
     */
    private long timeElapsed;

    /**
     * Creates a new Delayed Task
     * @param task
     * @param duration
     */
    public DelayedTaskInformation(Runnable task, float duration) {
        setTask(task);
        setDelayTime(duration);
    }

}
