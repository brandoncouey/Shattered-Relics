package com.shattered;

import com.shattered.client.ClientRegistering;
import com.shattered.engine.Engine;
import com.shattered.game.engine.RealmEngine;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.*;
import com.shattered.realm.GameRealm;
import com.shattered.connections.ServerType;
import com.shattered.connections.WorldListEntry;
import com.shattered.networking.session.RealmClientProxySession;
import com.shattered.system.SystemLogger;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetSocketAddress;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class BuildRealm extends Build {


    /**
     * Represents the Instance of this BuildProxy.
     */
    private static BuildRealm INSTANCE;

    /**
     * Represents the GameRealm Engine
     */
    private Engine engine;

    /**
     * Represents the Client Registry
     */
    private ClientRegistering clientRegistry;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        int portIndex = 1;

        //Ensures valid port increments
        if (args.length < 2) {

            ServerConstants.LIVE = true;
            ServerConstants.LIVE_DB = true;

        } else {

            //Represents the Port Index Increment
            portIndex = Integer.parseInt(args[0]);

            //Sets the Server to LIVE
            ServerConstants.LIVE = Boolean.parseBoolean(args[1]);
        }


        SystemLogger.sendSystemMessage("Building Relic-World on " + (ServerConstants.LIVE ? "LIVE" : "LOCAL") + " network.");

        if (ServerConstants.LIVE) {
            ServerConstants.CENTRAL_HOST = ServerConstants.LIVE_CENTRAL_HOST;
        }

        //Binds the socket and initializes the server
        getInstance().build(ServerType.REALM, "0.0.0.0", ServerConstants.REALM_DEFAULT_PORT + portIndex);

        //Increments from default port +1
        getInstance().getNetwork().setPortIndex(portIndex);

        //Initializes the GameRealm Engine
        getInstance().setEngine(new RealmEngine());
        getInstance().getEngine().run();

        //Inits the Client Registry
        getInstance().setClientRegistry(new ClientRegistering());


        //Connects to Central ServerConnections & Registers this Connection

        getInstance().getNetwork().authenticate(ServerType.REALM, getInstance().getNetwork().connect(ServerConstants.CENTRAL_HOST, ServerConstants.CENTRAL_DEFAULT_PORT), RealmSession.REALM_TOKEN);


    }

    /**
     *
     * @param channelFuture
     */
    public void invoke(ChannelFuture channelFuture) {


        //Registers Open Connection Opcode
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.P_OpenConnection, new ProtoListener<Proxy.OpenConnection>() {

            @Override
            public void handle(Proxy.OpenConnection message, Session session) {

                switch (message.getToken()) {

                    //Represents the P_OpenConnection
                    case ProxySession.PROXY_TOKEN: {
                        NetworkBootstrap.sendPacket(session.getChannel(), PacketOuterClass.Opcode.P_OpenConnection, Proxy.OpenConnection.newBuilder().setUuid(getNetwork().getConnectionUuid()).setToken(RealmSession.REALM_TOKEN).build());
                        break;
                    }

                    //Represents the P_OpenConnection->Client
                    case ClientSession.CLIENT_TOKEN: {

                        //Ensures not a duplicate
                        //TODO ensure object list does not have the cuuid as well
                        if (BuildRealm.getInstance().getClientRegistry().getClient(message.getUuid()) != null) return;

                        RealmClientProxySession proxySession = new RealmClientProxySession(session.getChannel(), message.getUuid());
                        session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(proxySession);
                        BuildRealm.getInstance().getClientRegistry().registerClient(proxySession);
                        SystemLogger.sendSystemMessage("P_OpenConnection -> Successfully Registered Client. Uuid=" + message.getUuid() + ", Token=" + message.getToken());
                        break;
                    }

                    //Handles all other connections
                    default: {
                        SystemLogger.sendSystemErrMessage("P_OpenConnection -> Unable to Register Connection. Uuid=" + message.getUuid() + ", Token=" + message.getToken());
                        session.getChannel().disconnect();
                        break;
                    }
                }

            }
        }, Proxy.OpenConnection.getDefaultInstance());


        /*
         * Registers the S_OpenConnection. Which is used for connecting to an internal server for additional services.
         */
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.S_OpenConnection, new ProtoListener<Sharding.ConnectionInfo>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Sharding.ConnectionInfo message, Session session) {


                ChannelFuture future = getNetwork().connect(message.getHost(), message.getPort());
                if (future.isSuccess()) {
                    ChannelSession channelSession = new ChannelSession(future.channel(), message.getCuuid());
                    channelSession.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(channelSession);
                    getNetwork().setChannelSession(channelSession);
                    getNetwork().getChannelSession().sendMessage(PacketOuterClass.Opcode.S_Register, Sharding.RegisterServer.newBuilder().setCuuid(getNetwork().getConnectionUuid()).setToken(RealmSession.REALM_TOKEN).build());
                    GameRealm.onChannelConnect();
                    SystemLogger.sendSystemMessage("S_OpenConnection -> Successfully connected to the channel server!");
                }

            }
        }, Sharding.ConnectionInfo.getDefaultInstance());


        /*
         * Registers the S_UpdateWorldList which is used for the player fetching the world list dynamically.
         * This currently updates the ENTIRE list.
         */
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.S_UpdateWorldList, new ProtoListener<Sharding.UpdateWorldList>() {

            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Sharding.UpdateWorldList message, Session session) {
                int entries = message.getEntryCount();
                for (Sharding.UpdateWorldList.Entry entry : message.getEntryList()) {
                    if (entry == null) continue;
                    GameRealm.registerWorld(new WorldListEntry(entry.getConnUuid(), entry.getIndex(), entry.getName(), entry.getLocation(), entry.getType(), entry.getPopulation(), new InetSocketAddress(entry.getHost(), entry.getPort())));
                }
            }
        }, Sharding.UpdateWorldList.getDefaultInstance());

        /*
         * Updates a S_UpdateWorldEntry with the specified World UUID with the updated information.
         * This currently updates a SINGLE Entry.
         */
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.S_UpdateWorldEntry, new ProtoListener<Sharding.UpdateWorldEntry>() {

            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Sharding.UpdateWorldEntry message, Session session) {
                WorldListEntry entry = GameRealm.forUuid(message.getEntry().getConnUuid());
                if (message.getEntry().getIndex() != -1) {
                    WorldListEntry updatedEntry = new WorldListEntry(message.getEntry().getConnUuid(), message.getEntry().getIndex(), message.getEntry().getName(), message.getEntry().getLocation(), message.getEntry().getType(), message.getEntry().getPopulation(), new InetSocketAddress(message.getEntry().getHost(), message.getEntry().getPort()));
                    if (entry == null)
                        GameRealm.registerWorld(updatedEntry);
                    else
                        entry = updatedEntry;
                } else {
                    if (entry != null) {
                        GameRealm.unregisterWorld(entry);
                    }
                }
            }
        }, Sharding.UpdateWorldEntry.getDefaultInstance());

    }


    /**
     * Gets the BuildProxy Instance
     * @return INSTANCE
     */
    public static BuildRealm getInstance() { if (INSTANCE == null) INSTANCE = new BuildRealm(); return INSTANCE; }
}
