package com.shattered.networking.session.ext;

import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost 7/15/18 : 1:47 PM
 */
public class RealmSession extends Session {


    /**
     * Represents the Realm Token
     */
    public static final String REALM_TOKEN = "sfk31234-fs1359d-cj8734-18kcbd-0987tnfjak31s";

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
    public RealmSession(Channel channel, String connUuid) {
        super(channel);
        setConnUuid(connUuid);
    }

    @Override
    public void invoke() {
        super.invoke();

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
        SystemLogger.sendSystemMessage("Realm service has been unregistered. Local Address[Address=" + getChannel().localAddress().toString() + "]");
    }
}
