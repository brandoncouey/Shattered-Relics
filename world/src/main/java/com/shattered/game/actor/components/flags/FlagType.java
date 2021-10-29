package com.shattered.game.actor.components.flags;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author JTlr Frost 10/29/2019 : 9:34 PM
 */
@RequiredArgsConstructor
public enum FlagType {

    /**
     * Represents the Transform Flag Update Type
     */
    TRANSFORM(0x1),

    /**
     * Represents the Model Block Flag Update Type
     */
    MODEL_BLOCK(0x2),

    /**
     * Represents if Target
     */
    TARGET(0x4),

    /**
     * Represents the Hit Marks Flag Update Type
     */
    HIT_MARK(0x8),

    /**
     * Represents the AnimSequence Flag Update Type
     */
    ANIMATION(0x10),

    /**
     * Represents projectile and animation Flag Update Type
     */
    PROJ_ANIMATION(0x20),

    /**
     * Represents the Map Marker Animation Flag
     */
    MAP_MARKER(0x40),

    ;

    /**
     * Represents the Flag Id
     */
    @Getter
    private final int flagId;
}
