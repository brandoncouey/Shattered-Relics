//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shattered.database.mysql;

import com.shattered.database.mysql.command.impl.SelectCommand;
import com.shattered.database.mysql.query.SQLQuery;
import com.shattered.database.mysql.query.result.QueryResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;

public class MySQLDatabase {
    private String name;
    private Connection connection;
    private MySQLConnectionStatus status;

    public MySQLDatabase(String name) {
        this.status = MySQLConnectionStatus.NOT_CONNECTED;
        this.name = name;
    }

    public void prepare(String host, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + this.getName() + "?zeroDateTimeBehavior=convertToNull", username, password);
            this.status = MySQLConnectionStatus.CONNECTED;
        } catch (ClassNotFoundException | SQLException var5) {
            this.status = MySQLConnectionStatus.UNABLE_TO_CONNECT;
        }

    }

    public QueryResult execute(SQLQuery query) {
        try {
            if (this.getStatus() != MySQLConnectionStatus.CONNECTED) {
                return null;
            } else {
                String constructed = query.construct();
                if (constructed == null) {
                    return null;
                } else {
                    PreparedStatement statement = this.getConnection().prepareStatement(constructed);
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

    public void terminate() {
        try {
            if (this.getStatus() != MySQLConnectionStatus.CONNECTED || this.getConnection() == null || this.getConnection().isClosed()) {
                return;
            }

            this.getConnection().close();
            this.status = MySQLConnectionStatus.NOT_CONNECTED;
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }

    public String getName() {
        return this.name;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public MySQLConnectionStatus getStatus() {
        return this.status;
    }

    public static enum MySQLConnectionStatus {
        NOT_CONNECTED,
        CONNECTED,
        UNABLE_TO_CONNECT;

        private MySQLConnectionStatus() {
        }
    }
}
