package com.shattered;

import com.google.protobuf.InvalidProtocolBufferException;
import com.shattered.engine.Engine;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.ChannelSession;
import com.shattered.networking.session.ext.RealmSession;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.connections.ServerConnections;
import com.shattered.connections.ServerType;
import com.shattered.sessions.ChannelGameServerSession;
import com.shattered.system.SystemLogger;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class BuildChannel extends Build {


    /**
     * Represents the Instance of this BuildProxy.
     */
    private static BuildChannel INSTANCE;

    /**
     * Represents the Realm Engine
     */
    private Engine engine;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        int portIndex = 1;

        if (args.length < 2) {
            ServerConstants.LIVE = true;
            ServerConstants.LIVE_DB = true;
        } else {

            //Represents the Port Index Increment
            portIndex = Integer.parseInt(args[0]);

            //Sets the Server to LIVE?
            ServerConstants.LIVE = Boolean.parseBoolean(args[1]);
        }

        SystemLogger.sendSystemMessage("Network SET " + (ServerConstants.LIVE ? "LIVE" : "LOCAL"));

        if (ServerConstants.LIVE)
            ServerConstants.CENTRAL_HOST = ServerConstants.LIVE_CENTRAL_HOST;

        //Binds the socket and initializes the server
        getInstance().build(ServerType.CHANNEL, "0.0.0.0", ServerConstants.CHANNEL_DEFAULT_PORT + portIndex);

        //Increments from default port +1
        getInstance().getNetwork().setPortIndex(portIndex);

        //Connects to Central ServerConnections & Registers this Connection
        getInstance().getNetwork().authenticate(ServerType.CHANNEL, getInstance().getNetwork().connect(ServerConstants.CENTRAL_HOST, ServerConstants.CENTRAL_DEFAULT_PORT), ChannelSession.CHANNEL_TOKEN);

    }

    /**
     *
     * @param channelFuture
     */
    public void invoke(ChannelFuture channelFuture) {


        ProtoEventListener.registerListener(PacketOuterClass.Opcode.S_Register, new ProtoListener<Sharding.RegisterServer>() {

            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Sharding.RegisterServer message, Session session) {

                //Represents the Incoming Server Type
                switch (message.getToken()) {


                    case RealmSession.REALM_TOKEN:
                    case WorldSession.WORLD_TOKEN: {
                        ChannelGameServerSession channelGameServerSession = new ChannelGameServerSession(session.getChannel(), message.getCuuid());
                        session.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(channelGameServerSession);
                        ServerConnections.addServer(channelGameServerSession);
                        SystemLogger.sendSystemMessage("S_Register -> Successfully registered ServerId=" + message.getCuuid() + ".");
                        break;
                    }
                }

            }
        }, Sharding.RegisterServer.getDefaultInstance());

    }


    /**
     * Gets the BuildProxy Instance
     * @return INSTANCE
     */
    public static BuildChannel getInstance() { if (INSTANCE == null) INSTANCE = new BuildChannel(); return INSTANCE; }
}
