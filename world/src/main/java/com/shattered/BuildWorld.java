package com.shattered;

import com.shattered.account.AccountInformation;
import com.shattered.account.responses.AccountResponses;
import com.shattered.client.ClientRegistering;
import com.shattered.connections.ServerType;
import com.shattered.datatable.UDataTableRepository;
import com.shattered.engine.Engine;
import com.shattered.game.GameWorld;
import com.shattered.game.actor.components.variable.PlayerVariableRepository;
import com.shattered.game.engine.WorldEngine;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.proto.Shared;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.WorldClientProxySession;
import com.shattered.networking.session.ext.ChannelSession;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.system.SystemCommandRepository;
import com.shattered.system.SystemLogger;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BuildWorld extends Build {


    /**
     * Represents the Instance of this BuildProxy.
     */
    private static BuildWorld INSTANCE;

    /**
     * Represents the Realm Engine
     */
    private Engine engine;

    /**
     * Represents the Client Registry
     */
    private ClientRegistering clientRegistry;

    @Getter
    @Setter
    private int index;


    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        //TODO assign ip-port to bind via args

        //Ensures valid port increments - fucking docker...
        if (args.length < 5) {
            SystemLogger.sendSystemErrMessage("Unsupported Arguments! Current Args: " + args.length + ", Expected: (id, name, location, type, live)");
            return;
        }

        //Represents the Port Index Increment
        int portIndex = Integer.parseInt(args[0]);

        //Represents the World Name
        String name = String.valueOf(args[1]);

        //Represents the Location of the World
        String location = String.valueOf(args[2]);

        //Represents the Type of the World
        String type = String.valueOf(args[3]);

        //Sets the Constants to Live.
        ServerConstants.LIVE = Boolean.parseBoolean(args[4]);

        SystemLogger.sendSystemMessage("Network SET " + (ServerConstants.LIVE ? "LIVE" : "LOCAL"));

        if (ServerConstants.LIVE) {
            ServerConstants.CENTRAL_HOST = ServerConstants.LIVE_CENTRAL_HOST;
            ServerConstants.LIVE_DB = true;
        }

        try {

            //Binds the socket and initializes the server
            getInstance().build(ServerType.WORLD, "0.0.0.0", ServerConstants.WORLD_DEFAULT_PORT + portIndex);

            //Initializes the game engine
            getInstance().setEngine(new WorldEngine());
            getInstance().getEngine().run();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            //Parses the UDataTables
            UDataTableRepository.parse();

            //Parses all Character Variables
            PlayerVariableRepository.parse();

            //Initializes the Game World
            GameWorld.initialize(name, location, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Increments from default port +1
        getInstance().getNetwork().setPortIndex(portIndex);

        //Sets Client Registry
        getInstance().setClientRegistry(new ClientRegistering());

        //Connects to Central ServerConnections & Registers this Connection
        getInstance().getNetwork().authenticate(ServerType.WORLD, getInstance().getNetwork().connect(ServerConstants.CENTRAL_HOST, ServerConstants.CENTRAL_DEFAULT_PORT), WorldSession.WORLD_TOKEN);
        NetworkBootstrap.sendPacket(getInstance().getNetwork().getCentralSession().getChannel(), PacketOuterClass.Opcode.S_WorldInformation, Sharding.WorldInformation.newBuilder().setCuuid(getInstance().getNetwork().getConnectionUuid()).setName(name).setIndex(portIndex).setLocation(GameWorld.WORLD_LOCATION).setType(GameWorld.WORLD_TYPE).setPopulation(GameWorld.getPopulation()).build());
        SystemLogger.sendSystemMessage("S_WorldInformation -> Registering World Information {cuuid=" + getInstance().getNetwork().getConnectionUuid() + ", name=" + name + "}");
        SystemCommandRepository.startup();
    }

    /**
     *
     * @param channelFuture
     */
    public void invoke(ChannelFuture channelFuture) {



        ProtoEventListener.registerListener(PacketOuterClass.Opcode.S_OpenConnection, new ProtoListener<Sharding.ConnectionInfo>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Sharding.ConnectionInfo message, Session session)  {

                ChannelFuture future = getNetwork().connect(message.getHost(), message.getPort());
                if (future.isSuccess()) {
                    ChannelSession channelSession = new ChannelSession(future.channel(), message.getCuuid());
                    channelSession.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(channelSession);
                    getNetwork().setChannelSession(channelSession);
                    getNetwork().getChannelSession().sendMessage(PacketOuterClass.Opcode.S_Register, Sharding.RegisterServer.newBuilder().setCuuid(getNetwork().getConnectionUuid()).setToken(WorldSession.WORLD_TOKEN).build());
                    GameWorld.onChannelConnect();
                    SystemLogger.sendSystemMessage("S_OpenConnection -> Successfully connected to the channel server!");
                }

            }
        }, Sharding.ConnectionInfo.getDefaultInstance());

        ProtoEventListener.registerListener(PacketOuterClass.Opcode.P_WorldCharacterInformation, new ProtoListener<Proxy.WorldCharacterInformation>() {

            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Proxy.WorldCharacterInformation message, Session session) {
                WorldClientProxySession proxySession = new WorldClientProxySession(session.getChannel(), message.getCuuid(), message.getAccountId(), message.getPermissionLevel(), message.getAccountName(), message.getCharacterId(), message.getCharacterName(), message.getPassword());
                proxySession.getChannel().attr(getNetwork().getNetworkHandler().getSessionKey()).set(proxySession);
                BuildWorld.getInstance().getClientRegistry().registerClient(proxySession);
                SystemLogger.sendSystemMessage("P_WorldInformation -> Successfully Registered Client. Uuid=" + message.getCuuid() + ", Token=" + message.getToken());

                //TODO this is tecnically 'world login' method. Must add preventable checks from logging in here.

                if (GameWorld.getCharacters().size() >= GameWorld.MAXIMUM_PLAYERS) {
                    proxySession.sendMessage(PacketOuterClass.Opcode.SMSG_LOGIN_RESPONSE, Shared.LoginResponse.newBuilder().setResponseId(AccountResponses.SERVER_FULL.ordinal()).build());
                    return;
                }

                if (GameWorld.containsPlayer(message.getCharacterName())) {
                    proxySession.sendMessage(PacketOuterClass.Opcode.SMSG_LOGIN_RESPONSE, Shared.LoginResponse.newBuilder().setResponseId(AccountResponses.ACCOUNT_ACTIVE_SESSION.ordinal()).build());
                    //TODO kick from realm // should NOT be a thing, but double checking.
                    return;
                }

                //Send proxy disconnect + response here.
                proxySession.sendMessage(PacketOuterClass.Opcode.P_ServerTransferResponse, Proxy.ServerTransferResponse.newBuilder().setServerType(ServerType.WORLD.ordinal()).setResponse(AccountResponses.SUCCESSFUL.ordinal()).build());
                proxySession.sendMessage(PacketOuterClass.Opcode.SMSG_LOGIN_RESPONSE, Shared.LoginResponse.newBuilder().setResponseId(AccountResponses.SUCCESSFUL.ordinal()).build());
                proxySession.sendMessage(PacketOuterClass.Opcode.SMSG_WORLD_LOGIN_TRANSFER);
                proxySession.getAccount().onAwake();
                proxySession.getAccount().onStart();

            }
        }, Proxy.WorldCharacterInformation.getDefaultInstance());


    }

    /**
     * Caster for the World Engine Kotlin Class
     * @return the World Engine
     */
    public WorldEngine getEngine() {
        return (WorldEngine) engine;
    }


    /**
     * Gets the BuildProxy Instance
     * @return INSTANCE
     */
    public static BuildWorld getInstance() { if (INSTANCE == null) INSTANCE = new BuildWorld(); return INSTANCE; }
}
