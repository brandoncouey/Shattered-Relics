package com.shattered.account;

import com.google.protobuf.GeneratedMessageV3;
import com.shattered.account.components.ChannelAccountComponentManager;
import com.shattered.account.components.ChannelAccountComponents;
import com.shattered.account.components.friends.ChannelFriendComponent;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.ecs.Component;
import com.shattered.utilities.ecs.Components;
import com.shattered.utilities.ecs.ProcessInterval;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * @author JTlr Frost 2/1/2020 : 3:25 PM
 * This SHOULD really be extending Account but fuck itl....
 */
@Data
public class ChannelAccount {

    /**
     * Represents the Channel of the Player
     */
    @Getter
    private Channel channel;

    /**
     * Represents the Account ID.
     */
    private int uuid;

    /**
     * Represents the Accounts Connection UUID
     * The same UUID to represent the World Connection.
     */
    private String connectionUuid;

    /**
     * Represents the Account Name and/or Character Name
     */
    private String name;

    /**
     * Represents the Location you're at.
     */
    private String location;

    /**
     * Represents the ServerName you're on.
     */
    private String serverName;

    /**
     * Represents the time of offline. If its -1 it will mean they are online.
     */
    private long offlineSince;


    /**
     * Represents the Channel Account Component Manager
     */
    @Getter
    @Setter
    private ChannelAccountComponentManager componentManager;

    /**
     *
     * @param channel
     * @param uuid
     * @param connectionUuid
     * @param name
     * @param location
     * @param serverName
     * @param offlineSince
     */
    public ChannelAccount(Channel channel, int uuid, String connectionUuid, String name, String location, String serverName, long offlineSince) {
        setChannel(channel);
        setUuid(uuid);
        setConnectionUuid(connectionUuid);
        setName(name);
        setLocation(location);
        setServerName(serverName);
        setOfflineSince(offlineSince);
        setComponentManager(new ChannelAccountComponentManager(this));
        addComponents();
    }

    /**
     * Adds the required components to the list.
     */
    public void addComponents() {
        getComponentManager().attatch(ChannelAccountComponents.FRIENDS_LIST, new ChannelFriendComponent(this));
    }


    /**
     * Called upon the Account Being Registered
     */
    public void onRegistered() {
        getComponentManager().onStart();
        component(ChannelAccountComponents.FRIENDS_LIST).sendFriendsList();
        SystemLogger.sendSystemMessage(getServerName() + ": " + getName() + " has come online.");
    }

    /**
     * Called each world cycle
     */
    public void onTick(long deltaSeconds) {
        getComponentManager().onTick(deltaSeconds);
    }

    /**
     * Called upon the Account being UnRegistered.
     */
    public void onUnregistered() {
        getComponentManager().onFinish();
        SystemLogger.sendSystemMessage(getServerName() + ": " + getName() + " has went offline.");
    }


    /**
     * Gets the Desired Component
     * @param components
     * @param <T>
     * @return
     */
    public <T extends Component> T component(Components<T> components) {
        return getComponentManager().get(components);
    }

    /**
     * Sends a message to the client with an empty payload.
     * @param opcode
     */
    public void sendMessage(PacketOuterClass.Opcode opcode) {
        sendMessage(opcode, PacketOuterClass.EmptyPayload.newBuilder().build());
    }

    /**
     *
     * @param opcode
     * @param message
     */
    public void sendMessage(PacketOuterClass.Opcode opcode, GeneratedMessageV3 message) {
        if (channel == null || !channel.isActive() || !channel.isOpen()) return;
        channel.writeAndFlush(PacketOuterClass.Packet.newBuilder().setOpcode(opcode).setPayload(message.toByteString()).build())
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }


}
