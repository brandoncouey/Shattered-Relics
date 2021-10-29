package com.shattered.script.api;

import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.api.impl.PlayerAPI;
import com.shattered.script.api.impl.DataTableAPI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NonNull
@RequiredArgsConstructor
public abstract class RelicWorldAPI {

    /**
     * Represents the Data Table API
     * This API is used for grabbing selected information throughout all our data tables
     * i.e item, npc, object information
     */
    public final DataTableAPI tables = new DataTableAPI();

    /**
     * Gets the Current World Name
     * @return the world name
     */
    public abstract String getName();

    /**
     * Gets the Current Population of the World
     * @return the population
     */
    public abstract String getPopulation();

    /**
     * Gets a List of all Characters
     * @return all the players
     */
    public abstract List<PlayerAPI> getPlayers();

    /**
     * Gets the player by their name
     * @param name
     * @return the player
     */
    public abstract PlayerAPI player(String name);

    /**
     * Checks if the player is in the world
     * @param name
     * @return is in the world
     */
    public abstract boolean contains_player(String name);

    /**
     * Checks if a player is in the world
     * @param clientIndex
     * @return is in the world
     */
    public abstract boolean contains_player(int clientIndex);

    /**
     * Finds a player by their client index
     * @param clientIndex
     * @return the player
     */
    public abstract PlayerAPI player(int clientIndex);

    /**
     * Sends a system message to all players in the world
     * @param message
     */
    public abstract void system_message(String message);

    /**
     * Sends a system message to the specified player
     * @param character
     * @param message
     */
    public abstract void system_message(PlayerAPI character, String message);
}
