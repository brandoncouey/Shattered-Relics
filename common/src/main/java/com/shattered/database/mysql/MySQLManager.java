package com.shattered.database.mysql;

import com.shattered.database.DatabaseService;
import com.shattered.database.mysql.query.command.SQLCommand;
import com.shattered.database.mysql.query.result.QueryResult;
import com.shattered.system.SystemLogger;
import lombok.Getter;

import java.sql.SQLException;
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
	 * @param databases
	 */
	public MySQLManager(MySQLDatabase[] databases) {

		/*
		 * Loop through the MySQL databases
		 */
		for (MySQLDatabase database : databases) {

			/*
			 * Add MySQL datatable to the mapping
			 */
			getDatabases().put(database.getName(), database);

		}

	}

	/**
	 * Connects to the specified database host with the user and password
	 * @return the sql manager
	 */
	public MySQLManager connect() {

		try {

			/*
			 * Loop through each MySQL datatable
			 */
			for (Entry<String, MySQLDatabase> entry : databases.entrySet()) {

				MySQLDatabase database = entry.getValue();

				/**
				 * Used for re-connections
				 */
				if (database.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
					continue;

				/*
				 * Prepare the MySQL datatable
				 */
				database.connect();

				/*
				 * Check if there is connectivity with the MySQL Server
				 */
				if (database.getStatus() != MySQLDatabase.ConnectionStatus.CONNECTED) {
					SystemLogger.sendDatabaseInformation(DatabaseService.MYSQL, "Unable to connect with '" + database.getName() + "' queries.");
				} else {
					SystemLogger.sendDatabaseInformation(DatabaseService.MYSQL, "Connected to '" + database.getName() + "' datatable.");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;

	}

	/**
	 * Executes a sql command to the specified database name
	 * @param name
	 * @param command
	 * @return the query result
	 */
	public QueryResult execute(String name, SQLCommand command) {


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

	}

	/**
	 * Checks if the database is currently connected
	 * @param name
	 * @return database connected
	 */
	public boolean isConnected(String name) {
		if (!getDatabases().containsKey(name)) return false;
		if (getDatabases().get(name).getConnection() == null) return false;
		try {
			return !getDatabases().get(name).getConnection().isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
