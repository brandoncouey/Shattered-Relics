package com.shattered.script.api.impl;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.script.api.RelicActorVarAPI;

public class ActorVarAPI extends RelicActorVarAPI {

    /**
     * Creates the implementation of the Character Var API
     * @param actor
     */
    public ActorVarAPI(Actor actor) {
        super(actor);
    }

    /**
     * Sets a saved integer variable for the variable with the provided variable name
     *
     * @param name
     * @param value
     */
    @Override
    public void set_str(String name, int value) {
        actor.component(ActorComponents.VAR).setVarInt(name, value);
    }

    /**
     * Increments a saved integer by the default value of 1 for the variable with the provided variable name.
     *
     * @param name
     */
    @Override
    public void increment_int(String name) {
        actor.component(ActorComponents.VAR).incrementVarInt(name);
    }

    /**
     * Increments a saved integer variable by the given value for the variable with the provided variable name
     *
     * @param name
     * @param value
     */
    @Override
    public void increment_int(String name, int value) {
        actor.component(ActorComponents.VAR).incrementVarInt(name, value);
    }

    /**
     * Decrements a saved integer variable with the default amount of 1.
     *
     * @param name
     */
    @Override
    public void decrement_int(String name) {
        actor.component(ActorComponents.VAR).decrementVarInt(name);
    }

    /**
     * Decrements a saved integer with the given amount
     *
     * @param name
     * @param value
     */
    @Override
    public void decrement_int(String name, int value) {
        actor.component(ActorComponents.VAR).decrementVarInt(name, value);
    }

    /**
     * Sets the string variable with the provided value for the variable with the provided var name
     *
     * @param name
     * @param value
     */
    @Override
    public void set_str(String name, String value) {
        actor.component(ActorComponents.VAR).setVarString(name, value);
    }

    /**
     * Gets the int variable value from the variable with the provided var name
     *
     * @param name
     * @return the value of the variable
     */
    @Override
    public int get_int(String name) {
        return actor.component(ActorComponents.VAR).getVarInt(name);
    }

    /**
     * Gets the string variable value from the variable with the provided var name
     *
     * @param name
     * @return the value of the variable
     */
    @Override
    public String get_str(String name) {
        return actor.component(ActorComponents.VAR).getVarString(name);
    }

    /**
     * Sets the transient integer variable to the provided value for the variable with the provided var id.
     *
     * @param name
     * @param value
     */
    @Override
    public void set_tint(String name, int value) {
        actor.component(ActorComponents.TRANS_VAR).setVarInt(name, value);
    }

    /**
     * Increments the transient integer variable with the default value of 1 for the variable with the provided var name
     *
     * @param name
     */
    @Override
    public void increment_tint(String name) {
        actor.component(ActorComponents.TRANS_VAR).incrementVarInt(name);
    }

    /**
     * Increments the transient integer variable with the provided value amount for the variable with the provided var name
     *
     * @param name
     * @param value
     */
    @Override
    public void increment_tint(String name, int value) {
        actor.component(ActorComponents.TRANS_VAR).incrementVarInt(name, value);
    }

    /**
     * Decrements the transient integer variable with the default value of 1 for the variable with the provided var name
     *
     * @param name
     */
    @Override
    public void decrement_tint(String name) {
        actor.component(ActorComponents.TRANS_VAR).decrementVarInt(name);
    }

    /**
     * Decrements the transient integer variable with the provided value amount for the variable with the provided var name
     *
     * @param name
     * @param value
     */
    @Override
    public void decrement_tint(String name, int value) {
        actor.component(ActorComponents.TRANS_VAR).decrementVarInt(name, value);
    }

    /**
     * Sets the transient string variable with the provided value for the variable with the provided var name
     *
     * @param name
     * @param value
     */
    @Override
    public void set_tstr(String name, String value) {
        actor.component(ActorComponents.TRANS_VAR).setVarString(name, value);
    }

    /**
     * Sets the transient boolean variable with the provided value for the variable with the provided var name
     *
     * @param name
     * @param value
     */
    @Override
    public void set_tbool(String name, Boolean value) {
        actor.component(ActorComponents.TRANS_VAR).setVarBool(name, value);
    }

    /**
     * Gets the transient int variable value from the variable from the provided var name
     *
     * @param name
     * @return the value variable value
     */
    @Override
    public int get_tint(String name) {
        return actor.component(ActorComponents.TRANS_VAR).getVarInt(name);
    }

    /**
     * Gets the transient string variable value from the variable from the provided var name
     *
     * @param name
     * @return the value of the variable
     */
    @Override
    public String set_tstr(String name) {
        return actor.component(ActorComponents.TRANS_VAR).getVarString(name);
    }

    /**
     * Gets the transient boolean variable value from the variable from the provided var name
     *
     * @param name
     * @return the value of the variable
     */
    @Override
    public Boolean get_tbool(String name) {
        return actor.component(ActorComponents.TRANS_VAR).getVarBool(name);
    }
}
