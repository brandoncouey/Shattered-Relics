package com.shattered.sessions;

import com.shattered.account.ChannelAccount;
import com.shattered.connections.AccountConnections;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.WorldSession;
import io.netty.channel.Channel;

/**
 * @author JTlr Frost 2/1/2020 : 11:39 AM
 */
public class ChannelGameServerSession extends WorldSession {


    /**
     * Creates a new Channel Line Session for the World Server
     *
     * @param channel
     * @param connUuid
     */
    public ChannelGameServerSession(Channel channel, String connUuid) {
        super(channel, connUuid);
    }

    /**
     * Invokes the ProtoEventListeners..
     */
    @Override
    public void invoke() {
        super.invoke();

        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.C_Register_Player, new ProtoListener<com.shattered.networking.proto.Channel.RegisterPlayer>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(com.shattered.networking.proto.Channel.RegisterPlayer message, Session session)  {


                int uuid = message.getUuid();
                String connUuid = message.getConnectionUuid();
                String name = message.getName();
                String location = message.getLocation();
                String serverName = message.getServerName();

                //Registers and sets the player online...
                ChannelAccount account = new ChannelAccount(session.getChannel(), uuid, connUuid, name, location, serverName, -1);
                account.getComponentManager().onFetchData();
                AccountConnections.registerAccount(account);
                account.onRegistered();

            }
        }, com.shattered.networking.proto.Channel.RegisterPlayer.getDefaultInstance());


        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.C_Unregister_Player, new ProtoListener<com.shattered.networking.proto.Channel.UnregisterPlayer>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(com.shattered.networking.proto.Channel.UnregisterPlayer message, Session session)  {

                //TODO let players know that this player is offline...

                //Registers and sets the player online...
                ChannelAccount account = AccountConnections.getAccountForId(message.getUuid());
                if (account != null) {
                    account.onUnregistered();
                }

                AccountConnections.deregisterAccount(message.getUuid());

            }
        }, com.shattered.networking.proto.Channel.UnregisterPlayer.getDefaultInstance());





    }
}
