package com.shattered.sessions.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.shattered.account.AccountInformation;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.RealmSession;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.connections.ServerType;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/16/2019
 */
public class ProxyClientRealmSession extends RealmSession {


    /**
     * Represents the Proxy Client Session
     * Messages received from the Realm will be redirected to the Client
     */
    @Getter
    @Setter
    private ProxyClientSession proxyClientSession;

    /**
     * Creates a new Channel Line Session for the World Server
     *
     * @param channel
     * @param connUuid
     */
    public ProxyClientRealmSession(Channel channel, String connUuid, ProxyClientSession proxyClientSession) {
        super(channel, connUuid);
        setProxyClientSession(proxyClientSession);
    }
    
    @Override
    public void invoke() {

        //Registers the Transmit Account Information
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.P_TransmitAccount, new ProtoListener<Proxy.TransmitAccount>() {

            /**
             * @param message
             * @param session
             * @throws InvalidProtocolBufferException
             */
            @Override
            public void handle(Proxy.TransmitAccount message, Session session) {
                ProxyClientRealmSession realmSession = (ProxyClientRealmSession) session;
                realmSession.getProxyClientSession().setAccountInformation(new AccountInformation(message.getAccountName(), getConnUuid(), message.getEmail(), message.getPassword(), AccountInformation.AccountLevel.forId(message.getAccountLevel())));
            }
        }, Proxy.TransmitAccount.getDefaultInstance());
        
        //Registers the Transfer to world opcode
        ProtoEventListener.registerListener(PacketOuterClass.Opcode.P_TransferToWorld, new ProtoListener<Proxy.TransferToWorld>() {

            /**
             * @param message
             * @param session
             * @throws InvalidProtocolBufferException
             */
            @Override
            public void handle(Proxy.TransferToWorld message, Session session) {

                switch (message.getToken()) {
                    case WorldSession.WORLD_TOKEN: {
                        int accountId = message.getAccountId();
                        String accountName = message.getAccountName();
                        int characterId = message.getCharacterId();
                        String characterName = message.getCharacterName();
                        String mapName = message.getMapName();
                        String password = message.getPassword();
                        String connectionUuid = message.getCuuid();
                        ProxyClientRealmSession realmSession = (ProxyClientRealmSession) session;
                        realmSession.getProxyClientSession().connect(ServerType.WORLD, message.getHost(), message.getPort());
                        realmSession.getProxyClientSession().transferToWorld(message.getPermissionLevel(), characterId, characterName, mapName, connectionUuid, accountId, accountName, password);
                        break;
                    }
                }
                
                
            }
        }, Proxy.TransferToWorld.getDefaultInstance());
    }

    /**
     * 618-734-2162
     * @param message
     */
    @Override
    public void messageReceived(Object message) {
        if (message instanceof PacketOuterClass.Packet) {
            if (((PacketOuterClass.Packet) message).getOpcode().name().startsWith("P_")) {
                super.messageReceived(message);
                return;
            }
            if (((PacketOuterClass.Packet) message).getOpcode().name().startsWith("SMSG")) {
                if (getProxyClientSession().getChannel().isActive()) {
                    getProxyClientSession().getChannel().writeAndFlush(message);
                    SystemLogger.sendSystemMessage("ProxyClientRealmSession -> Writing Proxy Client Message. Opcode=" + ((PacketOuterClass.Packet) message).getOpcode().name());
                }
            } else {
                SystemLogger.sendSystemErrMessage("ProxyClientRealmSession -> Unhandled Opcode Message=" + ((PacketOuterClass.Packet) message).getOpcode().name());
            }
            
        }
    }

    /**
     * Disconnects from the Realm
     */
    @Override
    public void disconnect() {
        super.disconnect();
        getChannel().disconnect();
        getProxyClientSession().setProxyClientRealmSession(null);
    }
}
