package com.shattered.game.actor.character.player.component.channel.friend;

import com.google.protobuf.GeneratedMessageV3;
import com.shattered.BuildWorld;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.game.GameWorld;
import com.shattered.account.Account;
import com.shattered.game.actor.character.player.Player;
import com.shattered.game.actor.character.player.component.PlayerComponents;
import com.shattered.game.actor.character.player.component.channel.ChannelComponent;
import com.shattered.game.actor.character.player.component.channel.ChannelComponents;
import com.shattered.game.actor.object.component.GameObjectComponents;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.listeners.WorldProtoListener;
import com.shattered.networking.proto.Channel;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.session.Session;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Frost 2/1/2020 : 10:21 AM
 */
public class ChannelFriendComponent extends ChannelComponent {


    /**
     * Represents the List of Friends
     */
    @Getter
    private final Map<Integer, FriendInformation> friends = new ConcurrentHashMap<>();

    /**
     * Represents the List of Character Id's that are ignored.
     */
    @Getter
    private final List<Integer> ignores = new CopyOnWriteArrayList<>();

    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ChannelFriendComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Checks if the specified player is a friend or not.
     * @param name
     * @return is a valid friend
     */
    public boolean hasFriend(String name) {
        for (FriendInformation friend : friends.values()) {
            if (friend == null)
                continue;
            if (friend.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }


    /**
     * Adds a Friend with the Desired Character Name.
     * @param name
     */
    public void addFriend(String name) {
        if (hasFriend(name)) {
            sendSystemMessage("This player is already on your friends list.");
            return;
        }
        //TODO fetch their data, and append to client list.
    }

    public void removeFriend(String name) {

        //check if exists...
    }


    public void requestFriendsList() {
        sendChannelMessage(PacketOuterClass.Opcode.C_Friends_List, Channel.FetchFriendsList.newBuilder().
                setUuid(getPlayer().getAccount().getAccountInformation().getAccountId()).
                build());
    }


    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_PRIVATE_MESSAGE, new WorldProtoListener<Shared.SendPrivateMessage>() {
            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(Shared.SendPrivateMessage message, Player player) {

                if (GameWorld.containsPlayer(message.getTo())) {
                    Player to = GameWorld.findPlayer(message.getTo());
                    if (to != null) {
                        player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).sendPrivateMessage("To " + to.getName(), message.getMessage(), player.getAccount().getAccountInformation().getAccountLevel().ordinal());
                        to.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).sendPrivateMessage(player.getName(), message.getMessage(), player.getAccount().getAccountInformation().getAccountLevel().ordinal());
                    } else {
                        player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).sendDefaultMessage("Unable to send your message.");
                    }
                    return;
                }
                //TODO this should really verify the message / player has been found and received, but fuck it. Shows they online if it can't receive it. #L2code.
                player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).sendPrivateMessage("To " + message.getTo(), message.getMessage(), player.getAccount().getAccountInformation().getAccountLevel().ordinal());
                sendChannelMessage(PacketOuterClass.Opcode.C_PrivateMessage, Channel.SendPrivateMessage.newBuilder().
                        setFrom(player.getName()).
                        setTo(message.getTo()).
                        setMessage(message.getMessage()).
                        setPermissionLevel(player.getAccount().getAccountInformation().getAccountLevel().ordinal()).
                        build());
            }
        }, Shared.SendPrivateMessage.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.CMSG_ADD_FRIEND, new WorldProtoListener<Shared.FriendName>() {
            /**
             * @param message
             * @param player
             */
            @Override
            public void handle(Shared.FriendName message, Player player) {

                //TODO add friend..
                ChannelFriendComponent channelFriend = player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL);

                if (channelFriend != null) {

                    if (!isChannelActive()) {
                        channelFriend.sendDefaultMessage("Friends server is currently unavailable.");
                        return;
                    }

                    if (channelFriend.hasFriend(message.getName())) {
                        channelFriend.sendDefaultMessage("You are already friends with that player.");
                        return;
                    }
                    if (player.getName().equalsIgnoreCase(message.getName())) {
                        channelFriend.sendDefaultMessage("You cannot add your self as a friend.");
                        return;
                    }
                    channelFriend.sendChannelMessage(PacketOuterClass.Opcode.C_AddFriend, Channel.AddRemoveFriend.newBuilder().
                            setUuid(player.getAccount().getAccountInformation().getAccountId()).
                            setName(message.getName()).
                            build());
                }

            }
        }, Shared.FriendName.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_PrivateMessage, new ProtoListener<Channel.ReceivePrivateMessage>() {

            /**
             *
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.ReceivePrivateMessage message, Session session) {
                Player player = GameWorld.findPlayerByUuid(message.getTuuid());
                if (player != null) {
                    player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).sendPrivateMessage(message.getFrom(), message.getMessage(), message.getPermissionLevel());
                }
            }
        }, Channel.ReceivePrivateMessage.getDefaultInstance());


        //Updates the entire friends list...
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_Friends_List, new ProtoListener<Channel.FriendsList>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.FriendsList message, Session session) {
                //Makes sure this gets back to the rightful player.
                Player player = GameWorld.findPlayerByUuid(message.getUuid());

                if (player != null) {
                    Shared.FriendsList.Builder builder = Shared.FriendsList.newBuilder();

                    for (Channel.Friend friend : message.getFriendList()) {
                        player.component(PlayerComponents.SOCIAL_CHANNEL).channel(ChannelComponents.FRIENDS_CHANNEL).getFriends().put(friend.getUuid(), new FriendInformation(friend.getUuid(), friend.getConnectionUuid(), friend.getName(), friend.getLocation(), friend.getServerName()));

                        builder.addEntry(Shared.Friend.newBuilder().
                                setIndex(friend.getUuid()).
                                setName(friend.getName()).
                                setLocation(friend.getLocation()).
                                setServer(friend.getServerName()).
                                build());
                    }
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_FRIENDS_LIST, builder.build());
                }
            }
        }, Channel.FriendsList.getDefaultInstance());



        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_OfflineStatus_Player, new ProtoListener<Channel.PlayerOffline>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.PlayerOffline message, Session session) {

                Player player = GameWorld.findPlayerByUuid(message.getTuuid());

                if (player != null) {
                    player.component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage(message.getName() + " has gone offline.");
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_FRIEND, Shared.Friend.newBuilder().
                            setIndex(message.getUuid())
                            .setName(message.getName()).
                                    setLocation("Offline for 1 minute").
                                    setServer("Offline").
                                    build());
                }


            }
        }, Channel.PlayerOffline.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_AddFriend, new ProtoListener<Channel.Friend>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.Friend message, Session session) {

                Player player = GameWorld.findPlayerByUuid(message.getTuuid());
                if (player != null) {
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_FRIEND, Shared.Friend.newBuilder()
                            .setIndex(message.getUuid())
                            .setName(message.getName())
                            .setLocation(message.getLocation())
                            .setServer(message.getServerName())
                            .build());

                }
            }
        }, Channel.Friend.getDefaultInstance());



        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_OnlineStatus_Player, new ProtoListener<Channel.PlayerOnlineStatus>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.PlayerOnlineStatus message, Session session) {

                Player player = GameWorld.findPlayerByUuid(message.getTuuid());

                if (player != null) {
                    player.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_FRIEND, Shared.Friend.newBuilder()
                            .setIndex(message.getUuid())
                            .setName(message.getName())
                            .setLocation(message.getLocation())
                            .setServer(message.getServerName())
                            .build());
                }


            }
        }, Channel.PlayerOnlineStatus.getDefaultInstance());



    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {
        if (!isChannelActive()) {
            sendDefaultMessage("Friends server is currently unavailable.");
            return;
        }

        String location = getPlayer().component(GameObjectComponents.ZONE_COMPONENT).getZoneName();
        location = location.replace("_", "");

        sendChannelMessage(PacketOuterClass.Opcode.C_Register_Player, Channel.RegisterPlayer.newBuilder().
                setUuid(getPlayer().getAccount().getAccountInformation().getAccountId()).
                setConnectionUuid(BuildWorld.getInstance().getNetwork().getConnectionUuid()).
                setName(getPlayer().getName()).
                setLocation(location).
                setServerName(GameWorld.WORLD_NAME).
                build());

        DelayedTaskTicker.delayTask(this::requestFriendsList, 1);
    }

    /**
     * Updates the Online Status for the Player.
     */
    public void sendUpdatedOnlineStatus() {
        sendChannelMessage(PacketOuterClass.Opcode.C_OnlineStatus_Player, Channel.PlayerOnlineStatus.newBuilder().
                setUuid(getPlayer().getAccount().getAccountInformation().getAccountId()).
                setConnectionUuid(BuildWorld.getInstance().getNetwork().getConnectionUuid()).
                setName(getPlayer().getName()).
                setServerName(GameWorld.WORLD_NAME).
                setLocation(getPlayer().component(GameObjectComponents.ZONE_COMPONENT).getZoneName()).build());
    }

    /**
     *
     * @param from
     * @param message
     * @param permissionLevel
     */
    public void sendPrivateMessage(String from, String message, int permissionLevel) {
        Shared.ReceivePrivateMessage.Builder privateMessage = Shared.ReceivePrivateMessage.newBuilder();
        privateMessage.setFrom(from);
        privateMessage.setMessage(message);
        privateMessage.setPermissionLevel(permissionLevel);
        sendMessage(PacketOuterClass.Opcode.SMSG_PRIVATE_MESSAGE, privateMessage.build());
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaTime) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        sendChannelMessage(PacketOuterClass.Opcode.C_Unregister_Player, Channel.UnregisterPlayer.newBuilder().setUuid(getPlayer().getAccount().getAccountInformation().getAccountId()).build());
    }

    /**
     * Sends a message to the Channel Server
     * @param opcode
     * @param messageV3
     */
    public void sendChannelMessage(PacketOuterClass.Opcode opcode, GeneratedMessageV3 messageV3) {
        if (isChannelActive()) {
            BuildWorld.getInstance().getNetwork().getChannelSession().sendMessage(opcode, messageV3);
        } else {
            getPlayer().component(PlayerComponents.SOCIAL_CHANNEL).sendDefaultMessage("Friends server is currently unavailable.");
        }
    }

    /**
     * Checks if the Channel Server has an Active Server
     * @return is active
     */
    public boolean isChannelActive() {
        return BuildWorld.getInstance().getNetwork().getChannelSession() != null && BuildWorld.getInstance().getNetwork().getChannelSession().getChannel().isActive();
    }
}
