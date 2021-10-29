package com.shattered.script.api.impl;

import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.script.api.RelicPlayerChannelAPI;

public class PlayerChannelAPI extends RelicPlayerChannelAPI {


    /**
     * Creates a new Constructor for the Channel API
     * @param player
     */
    public PlayerChannelAPI(Player player) {
        super(player);
    }

    /**
     * @param message
     */
    @Override
    public void send_default_message(String message) {
        player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(message);
    }
}
