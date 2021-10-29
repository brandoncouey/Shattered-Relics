package com.shattered.game.actor.emitter;

import com.shattered.game.actor.Actor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author JTlr Frost 11/6/2019 : 6:01 PM
 */
@EqualsAndHashCode(callSuper = true)
public class Emitter extends Actor {
    
    /**
     * Instantiates a new Emitter with the provided id.
     * @param id
     */
    public Emitter(int id) {
        super(id);
    }

    /**
     * Represents the Name of the Game Object
     *
     * @return
     */
    @Override
    public String getName() {
        return "Emitter";
    }
}
