package com.shattered.game.actor.character.player.component.interaction.dialogue;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DialogueOption {

    /**
     * Represents the Sprite Id for the Dialog Option
     */
    public final int spriteId;

    /**
     * Represents the Text of the Option (button)
     */
    public final String optionText;
}
