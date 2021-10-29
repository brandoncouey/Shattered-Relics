package com.shattered.script.types;

import com.shattered.game.actor.components.ActorComponents;
import com.shattered.script.api.RelicEngineAPI;
import com.shattered.script.api.RelicMathAPI;
import com.shattered.script.api.RelicUtilityAPI;
import com.shattered.script.api.RelicWorldAPI;
import com.shattered.script.api.impl.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class ActionScript {

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
    protected PlayerAPI player;

    /**
     * Creates a new Action Script Instance with the desired parameters
     * @param params
     */
   public ActionScript(Object... params) {

   }


    /**
     * Represents the name of the action to trigger
     * @return action name
     */
    public abstract String action_name();

    /**
     * Represents the method that checks if the actor can continue / start this action.
     */
    public abstract boolean can_start();

    /**
     * Method called upon starting the object action
     */
    public abstract void on_start();

    /**
     * Method called every second of the object action
     */
    public abstract int on_tick();

    /**
     * Method called upon finishing / leaving the action
     */
    public abstract void on_finished();

    /**
     * Method used for pausing the current action from processing the on_tick for x amount of seconds.
     * @param seconds
     */
    public void wait(int seconds) {
        if (player.getPlayer().component(ActorComponents.ACTION).isDoingAction()) {
            player.getPlayer().component(ActorComponents.ACTION).setDelay(seconds);
        }
    }

}
