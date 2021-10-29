package com.shattered.router;

import com.shattered.networking.session.ext.CentralSession;
import com.shattered.networking.session.ext.RealmSession;
import com.shattered.networking.session.ext.WorldSession;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JTlr Frost 6/4/2019 : 11:04 PM
 */
public class ProxyRouting {


    /**
     * Represents the Realm Session
     */
    @Getter
    @Setter
    private static RealmSession realmSession;

    /**
     * Represents the Realm Socket Address
     */
    @Getter
    @Setter
    private static InetSocketAddress realmAddress;

    //TODO do deregistering.
    /**
     * Represents the Connections
     */
    @Getter
    private static final Map<String, Channel> clientConnections = new HashMap<>();

    /**
     * Represents the World Connections
     * @param cuuid
     * @param session
     */
    @Getter
    private static final Map<String, WorldSession> worldConnections = new HashMap<>();

    
}
