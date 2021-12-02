//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shattered.database.mysql;


import com.shattered.ServerConstants;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.SQLQuery;
import com.shattered.database.mysql.query.result.QueryResult;
import com.shattered.system.SystemLogger;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;

@Data
@RequiredArgsConstructor
public class MySQLDatabase {

    /**
     * Represents the Connection Status
     */
    public enum ConnectionStatus { NOT_CONNECTED, CONNECTED, UNABLE_TO_CONNECT }

    /**
     * Represents the name of the database
     */
    private final String name;

    /**
     * Represents the host of the database
     */
    private final String host;

    /**
     * Represents the username of the database
     */
    private final String username;

    /**
     * Represents the password of the database
     */
    private final String password;

    /**
     * Represents the connection of the database
     */
    private Connection connection;

    /**
     * Represents the current status of the database (not updated realtime)
     */
    private ConnectionStatus status = ConnectionStatus.NOT_CONNECTED;


    /**
     * Prepares a connection for the database
     */
    public MySQLDatabase connect() {
        status = ConnectionStatus.NOT_CONNECTED;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (ServerConstants.LIVE_DB) {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + getName() + "?zeroDateTimeBehavior=convertToNull", username, password);
            } else {
                connection = DriverManager.getConnection("jdbc:mysql://" + "127.0.0.1" + "/" + getName() + "?zeroDateTimeBehavior=convertToNull", "root", "");
            }
            SystemLogger.sendSystemMessage("Successfully connected to " + getName() + " database services...");
            status = ConnectionStatus.CONNECTED;
        } catch (ClassNotFoundException | SQLException e) {
            SystemLogger.sendSystemErrMessage("Unable to connect to " + getName() + " database services...");
            status = ConnectionStatus.UNABLE_TO_CONNECT;
        }
        return this;
    }

    /**
     * Executes an SQL Query
     * @param query
     * @return
     */
    public QueryResult execute(SQLQuery query) {
        try {
            if (this.getStatus() != ConnectionStatus.CONNECTED) {
                return null;
            } else {
                String constructed = query.construct();
                if (constructed == null) {
                    return null;
                } else {
                    PreparedStatement statement = this.getConnection().prepareStatement(constructed);
                    Iterator iterator = query.getParameters().entrySet().iterator();

                    Entry entry;
                    while(iterator.hasNext()) {
                        entry = (Entry) iterator.next();
                        if (entry.getValue() instanceof Integer) {
                            statement.setInt((Integer)entry.getKey(), (Integer)entry.getValue());
                        } else if (entry.getValue() instanceof String) {
                            statement.setString((Integer)entry.getKey(), (String)entry.getValue());
                        } else if (entry.getValue() instanceof Long) {
                            statement.setLong((Integer)entry.getKey(), (Long)entry.getValue());
                        } else if (entry.getValue() instanceof Double) {
                            statement.setDouble((Integer)entry.getKey(), (Double)entry.getValue());
                        } else {
                            statement.setString((Integer)entry.getKey(), entry.getValue().toString());
                        }
                    }

                    entry = null;
                    QueryResult result;
                    if (query instanceof SelectCommand) {
                        result = new QueryResult(query, statement.executeQuery());
                    } else {
                        result = new QueryResult(query, statement.executeUpdate());
                    }

                    return result;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Terminates the current database conection
     */
    public void terminate() {
        try {
            if (this.getStatus() != ConnectionStatus.CONNECTED || this.getConnection() == null || this.getConnection().isClosed())
                return;

            this.getConnection().close();
            this.status = ConnectionStatus.NOT_CONNECTED;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
