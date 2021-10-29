package com.shattered.game.actor.character.player.component.combat.ability;

/**
 * @author Brad
 *
 * Each entry is used for an ability identifier
 * These are considered STRICTLY Ability Slots
 */
public enum AbilityKey {

    /**
     * Represents every 'Click' ability slot
     */
    LEFT_CLICK, SHIFT_LEFT_CLICK, RIGHT_CLICK, SHIFT_RIGHT_CLICK,

    /**
     * Represents every 'Normal' ability slot modifier with the specified key
     */
    Q, E, R, T, F, X, Z,

    /**
     * Represents every 'Shift' ability slot modifier with the specified key
     */
    SHIFT_Q, SHIFT_E, SHIFT_R, SHIFT_T, SHIFT_F, SHIFT_X, SHIFT_Z


}
