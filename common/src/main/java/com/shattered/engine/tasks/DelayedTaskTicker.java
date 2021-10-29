package com.shattered.engine.tasks;

import com.shattered.utilities.ecs.ProcessComponent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author JTlr Frost 11/6/2019 : 6:44 PM
 */
public class DelayedTaskTicker {


    /**
     * Represents all of the Current Tasks
     */
    private static final List<DelayedTaskInformation> delayedTasks = Collections.synchronizedList(new LinkedList<DelayedTaskInformation>());


    /**
     * Processes all task
     * @param deltaTime
     */
    public static void tickTasks(long deltaTime) {
        for (DelayedTaskInformation task : delayedTasks.toArray(new DelayedTaskInformation[delayedTasks.size()])) {
            task.setTimeElapsed(task.getTimeElapsed() + deltaTime);
            if (task.getTimeElapsed() < task.getDelayTime() * 1000) {
                continue;
            }
            task.getTask().run();
            delayedTasks.remove(task);
        }
    }


    /**
     * Delays a task for x miliseconds
     * @param task
     * @param duration
     */
    public static void delayTask(Runnable task, float duration) {
        if (task == null || duration < 0)
            return;
        delayedTasks.add(new DelayedTaskInformation(task, duration));
    }


}
