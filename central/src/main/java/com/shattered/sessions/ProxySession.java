package com.shattered.sessions;

import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;

/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 */
public class ProxySession extends Session {

    /**
     * Represents the Proxy Token
     * This token is used to Identify the Incoming server as a Proxy.
     */
    public static final String PROXY_TOKEN = "121d9746-efdd-4b73-9af0-151b02160270";

    /**
     * Creates a new Channel Line Session for the World ServerConnections
     * @param channel
     */
    public ProxySession(Channel channel) {
        super(channel);
    }

    /**
     * Receives the Incoming Client Messages
     * @param object
     */
    @Override
    public void messageReceived(Object object) {
        super.messageReceived(object);
    }


    /**
     * Disconnects the {@link Channel}
     */
    @Override
    public void disconnect() {
        SystemLogger.sendSystemMessage("Proxy Service has disconnected!");
    }
}
