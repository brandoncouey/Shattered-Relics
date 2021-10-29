package com.shattered.script.api;

import com.shattered.game.actor.character.npc.NPC;
import com.shattered.script.api.impl.ActorVarAPI;
import com.shattered.script.api.impl.ZoneAPI;
import lombok.Getter;

public abstract class RelicNpcAPI extends RelicCharacterAPI {


    /**
     * Represents the Character referenced for API
     */
    @Getter
    protected final NPC npc;

    /**
     * Creates a new NPC API
     * @param npc
     */
    public RelicNpcAPI(NPC npc) {
        super(npc);
        this.npc = npc;
    }

    /**
     * Gets the NPC Id
     * @return the npc id
     */
    public abstract int getId();

    /**
     * Forces the npc to say a message
     * @param message
     */
    public abstract void say(String message);

    /**
     * Forces the npc to yell a message.
     * @param message
     */
    public abstract void yell(String message);

    /**
     * Makes the npc play a specific animation with the specified animation id
     * @param id
     */
    public abstract void play_animation(int id);

    /**
     * Makes the npc play a specific animation with the specified animation id
     * @param animation
     */
    public abstract void play_animation(String animation);

    /**
     * Makes the NPC Stop playing their current animation
     */
    public abstract void stop_animation();

    /**
     * Destroys the npc and removes from the world
     */
    public abstract void destroy();


}
