package com.shattered.networking.session.ext;

import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 */
public abstract class ClientSession extends Session {

    /**
     * Represents the Representive client token
     */
    public static final String CLIENT_TOKEN = "964d2ac5-b9c2-492a-8221-6c4e4f8ff3cb";

    /**
     * Represents the Client uuid
     */
    @Getter
    @Setter
    private String connUuid;

    /**
     * Creates a new Channel Line Session for the World ServerConnections
     * Sets the Initial Timeout to 30 seconds for initial delay.
     * @param channel
     */
    public ClientSession(Channel channel, String connUuid) {
        super(channel);
        setConnUuid(connUuid);
    }


    /**
     * Disconnects the {@link Channel}
     */
    @Override
    public void disconnect() {
        SystemLogger.sendSystemMessage("Client has been unregistered. Local Address[Address=" + getChannel().localAddress().toString() + ", Uuid=" + getConnUuid() + "]");
    }
}
