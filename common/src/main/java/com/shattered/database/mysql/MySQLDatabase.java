//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shattered.database.mysql;


import com.shattered.ServerConstants;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.SQLQuery;
import com.shattered.database.mysql.query.result.QueryResult;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;

public class MySQLDatabase {

    /**
     * Represents the Connection Status
     */
    public enum ConnectionStatus { NOT_CONNECTED, CONNECTED, UNABLE_TO_CONNECT }

    /**
     * Represents the name of the database
     */
    @Getter
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
     * Represents the connection status of the database
     */
    @Getter
    private ConnectionStatus status = ConnectionStatus.NOT_CONNECTED;

    /**
     * Creates a new instance of the SQL Database
     * @param name
     * @param host
     * @param username
     * @param password
     */
    public MySQLDatabase(String name, String host, String username, String password) {
        this.name = name;
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * Attempts connection of the sql database
     */
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (ServerConstants.LIVE_DB) {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + name + "?zeroDateTimeBehavior=convertToNull", username, password);
            } else {
                this.connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/" + name + "?zeroDateTimeBehavior=convertToNull", "root", "");
            }
            this.status = ConnectionStatus.CONNECTED;
        } catch (ClassNotFoundException | SQLException var5) {
            this.status = ConnectionStatus.UNABLE_TO_CONNECT;
        }

    }

    public QueryResult execute(SQLQuery query) {
        try {
            if (status != ConnectionStatus.CONNECTED) {
                return null;
            } else {
                String constructed = query.construct();
                if (constructed == null) {
                    return null;
                } else {
                    PreparedStatement statement = connection.prepareStatement(constructed);
                    Iterator var5 = query.getParameters().entrySet().iterator();

                    Entry entry;
                    while(var5.hasNext()) {
                        entry = (Entry)var5.next();
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
        } catch (SQLException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    /**
     * Terminates the current sql database
     */
    public void terminate() {
        try {
            if (status != ConnectionStatus.CONNECTED || connection == null || connection.isClosed()) {
                return;
            }

            connection.close();
            status = ConnectionStatus.NOT_CONNECTED;
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }

}
