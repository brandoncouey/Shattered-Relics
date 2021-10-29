package com.shattered.networking.listeners;


import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;


/**
 * @author JTlrFrost
 * @param <T>
 */
public interface ProtoListener<T extends Message> {

    /**
     *
     * @param message
     * @param session
     */
    default void handle(T message, Session session) {
        
    }

    /**
     * Handles the Raw Message
     * @param message
     * @param session
     */
    default void handleRaw(Message message, Session session) {
        try {
            handle((T) message, session);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param channel
     * @param opcode
     * @param message
     */
    default void sendPacket(Channel channel, PacketOuterClass.Opcode opcode, GeneratedMessageV3 message) {
        if (channel == null || !channel.isActive() || !channel.isOpen()) return;
        channel.writeAndFlush(PacketOuterClass.Packet.newBuilder().setOpcode(opcode).setPayload(message.toByteString()).build())
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
