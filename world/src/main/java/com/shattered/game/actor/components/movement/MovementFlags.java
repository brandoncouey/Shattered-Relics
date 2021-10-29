package com.shattered.game.actor.components.movement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public enum MovementFlags {

    FORWARD(0x1),

    BACKWARD(0x2),

    LEFT(0x4),

    RIGHT(0x8),

    WALKING(0x10),

    JUMPING(0x20),

    TELEPORT(0x40),

    FLYING(0x80),

    SWIMMING(0x100),

    FALLING(0x200),

    LOCKED(0x400, true),

    ;

    /**
     * Represents the Flag Id of the Movement Flag
     */
    @Getter
    private final int flag;

    /**
     * Represents if the flag gets removed manually
     */
    @Getter
    private final boolean clearManually;

    /**
     *
     * @param flag
     */
    MovementFlags(int flag) {
        this.flag = flag;
        this.clearManually = false;
    }

    /**
     *
     * @param flag
     * @param clearManually
     */
    MovementFlags(int flag, boolean clearManually) {
        this.flag = flag;
        this.clearManually = clearManually;
    }
}
