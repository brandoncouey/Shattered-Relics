package com.shattered.networking.session.listeners;

import io.netty.channel.ChannelFuture;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
public interface ChannelListener {

    /**
     *
     * @param channelFuture
     */
    void invoke(ChannelFuture channelFuture);
}
