package com.shattered.networking.session.ext;

import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 */
public class WorldSession extends Session {

    /**
     * Represents the World Token
     */
    public static final String WORLD_TOKEN = "00f3lsf#e2-b34f-as8-sks91334-ifj313cSfK";

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
    public WorldSession(Channel channel, String connUuid) {
        super(channel);
        setConnUuid(connUuid);
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
        SystemLogger.sendSystemMessage("World service has been unregistered. Local Address[Address=" + getChannel().localAddress().toString() + "]");
    }
}
