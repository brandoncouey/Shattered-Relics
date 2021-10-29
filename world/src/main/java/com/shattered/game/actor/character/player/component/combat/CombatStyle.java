package com.shattered.game.actor.character.player.component.combat;

/**
 * @author JTlr Frost 11/18/2019 : 4:11 PM
 */
public enum CombatStyle {

    /**
     * Represents the Melee Attack Style
     */
    MELEE,

    /**
     * Repreents the Archery Attack Style
     */
    ARCHERY,

    /**
     * Represents the Magic Attack Style
     */
    MAGIC


    ;


    /**
     *
     * @param index
     * @return
     */
    public static CombatStyle forId(int index) {
        switch (index) {
            case 1:
                return ARCHERY;
            case 2:
                return MAGIC;
        }
        return MELEE;
    }

}
