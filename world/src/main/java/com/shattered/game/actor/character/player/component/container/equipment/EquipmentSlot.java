package com.shattered.game.actor.character.player.component.container.equipment;

/**
 * @author JTlr Frost 11/2/2019 : 9:21 PM
 */
public enum EquipmentSlot {

    /**
     * Represents the NONE (For Items that are not of an equipment)
     */
    NONE,
    
    /**
     * Represents the Head Equipment Slot
     */
    HEAD,

    /**
     * Represents the Ear Ring
     * @slot left
     */
    EAR_RING_LEFT,

    /**
     * Represents the Ear Ring
     * @slot right
     */
    EAR_RING_RIGHT,

    /**
     * Represents the Necklace
     */
    NECKLACE,

    /**
     * Represents the Shoulder Pads
     */
    SHOULDERS,

    /**
     * Represents the Cloak
     * @alt Cape
     */
    BACK,

    /**
     * Represents the Chest Piece
     */
    CHEST,

    /**
     * Represents the Belt
     */
    BELT,

    /**
     * Represents the Pants Piece
     */
    PANTS,

    /**
     * Represents the Wrists
     */
    WRISTS,

    /**
     * Represents the Ring
     * @slot left
     */
    RING_LEFT,

    /**
     * Represents the Ring
     * @slot right
     */
    RING_RIGHT,

    /**
     * Represents the Glove
     * @slot left
     */
    GLOVES,

    /**
     * Represents the Left Hand
     * @slot Main
     */
    MAIN_HAND,

    /**
     * Represents the Right Hand
     * @slot Off-hand
     */
    OFF_HAND,

    /**
     * Represents the Boots
     */
    BOOTS,

    /**
     * Represents the Offspec Main Hand
     */
    OFFSPEC_MAIN_HAND,

    /**
     * Represennts the Offspec Offhand
     */
    OFFSPEC_OFF_HAND,

    /**
     * Represents the First set of Grimoire
     */
    GRIMOIRE_1,

    /**
     * Represents the Second set of Grimoire
     */
    GRIMOIRE_2
    
    ;

    /**
     * Gets the Appropriate Slot for Item
     * @param index
     * @return
     */
    public static EquipmentSlot forId(int index) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.ordinal() == index)
                return slot;
        }
        return null;
    }

    /**
     * Gets the appropriate slot by name.
     * @param name
     * @return
     */
    public static EquipmentSlot forName(String name) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (name.toLowerCase().contains("grimoire"))
                return EquipmentSlot.GRIMOIRE_1;
            if (slot.name().replace("_", "").equalsIgnoreCase(name))
                return slot;
        }
        return null;
    }
    
    
}
