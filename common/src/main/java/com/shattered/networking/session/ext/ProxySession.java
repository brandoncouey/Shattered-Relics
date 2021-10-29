package com.shattered.networking.session.ext;

import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 */
public class ProxySession extends Session {


    /**
     * Represents the Realm Token
     */
    public static final String PROXY_TOKEN = "121d9746-efdd-4b73-9af0-151b02160270";

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
    public ProxySession(Channel channel) {
        super(channel);
    }

    /**
     * Receives the Incoming Client Messages
     * @param message
     */
    @Override
    public void messageReceived(Object message) {
        //TODO
    }


    /**
     * Disconnects the {@link Channel}
     */
    @Override
    public void disconnect() {
        SystemLogger.sendSystemMessage("Proxy Service has been unregistered. Local Address[Address=" + getChannel().localAddress().toString() + "]");
    }
}
