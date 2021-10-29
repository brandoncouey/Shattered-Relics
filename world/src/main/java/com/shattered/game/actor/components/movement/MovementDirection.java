package com.shattered.game.actor.components.movement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MovementDirection {

    BACKWARD_LEFT(-135),

    BACKWARD(-180),

    BACKWARD_RIGHT(135),

    FORWARD_LEFT(-45),

    LEFT(-90),

    FORWARD_RIGHT(45),

    RIGHT(90),

    FORWARD(0)



    ;

    @Getter
    public final int degrees;
}
