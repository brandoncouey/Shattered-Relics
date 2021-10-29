package com.shattered.game.actor.object.component.transform;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author JTlr Frost 12/7/2019 : 1:57 PM
 */
@Data
@RequiredArgsConstructor
public class Velocity {


    /**
     * Represents the X Value
     */
    private final int x;

    /**
     * Represents the Y Value
     */
    private final int y;

    /**
     * Represents the Z Value
     */
    private final  int z;



}
