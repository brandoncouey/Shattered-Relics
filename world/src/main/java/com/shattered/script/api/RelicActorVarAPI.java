package com.shattered.script.api;

import com.shattered.game.actor.Actor;
import com.shattered.game.actor.character.Character;
import com.shattered.game.actor.character.player.Player;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NonNull
@RequiredArgsConstructor
public abstract class RelicActorVarAPI {

    /**
     * Represents the Character for the Quest API
     */
    @Getter
    protected final Actor actor;

    /**
     * Sets a saved integer variable for the variable with the provided variable name
     * @param name
     */
    public abstract void set_str(String name, int value);

    /**
     * Increments a saved integer by the default value of 1 for the variable with the provided variable name.
     * @param name
     */
    public abstract void increment_int(String name);

    /**
     * Increments a saved integer variable by the given value for the variable with the provided variable name
     * @param name
     * @param value
     */
    public abstract void increment_int(String name, int value);

    /**
     * Decrements a saved integer variable with the default amount of 1.
     * @param name
     */
    public abstract void decrement_int(String name);

    /**
     * Decrements a saved integer with the given amount
     * @param name
     * @param value
     */
    public abstract void decrement_int(String name, int value);

    /**
     * Sets the string variable with the provided value for the variable with the provided var name
     * @param name
     * @param value
     */
    public abstract void set_str(String name, String value);

    /**
     * Gets the int variable value from the variable with the provided var name
     * @param name
     * @return the value of the variable
     */
    public abstract int get_int(String name);

    /**
     * Gets the string variable value from the variable with the provided var name
     * @param name
     * @return the value of the variable
     */
    public abstract String get_str(String name);

    /**
     * Sets the transient integer variable to the provided value for the variable with the provided var id.
     * @param name
     * @param value
     */
    public abstract void set_tint(String name, int value);

    /**
     * Increments the transient integer variable with the default value of 1 for the variable with the provided var name
     * @param name
     */
    public abstract void increment_tint(String name);

    /**
     * Increments the transient integer variable with the provided value amount for the variable with the provided var name
     * @param name
     * @param value
     */
    public abstract void increment_tint(String name, int value);

    /**
     * Decrements the transient integer variable with the default value of 1 for the variable with the provided var name
     * @param name
     */
    public abstract void decrement_tint(String name);

    /**
     * Decrements the transient integer variable with the provided value amount for the variable with the provided var name
     * @param name
     * @param value
     */
    public abstract void decrement_tint(String name, int value);

    /**
     * Sets the transient string variable with the provided value for the variable with the provided var name
     * @param name
     * @param value
     */
    public abstract void set_tstr(String name, String value);

    /**
     * Sets the transient boolean variable with the provided value for the variable with the provided var name
     * @param name
     * @param value
     */
    public abstract void set_tbool(String name, Boolean value);

    /**
     * Gets the transient int variable value from the variable from the provided var name
     * @param name
     * @return the value variable value
     */
    public abstract int get_tint(String name);

    /**
     * Gets the transient string variable value from the variable from the provided var name
     * @param name
     * @return the value of the variable
     */
    public abstract String set_tstr(String name);

    /**
     * Gets the transient boolean variable value from the variable from the provided var name
     * @param name
     * @return the value of the variable
     */
    public abstract Boolean get_tbool(String name);


}
