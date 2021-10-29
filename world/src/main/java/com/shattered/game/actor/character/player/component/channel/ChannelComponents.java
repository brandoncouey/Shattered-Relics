package com.shattered.game.actor.character.player.component.channel;

import com.shattered.game.actor.character.player.component.channel.friend.ChannelFriendComponent;
import com.shattered.game.actor.character.player.component.channel.party.ChannelPartyComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost 2/1/2020 : 10:18 AM
 */
public class ChannelComponents <T extends Component> extends Components {


    /**
     * Represents the Friends Channel Component
     */
    public static final Components<ChannelFriendComponent> FRIENDS_CHANNEL = new Components<>(() -> new ChannelFriendComponent(null));

    /**
     * Represents the Party Channel Component
     */
    public static final Components<ChannelPartyComponent> PARTY_CHANNEL = new Components<>(() -> new ChannelPartyComponent(null));


    /**
     * @param supplier
     */
    public ChannelComponents(Supplier supplier) {
        super(supplier);
    }
}
