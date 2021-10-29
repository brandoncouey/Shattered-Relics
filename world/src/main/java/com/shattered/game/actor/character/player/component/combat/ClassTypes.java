package com.shattered.game.actor.character.player.component.combat;

import com.shattered.game.actor.object.item.Item;

import java.util.Arrays;

public enum ClassTypes {

    /**
     * Represents the No Class Type
     */
    NONE,

    /**
     * Represents the Warrior Class
     */
    WARRIOR,

    /**
     * Represents the Archer Class
     */
    ARCHER,

    /**
     * Represents the Fire Mage Class
     */
    FIRE_MAGE,

    /**
     * Represents the Air Mage
     */
    AIR_MAGE

    ;

    /**
     * Converts a Grimoire to a Class
     * @param item
     * @return the class type for a grimoire
     */
    public static ClassTypes forGrimoire(Item item) {
        for (ClassTypes type : ClassTypes.values()) {
            if (item == null)
                return ClassTypes.NONE;
            if (item.getName().toLowerCase().contains(type.name().toLowerCase()))
                return type;
        }
        return ClassTypes.NONE;
    }
}
