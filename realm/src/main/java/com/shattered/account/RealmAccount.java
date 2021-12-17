package com.shattered.account;

import com.shattered.account.components.RealmAccountComponents;
import com.shattered.account.components.friend.FriendChannelComponent;
import com.shattered.account.components.managers.characters.RealmCharacterManagerComponent;
import com.shattered.account.components.managers.RealmListManagerComponent;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.RealmProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.realm.GameRealm;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;
import io.netty.channel.Channel;

/**
 * @author JTlr Frost 8/31/2019 : 2:04 AM
 */
public class RealmAccount extends Account {


    /**
     * Creates a new {@linkplain Account} for the player
     *
     * @param channel
     * @param accountInformation
     */
    public RealmAccount(Channel channel, AccountInformation accountInformation) {
        super(channel, accountInformation);
        addComponents();
        getComponentManager().onFetchData();

        //Registers Realm Loaded Listener
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.CMSG_REALM_LOADED, new RealmProtoListener<PacketOuterClass.EmptyPayload>() {

            /**
             * @param message
             * @param account
             */
            @Override
            public void handle(PacketOuterClass.EmptyPayload message, RealmAccount account) {
                account.getComponentManager().onWorldAwake();
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());


    }

    @Override
    public void onStart() {
        GameRealm.addAccount(this);
        super.onStart();
        getAccountInformation().setStatus(AccountInformation.OnlineStatus.REALM);
        getAccountInformation().transmit(getChannel());
        SystemLogger.sendSystemMessage(getAccountInformation().getAccountName() + " has logged in.");
    }

    /**
     * Adds the Components
     */
    @Override
    public void addComponents() {
        super.addComponents();
        getComponentManager().attatch(RealmAccountComponents.REALM_LIST_MANAGER, new RealmListManagerComponent(this));
        getComponentManager().attatch(RealmAccountComponents.CHARACTER_MANAGER, new RealmCharacterManagerComponent(this));
        getComponentManager().attatch(RealmAccountComponents.FRIENDS_CHANNEL, new FriendChannelComponent(this));
    }

    @Override
    public void onFinish() {
        getComponentManager().onUpdateData();
        super.onFinish();
        GameRealm.removeAccount(this);
    }

    /**
     * Represents the Name of the Game Object
     *
     * @return
     */
    public String getName() {
        if (getAccountInformation() != null)
            return getAccountInformation().getAccountName();
        return "Unavailable";
    }

    /**
     * Gets a piece of Game Object Component
     * @param components
     */
    public <T extends Component> T component(Components<T> components) {
        return getComponentManager().get(components);
    }
}
