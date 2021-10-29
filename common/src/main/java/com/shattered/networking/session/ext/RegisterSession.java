package com.shattered.networking.session.ext;

import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;

/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 */
public class RegisterSession extends Session {


    /**
     * Creates a new Channel Line Session for the World Server
     * @param channel
     */
    public RegisterSession(Channel channel) {
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
        SystemLogger.sendSystemMessage("Register Session has been unregistered. Local Address[Address=" + getChannel().localAddress().toString() + "]");
    }
}
