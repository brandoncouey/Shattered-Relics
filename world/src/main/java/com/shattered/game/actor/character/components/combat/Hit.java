package com.shattered.game.actor.character.components.combat;

import com.shattered.game.actor.character.player.component.combat.ClassTypes;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Hit {

    /**
     * Represents if the Hit is a Critical
     */
    private final boolean critical;

    /**
     * Represents the amount of damage of the hit
     */
    private final int damage;

    /**
     * Represents the Style of the hit.
     */
    private final CombatDefinitions.AttackStyle style;

    /**
     * Represents the Class Type of the hit.
     */
    private final ClassTypes classType;

}
