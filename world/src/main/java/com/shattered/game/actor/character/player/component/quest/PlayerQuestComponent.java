package com.shattered.game.actor.character.player.component.quest;

import com.shattered.account.Account;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.components.ActorComponents;
import com.shattered.game.component.WorldComponent;
import com.shattered.script.ScriptManager;
import com.shattered.script.types.QuestScript;
import com.shattered.utilities.VariableUtility;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JTlr Frost 11/15/2019 : 9:36 PM
 */
public class PlayerQuestComponent extends WorldComponent {



    /**
     * Represents a List of Current Quests
     */
    @Getter
    private final Map<String, QuestScript> quests;

    /**
     * Represents the Current Tracked Quests
     * 
     * Should not have a max track. Should be able to filter them accordingly on the client.
     */
    @Getter
    private Map<String, QuestScript> trackedQuests;
    

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public PlayerQuestComponent(Object gameObject) {
        super(gameObject);
        this.quests = new HashMap<>();
        this.trackedQuests = new HashMap<>();
    }

    /**
     * Starts the Quest
     * @param name
     */
    public boolean discover(String name) {
        try {
            if (getQuests().containsKey(name)) return true;
            QuestScript quest = ScriptManager.getQuestScript(name);
            getQuests().put(name, quest);
            component(ActorComponents.VAR).setVarInt(name + "_stage", 0);
            component(ActorComponents.VAR).setVarInt(name + "_state", QuestState.DISCOVERED.ordinal());
            component(PlayerComponents.WIDGET).sendQuestDiscoveredNotification(VariableUtility.formatString(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void start(String name) {
        try {
            if (!getQuests().containsKey(name)) return;
            QuestScript quest = getQuest(name);
            component(ActorComponents.VAR).setVarInt(name + "_stage", 1);
            component(ActorComponents.VAR).setVarInt(name + "_state", QuestState.IN_PROGRESS.ordinal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tracks a Current Quest
     * @param name
     * @return
     */
    public boolean track(String name) {
        if (getTrackedQuests().containsKey(name)) return true;
        QuestScript quest = getQuests().get(name);
        if (quest == null) return false;
        getTrackedQuests().put(name, quest);
        return true;
    }

    /**
     * Completes the quest specified
     * @param name
     */
    public boolean completeQuest(String name) {
        return true;
    }

    /**
     * Gets the Quest Stage, if the quest is not on-going it will result as -1
     * @param name
     * @return the stage
     */
    public int getStage(String name) {
        return component(ActorComponents.VAR).getVarInt(name + "_stage");
    }

    /**s
     * Sets the specified quest's current stage
     * @param name
     * @param stage
     */
    public void setStage(String name, int stage) {
        component(ActorComponents.VAR).setVarInt(name + "_stage", stage);
    }

    /**
     * Checks if the player has ever acquired the quest.
     * @param name
     * @ return has acquired the quest
     */
    public boolean hasQuest(String name) {
        return quests.containsKey(name);
    }

    /**
     * Returns the quest state
     * @param name
     * @return the quest state
     */
    public QuestState getState(String name) {
        return QuestState.forId(component(ActorComponents.VAR).getVarInt(name + "_state"));
    }

    /**
     * Gets the quest from the map.
     * @param name
     * @return the quest
     */
    public QuestScript getQuest(String name) {
        return quests.get(name);
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {

    }

    /**
     * Database name.
     */
    @Override
    public String getDatabaseName() {
        return null;
    }

    /**
     * Table name.
     */
    @Override
    public String getTableName() {
        return null;
    }
}
