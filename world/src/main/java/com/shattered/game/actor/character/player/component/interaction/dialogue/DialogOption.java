package com.shattered.game.actor.character.player.component.interaction.dialogue;

import lombok.Data;

/**
 * Represents the Dialog Option
 */
@Data
public class DialogOption {

    /**
     * Represents the Sprite Id (0 if none)
     */
    public int spriteId;

    /**
     * Represents the Button Text of a Dialog Option
     */
    public String text;

    /**
     * Creates a dialog option with only the button text
     * @param text
     */
    public DialogOption(String text) {
        this.text = text;
    }

    /**
     * Creates a dialog option with a set sprite id and button text.
     * @param spriteId
     * @param text
     */
    public DialogOption(int spriteId, String text) {
        this.spriteId = spriteId;
        this.text = text;
    }

}
