package com.shattered.script.api;

import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.quest.QuestState;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@NonNull
@RequiredArgsConstructor
public abstract class RelicPlayerQuestAPI {

    /**
     * Represents the Character for the Quest API
     */
    @Getter
    protected final Player player;

    /**
     * Represents the Current Quest you're working with
     */
    public String current_quest;

    /**
     *
     * @param name
     */
    public abstract void discover(String name);

    /**
     * Starts the quest with the current set current_quest val
     */
    public abstract boolean discover();

    /**
     * Completes the quest with the provided name.
     * @param name
     */
    public abstract void complete(String name);

    /**
     * Completes the quest with the current set current_quest value
     * @return completed
     */
    public abstract boolean complete();

    /**
     * Checks if the player has ever acquired the quest.
     * @param name
     * @ return has acquired the quest
     */
    public abstract boolean discovered(String name);

    /**
     * Checks if the current quest has ever been acquired.
     * @return acquired
     */
    public abstract boolean discovered();

    /**
     * Gets the Quest Stage, if the quest is not on-going it will result as 0
     * @param name
     * @return the stage
     */
    public abstract int getStage(String name);

    /**
     * Gets the quest stage of the current_quest value
     * If the current_quest val is null, it will return a -1 value.
     */
    public abstract int getStage();

    /**
     * Sets the Quest Stage to the new quest stage.
     * @param name
     * @param stage
     */
    public abstract void set_stage(String name, int stage);

    /**
     * Sets the quest stage of quest with the current set current_quest value
     * @param stage
     */
    public abstract void set_stage(int stage);

    /**
     * Increments the Quest Stage. Appends a +1 to the stage
     * @param name
     */
    public abstract void increment_stage(String name);

    /**
     * Increments the quest stage, appends a +1 val to the stage of quest with the current set current_quest value
     */
    public abstract void increment_stage();

    /**
     * Returns the quest state
     * @param name
     * @return the quest state
     */
    public abstract QuestState getState(String name);

    /**
     * Gets the current completion state of the quest stored in current_quest value
     * @return the state of the quest
     */
    public abstract QuestState getState();


}
