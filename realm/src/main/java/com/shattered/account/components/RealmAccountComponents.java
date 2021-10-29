package com.shattered.account.components;

import com.shattered.account.components.friend.FriendChannelComponent;
import com.shattered.account.components.managers.characters.RealmCharacterManagerComponent;
import com.shattered.account.components.managers.RealmListManagerComponent;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;

import java.util.function.Supplier;

/**
 * @author JTlr Frost 9/5/2019 : 11:05 PM
 */
public class RealmAccountComponents<T extends Component> extends Components {


    
    
    /**
     * Represents the GameRealm Character Manager Component
     */
    public static final Components<RealmCharacterManagerComponent> CHARACTER_MANAGER = new Components<>(() -> new RealmCharacterManagerComponent(null));

    /**
     * Represents the GameRealm List Manager Component
     */
    public static final Components<RealmListManagerComponent> REALM_LIST_MANAGER = new Components<>(() -> new RealmListManagerComponent(null));

    /**
     * Represents the Friends Channel Component
     */
    public static final Components<FriendChannelComponent> FRIENDS_CHANNEL = new Components<>(() -> new FriendChannelComponent(null));

   
    /**
     * @param supplier
     */
    public RealmAccountComponents(Supplier supplier) {
        super(supplier);
    }
    
}
