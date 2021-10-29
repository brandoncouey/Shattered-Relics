package com.shattered.script.api;

import com.shattered.game.actor.character.player.Player;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@NonNull
@RequiredArgsConstructor
public abstract class RelicPlayerChannelAPI {

    /**
     * Represents the Character for the Channel API
     */
    @Getter
    protected final Player player;

    /**
     *
     * @param message
     */
    public abstract void send_default_message(String message);
}
