package com.shattered.account.components.friends;

import com.shattered.account.Account;
import com.shattered.account.ChannelAccount;
import com.shattered.account.components.ChannelAccountComponents;
import com.shattered.account.components.ChannelComponent;
import com.shattered.connections.AccountConnections;
import com.shattered.database.mysql.MySQLColumn;
import com.shattered.database.mysql.MySQLCommand;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.Channel;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.session.Session;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JTlr Frost 2/1/2020 : 4:40 PM
 */

public class ChannelFriendComponent extends ChannelComponent {

    /**
     * Represents the Friends list
     */
    @Getter
    private final Map<Integer, ChannelAccount> friends = new ConcurrentHashMap<>();


    /**
     * Creates a new constructor setting the {@link Account}
     *
     * @param gameObject
     */
    public ChannelFriendComponent(Object gameObject) {
        super(gameObject);
    }



    /**
     * Checks if the player is a friend.
     * @param accountId
     * @return
     */
    public boolean containsFriend(int accountId) {
        for (ChannelAccount friend : getFriends().values()) {
            if (friend == null)
                continue;
            if (friend.getUuid() == accountId)
                return true;
        }
        return false;
    }

    /**
     * Checks if a player is a friend by their name.
     * @param name
     * @return
     */
    public boolean containsFriend(String name) {
        for (ChannelAccount friend : getFriends().values()) {
            if (friend == null)
                continue;
            if (friend.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    /**
     * Adds a Friend to the List
     * @param name
     */
    public void addFriend(String name) {
        //TODO insert into SQL
        ChannelAccount friend = AccountConnections.getAccountForName(name);
        if (friend != null) {

            getFriends().put(friend.getUuid(), friend);
            addFriendToSQL(friend.getUuid());

        } else {

            friend = findPlayer(name);

            if (friend != null) {
                getFriends().put(friend.getUuid(), friend);
                addFriendToSQL(friend.getUuid());

            } else {
                //TODO Player is not an actual player!
                System.out.println("Invalid Player!");
                return;
            }
        }


        getAccount().sendMessage(PacketOuterClass.Opcode.C_AddFriend, Channel.Friend.newBuilder().setTuuid(getAccount().getUuid()).setUuid(friend.getUuid()).setConnectionUuid(friend.getConnectionUuid()).setName(friend.getName()).setLocation(friend.getLocation())
                .setServerName(friend.getServerName()).build());
    }

    /**
     * Initializes the content.
     * Used for 'Pre-Loading' data from Storage
     */
    @Override
    public void onStart() {
        sendFriendsUpdatedStatus();


        /**
         * ========================= BEGIN OF PACKETS ==================================
         */

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_OnlineStatus_Player, new ProtoListener<com.shattered.networking.proto.Channel.PlayerOnlineStatus>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(com.shattered.networking.proto.Channel.PlayerOnlineStatus message, Session session)  {

                int uuid = message.getUuid();
                String playerName = message.getName();
                String serverName = message.getServerName();
                String location = message.getLocation();

                ChannelAccount account = AccountConnections.getAccountForId(uuid);
                if (account != null) {
                    account.setName(playerName);
                    account.setServerName(serverName);
                    account.setLocation(location);
                    account.component(ChannelAccountComponents.FRIENDS_LIST).sendFriendsUpdatedStatus();
                }


            }
        }, com.shattered.networking.proto.Channel.PlayerOnlineStatus.getDefaultInstance());

        //TODO lets make this one load from the current character....
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_Friends_List, new ProtoListener<Channel.FetchFriendsList>() {

            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.FetchFriendsList message, Session session) {


                ChannelAccount account = AccountConnections.getAccountForId(message.getUuid());
                if (account != null) {
                    account.component(ChannelAccountComponents.FRIENDS_LIST).sendFriendsList();
                }

            }
        }, Channel.FetchFriendsList.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_AddFriend, new ProtoListener<Channel.AddRemoveFriend>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.AddRemoveFriend message, Session session) {
                ChannelAccount account = AccountConnections.getAccountForId(message.getUuid());
                if (account != null) {
                    account.component(ChannelAccountComponents.FRIENDS_LIST).addFriend(message.getName());
                }
            }
        }, Channel.AddRemoveFriend.getDefaultInstance());


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.C_PrivateMessage, new ProtoListener<Channel.SendPrivateMessage>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Channel.SendPrivateMessage message, Session session) {
                ChannelAccount to = AccountConnections.getAccountForName(message.getTo());
                if (to != null) {
                    to.sendMessage(PacketOuterClass.Opcode.C_PrivateMessage, Channel.ReceivePrivateMessage.newBuilder().
                            setTuuid(to.getUuid()).
                            setFrom(message.getFrom()).
                            setMessage(message.getMessage()).
                            setPermissionLevel(message.getPermissionLevel()).
                            build());
                }
            }
        }, Channel.SendPrivateMessage.getDefaultInstance());
    }

    /**
     * Used for using the data after storage load is finished.
     */
    @Override
    public void onWorldAwake() {

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

        for (ChannelAccount connected : AccountConnections.getChannelAccounts().values()) {
            if (connected == null) continue;
            if (containsFriend(connected.getUuid())) {
                connected.sendMessage(PacketOuterClass.Opcode.C_OfflineStatus_Player, Channel.PlayerOffline.newBuilder()
                        .setTuuid(connected.getUuid())
                        .setUuid(getAccount().getUuid()).setName(getAccount().getName()).build());
            }
        }
    }

    /**
     * Sends Friends Updated Online Status
     */
    public void sendFriendsUpdatedStatus() {
        for (ChannelAccount connected : AccountConnections.getChannelAccounts().values()) {
            if (connected == null)
                continue;
            if (connected == getAccount())
                continue;
            if (connected.component(ChannelAccountComponents.FRIENDS_LIST).containsFriend(getAccount().getUuid())) {
                connected.sendMessage(PacketOuterClass.Opcode.C_OnlineStatus_Player,
                        Channel.PlayerOnlineStatus.newBuilder()
                                .setTuuid(connected.getUuid())
                                .setConnectionUuid(getAccount().getConnectionUuid()).setUuid(getAccount().getUuid())
                                .setName(getAccount().getName()).
                                setLocation(getAccount().getLocation()).
                                setServerName(getAccount().getServerName())
                                .build());
            }
        }
    }

    /**
     * Sends the Entire Friends List
     */
    public void sendFriendsList() {
        Channel.FriendsList.Builder builder = Channel.FriendsList.newBuilder();
        builder.setUuid(getAccount().getUuid());

        for (ChannelAccount friend : getFriends().values()) {
            if (friend == null)
                continue;
            if (friend.getUuid() == getAccount().getUuid())
                continue;
            if (friend.getName().equalsIgnoreCase(getAccount().getName()))
                continue;
            builder.addFriend(Channel.Friend.newBuilder().
                    setUuid(friend.getUuid()).
                    setConnectionUuid(friend.getConnectionUuid()).
                    setName(friend.getName()).
                    setLocation(friend.getLocation()).
                    setServerName(friend.getServerName()).build());
        }
        getAccount().sendMessage(PacketOuterClass.Opcode.C_Friends_List, builder.build());
    }

    /**
     * Gets the Fetch Conditions
     * @return the fetch conditions
     */
    @Override
    public WhereConditionOption[] getFetchConditions() {
        return new WhereConditionOption[] { new WhereConditionOption("accountId", getAccount().getUuid()) };
    }


    /**
     * Database name.
     */
    @Override
    public String getDatabaseName() {
        return "shatteredrelics";
    }

    /**
     * Table name.
     */
    @Override
    public String getTableName() {
        return "friends_list";
    }

    /**
     * Fetches the Friends List.
     * @return
     */
    @Override
    public boolean fetch() {
        ResultSet results = getResults();

        if (!hasResults())
            return false;

        try {


            while (results.next()) {

                ChannelAccount account = loadFriend(results.getInt("friend_id"));
                if (account != null) {
                    if (!getAccount().getName().equalsIgnoreCase(account.getName())) {
                        if (!containsFriend(account.getName())) {
                            friends.put(account.getUuid(), account);
                        }
                    }
                }

            }
            sendFriendsList();
            return true;

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Finds the Player by Username
     * @param name
     * @return
     */
    public ChannelAccount findPlayer(String name) {

        ChannelAccount channelAccount = null;

        if (AccountConnections.isOnline(name) && ((channelAccount = AccountConnections.getAccountForName(name)) != null)) {
            return new ChannelAccount(channelAccount.getChannel(), channelAccount.getUuid(), channelAccount.getConnectionUuid(), channelAccount.getName(), channelAccount.getLocation(), channelAccount.getServerName());
        }

        ResultSet results = getResults(getDatabaseName(), "information", new WhereConditionOption[]{ new WhereConditionOption("name", name) });

        if (!hasResults())
            return null;

        try {


            if (results.next())
                return new ChannelAccount(null, results.getInt("accountId"), "", results.getString("name"), "Unavailable", "Offline");

            return null;

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Loads the Friends Information
     * @param uuid
     * @return
     */
    public ChannelAccount loadFriend(int uuid) {

        ChannelAccount channelAccount = null;

        if (AccountConnections.isOnline(uuid) && ((channelAccount = AccountConnections.getAccountForId(uuid)) != null)) {
            return new ChannelAccount(channelAccount.getChannel(), uuid, channelAccount.getConnectionUuid(), channelAccount.getName(), channelAccount.getLocation(), channelAccount.getServerName());
        }


        ResultSet results = getResults(getDatabaseName(), "information", new WhereConditionOption[]{ new WhereConditionOption("account_id", uuid) });

        if (results == null) {
            return null;
        }

        try {


            if (results.next()) {
                return new ChannelAccount(null, uuid, "", results.getString("name"), "Unavailable", "Offline");
            }

            return null;

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Adds a Friend to SQL
     */
    private void addFriendToSQL(int uuid) {
        try {
            List<MySQLColumn> columns = new ArrayList<>();
            columns.add(new MySQLColumn("accountId", getAccount().getUuid()));
            columns.add(new MySQLColumn("friend_id", uuid));
            entry(getDatabaseName(), "friends_list", columns, MySQLCommand.INSERT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
