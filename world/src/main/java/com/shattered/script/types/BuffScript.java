package com.shattered.script.types;

import com.shattered.game.actor.components.ActorComponents;
import com.shattered.script.api.*;
import com.shattered.script.api.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class BuffScript {

    /**
     * Represents the Game World API
     */
    public final RelicWorldAPI world = new WorldAPI();

    /**
     * Represents the Data Table API
     * This API is used for grabbing selected information throughout all our data tables
     * i.e item, npc, object information
     */
    public final DataTableAPI tables = new DataTableAPI();

    /**
     * Represents the Game World API
     */
    public final RelicEngineAPI engine = new EngineAPI();

    /**
     * Represents the Math API
     */
    public final RelicMathAPI math = new MathAPI();

    /**
     * Represents the Utility API
     */
    public final RelicUtilityAPI utility = new UtilityAPI();

    /**
     * Represents the Character API Associated with this buff
     * parent class to handle all other entities.
     */
    @Setter
    @Getter
    protected RelicCharacterAPI character;

    /**
     * Represents the Character that is associated with the Source of the Buff/Debuff
     *      this could be the boss, or another player.
     *              This only gets set if the buff/debuff is from another source.
     */
    @Getter
    @Setter
    protected RelicCharacterAPI source;

    /**
     * Represents the amount of current stacks
     * DO NOT edit these. These are handled via core
     */
    @Getter
    @Setter
    protected int stacks = 1;

    /**
     * Represents the current duration
     * This is handled via core
     * This is data represented in milis
     */
    @Setter
    @Getter
    protected float duration;

    /**
     * Represents the current tick for the buff script
     */
    @Getter
    @Setter
    protected int tick;

    /**
     * Represents the name of the buff to trigger
     * @return buff name
     */
    public abstract String name();

    /**
     * Represents the method that checks if the actor can continue / start this action.
     */
    public abstract boolean can_apply();

    /**
     * Method called upon starting the object action
     */
    public abstract void on_applied();

    /**
     * Method called every second of the buff
     */
    public abstract int on_tick();

    /**
     * Method called upon finishing of the buff
     */
    public abstract void on_finished();


}
