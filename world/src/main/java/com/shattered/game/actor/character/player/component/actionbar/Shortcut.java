package com.shattered.game.actor.character.player.component.actionbar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Brad
 */
@RequiredArgsConstructor
public class Shortcut {

    /**
     * Represents the ID of the Shortcut Reference.
     */
    @Getter private final int id;


    /**
     * Represents the Type of the Shortcut
     */
    @Getter private final ShortcutType type;


}
