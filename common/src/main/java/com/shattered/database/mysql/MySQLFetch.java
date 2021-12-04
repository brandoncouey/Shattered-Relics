package com.shattered.database.mysql;

import com.shattered.Build;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;
import com.shattered.database.DatabaseService;
import com.shattered.database.mysql.query.result.QueryResult;
import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.system.SystemLogger;

import java.sql.ResultSet;

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

		SelectCommand select = new SelectCommand(getTableName());
		if (getFetchConditions() != null)
			select.addOptions(getFetchConditions());

		QueryResult query = dbManager.execute(getDatabaseName(), select);

		if (query == null) {
			Build.getDatabaseManager().connect();
			query = dbManager.execute(getDatabaseName(), select);
		}

		result = query.getResultSet();

		//Assuming connection is dropped. We will try again.
		if (result == null) {
			Build.getDatabaseManager().getDatabases().get(getDatabaseName()).connect();
			result = dbManager.execute(getDatabaseName(), select).getResultSet();
		}
		return result;
	}

	/**
	 * Fetches the results with the specified db name, table name and  conditions
	 * @param database
	 * @param table
	 * @param conditions
	 * @return the result set
	 */
	default ResultSet getResults(String database, String table, WhereConditionOption[] conditions) {
		ResultSet result = null;

		//Checks if connected, if not it will reconnect to the database.
		final MySQLManager dbManager = Build.getDatabaseManager();

		SelectCommand select = new SelectCommand(table);
		if (conditions != null)
			select.addOptions(conditions);

		QueryResult query = dbManager.execute(database, select);

		if (query == null) {
			Build.getDatabaseManager().getDatabases().get(database).connect();
			query = dbManager.execute(database, select);
		}

		result = query.getResultSet();

		//Assuming connection is dropped. We will try again.
		if (result == null) {
			Build.getDatabaseManager().getDatabases().get(database).connect();
			result = dbManager.execute(database, select).getResultSet();
		}
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
