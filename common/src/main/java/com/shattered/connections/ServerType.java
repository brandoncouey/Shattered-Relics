package com.shattered.connections;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
public enum ServerType {

    /**
     * Represents the Central ShatteredServer Type
     */
    CENTRAL,
    
    /**
     * Represents the Proxy ShatteredServer Type
     */
    PROXY,

    /**
     * Represents the Realm ShatteredServer Type
     */
    REALM,

    /**
     * Represents the World ShatteredServer Type
     */
    WORLD,

    /**
     * Represents the Channel Server
     */
    CHANNEL
    ;

    /**
     * Gets the Server Type for ID
     * @param id
     * @return
     */
    public static ServerType forId(int id) {
        for (ServerType type : ServerType.values()) {
            if (type.ordinal() == id)
                return type;
        }
        return null;
    }

}
