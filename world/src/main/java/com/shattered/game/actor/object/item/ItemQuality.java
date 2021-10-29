package com.shattered.game.actor.object.item;

/**
 * DataTable 'Quality' is reference by ordinal of the following qualities.
 */
public enum ItemQuality {

    /**
     * Represents 'None' Quality
     * Used on items that do not have an effective quality
     */
    NONE,

    POOR,

    FAIR,

    GOOD,

    EXCELLENT

    ;


    /**
     * Checks the Item Quality given the ordinal id
     * @param id
     * @return the item quality
     */
    public static ItemQuality forId(int id) {
        for (ItemQuality quality : ItemQuality.values()) {
            if (quality.ordinal() == id)
                return quality;
        }
        return null;
    }


}
