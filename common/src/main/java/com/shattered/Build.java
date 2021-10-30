package com.shattered;

import com.shattered.database.DatabaseConfiguration;
import com.shattered.database.mysql.MySQLManager;
import com.shattered.networking.NetworkBootstrap;
import com.shattered.networking.handlers.DefaultNetworkHandler;
import com.shattered.networking.session.listeners.ChannelListener;
import com.shattered.connections.ServerType;
import com.shattered.system.SystemLogger;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;



/**
 * @author JTlr Frost <brradc@gmail.com> 6/13/2019
 */
@Data
public abstract class Build implements ChannelListener {

    /**
     * Represents the Grizzly Database
     * */
    @Getter
    @Setter
    private static MySQLManager grizzlyDatabase;

    /**
     * Represents the Shattered Relics Database
     */
    @Getter
    @Setter
    private static MySQLManager shatteredDatabase;

    /**
     * Represents the Network Bootstrap
     */
    @Getter
    @Setter
    private NetworkBootstrap network;



    /**
     * Initializes a new Server
     * @param type
     * @param host
     * @param port
     */
    public void build(ServerType type, String host, int port) {

        try {
            long startTime = System.currentTimeMillis();
            //Binds the server to the desired address
            setNetwork(new NetworkBootstrap(new DefaultNetworkHandler()));

            if (!type.equals(ServerType.PROXY)) {
                //Checks if MySQL Should be Enabled # Development Configuration
                //Initializes the MySQL Database Connection
                SystemLogger.sendSystemMessage("Connecting to database services... Set=" + (ServerConstants.LIVE_DB || ServerConstants.LIVE ? "LIVE" : "LOCAL" + " services."));
                setGrizzlyDatabase(new MySQLManager(DatabaseConfiguration.GRIZZLY_DATABASES));
                setShatteredDatabase(new MySQLManager(DatabaseConfiguration.SHATTERED_DATABASES));
                if (ServerConstants.LIVE_DB || ServerConstants.LIVE) {
                    getGrizzlyDatabase().connect("grizzlyent.cpde7dtfjvvy.us-west-2.rds.amazonaws.com", "admin", "!003786dc");
                    getShatteredDatabase().connect("shatteredrelics.cpde7dtfjvvy.us-west-2.rds.amazonaws.com", "admin", "!003786dc");
                } else {
                    getGrizzlyDatabase().connect("127.0.0.1", "root", "");
                    getShatteredDatabase().connect("127.0.0.1", "root", "");
                }
            }

            ChannelFuture future = getNetwork().bootstrap(host, port);
            if (future.isSuccess()) invoke(future);
            SystemLogger.sendSystemMessage("Successfully launched Shattered Relics " + type.name().toLowerCase() + " server in " + (System.currentTimeMillis() - startTime) + "ms.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
