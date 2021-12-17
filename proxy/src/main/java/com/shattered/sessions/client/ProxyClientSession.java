package com.shattered.sessions.client;

import com.shattered.client.ClientConstants;
import com.shattered.account.AccountInformation;
import com.shattered.account.responses.AccountResponses;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.handlers.ClientNetworkHandler;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.listeners.ProtoType;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.ClientSession;
import com.shattered.router.ProxyRouting;
import com.shattered.connections.ServerType;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/16/2019
 */
public class ProxyClientSession extends ClientSession {



    /**
     * Represents the Client Network Bootstrap
     * Used for connections between Realm and Worlds
     */
    @Getter
    @Setter
    private NetworkBootstrap networkBootstrap;

    /**
     * Represents the Client's Realm Session Wrapped by the Proxy
     * Messages to-from the Realm will be handled within this Session
     */
    @Getter
    @Setter
    private ProxyClientRealmSession proxyClientRealmSession;

    /**
     * Represents the Client's World Session Wrapped by the Proxy
     * Messages to-from the World will be handled within this Session
     */
    @Getter
    @Setter
    private ProxyClientWorldSession proxyClientWorldSession;

    /**
     * Represents the Account Information
     */
    @Getter
    @Setter
    private AccountInformation accountInformation;

    /**
     * Represents the Last Ping Sent
     */
    @Getter
    @Setter
    private double lastPingSent;

    /**
     *
     * @param channel
     * @param connUuid
     * @param serverType
     * @param address
     * @param username
     * @param password
     */
    public ProxyClientSession(Channel channel, String connUuid, ServerType serverType, InetSocketAddress address, String username, String password) {
        this(channel, connUuid, serverType, address);
        if (getProxyClientRealmSession() != null) {
            getProxyClientRealmSession().sendMessage(PacketOuterClass.Opcode.CMSG_LOGIN_REQUEST, Shared.LoginRequest.newBuilder().setUserId(username).setPassword(password).buildPartial());
        }
    }

    /**
     * Creates a new Channel Line Session for the World ServerConnections
     *
     * @param channel
     * @param connUuid
     */
    public ProxyClientSession(Channel channel, String connUuid, ServerType serverType, InetSocketAddress address) {
        super(channel, connUuid);
        setNetworkBootstrap(new NetworkBootstrap(new ClientNetworkHandler()));
        //TODO update to a valid realm port
        //This should be a port of current available realms.
        if (address != null) {
            connect(serverType, address.getHostName(), address.getPort());
        } else {
            disconnect(false, "Could not connect to the server.");
        }
    }

    /**
     * Initializes the Ping Event 
     */
    @Override
    public void invoke() {

        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.P_ServerTransferResponse, new ProtoListener<Proxy.ServerTransferResponse>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Proxy.ServerTransferResponse message, Session session) {
                AccountResponses response = AccountResponses.forId(message.getResponse());
                ServerType type = ServerType.forId(message.getServerType());
                if (response == null || type == null) return;
                switch (type) {

                    case WORLD: {
                        if (session instanceof ProxyClientWorldSession) {
                            ProxyClientWorldSession clientSession = (ProxyClientWorldSession) session;
                            if (clientSession.getProxyClientSession().getProxyClientRealmSession() != null) {
                                clientSession.getProxyClientSession().getProxyClientRealmSession().getChannel().disconnect();
                                clientSession.getProxyClientSession().setProxyClientRealmSession(null);
                            }
                        }
                        break;
                    }
                }
            }
        }, Proxy.ServerTransferResponse.getDefaultInstance());

        new Thread(() -> {
            try {

                while (getChannel().isOpen()) {

                    //Enssure's a successful login before pinging
                    if (getAccountInformation() != null) {
                        sendMessage(PacketOuterClass.Opcode.U_Ping, PacketOuterClass.EmptyPayload.newBuilder().build());
                    }

                    Thread.sleep(1000 * 3);

                    if ((System.currentTimeMillis() - getLastPingReceived()) > 1000 * ClientConstants.CLIENT_TIMEOUT) {
                        //disconnect(false, "You have been disconnected from the server.");
                    }

                    //Checks to see if the realm has disconnected
                    if (getProxyClientRealmSession() != null && getProxyClientWorldSession() == null) {
                        if (!getProxyClientRealmSession().getChannel().isOpen()) {
                            setProxyClientRealmSession(null);
                            getChannel().disconnect();
                        }
                    }

                    //Checks to see if world has disconnected
                    if (getProxyClientWorldSession() != null && getProxyClientRealmSession() == null) {
                        if (!getProxyClientWorldSession().getChannel().isOpen()) {
                            setProxyClientWorldSession(null);
                            disconnectToRealm();
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * Attempts to Connect to a Realm?World Session for Initializing
     * @param type
     * @param address
     */
    public boolean connect(ServerType type, InetSocketAddress address) {
        return connect(type, address.getHostName(), address.getPort());
    }

    /**
     * Attempts to Connect to a Realm?World Session for Initializing
     * @param type
     * @param host
     * @param port
     */
    public boolean connect(ServerType type, String host, int port) {

        ChannelFuture future = getNetworkBootstrap().connect(host, port);

        //Ensures not connecting to wrong server
        if (type.equals(ServerType.PROXY)) return false;


        if (!future.isSuccess()) {
            disconnect(false, "Could not connect to the server.");
            return false;
        }


        //If Connnected to the Server
        if (future.isSuccess()) {

            switch (type) {

                case REALM: {
                    if (ProxyRouting.getRealmSession() == null) {
                        //TODO tell client cannot connect to server
                        SystemLogger.sendSystemErrMessage("Proxy Server does not have an active realm session.");
                        return false;
                    } else {
                        //Ensures the Proxy Current Realm Session is Active

                        if (ProxyRouting.getRealmSession().getChannel().isActive()) {


                            if (getProxyClientWorldSession() != null) {
                                getProxyClientWorldSession().disconnect();
                                setProxyClientWorldSession(null);
                            }

                            //Sends OpenConnection for the Lobby to Register the ProxyClientSession
                            NetworkBootstrap.sendPacket(future.channel(), PacketOuterClass.Opcode.P_OpenConnection, Proxy.OpenConnection.newBuilder().setUuid(getConnUuid()).setToken(CLIENT_TOKEN).build());

                            //Sets the Proxy Client Realm Session to the Proxy Realm Session
                            ProxyClientRealmSession clientRealmSession = new ProxyClientRealmSession(future.channel(), ProxyRouting.getRealmSession().getConnUuid(), this);
                            setProxyClientRealmSession(clientRealmSession);
                            future.channel().attr(getNetworkBootstrap().getNetworkHandler().getSessionKey()).set(clientRealmSession);

                            SystemLogger.sendSystemMessage("Successfully linked Uuid=" + getConnUuid() + ", to a Realm.");
                            return true;
                        } else {
                            //TODO tell the client cannot connect to server
                            //TODO attempt to get a new Realm Session
                            SystemLogger.sendSystemErrMessage("Proxy Server session does not have an active channel.");
                            return false;
                        }
                    }
                }

                case WORLD: {
                    ProxyClientWorldSession clientWorldSession = new ProxyClientWorldSession(future.channel(), getConnUuid(), this);
                    future.channel().attr(getNetworkBootstrap().getNetworkHandler().getSessionKey()).set(clientWorldSession);
                    setProxyClientWorldSession(clientWorldSession);
                    SystemLogger.sendSystemMessage("Successfully transferred world! Host=" + host + ", port=" + port);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Transfers the Player to the Realm
     */
    private void disconnectToRealm() {
        disconnectToRealm(getAccountInformation().getAccountName(), getAccountInformation().getPassword());
        sendMessage(PacketOuterClass.Opcode.SMSG_LOGOUT, Shared.RequestLogout.newBuilder().setRealm(true).build());
    }

    /**
     *
     * @param accountName
     * @param password
     */
    public void disconnectToRealm(String accountName, String password) {
        if (connect(ServerType.REALM, ProxyRouting.getRealmAddress())) {

            if (getProxyClientWorldSession() != null) {
                getProxyClientWorldSession().disconnect();
                setProxyClientWorldSession(null);
            }
            if (getProxyClientRealmSession() != null) {
                NetworkBootstrap.sendPacket(getProxyClientRealmSession().getChannel(), PacketOuterClass.Opcode.CMSG_LOGIN_REQUEST, Shared.LoginRequest.newBuilder().setUserId(accountName).setPassword(password).build());
            }
        } else {
            sendMessage(PacketOuterClass.Opcode.SMSG_LOGOUT_WITH_RESPONSE, Shared.LogoutWithResponse.newBuilder().setResponseId(AccountResponses.ERR_SYSTEM_UNAVAILABLE.ordinal()).build());
        }
    }

    /**
     *
     * @param characterName
     * @param connectionUuid
     */
    public void transferToWorld(int permissionLevel, int characterId, String characterName, String mapName, String connectionUuid, int accountId, String accountName, String password) {
        if (getProxyClientWorldSession() != null) {
            NetworkBootstrap.sendPacket(getProxyClientWorldSession().getChannel(), PacketOuterClass.Opcode.P_WorldCharacterInformation, Proxy.WorldCharacterInformation.newBuilder().setCuuid(connectionUuid).setToken(CLIENT_TOKEN).
                    setPermissionLevel(permissionLevel).setCharacterId(characterId).setCharacterName(characterName).setMapName(mapName).setAccountId(accountId).setAccountName(accountName).setPassword(password).build());

        }
    }

    /**
     * Message will result from packets sent from the
     *      client.
     * @param message
     */
    @Override
    public void messageReceived(Object message) {
        if (message instanceof PacketOuterClass.Packet) {
            ProtoType type = ProtoType.forOpcode(((PacketOuterClass.Packet) message).getOpcode());
            PacketOuterClass.Opcode opcode = ((PacketOuterClass.Packet) message).getOpcode();
            if (type != null) {

                /* Represents the Client -> Proxy Ping */
                if (opcode.equals(PacketOuterClass.Opcode.U_Ping)) {
                    setLastPingReceived(System.currentTimeMillis());
                    return;
                }

                switch (type) {

                    /* Routes to the Realm / World */
                    case SHARED: {


                        if (getProxyClientRealmSession() != null &&
                                getProxyClientRealmSession().getProxyClientSession() != null &&
                                getProxyClientRealmSession().getProxyClientSession().getChannel() != null) {

                            if (getProxyClientRealmSession().getProxyClientSession().getChannel().isActive()) {
                                getProxyClientRealmSession().getChannel().writeAndFlush(message);
                            }
                        } else if (getProxyClientWorldSession() != null && getProxyClientWorldSession().getChannel() != null) {
                            if (getProxyClientWorldSession().getProxyClientSession().getChannel().isActive()) {
                                getProxyClientWorldSession().getChannel().writeAndFlush(message);
                            }


                        } else {
                            sendMessage(PacketOuterClass.Opcode.SMSG_LOGIN_RESPONSE, Shared.LoginResponse.newBuilder().setResponseId(AccountResponses.ERR_SYSTEM_UNAVAILABLE.ordinal()).build());
                            getChannel().disconnect();
                        }
                        break;
                    }

                    /* Routes to the Realm */
                    case REALM: {
                        if (getProxyClientRealmSession() != null &&
                                getProxyClientRealmSession().getProxyClientSession() != null &&
                                getProxyClientRealmSession().getProxyClientSession().getChannel() != null) {

                            if (getProxyClientRealmSession().getProxyClientSession().getChannel().isActive()) {
                                getProxyClientRealmSession().getChannel().writeAndFlush(message);
                            }
                        } else {
                            SystemLogger.sendSystemErrMessage("Unable to route message to " + type);
                        }
                        break;
                    }

                    /* Routes to the World */
                    case WORLD: {
                        if (getProxyClientWorldSession() != null && getProxyClientWorldSession().getChannel() != null) {
                            if (getProxyClientWorldSession().getProxyClientSession().getChannel().isActive()) {
                                getProxyClientWorldSession().getChannel().writeAndFlush(message);
                            }
                        }
                        break;
                    }

                }
            }
        }
    }

    /**
     * Disconnects the Client
     */
    @Override
    public void disconnect() {
        //Disconnects from the Realm
        super.disconnect();
        if (getProxyClientRealmSession() != null) {
            if (getProxyClientRealmSession().getChannel().isOpen())
                getProxyClientRealmSession().getChannel().disconnect();
        }

        //Disconnects from the World
        if (getProxyClientWorldSession() != null) {
            if (getProxyClientWorldSession().getChannel().isOpen())
                getProxyClientWorldSession().getChannel().disconnect();
        }
        getChannel().disconnect();
        setProxyClientRealmSession(null);
        setProxyClientWorldSession(null);
    }

    /**
     * Checks if the Client is in the World
     * @return
     */
    public boolean isInWorld() {
        return getProxyClientWorldSession() != null && getProxyClientWorldSession().getChannel().isActive();
    }

    /**
     * Checks if the Client is in the Realm
     * @return
     */
    public boolean isInLobby() {
        return getProxyClientRealmSession() != null && getProxyClientRealmSession().getChannel().isActive();
    }

    /**
     *
     * @param toLobby
     */
    public void disconnect(boolean toLobby, String message) {
    }


}
