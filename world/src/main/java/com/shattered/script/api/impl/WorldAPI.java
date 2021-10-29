package com.shattered.script.api.impl;

import com.shattered.datatable.tables.NPCUDataTable;
import com.shattered.datatable.tables.ObjectUDataTable;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.npc.NPC;
import com.shattered.game.actor.object.WorldObject;
import com.shattered.game.actor.object.component.transform.Rotation;
import com.shattered.game.grid.GridCoordinate;
import com.shattered.script.api.RelicWorldAPI;

import java.util.ArrayList;
import java.util.List;

public class WorldAPI extends RelicWorldAPI {


    /**
     * Gets the Current World Name
     *
     * @return the world name
     */
    @Override
    public String getName() {
        return GameWorld.WORLD_NAME;
    }

    /**
     * Gets the Current Population of the World
     *
     * @return the population
     */
    @Override
    public String getPopulation() {
        return GameWorld.getPopulation();
    }

    /**
     * Gets a List of all Characters
     *
     * @return the characters
     */
    @Override
    public List<PlayerAPI> getPlayers() {
        ArrayList list = new ArrayList();
        for (Player player : GameWorld.getCharacters()) {
            if (player == null) continue;
            list.add(new PlayerAPI(player));
        }
        return list;
    }

    /**
     * Gets the player by their name
     *
     * @param name
     * @return the player
     */
    @Override
    public PlayerAPI player(String name) {
        Player player = GameWorld.findPlayer(name);
        if (player == null) return null;
        return new PlayerAPI(player);
    }

    /**
     * Checks if the player is in the world
     *
     * @param name
     * @return is in the world
     */
    @Override
    public boolean contains_player(String name) {
        return GameWorld.containsPlayer(name);
    }

    /**
     * Checks if a player is in the world
     *
     * @param clientIndex
     * @return is in the world
     */
    @Override
    public boolean contains_player(int clientIndex) {
        return GameWorld.containsPlayer(clientIndex);
    }

    /**
     * Finds a player by their client index
     *
     * @param clientIndex
     * @return the player
     */
    @Override
    public PlayerAPI player(int clientIndex) {
        Player player = GameWorld.findPlayer(clientIndex);
        if (player == null) return null;
        return new PlayerAPI(player);
    }


    /**
     * Sends a system message to all players in the world
     *
     * @param message
     */
    @Override
    public void system_message(String message) {
        GameWorld.sendSystemMessage(message);
    }

    /**
     * Sends a system message to the specified player
     *
     * @param character
     * @param message
     */
    @Override
    public void system_message(PlayerAPI character, String message) {
        GameWorld.sendSystemMessage(character.getPlayer(), message);
    }

}
