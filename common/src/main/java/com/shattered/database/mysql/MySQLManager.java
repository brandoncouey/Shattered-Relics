package com.shattered.database.mysql;

import com.shattered.database.DatabaseService;
import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.result.QueryResult;
import com.shattered.system.SystemLogger;
import lombok.Getter;

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
	private Map<String, MySQLDatabase> databases = new HashMap<>();

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
	public MySQLManager connect() {

		try {

			/*
			 * Loop through each MySQL datatable
			 */
			for (Entry<String, MySQLDatabase> entry : databases.entrySet()) {

				MySQLDatabase database = entry.getValue();

				if (database.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
					continue;

				database.connect();

				if (database.getStatus() != MySQLDatabase.ConnectionStatus.CONNECTED) {
					SystemLogger.sendDatabaseInformation(DatabaseService.MYSQL, "Unable to connect with '" + database.getName() + "' queries.");
				} else {
					SystemLogger.sendDatabaseInformation(DatabaseService.MYSQL, "Connected to '" + database.getName() + "' datatable.");
				}

			}

		} catch (Exception e) {
		}
		return this;

	}

	/**
	 * Executes a query result
	 * @param name
	 * @param command
	 * @return the query result
	 */
	public QueryResult execute(String name, SQLCommand command) {

		try {

			/*
			 * Get the MySQL datatable handler from the datatable manager
			 */
			MySQLDatabase database = getDatabases().get(name);

			/*
			 * Check if MySQL datatable handler is found
			 */
			if (database == null)
				return null;

			return database.execute(command);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
