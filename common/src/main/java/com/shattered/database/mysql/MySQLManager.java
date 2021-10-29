package com.shattered.database.mysql;

import com.shattered.database.DatabaseService;
import com.shattered.database.mysql.command.SQLCommand;
import com.shattered.database.mysql.query.result.QueryResult;
import com.shattered.system.SystemLogger;

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
	private Map<String, MySQLDatabase> DATABASES = new HashMap<>();

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
			getMySQLDatabases().put(database.getName(), database);

		}

	}

	/**
	 * @return
	 */
	public MySQLManager connect(String host, String username, String password) {

		try {

			/*
			 * Loop through each MySQL datatable
			 */
			for (Entry<String, MySQLDatabase> entry : DATABASES.entrySet()) {

				MySQLDatabase database = entry.getValue();

				/**
				 * Used for re-connections
				 */
				if (database.getStatus() == MySQLDatabase.MySQLConnectionStatus.CONNECTED)
					continue;

				/*
				 * Prepare the MySQL datatable
				 */
				database.prepare(host, username, password);

				/*
				 * Check if there is connectivity with the MySQL Server
				 */
				if (database.getStatus() != MySQLDatabase.MySQLConnectionStatus.CONNECTED) {
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
	 * 
	 * @param name
	 * @param command
	 * @return
	 */
	public QueryResult execute(String name, SQLCommand command) {

		try {

			/*
			 * Get the MySQL datatable handler from the datatable manager
			 */
			MySQLDatabase database = getMySQLDatabases().get(name);

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
	 * @param name
	 * @return
	 */
	public boolean isConnected(String name) {
		return getMySQLDatabases().containsKey(name) && getMySQLDatabases().get(name).getStatus() == MySQLDatabase.MySQLConnectionStatus.CONNECTED;
	}

	/**
	 * @return
	 */
	public Map<String, MySQLDatabase> getMySQLDatabases() {
		return this.DATABASES;
	}

}
