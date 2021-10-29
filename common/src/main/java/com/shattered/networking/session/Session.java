package com.shattered.networking.session;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.shattered.networking.listeners.ProtoEventListener;
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

        //Attempts to pull the Opcode from the message
        PacketOuterClass.Opcode opcode = ((PacketOuterClass.Packet) object).getOpcode();
        if (opcode == null) return;


        try {
            //Ensures a Valid Opcode
            if (ProtoEventListener.getBuilders().get(opcode) == null) return;

            //Ensures the Opcode is Registered
            ProtoListener listener = ProtoEventListener.forOpcode(opcode);
            if (listener == null) {
                SystemLogger.sendSystemErrMessage("Incoming Unhandled Opcode: " + opcode.name());
                return;
            }

            //Attempts to Decode the Message
            Message message = ProtoEventListener.decode((PacketOuterClass.Packet) object);
            if (message == null) return;

            //Handles the Message
            listener.handleRaw(message, this);

        } catch (Exception e) {
            SystemLogger.sendSystemMessage("Session could not handle opcode: " + opcode.name() + ", Cause: " + e.getCause() + ". (Probably not handled)");
        }
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
