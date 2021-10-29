package com.shattered.game.actor.container.loot;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PossibleLoot {

    /**
     * Represents the Item Id of the loot
     */
    private final String item;

    /**
     * Represents the Minimal amount for the loot
     */
    private final int minAmount;

    /**
     * Represents the Maximum amount for the loot
     */
    private final int maxAmount;

    /**
     * Represents the drop chance for the loot
     */
    private final float dropChance;
}
