package com.shattered.script.api;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.ActorState;
import com.shattered.game.actor.components.interaction.InteractionFlags;
import lombok.Getter;

public abstract class RelicActorAPI {


    /**
     * Represents the Actor Associated with this script
     */
    @Getter
    public final Actor actor;

    /**
     * Creates a constructor for the RelicActorAPI
     * @param actor
     */
    public RelicActorAPI(Actor actor) {
        this.actor = actor;
    }

    /**
     * Gets the Name of the NPC
     * @return the npc's name
     */
    public abstract String getName();

    /**
     * Gets the Client Index (the UUID) of the npc
     * @return the npc's uuid
     */
    public abstract int getIndex();

    /**
     * Gets the Actor's current state
     * @return the actor state
     */
    public abstract ActorState getState();

    /**
     * Applys flags to the npc for availability for interaction
     * @param flags
     */
    public abstract void flag(InteractionFlags... flags);

    /**
     * PLays the specified sfx at the actor's location
     * @param id
     */
    public abstract void play_rsfx(int id);

    /**
     * PLays the specified sfx at the actor's location
     * @param name
     */
    public abstract void play_rsfx(String name);


}
