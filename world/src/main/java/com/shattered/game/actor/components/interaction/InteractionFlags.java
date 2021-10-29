package com.shattered.game.actor.components.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InteractionFlags {


    /**
     * This is used for OBJECTS only.
     */
    CAN_INTERACT_WITH(0x1),

    /**
     * Represents if you can talk to this actor.
     */
    CAN_TALK_TO(0x2),

    /**
     * Represents if you can trade this actor.
     */
    TRADEABLE(0x4),

    /**
     * Represents if you can attack this actor.
     */
    ATTACKABLE(0x8),

    /**
     * Represents if you can pickpocket this actor
     */
    PICKPOCKETABLE(0x10),

    ;

    /**
     * Represents the Flag for Interaction Types
     */
    @Getter
    private final int flag;

}
