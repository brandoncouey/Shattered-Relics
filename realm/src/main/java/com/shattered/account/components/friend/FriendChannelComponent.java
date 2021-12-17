package com.shattered.account.components.friend;

import com.google.protobuf.GeneratedMessageV3;
import com.shattered.BuildRealm;
import com.shattered.account.Account;
import com.shattered.account.RealmAccount;
import com.shattered.account.components.RealmAccountComponents;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.listeners.RealmProtoListener;
import com.shattered.networking.proto.Channel;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.session.Session;
import com.shattered.realm.GameRealm;
import com.shattered.utilities.ecs.Component;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Frost 2/1/2020 : 10:21 AM
 */
public class FriendChannelComponent extends Component {


    //Should i really have a friends list on this server....?

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
    public FriendChannelComponent(Object gameObject) {
        super(gameObject);
    }

    /**
     * Checks if the specified character is a friend or not.
     * @param name
     * @return
     */
    public boolean hasFriend(String name) {
        for (FriendInformation friend : friends.values()) {
            if (friend == null) continue;
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
            return;
        }
        //TODO fetch their data, and append to client list.
    }

    public void removeFriend(String name) {

        //check if exists...
    }


    public void requestFriendsList() {
        sendChannelMessage(PacketOuterClass.Opcode.C_Friends_List, Channel.FetchFriendsList.newBuilder().setUuid(getAccount().getAccountInformation().getAccountId()).build());
    }


    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {

        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.CMSG_ADD_FRIEND, new RealmProtoListener<Shared.FriendName>() {
            /**
             * @param message
             * @param account
             */
            @Override
            public void handle(Shared.FriendName message, RealmAccount account) {
                //TODO add friend..
                FriendChannelComponent channelFriend = account.component(RealmAccountComponents.FRIENDS_CHANNEL);
                if (channelFriend != null) {

                    if (channelFriend.hasFriend(message.getName())) {
                        //channelFriend.sendDefaultMessage("You are already friends with that player.");
                        return;
                    }
                    if (account.getName().equalsIgnoreCase(message.getName())) {
                        //channelFriend.sendDefaultMessage("You cannot add your self as a friend.");
                        return;
                    }
                    channelFriend.sendChannelMessage(PacketOuterClass.Opcode.C_AddFriend, Channel.AddRemoveFriend.newBuilder().setUuid(account.getAccountInformation().getAccountId()).setName(message.getName()).build());
                }

            }
        }, Shared.FriendName.getDefaultInstance());

        //Updates the entire friends list...
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.C_Friends_List, new ProtoListener<Channel.FriendsList>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.FriendsList message, Session session) {

                //Makes sure this gets back to the rightful player.
                RealmAccount account = GameRealm.findAccount(message.getUuid());

                if (account != null) {
                    Shared.FriendsList.Builder builder = Shared.FriendsList.newBuilder();

                    for (Channel.Friend friend : message.getFriendList()) {
                        account.component(RealmAccountComponents.FRIENDS_CHANNEL).getFriends().put(friend.getUuid(), new FriendInformation(friend.getUuid(), friend.getConnectionUuid(), friend.getName(), friend.getLocation(), friend.getServerName()));

                        builder.addEntry(Shared.Friend.newBuilder().
                                setIndex(friend.getUuid()).
                                setName(friend.getName()).
                                setLocation(friend.getLocation()).
                                setServer(friend.getServerName()).
                                build());
                    }
                    account.sendMessage(PacketOuterClass.Opcode.SMSG_FRIENDS_LIST, builder.build());
                }
            }
        }, Channel.FriendsList.getDefaultInstance());



        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.C_OfflineStatus_Player, new ProtoListener<Channel.PlayerOffline>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.PlayerOffline message, Session session) {

                RealmAccount account = GameRealm.findAccount(message.getTuuid());

                if (account != null) {
                    //getAccount().component(RealmAccountComponents.FRIENDS_CHANNEL).sendDefaultMessage(message.getName() + " has gone offline.");
                    getAccount().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_FRIEND, Shared.Friend.newBuilder().
                            setIndex(message.getUuid()).
                            setName(message.getName()).
                            setLocation("Offline for 3 minutes").
                            setServer("Offline").
                            build());
                }


            }
        }, Channel.PlayerOffline.getDefaultInstance());

        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.C_AddFriend, new ProtoListener<Channel.Friend>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.Friend message, Session session) {

                RealmAccount account = GameRealm.findAccount(message.getTuuid());
                if (account != null) {
                    getAccount().sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_FRIEND, Shared.Friend.newBuilder()
                            .setIndex(message.getUuid())
                            .setName(message.getName())
                            .setLocation(message.getLocation())
                            .setServer(message.getServerName())
                            .build());

                    if (!message.getServerName().toLowerCase().contains("off"))
                       ;//getAccount().component(CharacterComponents.SOCIAL_CHANNEL).sendDefaultMessage(message.getName() + " has come online.");
                }
            }
        }, Channel.Friend.getDefaultInstance());


        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.C_OnlineStatus_Player, new ProtoListener<Channel.PlayerOnlineStatus>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.PlayerOnlineStatus message, Session session) {

                RealmAccount character = GameRealm.findAccount(message.getTuuid());

                if (character != null) {
                    character.sendMessage(PacketOuterClass.Opcode.SMSG_UPDATE_FRIEND, Shared.Friend.newBuilder()
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
        onChannelConnect();
    }

    /**
     * Called Upon on Channel Connect.
     */
    public void onChannelConnect() {
        sendChannelMessage(PacketOuterClass.Opcode.C_Register_Player, Channel.RegisterPlayer.newBuilder().
                setUuid(getAccount().getAccountInformation().getAccountId()).
                setConnectionUuid(BuildRealm.getInstance().getNetwork().getConnectionUuid()).
                setName(getAccount().component(RealmAccountComponents.CHARACTER_MANAGER).getCharacterInformation().getName()).
                setLocation("Unavailable").
                setServerName("Shattered Realm").
                build());
    }

    /**
     * Called once per world cycle per each instance.
     */
    @Override
    public void onTick(long deltaSeconds) {

    }

    /**
     * Called once Actor is Finished
     */
    @Override
    public void onFinish() {
        sendChannelMessage(PacketOuterClass.Opcode.C_Unregister_Player, Channel.UnregisterPlayer.newBuilder().
                setUuid(getAccount().getAccountInformation().getAccountId()).
                build());
    }

    /**
     * Sends a message to the Channel Server
     * @param opcode
     * @param messageV3
     */
    public void sendChannelMessage(PacketOuterClass.Opcode opcode, GeneratedMessageV3 messageV3) {
        if (BuildRealm.getInstance().getNetwork().getChannelSession() != null) {
            BuildRealm.getInstance().getNetwork().getChannelSession().sendMessage(opcode, messageV3);
        }
    }

    /**
     * Gets the GameRealm Account
     * @return
     */
    public RealmAccount getAccount() {
        return (RealmAccount) gameObject;
    }

    /**
     * Database name.
     */
    @Override
    public String getDatabaseName() {
        return null;
    }

    /**
     * Table name.
     */
    @Override
    public String getTableName() {
        return null;
    }
}
