package com.shattered;

import com.google.protobuf.InvalidProtocolBufferException;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.proto.Universal;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.ChannelSession;
import com.shattered.networking.session.ext.RealmSession;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.service.ServiceConnections;
import com.shattered.connections.ServerType;
import com.shattered.service.ServerService;
import com.shattered.connections.WorldListEntry;
import com.shattered.sessions.ProxySession;
import com.shattered.system.SystemLogger;
import com.shattered.threads.ServerTimeoutListener;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/12/2019
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = false)
public class BuildCentral extends Build {


    /**
     * Represents the Instance of this BuildCentral.
     */
    private static BuildCentral INSTANCE;

    /**
     * Represents the Server Responding Thread
     */
    private final ServerTimeoutListener serverTimeoutListener = new ServerTimeoutListener();


    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        if (args.length < 1) {
            ServerConstants.LIVE = true;

        } else {
            //Sets the Constants to Live.
            ServerConstants.LIVE = Boolean.parseBoolean(args[0]);
        }

        SystemLogger.sendSystemMessage("Network SET " + (ServerConstants.LIVE ? "LIVE" : "LOCAL"));

        getInstance().build(ServerType.CENTRAL, "0.0.0.0", ServerConstants.CENTRAL_DEFAULT_PORT);


        //Starts the Server Responder
        getInstance().getServerTimeoutListener().start();

    }

    /**
     * Gets the BuildCentral Instance
     * @return INSTANCE
     */
    public static BuildCentral getInstance() { if (INSTANCE == null) INSTANCE = new BuildCentral(); return INSTANCE; }

    /**
     *
     * @param channelFuture
     */
    @Override
    public void invoke(ChannelFuture channelFuture) {


        /* ------------------------ Registers Server Connections Listeners ------------------------ */
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.S_Register, new ProtoListener<Sharding.RegisterServer>() {
            @Override
            public void handle(Sharding.RegisterServer message, Session session)  {
                switch (message.getToken()) {

                    /* Proxy Connection */
                    case ProxySession.PROXY_TOKEN: {
                        //Avoids duplicated server connections
                        if (ServiceConnections.forUUID(message.getCuuid()) != null) {
                            session.disconnect();
                            return;
                        }

                        ProxySession proxySess = new ProxySession(session.getChannel());
                        ServiceConnections.registerService(message.getCuuid(), ServerType.PROXY, proxySess);
                        session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(proxySess);
                        SystemLogger.sendSystemMessage("S_Register -> Registered Proxy, ConnId=" + message.getCuuid());
                        break;
                    }

                    /* Realm Connection */
                    case RealmSession.REALM_TOKEN: {

                        //Avoids duplicated server connections
                        if (ServiceConnections.forUUID(message.getCuuid()) != null) {
                            session.disconnect();
                            return;
                        }

                        RealmSession realmSession = new RealmSession(session.getChannel(), message.getCuuid());
                        ServiceConnections.registerService(message.getCuuid(), ServerType.REALM, realmSession);
                        session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(realmSession);
                        SystemLogger.sendSystemMessage("S_Register -> Registered Realm, ConnId=" + message.getCuuid());
                        break;
                    }

                    /* World Connection */
                    case WorldSession.WORLD_TOKEN: {

                        //Avoids duplicated server connections
                        if (ServiceConnections.forUUID(message.getCuuid()) != null) {
                            session.disconnect();
                            return;
                        }

                        WorldSession worldSession = new WorldSession(session.getChannel(), message.getCuuid());
                        ServiceConnections.registerService(message.getCuuid(), ServerType.WORLD, worldSession);
                        session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(worldSession);
                        SystemLogger.sendSystemMessage("S_Register -> Registered World, ConnId=" + message.getCuuid());
                        break;
                    }
                    /* Channel Connection */
                    case ChannelSession.CHANNEL_TOKEN: {
                        //Avoids duplicated server connections
                        if (ServiceConnections.forUUID(message.getCuuid()) != null) {
                            session.disconnect();
                            return;
                        }

                        ChannelSession channelSession = new ChannelSession(session.getChannel(), message.getCuuid());
                        ServiceConnections.registerService(message.getCuuid(), ServerType.CHANNEL, channelSession);
                        session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(channelSession);
                        SystemLogger.sendSystemMessage("S_Register -> Registered Channel, ConnId=" + message.getCuuid());
                        break;
                    }

                    default:
                        SystemLogger.sendSystemErrMessage("Unprocessed S_Register Connection: Cuuid=" + message.getCuuid() + ", Token=" + message.getToken());
                        session.getChannel().disconnect();
                        break;
                }
            }
        }, Sharding.RegisterServer.getDefaultInstance());


        //Requests a Realm
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.P_RequestRealm, new ProtoListener<Proxy.RequestRealm>() {

            /**
             * @param message
             * @param channel
             * @throws InvalidProtocolBufferException
             */
            @Override
            public void handle(Proxy.RequestRealm message, Session session)  {

                //Ensures the cuuid is registered
                if (ServiceConnections.forUUID(message.getCuuid()) == null) {
                    SystemLogger.sendSystemErrMessage("P_RequestRealm -> Unregistered Cuuid=" + message.getCuuid());
                    return;
                }

                ServerService realmServer = ServiceConnections.getServerForType(ServerType.REALM);
                if (realmServer != null) {
                    //Ensures the Address is set.
                    if (realmServer.getAddress() == null) return;

                    NetworkBootstrap.sendPacket(session.getChannel(), PacketOuterClass.Opcode.P_TransferConnection, Proxy.TransferConnection.newBuilder().setCuuid(realmServer.getCuuid()).setToken(RealmSession.REALM_TOKEN).setHost(realmServer.getAddress().getHostName()).setPort(realmServer.getAddress().getPort()).build());
                    SystemLogger.sendSystemMessage("P_RequestRealm -> Successfully sent Proxy ConnId=" + message.getCuuid() + " to Realm=" + realmServer.getCuuid() + ", Address=" + realmServer.getAddress().toString());
                } else {
                    SystemLogger.sendSystemErrMessage("Could not find Proxy=" + message.getCuuid() + " a valid Realm Service.");
                }

            }
        }, Proxy.RequestRealm.getDefaultInstance());


        /*
         * This Registers the S_Unregister ProtoMessage for the unregistering of ServerServices.
         */
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.S_Unregister, new ProtoListener<Sharding.UnregisterServer>() {
            @Override
            public void handle(Sharding.UnregisterServer message, Session session) {
                ServiceConnections.unregister(message.getCuuid());
            }
        }, Sharding.UnregisterServer.getDefaultInstance());


        /*
         * This registers the U_Ping for keeping the servers alive.
         */
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.U_Ping, new ProtoListener<Universal.Ping>() {

            @Override
            public void handle(Universal.Ping message, Session session) {
                ServerService server = ServiceConnections.forUUID(message.getCuuid());
                if (server == null) {
                    session.getChannel().disconnect();
                    SystemLogger.sendSystemErrMessage("U_Ping -> Unauthenticated Connection.");
                } else {
                    server.getSession().setLastPingReceived(System.currentTimeMillis());
                }

            }
        }, Universal.Ping.getDefaultInstance());


        /*
         * This registers the S_ConnectionInfo which is used for the Server Service to register it's current Host/Port.
         */
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.S_ConnectionInfo, new ProtoListener<Sharding.ConnectionInfo>() {
            @Override
            public void handle(Sharding.ConnectionInfo message, Session session)  {
                ServerService server = ServiceConnections.forUUID(message.getCuuid());
                if (server != null) {
                    server.setAddress(new InetSocketAddress(message.getHost(), message.getPort()));
                    SystemLogger.sendSystemMessage("S_ConnectionInfo -> Type=" + server.getServerType().name() + ", Host=" + server.getAddress().toString());

                    //Feeds the Realm with World Information
                    if (server.getServerType() == ServerType.REALM) {
                        Sharding.UpdateWorldList.Builder builder = Sharding.UpdateWorldList.newBuilder();

                        //Loops through the worlds and appends them
                        for (WorldListEntry entry : ServiceConnections.getWorldListEntries().values()) {
                            if (entry == null) continue;
                            builder.addEntry(Sharding.UpdateWorldList.Entry.newBuilder().setConnUuid(entry.getConnectionUuid()).setIndex(entry.getId())
                                    .setHost(entry.getSocket().getHostName()).setPort(entry.getSocket().getPort()).setName(entry.getName())
                                    .setType(entry.getType()));
                        }
                        NetworkBootstrap.sendPacket(session.getChannel(), PacketOuterClass.Opcode.S_UpdateWorldList, builder.build());
                    }

                }
            }
        }, Sharding.ConnectionInfo.getDefaultInstance());



        //Registers the World Information Listener
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.S_WorldInformation, new ProtoListener<Sharding.WorldInformation> () {

            /**
             * @param message
             * @param channel
             * @throws InvalidProtocolBufferException
             */
            @Override
            public void handle(Sharding.WorldInformation message, Session session) {
                ServerService server = ServiceConnections.forUUID(message.getCuuid());
                if (server != null) {
                    ServiceConnections.registerService(new WorldListEntry(message.getCuuid(), message.getIndex(), message.getName(), message.getLocation(), message.getType(), message.getPopulation(), server.getAddress()));
                }
            }
        }, Sharding.WorldInformation.getDefaultInstance());

        //Used by the world for requesting channel server information...
        //Todo add others that's being used...
        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.S_RequestConnectionInfo, new ProtoListener<Sharding.RequestConnectionInfo>() {

            /**
             * @param message
             * @param session
             * @throws InvalidProtocolBufferException
             */
            @Override
            public void handle(Sharding.RequestConnectionInfo message, Session session)  {


                //Token is the type being requested.
                switch (message.getToken()) {

                    case ChannelSession.CHANNEL_TOKEN: {
                        ServerService server = ServiceConnections.getServerForType(ServerType.CHANNEL);
                        if (server != null) {
                            if (server.getAddress() != null) {
                                session.sendMessage(PacketOuterClass.Opcode.S_OpenConnection, Sharding.ConnectionInfo.newBuilder().setCuuid(server.getCuuid())
                                        .setToken(ChannelSession.CHANNEL_TOKEN).setHost(server.getAddress().getHostName()).setPort(server.getAddress().getPort()).build());
                            }
                        }
                        break;
                    }
                }


            }
        }, Sharding.RequestConnectionInfo.getDefaultInstance());
    }
}
