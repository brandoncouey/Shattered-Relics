package com.shattered.connections;

import com.shattered.ServerConstants;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @author JTlr Frost 8/31/2019 : 2:41 AM
 */
@Data
public class WorldListEntry {

    /**
     * Represents the Connection UUID
     */
    private String connectionUuid;

    /**
     * Represents the Id of the Realm Entry
     */
    private int id;

    /**
     * Represents the Name of the Realm Entry
     */
    private String name;

    /**
     * Represents the Location of the Realm Entry
     */
    private String location;

    /**
     * Represents the Type of the Realm Entry
     */
    private String type;

    /**
     * Represents the Population of the Realm Entry
     */
    private String population;

    /**
     * Represents the Socket Address of the Realm Entry
     */
    private InetSocketAddress socket;

    /**
     *
     * @param id
     * @param name
     * @param location
     * @param type
     */
    public WorldListEntry(String connectionUuid, int id, String name, String location, String type, String population) {
        this(connectionUuid, id, name, type, location, population, new InetSocketAddress("0.0.0.0", ServerConstants.WORLD_DEFAULT_PORT + id));
    }

    /**
     *
     * @param id
     * @param name
     * @param location
     * @param type
     */
    public WorldListEntry(String connectionUuid, int id, String name, String location, String type, String population, InetSocketAddress socket) {
        setConnectionUuid(connectionUuid);
        setId(id);
        setName(name);
        setLocation(location);
        setType(type);
        setPopulation(population);
        setSocket(socket);
    }
}
