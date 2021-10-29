package com.shattered.account;

import com.google.protobuf.GeneratedMessageV3;
import com.shattered.account.component.AccountComponentManager;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.ecs.ComponentManager;
import com.shattered.utilities.ecs.ProcessInterval;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/20/18 : 5:43 PM
 */
public abstract class Account {


    /**
     * Represents the Account Channel
     */
    @Setter
    @Getter
    private Channel channel;

    @Getter
    @Setter
    private String connectionUUID;

    /**
     * Represents the Account Information
     */
    @Setter
    @Getter
    private AccountInformation accountInformation;

    /**
     * Represents the CharacterInformation ContainerManager
     */
    @Getter
    @Setter
    protected ComponentManager componentManager;

    /**
     * Creates a new {@linkplain Account} for the player
     * @param channel
     * @param accountInformation
     */
    public Account(Channel channel, AccountInformation accountInformation) {
        setChannel(channel);
        setAccountInformation(accountInformation);
        setComponentManager(new AccountComponentManager(this));
        addComponents();
        
    }

    /**
     * Represents the {@code Account} initialization.
     */
    public void onStart() {
        getComponentManager().onStart();
    }

    /**
     * Method is called once per realm cycle
     */
    public void onTick(long deltaTime) {
        getComponentManager().onTick(deltaTime);
    }

    public void addComponents() { 
    }

    /**
     * Represents the {@code Account} Disconnection.
     */
    public void onFinish() {
        getComponentManager().onFinish();
        getAccountInformation().setStatus(AccountInformation.OnlineStatus.OFFLINE);
        SystemLogger.sendSystemMessage(getAccountInformation().getAccountName() + " has logged out.");
        
        if (channel != null && channel.isActive()) {
            channel.disconnect();
        }

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
