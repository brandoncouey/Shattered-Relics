package com.shattered.game.actor.character.components.combat;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class CombatDefinitions {

    public static final int CC_MAGIC_LOCKED = 1, CC_DISARMED = 2, CC_STUNNED = 3, CC_SNARED = 4, CC_DISORIENTED = 5;

    public static final double
            WEAKNESS_MULTIPLIER_STRONGEST = 0.80,
            WEAKNESS_MULTIPLIER_STRONG = 0.65,
            WEAKNESS_MULTIPLIER_NEUTRAL = 0.55,
            WEAKNESS_MULTIPLIER_LOW = 0.45;

    /**
     * Represents the Attack Style
     */
    public enum AttackStyle {
        MELEE, ARCHERY, MAGIC
    }

}
