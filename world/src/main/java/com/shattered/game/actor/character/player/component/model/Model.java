package com.shattered.game.actor.character.player.component.model;

import lombok.Data;


/**
 * @author JTlr Frost 10/25/2019 : 12:17 PM
 */
@Data
public class Model {


    /**
     * Represents all potential race types
     */
    public enum Race {

        HUMAN,

        ELF,

        DWARF,

        ORC,

        TROLL

        ;

        /**
         * Converts the Race Id to Race
         * @param raceId
         * @return
         */
        public static Race forId(int raceId) {
            for (Race r : Race.values())
                if (raceId == r.ordinal())
                    return r;
            return null;
        }

    }

    /**
     * Represents if Male Gender
     */
    private boolean male;

    /**
     * Represents the current Race
     */
    private Race race = Race.HUMAN;

    /**
     * Represents the Body Color
     */
    private int bodyColor;

    /**
     * Represents the Hair Style of the Model
     */
    private int hairStyle;

    /**
     * Represents the Hair Color of the Model
     */
    private int hairColor;

    /**
     * Represents the Hair Style of the Model
     */
    private int eyebrowStyle;

    /**
     * Represents the Hair Color of the Model
     */
    private int eyebrowColor;

    /**
     * Represents the Beard Style of the Model
     */
    private int beardStyle;

    /**
     * Represents the Beard Color of the Model
     */
    private int beardColor;

    /**
     * Represents the Eye Color of the Model
     */
    private int eyeColor = 1;

    /**
     * Represents the Skin Color of the Current Model
     */
    private int skinColor = 8;

    /**
     * Represents if the Model is Hidden
     */
    private boolean hidden;

    /**
     * Represents the current Model Blend Pose Id
     */
    private int blendId;

}
