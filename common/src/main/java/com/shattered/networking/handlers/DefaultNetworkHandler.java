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
import io.netty.handler.timeout.ReadTimeoutException;

import java.io.IOException;
import java.util.Map;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/16/2019
 */
@SuppressWarnings("Duplicates")
@ChannelHandler.Sharable
public class DefaultNetworkHandler extends NetworkHandler {

    /**
     * Creates a Default Network Handler
     */
    public DefaultNetworkHandler() {
        super("DefaultNetworkHandler");
    }

    /**
     *
     * @param ctx
     * @param packet
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketOuterClass.Packet packet) {
        PacketOuterClass.Opcode opcode = packet.getOpcode();
        try {
            Message message = ProtoEventListener.decode(packet);
            if (opcode != null && message != null) {

                if (ProtoEventListener.getBuilders().get(opcode) != null) {
                    ProtoEventListener.decode(packet);
                    if (ProtoEventListener.forOpcode(opcode) != null) {
                        for (Map.Entry<ProtoListener<?>, PacketOuterClass.Opcode> listener : ProtoEventListener.getListeners().entrySet()) {
                            if (listener == null) continue;
                            if (listener.getValue().equals(opcode))
                                listener.getKey().handleRaw(message, ctx.channel().attr(getSessionKey()).get());
                        }
                    }
                }
            }

        } catch (Exception e) {
            SystemLogger.sendSystemErrMessage("DefaultNetworkHandler could not handle default opcode: " + (opcode == null ? "unknown" : opcode.name()) + ", Cause: " + e.getMessage() + ", Could not be handled.)");

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

    /**
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    /*
     * (non-Javadoc)
     *
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelUnregistered(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        try {
            Channel channel = ctx.channel();

            if (channel == null) return;

            Session session = channel.attr(getSessionKey()).get();
            if (session != null)
                session.disconnect();

            if (channel.isRegistered())
                channel.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            Channel channel = ctx.channel();

            if (cause instanceof ReadTimeoutException || cause instanceof IOException) {
                return;
            }

           /* if (channel.isRegistered())  {
                SystemLogger.sendSystemErrMessage("DefaultNetworkHandler -> Exception=" + cause.getLocalizedMessage());
                channel.close();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
