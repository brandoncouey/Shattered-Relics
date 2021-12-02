package com.shattered.database.mysql;

import com.shattered.Build;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;
import com.shattered.database.DatabaseService;
import com.shattered.system.SystemLogger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JTlr Frost | Mar 13, 2018 : 8:03:47 PM
 */
public interface MySQLFetch {

	/**
	 * Database name.
	 */
	default String getDatabaseName() {
		return "shatteredrelics";
	}

	/**
	 * Table name.
	 */
	default String getTableName() {
		return "";
	}

	/**
	 * Method used for Fetching SQL Tables
	 */
	default boolean fetch() {
		return false;
	}

	/**
	 * Conditions.
	 */
	default WhereConditionOption[] getFetchConditions() {
		return null;
	}

	/**
	 * Fetches all columns from the datatable.
	 */
	default ResultSet getResults() {
		ResultSet result = null;

		//Checks if connected, if not it will reconnect to the database.
		final MySQLManager dbManager = Build.getDatabaseManager();
		if (!dbManager.isConnected(getDatabaseName())) {
			Build.connectToDatabases();
		}

		SelectCommand select = new SelectCommand(getTableName());
		if (getFetchConditions() != null)
			select.addOptions(getFetchConditions());


		result = dbManager.execute(getDatabaseName(), select).getResultSet();

		//Assuming connection is dropped. We will try again.
		if (result == null) {
			Build.getDatabaseManager().getDatabases().get(getDatabaseName()).prepare();
			result = dbManager.execute(getDatabaseName(), select).getResultSet();
		}

		/*try {

			result.last();
			result.getRow();
			result.beforeFirst();

		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		return result;
	}

	default ResultSet getResults(String databaseName, String tableName, WhereConditionOption[] conditions) {
		ResultSet result = null;
		final MySQLManager dbManager = Build.getDatabaseManager();

		if (!dbManager.isConnected(databaseName)) {
			SystemLogger.sendDatabaseErr(DatabaseService.MYSQL, "Could not `fetch` MySQL Results from " + getDatabaseName() + ", [Reason=MySQL connection is currently not established.]");
			return null;
		}

		SelectCommand select = new SelectCommand(tableName);
		if (conditions != null)
			select.addOptions(conditions);

		result = dbManager.execute(databaseName, select).getResultSet();

		//Assuming connection is dropped. We will try again.
		if (result == null) {
			Build.getDatabaseManager().getDatabases().get(databaseName).prepare();
			result = dbManager.execute(databaseName, select).getResultSet();
		}

		/*try {

			result.last();
			result.getRow();
			result.beforeFirst();

		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		return result;
	}

	/**
	 * Checks if the Conditions has Results
	 * @return has results
	 */
	default boolean hasResults() {
		try {
			if (getResults() == null)
				return false;
			if (getResults() != null) {
				if (getResults().next())
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
