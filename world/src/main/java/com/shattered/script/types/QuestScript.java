package com.shattered.script.types;

import com.shattered.game.actor.ability.Ability;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.quest.QuestRequirements;
import com.shattered.game.actor.character.player.component.quest.QuestRewards;
import com.shattered.game.actor.character.player.component.quest.QuestState;
import com.shattered.game.actor.container.Containers;
import com.shattered.game.actor.object.item.Item;
import com.shattered.script.api.*;
import com.shattered.script.api.impl.*;

public abstract class QuestScript {

    /**
     * Represents the Game World API
     */
    public final RelicWorldAPI world = new WorldAPI();

    /**
     * Represents the Game World API
     */
    public final RelicEngineAPI engine = new EngineAPI();

    /**
     * Represents the Data Table API
     * This API is used for grabbing selected information throughout all our data tables
     * i.e item, npc, object information
     */
    public final DataTableAPI tables = new DataTableAPI();

    /**
     * Represents the Math API
     */
    public final RelicMathAPI math = new MathAPI();

    /**
     * Represents the Utility API
     */
    public final RelicUtilityAPI utility = new UtilityAPI();

    /**
     * Represents the Quest Rewards
     */
    public final QuestRewards rewards = new QuestRewards();

    /**
     * Represents the Quest Requirements
     */
    public final QuestRequirements requirements = new QuestRequirements();

    /**
     * Constructs the quest
     */
    public abstract void construct();

    /**
     * Registers the Quest for the Quest Name
     * @return the quest name
     */
   public abstract String name();

    /**
     * Gets the Quest State for the current quest
     * @param player
     * @return the quest state
     */
   public QuestState getState(RelicPlayerAPI player) {
       return QuestState.forId(player.vars.get_int(name() + "_state"));
   }

    /**
     * Gets the Stage for the PlayerAPI
     * @param player
     * @return the stage number
     */
   public int getStage(RelicPlayerAPI player) {
       return player.vars.get_int(name() + "_stage");
   }

   public void displayLog(RelicPlayerAPI player) {
   }

   public void complete(RelicPlayerAPI player) {
       
   }

    public void giveRewards(RelicPlayerAPI player) {
        for (Item item : rewards.getItems()) {
            player.getContainers().inv_add_item(item);
        }
        for (Ability ability : rewards.getAbilitys()) {
            player.getPlayer().container(Containers.ABILITY_BOOK).add(ability);
        }
        for (String message : rewards.getDescriptions()) {
            player.getChannel().send_default_message("You now have " + message);
        }
    }

}
