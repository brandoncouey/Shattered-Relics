package com.shattered.connections;

import com.shattered.networking.session.Session;
import com.shattered.sessions.ChannelGameServerSession;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Frost 2/1/2020 : 11:49 AM
 */
public class ServerConnections {

    @Getter
    public static final List<Session> servers = new CopyOnWriteArrayList<>();

    /**
     * Gets a List of all of the World Servers Connected
     * @return
     */
    public List<ChannelGameServerSession> getGameServers() {
        List<ChannelGameServerSession> servers = new ArrayList<>();
        for (Session session : getServers()) {
            if (session == null) continue;
            if (session instanceof ChannelGameServerSession)
                servers.add((ChannelGameServerSession) session);
        }
        return servers;
    }

    /**
     * Gets the Session for Connection UUID
     * @param connectionUUID
     * @return
     */
    public Session getSessionForConnectionUUID(String connectionUUID) {
        for (Session session : servers) {
            if (session == null) continue;

            if (session instanceof ChannelGameServerSession) {
                ChannelGameServerSession worldSession = (ChannelGameServerSession) session;
                if (worldSession.getConnUuid().equals(connectionUUID))
                    return worldSession;

            }
        }
        return null;
    }

    /**
     * Adds a server to the session list.
     * @param session
     */
    public static void addServer(Session session) {
        if (servers.contains(session)) return;
        servers.add(session);
    }

    /**
     * Removes a server from the session list.
     * @param session
     */
    public static void removeServer(Session session) {
        if (!servers.contains(session)) return;
        servers.remove(session);
    }
}
