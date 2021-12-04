package com.shattered.database.mysql;

import com.shattered.Build;
import com.shattered.database.DatabaseService;
import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.result.QueryResult;
import com.shattered.system.SystemLogger;
import lombok.Getter;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author JTlr Frost | Mar 13, 2018 : 7:34:58 PM
 */
public class MySQLManager {

	/**
	 * A map of {@link MySQLDatabase} based on datatable name
	 */
	@Getter
	private final Map<String, MySQLDatabase> databases = new HashMap<>();

	/**
	 * Creates a new sql manager
	 * @param databases
	 */
	public MySQLManager(MySQLDatabase[] databases) {
		for (MySQLDatabase database : databases) {
			getDatabases().put(database.getName(), database);
		}
	}

	/**
	 * Represent the SQL Manager
	 * @return the manager
	 */
	public void connect() {
		for (Entry<String, MySQLDatabase> entry : databases.entrySet()) {
			MySQLDatabase database = entry.getValue();
			database.setStatus(MySQLDatabase.ConnectionStatus.NOT_CONNECTED);
			database.connect();
		}
	}

	/**
	 * Executes a query result
	 * @param name
	 * @param command
	 * @return the query result
	 */
	public QueryResult execute(String name, SQLCommand command) {
		MySQLDatabase database = getDatabases().get(name);
		if (database == null)
			return null;
		return database.execute(command);
	}

	/**
	 * Checks if the database is connected
	 * @param name
	 * @return connected
	 */
	public boolean isConnected(String name) {
		return getDatabases().containsKey(name) && getDatabases().get(name).getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED;
	}

}
