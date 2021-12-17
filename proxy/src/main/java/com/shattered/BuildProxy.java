package com.shattered;

import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.ProxySession;
import com.shattered.networking.session.ext.RealmSession;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.networking.session.listeners.ChannelListener;
import com.shattered.router.ProxyRouting;
import com.shattered.networking.session.ext.ClientSession;
import com.shattered.connections.ServerType;
import com.shattered.sessions.client.ProxyClientSession;
import com.shattered.system.SystemLogger;
import com.shattered.threads.ServerListenerThread;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/12/2019
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BuildProxy extends Build implements ChannelListener {


    /**
     * Represents the Instance of this BuildProxy.
     */
    private static BuildProxy INSTANCE;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        int portIndex = 1;

        //Ensures valid port increments
        if (args.length < 2) {
            ServerConstants.LIVE = true;

        } else {

            portIndex = Integer.parseInt(args[0]);
            //Sets the Server to LIVE.
            ServerConstants.LIVE = Boolean.parseBoolean(args[1]);
        }


        SystemLogger.sendSystemMessage("Network SET " + (ServerConstants.LIVE ? "LIVE" : "LOCAL"));

        //Binds the socket and initializes the server
        getInstance().build(ServerType.PROXY, "0.0.0.0", ServerConstants.PROXY_DEFAULT_PORT);

        if (ServerConstants.LIVE)
            ServerConstants.CENTRAL_HOST = ServerConstants.LIVE_CENTRAL_HOST;

        //Connects to Central ServerConnections & Registers this Connection
        getInstance().getNetwork().authenticate(ServerType.PROXY, getInstance().getNetwork().connect(ServerConstants.CENTRAL_HOST, ServerConstants.CENTRAL_DEFAULT_PORT), ProxySession.PROXY_TOKEN);

        //Listens for Disconnects and Reconnection
        ServerListenerThread listener = new ServerListenerThread();
        listener.start();
    }

    /**
     *
     * @param channelFuture
     */
    @Override
    public void invoke(ChannelFuture channelFuture) {

        /* ------------------ P_OpenConnection Listener  ------------------ */
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.P_OpenConnection, new ProtoListener<Proxy.OpenConnection>() {
            @Override
            public void handle(Proxy.OpenConnection message, Session session) {
                switch (message.getToken()) {

                    case ClientSession.CLIENT_TOKEN: {
                        if (ProxyRouting.getClientConnections().get(message.getUuid()) == null) {
                            ProxyRouting.getClientConnections().put(message.getUuid(), session.getChannel());
                            SystemLogger.sendSystemMessage("P_OpenConnection -> Opening a new client connection Cuuid=" + message.getUuid());
                            session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(new ProxyClientSession(session.getChannel(), message.getUuid(), ServerType.REALM, ProxyRouting.getRealmAddress()));
                        }
                        break;
                    }

                    case WorldSession.WORLD_TOKEN: {
                        if (ProxyRouting.getWorldConnections().get(message.getUuid()) != null) {
                            WorldSession worldSession = new WorldSession(session.getChannel(), message.getUuid());
                            session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(worldSession);
                            ProxyRouting.getWorldConnections().put(message.getUuid(), worldSession);
                        }
                        break;
                    }

                    case RealmSession.REALM_TOKEN: {
                        if (ProxyRouting.getRealmSession() == null) {
                            RealmSession realmSession = new RealmSession(session.getChannel(), message.getUuid());
                            session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(realmSession);
                            ProxyRouting.setRealmSession(realmSession);
                            SystemLogger.sendSystemMessage("P_OpenConnection -> Opening a new realm connection Cuuid=" + message.getUuid());
                        }
                        break;
                    }

                    default: {
                        //Ensures no authenticated connections connect.
                        SystemLogger.sendSystemErrMessage("P_OpenConnection -> Unhandled connection Cuuid=" + message.getUuid() + ", Token=" + message.getToken());
                        session.getChannel().disconnect();
                        break;
                    }
                }
            }
        }, Proxy.OpenConnection.getDefaultInstance());


        /* ------------------ P_TransferConnection Listener ------------------ */

        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.P_TransferConnection, new ProtoListener<Proxy.TransferConnection>() {
            @Override
            public void handle(Proxy.TransferConnection message, Session session) {

                switch (message.getToken()) {
                    case RealmSession.REALM_TOKEN: {

                        //Ensures the host is not a hoax and is valid.
                        if (message.getHost().isEmpty() || message.getHost() == null) {
                            NetworkBootstrap.sendPacket(session.getChannel(), PacketOuterClass.Opcode.P_RequestNewTransferConnection,
                                    Proxy.RequestNewTransferConnection.newBuilder().setTccuid(message.getCuuid()).setCuuid(BuildProxy.getInstance().getNetwork().getConnectionUuid()).setToken(ProxySession.PROXY_TOKEN).build());
                            //TODO request new connection
                            //TODO We MUST! Have a connection to a realm
                            SystemLogger.sendSystemErrMessage("BuildProxy -> P_TransferConnection: Unavailable Host=" + message.getHost());
                            return;
                        }

                        //Ensures one of the ports could potentially be one of ours.
                        if (message.getPort() < 17000 || message.getPort() > 19000) {
                            //TODO request new connection
                            //TODO We MUST! Have a connection to a realm
                            // SystemLogger.sendSystemErrMessage("BuildProxy -> P_TransferConnection: Unavailable Port=" + message.getPort());
                            // return;
                        }

                        //Disconnects from any current existing realm server
                        if (ProxyRouting.getRealmSession() != null) {
                            if (ProxyRouting.getRealmSession().getChannel().isActive())
                                ProxyRouting.getRealmSession().disconnect();
                            ProxyRouting.setRealmSession(null);
                        }

                        //Initializes a new connection to the new realm server
                        ChannelFuture future = BuildProxy.getInstance().getNetwork().connect(message.getHost(), message.getPort());
                        SystemLogger.sendSystemMessage("Transferring Realm Services... New Host=" + message.getHost() + ", Port=" + message.getPort());
                        if (future.isSuccess()) {

                            NetworkBootstrap.sendPacket(future.channel(), PacketOuterClass.Opcode.P_OpenConnection, Proxy.OpenConnection.newBuilder().setUuid(BuildProxy.getInstance().getNetwork().getConnectionUuid()).setToken(ProxySession.PROXY_TOKEN).build());
                            ProxyRouting.setRealmAddress(new InetSocketAddress(message.getHost(), message.getPort()));
                            //TODO send realm open_connection
                            //TODO register Openconnection for realm to register ctx to a session and add to proxyrouting
                            SystemLogger.sendSystemMessage("Successfully transferred to new Realm-> Host=" + message.getHost() + ", Port=" + message.getPort());

                        } else {
                            //TODO request new connection
                            //TODO We MUST! Have a connection to a realm
                            SystemLogger.sendSystemErrMessage("BuildProxy -> P_TransferConnection: Could not establish a connection with Host=" + message.getHost() + "/" + message.getPort());
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }, Proxy.TransferConnection.getDefaultInstance());
    }

    /**
     * Gets the BuildProxy Instance
     * @return INSTANCE
     */
    public static BuildProxy getInstance() { if (INSTANCE == null) INSTANCE = new BuildProxy(); return INSTANCE; }

}
