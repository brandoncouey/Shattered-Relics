package com.shattered.service;

import com.shattered.connections.ServerType;
import com.shattered.networking.session.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
@RequiredArgsConstructor
public class ServerService {

    /**
     * Represents the Connection UUID
     * This ID is used to identify the exact instance of the server.
     */
    @Getter
    private final String cuuid;

    /**
     * Represents the Server Type
     */
    @Getter
    private final ServerType serverType;

    /**
     * Represents the Current Session of the Server
     */
    @Getter
    private final Session session;

    /**
     * Represents the Server Socket Address
     */
    @Getter
    @Setter
    private InetSocketAddress address;
}
