package com.shattered.networking.session;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.shattered.networking.listeners.ProtoEventRepository;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/15/18 : 1:36 AM
 */
public abstract class Session {

    /**
     * Represents the Channel of the Client
     */
    @Getter
    protected Channel channel;

    /**
     * Represents Last time Pinged
     */
    @Getter
    @Setter
    protected long lastPingReceived;


    /**
     *
     * @param channel
     */
    public Session(Channel channel) {
        this.channel = channel;
        setLastPingReceived(System.currentTimeMillis());
        invoke();
    }


    /**
     * Receives the Incoming Client Messages
     * @param object
     */
    public void messageReceived(Object object) {
        if (!(object instanceof PacketOuterClass.Packet)) return;

        PacketOuterClass.Opcode opcode = ((PacketOuterClass.Packet) object).getOpcode();

        try {

            if (ProtoEventRepository.forOpcode(opcode) == null) {
                SystemLogger.sendSystemErrMessage("Unhandled incoming packet, Opcode=" + opcode.name() + ".");
                return;
            }

            Message message = ProtoEventRepository.decode((PacketOuterClass.Packet) object);
            if (message == null)  {
                SystemLogger.sendSystemErrMessage("Dropping packet! Message is error, unknown packet.");
                return;
            }

            handle(opcode, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the incoming packet
     * @param opcode
     * @param message
     */
    private void handle(PacketOuterClass.Opcode opcode, Message message) {
        ProtoListener<?> handler = (ProtoListener<?>) ProtoEventRepository.forOpcode(opcode);

        if (handler == null || ProtoEventRepository.forOpcode(opcode) == null) {
            SystemLogger.sendSystemErrMessage("We have an unidentified packet being dropped! Null Handler and Opcode!");
            return;
        }
        handler.handleRaw(message, this);
    }

    /**
     * Called Upon Constructor for Invoking Proto Registers
     */
    public void invoke() {

    }

    /**
     * The Default Method for Disconnecting the Client's Session
     */
    public void disconnect() {
        if (getChannel() != null) {
            if (getChannel().isActive())
                disconnect();
        }
    }

    /**
     *
     * @param opcode
     */
    public void sendMessage(PacketOuterClass.Opcode opcode) {
       sendMessage(opcode, PacketOuterClass.EmptyPayload.newBuilder().build());
    }

    /**
     *
     * @param opcode
     * @param message
     */
    public void sendMessage(PacketOuterClass.Opcode opcode, GeneratedMessageV3 message) {
        if (channel == null || !channel.isActive() || !channel.isOpen()) return;
        channel.writeAndFlush(PacketOuterClass.Packet.newBuilder().setOpcode(opcode).setPayload(message.toByteString()).build())
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }


}
