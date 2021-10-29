package com.shattered.game.actor.components.movement;

import com.shattered.game.actor.object.component.transform.Vector3;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class WalkStep {

    /**
     * Represents the Location
     */
    private final Vector3 location;

    /**
     * Represents the Direction of the Step
     */
    private final float direction;
}
