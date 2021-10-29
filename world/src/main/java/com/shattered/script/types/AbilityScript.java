package com.shattered.script.types;

import com.shattered.datatable.tables.AbilityUDataTable;
import com.shattered.game.actor.character.components.combat.CombatDefinitions;
import com.shattered.game.actor.character.player.component.combat.ClassTypes;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.api.*;
import com.shattered.script.api.impl.*;
import com.shattered.utilities.ecs.ProcessInterval;
import lombok.Getter;
import lombok.Setter;

/**
 * Script used for giving abilities functionality
 */
public abstract class AbilityScript {


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
     * Represents the location of the ability
     *  this is only used for abilities that have select a coordinate
     */
    @Setter
    public GridCoordinate selectedCoordinate;

    /**
     * Represents the pitch for the ability for projectile
     *  this is only used for abilities that have projectiles.
     */
    @Setter
    public float pitch;

    /**
     * Represents the name of the ability
     * @return the ability name
     */
    public abstract String name();

    /**
     * Represents the type of ability attack style
     * @return the attack style type
     */
    public abstract CombatDefinitions.AttackStyle style();

    /**
     * Represents the Class Type for the Ability
     * @return
     */
    public abstract ClassTypes class_type();

    /**
     * Represents if this is a channeled ability action
     * @return
     */
    public boolean is_channel() {
        return false;
    }

    /**
     * Represents if you're allowed to walk while casting this ability
     * @return
     */
    public boolean can_walk() {
        return true;
    }

    /**
     * Method for checking of the player is able to use the ability
     *
     * @param character
     * @return can use
     */
    public abstract boolean can_use(PlayerAPI character);

    /**
     * Method called upon using of an ability
     * @param character
     */
    public abstract void on_use(PlayerAPI character);

    //TODO Make characterAPI?
    public void on_tick(PlayerAPI character, ProcessInterval interval) {

    }

    /**
     * Method called upon starting of casting the ability
     * @param character
     */
    public boolean on_cast(PlayerAPI character) {
        return true;
    }


    //Abilities that linger around and tick must be attatched to a npc i.e a totem

    //Abilities that get targeted into a selected area, must delay a request of all areas in the area and apply damage back to the script of the entities i.e meteor

    public void on_hit(PlayerAPI character, RelicCharacterAPI target) {

    }

    public void on_canceled(PlayerAPI character) {

    }

    public AbilityUDataTable getTable() {
        return AbilityUDataTable.forName(name());
    }


}
