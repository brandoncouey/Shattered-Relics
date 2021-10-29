package com.shattered.networking;

import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.session.Session;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.Getter;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/12/2019
 */
public abstract class NetworkHandler extends SimpleChannelInboundHandler<PacketOuterClass.Packet> {


    /**
     * Represents the Handler
     * Case Sensitive
     */
    @Getter
    private String handler;

    /**
     *
     * @param handler
     */
    public NetworkHandler(String handler) {
        this.handler = handler;
    }

    /**
     * The attribute key of the {@link Session} for the networking.
     */
    @Getter
    private final AttributeKey<Session> sessionKey = AttributeKey.valueOf(handler + ".attr");

}
