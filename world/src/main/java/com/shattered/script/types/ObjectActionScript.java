package com.shattered.script.types;

import com.shattered.game.actor.character.player.component.interaction.InteractionModifier;
import com.shattered.script.api.RelicEngineAPI;
import com.shattered.script.api.RelicMathAPI;
import com.shattered.script.api.RelicUtilityAPI;
import com.shattered.script.api.RelicWorldAPI;
import com.shattered.script.api.impl.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class ObjectActionScript extends ActionScript {

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
     * Represents the Character API Associated with this action
     *
     *
     * TODO right now lets focus on the player, then later on i will make a
     * parent class to handle all other entities.
     */
    @Setter
    protected PlayerAPI character;

    /**
     * Represents the Object we are currently targeting
     */
    @Setter
    protected ObjectAPI obj;

    /**
     * Represents the Interaction Modifiers this is binded to
     * @return the interaction modifiers available for register
     */
    public InteractionModifier[] for_modifiers() {
        return null;
    }

    /**
     * Represents the normal
     * @return the single modifier to activate this action
     */
    public InteractionModifier for_modifier() {
        return null;
    }

    /**
     * Represents the the array of objects this action is registered for
     * @return array of objects
     */
    public String[] for_objects() {
        return null;
    }

    /**
     * Represents the single object this action is registered for
     * @return the world object
     */
    public String for_object() {
        return null;
    }

}
