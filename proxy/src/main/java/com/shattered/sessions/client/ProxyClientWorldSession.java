package com.shattered.sessions.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.WorldSession;
import com.shattered.router.ProxyRouting;
import com.shattered.connections.ServerType;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 8/31/2019 : 2:58 AM
 */
public class ProxyClientWorldSession extends WorldSession {


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
    public ProxyClientWorldSession(Channel channel, String connUuid, ProxyClientSession proxyClientSession) {
        super(channel, connUuid);
        setProxyClientSession(proxyClientSession);
    }

    @Override
    public void invoke() {
        super.invoke();

        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.P_TransferWorldToRealm, new ProtoListener<Proxy.RequestWorldToRealmTransfer>() {
            /**
             * @param message
             * @param session
             */
            @Override
            public void handle(Proxy.RequestWorldToRealmTransfer message, Session session) {
                String accountName = message.getAccountName();
                String password = message.getAccountPassword();
                ProxyClientWorldSession worldSession = (ProxyClientWorldSession) session;
                worldSession.getProxyClientSession().disconnectToRealm(accountName, password);
            }

        }, Proxy.RequestWorldToRealmTransfer.getDefaultInstance());


        ProtoEventRepository.registerListener(PacketOuterClass.Opcode.P_ServerAvailability, new ProtoListener<Proxy.ServerAvailability>() {
            /**
             * @param message
             * @param session
             * @throws InvalidProtocolBufferException
             */
            @Override
            public void handle(Proxy.ServerAvailability message, Session session) {
                ServerType type = ServerType.forId(message.getType());
                if (type == null) return;
                switch (type) {
                    case REALM:
                        boolean available = ProxyRouting.getRealmSession() != null && ProxyRouting.getRealmSession().getChannel().isActive();
                        session.sendMessage(PacketOuterClass.Opcode.P_ServerAvailability, Proxy.ServerAvailabilityResponse.newBuilder().setAvailable(available).build());
                        break;
                    default:
                        System.err.println("Unhandled server availability check for the type="+ type + ".");
                        break;
                }

            }

        }, Proxy.ServerAvailability.getDefaultInstance());


    }

    @Override
    public void messageReceived(Object message) {
        if (message instanceof PacketOuterClass.Packet) {
            if (((PacketOuterClass.Packet) message).getOpcode().name().startsWith("P_")) {
                super.messageReceived(message);
                return;
            }
            String opcodeName = ((PacketOuterClass.Packet) message).getOpcode().name();
            if (opcodeName.startsWith("SMSG") || opcodeName.startsWith("U_")) {
                if (getProxyClientSession().getChannel().isActive()) {
                    getProxyClientSession().getChannel().writeAndFlush(message);
                    if (!((PacketOuterClass.Packet) message).getOpcode().name().contains("SYNCHRONIZE"))
                        SystemLogger.sendSystemMessage("ProxyClientRealmSession -> Writing Proxy Client Message. Opcode=" + ((PacketOuterClass.Packet) message).getOpcode().name());
                }
            } else {
                SystemLogger.sendSystemErrMessage("ProxyClientRealmSession -> Unhandled Opcode Message=" + ((PacketOuterClass.Packet) message).getOpcode().name());
            }

        }
    }

    /**
     * Disconnects from the World
     */
    @Override
    public void disconnect() {
        super.disconnect();
        getChannel().disconnect();
        getProxyClientSession().setProxyClientWorldSession(null);
    }
}
