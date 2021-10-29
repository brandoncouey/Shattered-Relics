package com.shattered.script.types;

import com.shattered.game.actor.character.components.combat.CombatDefinitions;
import com.shattered.game.actor.character.components.combat.Hit;
import com.shattered.script.api.*;
import com.shattered.script.api.impl.*;
import lombok.Getter;
import lombok.Setter;

public abstract class NPCCombatScript {

    /**
     * Represents the NPC associated with this script
     */
    @Setter
    protected NpcAPI npc;

    /**
     * Represents the current tick of the script.
     */
    public int ticks;

    /**
     * Represents the distance the npc needs to be at to attack their enemy.
     */
    public int distance = 300;

    /**
     * Represents if the npc should move to target or not if out of range.
     */
    public boolean followTarget = true;

    /**
     * Represents the Game World API
     */
    public final RelicWorldAPI world = new WorldAPI();

    /**
     * Represents the Game World APIRe
     */
    public final RelicEngineAPI engine = new EngineAPI();

    /**
     * Represents the Data Table API
     * This API is used for grabbing selected information throughout all our data tables
     * i.e item, npc, object information
     */
    public final DataTableAPI tables = new DataTableAPI();

    /**
     * Represents the Math API
     */
    public final RelicMathAPI math = new MathAPI();

    /**
     * Represents the Utility API
     */
    public final RelicUtilityAPI utility = new UtilityAPI();

    /**
     * Registers the combat script for multiple npcs
     * @return
     */
    public String[] for_npcs() {
        return null;
    }

    /**
     * Registers combat script for a specific npc
     * @return the npc
     */
    public String fornpc() {
        return null;
    }

    /**
     * Checks if the target can be attacked
     * @param target
     * @return can be attacked
     */
    public boolean can_be_attacked(RelicCharacterAPI target) {
        return true;
    }


    /**
     * Called upon the npc being attacked
     * @param target
     */
    public void on_attacked(RelicCharacterAPI target, Hit hit) {

    }

    /**
     * Called upon the npc being
     * @param target
     */
    public void on_hit(RelicCharacterAPI target, Hit hit) {

    }

    /**
     * Called every 1s that the npc is in combat.
     * @param deltaTime
     */
    public void on_tick(long deltaTime) {

    }

    /**
     * Called upon the npc dying
     * @param source
     */
    public void on_death(RelicCharacterAPI source) {

    }

}
