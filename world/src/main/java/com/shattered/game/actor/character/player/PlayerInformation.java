package com.shattered.game.actor.character.player;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author JTlr Frost 7/27/18 : 7:59 PM
 */
@Data
@RequiredArgsConstructor
public class PlayerInformation {

    /**
     * Represents the Id of the Character
     */
    @Getter
    private final int id;

    /**
     * Represents the name of the Character
     */
    @Getter
    private final String name;

}
