package com.shattered.client;

import com.shattered.networking.session.ext.ClientSession;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/15/2019
 */
public class ClientRegistering {

    /**
     * Represents a current list of ongoing proxies.
     */
    @Getter
    private List<ClientSession> clientSessions = new ArrayList<>();

    /**
     * Registers a current ongoing proxy session
     * @param session
     */
    public void registerClient(ClientSession session) {
        for (ClientSession entry : getClientSessions()) {

            //Ensures the current entry is valid otherwise it will remove from list
            if (entry == null) {
                clientSessions.remove(entry);
                continue;
            }

            //Ensures no duplicated sessions with same uuid
            if (entry.getConnUuid().equals(session.getConnUuid())) return;

            //Ensures no duplicated objects - redundant but safe
            if (getClientSessions().contains(session)) return;

            //Ensures the current channel is still remaining alive
            if (!session.getChannel().isActive()) return;

            //Appends the current registering proxy session into place
            getClientSessions().add(session);
        }
    }

    /**
     * Loops through to find a specific proxy given the
     *      connUuid.
     * @param connUuid
     * @return
     */
    public ClientSession getClient(String connUuid) {
        for (ClientSession entry  : getClientSessions()) {

            //Ensures the current entry is valid otherwise it will remove from list
            if (entry == null) {
                clientSessions.remove(entry);
                continue;
            }

            if (entry.getConnUuid().equals(connUuid)) return entry;
        }
        return null;
    }
}
