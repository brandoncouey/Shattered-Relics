package com.shattered.game.actor;

/**
 * @author JTlr Frost 10/24/2019 : 11:53 PM
 */
public enum ActorState {

    /**
     * Represents if the Actor is Currently being Constructed
     */
    CONSTRUCT,

    /**
     * Represents if the Actor is Currently Alive
     */
    ALIVE,

    /**
     * Represents if the Actor is Currently Dead
     */
    DEAD,

    /**
     * Represents if the  Actor is Currently Finished (Will Not Respond)
     */
    FINISHED
}
