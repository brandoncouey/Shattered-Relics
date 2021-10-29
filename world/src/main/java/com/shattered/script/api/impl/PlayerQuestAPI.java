package com.shattered.script.api.impl;

import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.quest.QuestState;
import com.shattered.script.api.RelicPlayerQuestAPI;

public class PlayerQuestAPI extends RelicPlayerQuestAPI {


    /**
     * Creates a new Constructor for the Channel API
     * @param player
     */
    public PlayerQuestAPI(Player player) {
        super(player);
    }

    /**
     * Starts the quest with the given name.
     * @param name
     */
    @Override
    public void discover(String name) {
        player.component(PlayerComponents.QUEST).discover(name);
    }

    /**
     * Starts the quest with the current set current_quest val
     */
    @Override
    public boolean discover() {
        if (current_quest == null || current_quest.isEmpty())
            return false;
        return player.component(PlayerComponents.QUEST).discover(current_quest);
    }

    /**
     * @param name
     */
    @Override
    public void complete(String name) {
        player.component(PlayerComponents.QUEST).completeQuest(name);
    }

    /**
     * Completes the quest with the current set current_quest value
     * @return completed
     */
    @Override
    public boolean complete() {
        if (current_quest == null || current_quest.isEmpty())
            return false;
        return player.component(PlayerComponents.QUEST).completeQuest(current_quest);
    }


    /**
     * Checks if the player has ever acquired the quest.
     *
     * @param name
     * @ return has acquired the quest
     */
    @Override
    public boolean discovered(String name) {
        return player.component(PlayerComponents.QUEST).hasQuest(name);
    }

    /**
     * Checks if the current quest has ever been acquired.
     *
     * @return acquired
     */
    @Override
    public boolean discovered() {
        if (current_quest == null || current_quest.isEmpty())
            return false;
        return player.component(PlayerComponents.QUEST).hasQuest(current_quest);
    }

    /**
     * Gets the Quest Stage, if the quest is not on-going it will result as 0
     *
     * @param name
     * @return the stage
     */
    @Override
    public int getStage(String name) {
        return player.component(PlayerComponents.QUEST).getStage(name);
    }

    /**
     * Gets the quest stage of the current_quest value
     * If the current_quest val is null, it will return a -1 value.
     */
    @Override
    public int getStage() {
        if (current_quest == null || current_quest.isEmpty())
            return -1;
        return getStage(current_quest);
    }

    /**
     * Sets the Quest Stage to the new quest stage.
     *
     * @param name
     * @param stage
     */
    @Override
    public void set_stage(String name, int stage) {
        player.component(PlayerComponents.QUEST).setStage(name, stage);
    }

    /**
     * Sets the quest stage of quest with the current set current_quest value
     * @param stage
     */
    @Override
    public void set_stage(int stage) {
        if (current_quest == null || current_quest.isEmpty())
            return;
        set_stage(current_quest, stage);
    }

    /**
     * Increments the Quest Stage. Appends a +1 to the stage
     *
     * @param name
     */
    @Override
    public void increment_stage(String name) {
        player.component(PlayerComponents.QUEST).setStage(name, player.component(PlayerComponents.QUEST).getStage(name) + 1);
    }

    /**
     * Increments the quest stage, appends a +1 val to the stage of quest with the current set current_quest value
     */
    @Override
    public void increment_stage() {
        if (current_quest == null || current_quest.isEmpty())
            return;
        increment_stage(current_quest);
    }

    /**
     * Gets the current completion state of the quest
     *
     * @param name
     * @return the quest state
     */
    @Override
    public QuestState getState(String name) {
        return player.component(PlayerComponents.QUEST).getState(name);
    }

    /**
     * Gets the current completion state of the quest stored in current_quest value
     * @return the state of the quest
     */
    @Override
    public QuestState getState() {
        if (current_quest == null || current_quest.isEmpty())
            return null;
        return getState(current_quest);
    }

}
