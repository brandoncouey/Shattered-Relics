package com.shattered.utilities.ecs;

/**
 *
 */
public enum ProcessInterval {

    /**
     * This interval is called very 30ms on the world game logic thread
     */
    DEFAULT,

    /**
     * This interval is called every 100ms on the world game logic thread
     */
    TENTH,

    /**
     * This interval is called very 500ms on the world game logic thread
     */
    HALF_SECOND,

    /**
     * This interval is called every 1000ms on the world game logic thread
     */
    SECOND

}
