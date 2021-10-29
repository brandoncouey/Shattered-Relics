package com.shattered.networking.handlers;

import com.google.protobuf.Message;
import com.shattered.networking.NetworkHandler;
import com.shattered.networking.listeners.ProtoEventListener;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.session.Session;
import com.shattered.networking.session.ext.RegisterSession;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;
import java.util.Map;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/16/2019
 */
@ChannelHandler.Sharable
public class ClientNetworkHandler extends NetworkHandler {


    /**
     * Creates a Client NetworkHandler
     */
    public ClientNetworkHandler() {
        super("ClientNetworkHandler");
    }

    /**
     *
     * @param ctx
     * @param packet
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketOuterClass.Packet packet) throws Exception {
        PacketOuterClass.Opcode opcode = packet.getOpcode();
        try {
            Message message = ProtoEventListener.decode(packet);
            if (opcode != null && message != null) {

                if (ProtoEventListener.getBuilders().get(opcode) != null) {
                    ProtoEventListener.decode(packet);
                    if (ProtoEventListener.forOpcode(opcode) != null) {
                        for (Map.Entry<ProtoListener<?>, PacketOuterClass.Opcode> entry : ProtoEventListener.getListeners().entrySet()) {
                            if (entry.getValue() == opcode)
                                entry.getKey().handleRaw(message, ctx.channel().attr(getSessionKey()).get());
                        }
                       // ProtoEventListener.getListeners().gethandleRaw(message, ctx.channel().attr(getSessionKey()).get());
                    }
                }
            }

        } catch (Exception e) {
            SystemLogger.sendSystemErrMessage("ClientNetworkHandler could not handle client opcode: " + (opcode == null ? "unknown" : opcode.name()) + ", Cause: " + e.getMessage() + ", Could not be handled.)");

        }
    }

    /**
     *
     * @param ctx
     * @param message
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        try {

            Channel channel = ctx.channel();
            Session session = channel.attr(getSessionKey()).get();

            if (channel != null && session == null) {
                channel.attr(getSessionKey()).set(new RegisterSession(channel));
            }

            if (session != null && message != null) {
                session.messageReceived(message);
                return;
            }
            if (message instanceof PacketOuterClass.Packet) {
                PacketOuterClass.Packet packet = (PacketOuterClass.Packet) message;
                channelRead0(ctx, packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
