package com.shattered.networking.session.ext;

import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 */
public class ChannelSession extends Session {


    /**
     * Represents the Realm Token
     */
    public static final String CHANNEL_TOKEN = "13fas3-SfaFz-8k3a-qZrkV-0193Khjm1fk";

    /**
     * Represents the Current Connection Id
     */
    @Getter
    @Setter
    private String connUuid;

    /**
     * Creates a new Channel Line Session for the World Server
     * @param channel
     */
    public ChannelSession(Channel channel, String connUuid) {
        super(channel);
        this.connUuid = connUuid;
    }

    /**
     * Receives the Incoming Client Messages
     * @param message
     */
    @Override
    public void messageReceived(Object message) {
        super.messageReceived(message);
    }


    /**
     * Disconnects the {@link Channel}
     */
    @Override
    public void disconnect() {
        SystemLogger.sendSystemMessage("Channel Service has been unregistered. Local Address[Address=" + getChannel().localAddress().toString() + "]");
    }
}
