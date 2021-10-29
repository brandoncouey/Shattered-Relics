package com.shattered.game.actor.character.player.component.channel;

import com.shattered.account.Account;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.component.WorldComponent;

/**
 * @author JTlr Frost 2/1/2020 : 10:20 AM
 */
public abstract class ChannelComponent extends WorldComponent {


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ChannelComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Sends a System Message to the {@link Character}
     * @param message
     */
    public void sendSystemMessage(String message) {
        channel().sendMessage(ChannelType.SYSTEM_MESSAGE, message);
    }

    /**
     * Sends a Default Message
     * @param message
     */
    public void sendDefaultMessage(String message) {
        channel().sendDefaultMessage(message);
    }

    /**
     * Gets the Channel Component
     * @return
     */
    public PlayerChannelComponent channel() {
        return getPlayer().component(PlayerComponents.SOCIAL_CHANNEL);
    }



}
