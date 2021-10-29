package com.shattered.game.actor.character.player.component.interaction;

public enum InteractionModifier {

    /* Represents 'E' Default Normal Modifier */
    NORMAL,

    /* Represents the Shift + E Default Shift Modifier */
    SHIFT,

    /* Represents the Cntrl + E Default Cntrl Modifier */
    CNTRL,

    /* Represents the Alt + E Default Alt Modifier */
    ALT,

    /* Represents the Shift + Cntrl + E Default Shift + Cntrl Modifier */
    SHIFT_CNTRL,

    ;


    /**
     * Gets the Interaction Modifier
     * @param id
     * @return
     */
    public static InteractionModifier forId(int id) {
        for (InteractionModifier interactionModifier : InteractionModifier.values())
            if (interactionModifier.ordinal() == id)
                return interactionModifier;
            return null;
    }


}
