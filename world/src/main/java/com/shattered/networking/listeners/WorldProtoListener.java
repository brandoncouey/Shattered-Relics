package com.shattered.networking.listeners;

import com.google.protobuf.Message;
import com.shattered.game.actor.character.player.Player;

/**
 * @author JTlr Frost 9/8/2019 : 10:38 PM
 */
public abstract class WorldProtoListener <T extends Message> implements ProtoListener {


    /**
     *
     * @param message
     * @param player
     */
    public abstract void handle(T message, Player player);

    /**
     * Handles the Raw Message
     * @param message
     * @param player
     */
    public void handleRaw(Message message, Player player) {
        try {
            handle((T) message, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
