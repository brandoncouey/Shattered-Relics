package com.shattered.game.actor.components.animation;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author JTlr Frost 10/29/2019 : 9:28 PM
 */
@Data
@RequiredArgsConstructor
public class AnimSequence {


    /**
     * Represents the AnimSequence Id
     */
    private final int id;

    /**
     * Represents if the anim sequence is already playing
     */
    private boolean isPlaying;
    
    
}
