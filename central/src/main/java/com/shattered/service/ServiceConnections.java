package com.shattered.service;

import com.shattered.connections.ServerType;
import com.shattered.connections.WorldListEntry;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.session.Session;
import com.shattered.system.SystemLogger;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
public class ServiceConnections {

    /**
     * Represents a List of current Services of all Server Types.
     */
    @Getter
    private static List<ServerService> services = new CopyOnWriteArrayList<>();


    /**
     * Represents the World List Entries
     * This world list is separate from Realm.getWorldList() -> Realm.module
     * This world list is used for world->central of updating their current status
     * Realm.getWorldList() -> Realm.module is used for requesting and filling information
     *      to send to the client.
     */
    @Getter
    private static Map<String, WorldListEntry> worldListEntries = new ConcurrentHashMap<>();



    /**
     * Registers a {@link ServerService} with the provided Service Information
     *
     * @param connectionUid
     * @param type
     * @param session
     */
    public static void registerService(String connectionUid, ServerType type, Session session) {
        synchronized (getServices()) {
            getServices().add(new ServerService(connectionUid, type, session));
            SystemLogger.sendSystemMessage("ServerConnections -> Server Registered. Type=" + type.name() + ", ConnectionId=" + connectionUid);
        }
    }

    /**
     * Unregisters a {@link ServerService} by it's CUUID.
     * @param connectionUid
     */
    public static void unregister(String connectionUid) {
        synchronized (getServices()) {
            ServerService server = forUUID(connectionUid);
            if (server == null) return;

            //Checks if a World; If so it will unregister the entry.
            if (server.getServerType() == ServerType.WORLD)
                unregisterWorldEntry(connectionUid);

            services.remove(server);
            SystemLogger.sendSystemErrMessage("ServerConnections -> Server Unregistered. Type=" + server.getServerType().name() + ", ConnectionId=" + connectionUid);
        }
    }

    /**
     * Fetches a {@link com.shattered.networking.proto.Realm.UpdateRealmList.WorldEntry} by it's CUUID.
     * @param connectionUid
     *
     * @return the world entry.
     */
    public static WorldListEntry forEntryByUUID(String connectionUid) {
        for (WorldListEntry entry : getWorldListEntries().values()) {
            if (entry == null) continue;
            if (entry.getConnectionUuid().equals(connectionUid))
                return entry;
        }
        return null;
    }

    /**
     * Registers a {@link com.shattered.networking.proto.Realm.UpdateRealmList.WorldEntry} to the list.
     * Notify's all on-going realms of the {@link com.shattered.networking.proto.Realm.UpdateRealmList.WorldEntry} registry.
     *
     * @param entry
     */
    public static void registerService(WorldListEntry entry) {
        synchronized (getWorldListEntries()) {

            if (!getWorldListEntries().containsKey(entry.getConnectionUuid()))
                SystemLogger.sendSystemMessage("ServerConnections -> World Registered {cuuid=" + entry.getConnectionUuid() + ", name=" + entry.getName() + "}");

            getWorldListEntries().put(entry.getConnectionUuid(), entry);

            //Sends Entry to the Realm
            for (ServerService server : getServices()) {
                if (server == null) return;
                if (server.getServerType() != ServerType.REALM) continue;
                NetworkBootstrap.sendPacket(server.getSession().getChannel(), PacketOuterClass.Opcode.S_UpdateWorldEntry, Sharding.UpdateWorldEntry.newBuilder().setEntry(
                        Sharding.UpdateWorldList.Entry.newBuilder().setConnUuid(entry.getConnectionUuid()).setIndex(entry.getId()).setHost(entry.getSocket().getHostName())
                        .setPort(entry.getSocket().getPort()).setName(entry.getName()).setLocation(entry.getLocation()).setType(entry.getType()).setPopulation(entry.getPopulation()).build()
                ).build());
                SystemLogger.sendSystemMessage("S_UpdateWorldEntry -> Updating Entry for " + entry.getName() + " for Realm=" + server.getCuuid());
            }


        }
    }

    /**
     * Unregisters a {@link com.shattered.networking.proto.Realm.UpdateRealmList.WorldEntry} from the world list given it's connection UUID.
     * Notify's all on-going realms of the {@link com.shattered.networking.proto.Realm.UpdateRealmList.WorldEntry} being unregistered.
     *
     * @param connectionUid
     */
    public static void unregisterWorldEntry(String connectionUid) {
        synchronized (getWorldListEntries()) {
            for (WorldListEntry entry : getWorldListEntries().values()) {
                if (entry == null) continue;
                if (entry.getConnectionUuid().equals(connectionUid)) {
                    //Sends Entry to the Realm
                    for (ServerService server : getServices()) {
                        if (server == null) return;
                        if (server.getServerType() != ServerType.REALM) continue;
                        NetworkBootstrap.sendPacket(server.getSession().getChannel(), PacketOuterClass.Opcode.S_UpdateWorldEntry, Sharding.UpdateWorldEntry.newBuilder().setEntry(
                                Sharding.UpdateWorldList.Entry.newBuilder().setConnUuid(entry.getConnectionUuid()).setIndex(-1).setName("Unavailable").build()
                        ).build());
                        SystemLogger.sendSystemMessage("S_UpdateWorldEntry -> Updating Entry for " + entry.getName() + " for Realm=" + server.getCuuid());
                    }
                    getWorldListEntries().remove(connectionUid);
                    SystemLogger.sendSystemMessage("ServerConnections -> World Unregistered {cuuid=" + connectionUid + ", name=" + entry.getName() + "}");
                }
            }
        }
    }

    /**
     * Finds a Server Service by their Connection UUID
     *
     * @param uuid
     * @return the service
     */
    public static ServerService forUUID(String uuid) {
        for(ServerService service : getServices()) {
            if (service == null) continue;
            if (service.getCuuid().equals(uuid))
                return service;
        }
        return null;
    }

    /**
     * Returns a list of all connections for their type
     *
     * @param type
     * @return the service
     */
    public static List<ServerService> getServersForType(ServerType type) {
        List<ServerService> servers = null;
        for (ServerService service : getServices()) {
            if (service == null) continue;
            if (service.getServerType().equals(type)) {
                if (servers == null)
                    servers = new ArrayList<>();
                servers.add(service);
            }
        }
        return servers;
    }

    /**
     * Gets a Type of Service
     *
     * @param type
     * @return the service
     */
    public static ServerService getServerForType(ServerType type) {
        for (ServerService service : getServices()) {
            if (service == null || service.getServerType() == null)  continue;
            if (service.getServerType().equals(type)) return service;
        }
        return null;
    }
}
