package com.shattered.datatable.tables.types;

import com.shattered.game.actor.character.player.component.quest.QuestState;
import lombok.Data;

@Data
public class QuestRequirement {

    /**
     * Represents the name of the quest
     */
    private String name;

    /**
     * Represents the state required
     */
    private QuestState state = QuestState.DISCOVERED;

    /**
     * Represents the  description of the quest requirement
     */
    private String description;

    /**
     * Converts state to QuestState
     * @param state
     * @return the quest state
     */
    public static QuestState forState(String state) {
        for (QuestState qs : QuestState.values()) {
            if (qs.name().equalsIgnoreCase(state))
                return qs;

        }
        return QuestState.DISCOVERED;
    }
}
