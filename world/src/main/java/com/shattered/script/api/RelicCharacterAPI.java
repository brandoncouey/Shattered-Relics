package com.shattered.script.api;

import com.shattered.game.actor.character.Character;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.api.impl.ActorVarAPI;
import com.shattered.script.api.impl.CharacterCombatAPI;
import com.shattered.script.api.impl.ZoneAPI;

public abstract class RelicCharacterAPI extends RelicActorAPI {

    /**
     * Represents the Zone API
     * This API is used for any variable related to the zone
     */
    public final ZoneAPI zone;

    /**
     * Represents the Var API
     */
    public final ActorVarAPI vars;

    /**
     * Represents the Character Combat API
     */
    public final RelicCharacterCombatAPI combat;

    /**
     * Creates a constructor for the RelicCharacterAPI
     * @param character
     */
    public RelicCharacterAPI(Character character) {
        super(character);
        this.zone = new ZoneAPI(character);
        this.vars = new ActorVarAPI(character);
        this.combat = new CharacterCombatAPI(character);
    }

    /**
     * Moves the Character to a specific character
     * @param character
     */
    public abstract void move_to(RelicCharacterAPI character);

    /**
     * Moves the character to a specific coordinate
     * @param location
     */
    public abstract void move_to(GridCoordinate location);

    /**
     * Checks if the character is currently moving
     * @return is moving
     */
    public abstract boolean getMoving();

    /**
     * Decreases the characters movement speed by the specified percent
     * @param percent
     *
     * @return the amount of percent
     */
    public abstract int decrease_speed_by_percent(int percent);

    /**
     * Decreases the characters movement speed by the specified percent
     * @param amount
     *
     */
    public abstract void decrease_speed(int amount);

    /**
     * Decreases the characters movement speed by the specified percent
     *  for the given amount of seconds
     * @param percent
     * @param duration
     *
     * @return the amount of percent
     */
    public abstract int decrease_speed(int percent, float duration);

    /**
     * Increases the character movement speed by
     * @param percent
     *
     * @return the amount of percent
     */
    public abstract int increase_speed_by_percent(int percent);

    /**
     * Increases the character movement speed by
     * @param amount
     */
    public abstract void increase_speed(int amount);

    /**
     * Increases the character movement speed by the specified percent for
     *  the given amount of seconds
     * @param percent
     * @param duration
     *
     * @return the amount of percent
     */
    public abstract int increase_speed(int percent, float duration);

    /**
     * Makes the Character face a specific Actor
     * @param actor
     */
    public abstract void face_actor(RelicActorAPI actor);

    /**
     * Makes the Character face a specific coordinate
     * @param coordinate
     */
    public abstract void face_location(GridCoordinate coordinate);

    /**
     * Locks the npc, making them unable to move or rotate.
     */
    public abstract void lock();

    /**
     * Unlocks the npc and gives them the ability to move and rotate.
     */
    public abstract void unlock();

    /**
     * Locks the npc for the length of the specified delay in seconds and then unlocks them once the
     * duration has ended.
     * @param delay
     */
    public abstract void lock(int delay);

    /**
     * Adds the specified buff with the desired duration to the character
     * @param name
     * @param duration
     */
    public abstract void add_buff(String name, float duration);

    /**
     * Adds teh specified buff with the desired stacks and time duration.
     * @param name
     * @param stacks
     * @param duration
     */
    public abstract void add_buff(String name, int stacks, float duration);

    /**
     * Adds the specified buff with the desired duration to the character
     * @param name
     * @param source
     * @param duration
     */
    public abstract void add_buff(String name, RelicCharacterAPI source, float duration);

    /**
     * Adds teh specified buff with the desired stacks and time duration.
     * @param name
     * @param source
     * @param stacks
     * @param duration
     */
    public abstract void add_buff(String name, RelicCharacterAPI source, int stacks, float duration);

    /**
     * Removes the specified buff from the character.
     * @param name
     */
    public abstract void remove_buff(String name);

    /**
     * Checks if the sepcified buff is currently active
     * @param name
     */
    public abstract boolean has_buff(String name);




}
