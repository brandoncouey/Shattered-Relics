package com.shattered.game.actor.character.player.component.quest;

import com.shattered.game.actor.ability.Ability;
import com.shattered.game.actor.object.item.Item;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author JTlr Frost 11/15/2019 : 9:35 PM
 */
@Data
public class QuestRewards {

    /**
     * Represents a List of {@link Item}'s as a reward.
     */
    public ArrayList<Item> items = new ArrayList<>();

    /**
     * Represents an Ability as a Reward
     */
    public ArrayList<Ability> abilitys = new ArrayList<>();

    /**
     * Represents a Description of a Non Physical Reward
     */
    public ArrayList<String> descriptions = new ArrayList<>();
    
}
