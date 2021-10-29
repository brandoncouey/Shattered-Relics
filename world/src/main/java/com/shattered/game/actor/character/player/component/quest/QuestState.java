package com.shattered.game.actor.character.player.component.quest;

/**
 * @author JTlr Frost 11/15/2019 : 9:38 PM
 */
public enum QuestState {

    /** Represents as a Quest that has not been discovered yet */
    UNDISCOVERED,

    /** Represents a discovered quest, but not started */
    DISCOVERED,
    
    /** Represents the Quest State In Progress */
    IN_PROGRESS,
    
    /** Represents the Quest State is Finished */
    COMPLETED

    ;

    /**
     * Gets the Quest State for Id
     * @param stateId
     * @return the quest state
     */
    public static QuestState forId(int stateId) {
        for (QuestState state : QuestState.values()) {
            if (state.ordinal() == stateId) return state;
        }
        return QuestState.UNDISCOVERED;
    }
    
}
