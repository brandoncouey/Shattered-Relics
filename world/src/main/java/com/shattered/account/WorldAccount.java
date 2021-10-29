package com.shattered.account;

import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.PlayerInformation;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 8/31/2019 : 2:03 AM
 */
public class WorldAccount extends Account {



    /**
     * Represents the current selected Character (toon) for this account
     */
    @Setter
    @Getter
    private Player player;

    /**
     * Creates a new {@linkplain Account} for the player
     *
     * @param channel
     * @param accountInformation
     */
    public WorldAccount(Channel channel, AccountInformation accountInformation, PlayerInformation playerInformation) {
        super(channel, accountInformation);
        setPlayer(new Player(channel, this, playerInformation));
    }

    /**
     * Represents when the Account has been successfully initialized.
     */
    public void onAwake() {
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_WORLD_LOADED, new WorldProtoListener<PacketOuterClass.EmptyPayload>() {

            @Override
            public void handle(PacketOuterClass.EmptyPayload message, Player player) {
                player.component(PlayerComponents.WORLD_LEVEL_MANAGER).setWorldLevelLoaded(true);
                player.onWorldAwake();
            }
        }, PacketOuterClass.EmptyPayload.getDefaultInstance());
    }

    /**
     * Called after {@link WorldAccount#onStart()} for handling of loaded information.
     */
    @Override
    public void onStart() {
        getPlayer().onAwake();
        getAccountInformation().setStatus(AccountInformation.OnlineStatus.WORLD);
        getComponentManager().onStart();
        SystemLogger.sendSystemMessage(getAccountInformation().getAccountName() + " has logged in.");
    }

    /**
     * Method called upon logging out / disconnection.
     */
    @Override
    public void onFinish() {
        super.onFinish();
        getPlayer().onFinish();
    }

    /**
     * Adds all necessary components for the {@link Account}
     */
    @Override
    public void addComponents() {
        super.addComponents();
    }

    /**
     * Represents the Name of the Game Account.
     *
     * @return
     */
    public String getName() {
        return getAccountInformation().getAccountName();
    }

}
