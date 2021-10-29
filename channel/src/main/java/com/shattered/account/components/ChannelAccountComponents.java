package com.shattered.account.components;

import com.shattered.account.components.friends.ChannelFriendComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost 2/1/2020 : 4:39 PM
 */
public class ChannelAccountComponents <T extends Component> extends Components {


    /**
     * Represents the Friends List Componoent
     */
    public static final Components<ChannelFriendComponent> FRIENDS_LIST = new Components<>(() -> new ChannelFriendComponent(null));


    /**
     * @param supplier
     */
    public ChannelAccountComponents(Supplier supplier) {
        super(supplier);
    }
}
