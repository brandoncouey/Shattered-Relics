package com.shattered.database.mysql;

import com.shattered.Build;
import com.shattered.database.mysql.query.command.impl.DeleteCommand;
import com.shattered.database.mysql.query.command.impl.InsertCommand;
import com.shattered.database.mysql.query.command.impl.SelectCommand;
import com.shattered.database.mysql.query.command.impl.UpdateCommand;
import com.shattered.database.mysql.query.options.impl.TableColumnValueOption;
import com.shattered.database.mysql.query.options.impl.WhereConditionOption;
import com.shattered.database.mysql.query.result.QueryResult;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author JTlr Frost | Mar 13, 2018 : 9:40:48 PM
 */
public interface MySQLEntry {

	/**
	 * Conditions.
	 */
	default WhereConditionOption[] getUpdateConditions() {
		return null;
	}

	/**
	 * Conditions.
	 */
	default WhereConditionOption[] getDeleteConditions() {
		return null;
	}

	/**
	 * Inserts a new SQL Column
	 */
	default boolean insert() {
		return false;
	}

	/**
	 * Updates an existing SQL Column
	 */
	default boolean update() {
		return false;
	}

	/**
	 * Deletes an existing SQL Column
	 */
	default void delete() {

	}

	default void entry(String databaseName, String tableName, MySQLCommand commandType, MySQLColumn... commands) {
		List<MySQLColumn> columns = new ArrayList<>(Arrays.asList(commands));
		entry(databaseName, tableName, columns, commandType);
	}

	/**
	 * @param databaseName
	 * @param tableName
	 * @param values
	 * @param commandType
	 */
	default void entry(String databaseName, String tableName, List<MySQLColumn> values, MySQLCommand commandType) {

		QueryResult statement = null;

		//Checks if connected, if not it will reconnect to the database.
		final MySQLManager database = Build.getDatabaseManager();
		if (!database.isConnected(databaseName)) {
			Build.connectToDatabases();
		}

		switch (commandType) {

			case UPDATE: {

				/*
				 * Construct a select command for gathering player profile
				 */
				SelectCommand command = new SelectCommand(tableName);
				command.addOptions(getUpdateConditions());

				/*
				 * Execute the constructed select command
				 */
				statement = Build.getDatabaseManager().execute(databaseName, command);

				//Assuming connection is dropped. We will try again.
				if (statement == null) {
					MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
					if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
						statement = Build.getDatabaseManager().execute(databaseName, command);
				}

				/*
				 * Check if a player record is found
				 */
				if (statement.getRowCount() > 0) {
					UpdateCommand update = new UpdateCommand(tableName);

					for (int i = 0; i < values.size(); i++) {
						update.addOption(new TableColumnValueOption(values.get(i).getName(), values.get(i).getValue()));
					}

					if (getUpdateConditions() != null)
						update.addOptions(getUpdateConditions());

					statement = Build.getDatabaseManager().execute(databaseName, update);

					//Assuming connection is dropped. We will try again.
					if (statement == null) {
						MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
						if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
							statement = Build.getDatabaseManager().execute(databaseName, update);
					}

					if (statement != null)
						statement.terminate();
				} else {
					InsertCommand insert = new InsertCommand(tableName);

					for (int i = 0; i < values.size(); i++) {
						insert.addOption(new TableColumnValueOption(values.get(i).getName(), values.get(i).getValue()));
					}

					statement = Build.getDatabaseManager().execute(databaseName, insert);

					//Assuming connection is dropped. We will try again.
					if (statement == null) {
						MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
						if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
							statement = Build.getDatabaseManager().execute(databaseName, insert);
					}

					if (statement != null)
						statement.terminate();
				}

				break;
			}

			case INSERT: {
				InsertCommand insert = new InsertCommand(tableName);

				for (int i = 0; i < values.size(); i++) {
					insert.addOption(new TableColumnValueOption(values.get(i).getName(), values.get(i).getValue()));
				}

				statement = Build.getDatabaseManager().execute(databaseName, insert);

				//Assuming connection is dropped. We will try again.
				if (statement == null) {
					MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
					if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
						statement = Build.getDatabaseManager().execute(databaseName, insert);
				}

				if (statement != null)
					statement.terminate();

				break;
			}

			case DELETE_FROM: {

				/*
				 * Construct a select command for gathering player profile
				 */
				SelectCommand deleteCommand = new SelectCommand(tableName);
				deleteCommand.addOptions(getDeleteConditions());

				/*
				 * Execute the constructed select command
				 */
				statement = Build.getDatabaseManager().execute(databaseName, deleteCommand);

				//Assuming connection is dropped. We will try again.
				if (statement == null) {
					MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
					if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
						statement = Build.getDatabaseManager().execute(databaseName, deleteCommand);
				}

				/*
				 * Check if a player record is found
				 */
				if (statement.getRowCount() > 0) {
					DeleteCommand delete = new DeleteCommand(tableName);


					if (getDeleteConditions() != null)
						delete.addOptions(getDeleteConditions());

					statement = Build.getDatabaseManager().execute(databaseName, delete);

					//Assuming connection is dropped. We will try again.
					if (statement == null) {
						MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
						if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
							statement = Build.getDatabaseManager().execute(databaseName, delete);
					}

					if (statement != null)
						statement.terminate();
				}
				break;
			}

			default:
				break;

		}
	}

	default void entry(String databaseName, String tableName, List<MySQLColumn> values, MySQLCommand commandType, WhereConditionOption... conditions) {

		QueryResult statement = null;

		//Checks if connected, if not it will reconnect to the database.
		if (!Build.getDatabaseManager().isConnected(databaseName)) {
			Build.connectToDatabases();
		}

		switch (commandType) {

			case UPDATE: {

				/*
				 * Construct a select command for gathering player profile
				 */
				SelectCommand command = new SelectCommand(tableName);
				if (conditions != null)
					command.addOptions(conditions);

				/*
				 * Execute the constructed select command
				 */
				statement = Build.getDatabaseManager().execute(databaseName, command);

				//Assuming connection is dropped. We will try again.
				if (statement == null) {
					MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
					if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
						statement = Build.getDatabaseManager().execute(databaseName, command);
				}

				/*
				 * Check if a player record is found
				 */
				if (statement.getRowCount() > 0) {
					UpdateCommand update = new UpdateCommand(tableName);

					for (int i = 0; i < values.size(); i++) {
						update.addOption(new TableColumnValueOption(values.get(i).getName(), values.get(i).getValue()));
					}

					if (conditions != null)
						update.addOptions(conditions);

					statement = Build.getDatabaseManager().execute(databaseName, update);

					//Assuming connection is dropped. We will try again.
					if (statement == null) {
						MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
						if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
							statement = Build.getDatabaseManager().execute(databaseName, update);
					}

					if (statement != null)
						statement.terminate();
				} else {
					InsertCommand insert = new InsertCommand(tableName);

					for (int i = 0; i < values.size(); i++) {
						insert.addOption(new TableColumnValueOption(values.get(i).getName(), values.get(i).getValue()));
					}

					statement = Build.getDatabaseManager().execute(databaseName, insert);

					//Assuming connection is dropped. We will try again.
					if (statement == null) {
						MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
						if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
							statement = Build.getDatabaseManager().execute(databaseName, insert);
					}

					if (statement != null)
						statement.terminate();
				}

				break;
			}

			case INSERT: {
				InsertCommand insert = new InsertCommand(tableName);

				for (int i = 0; i < values.size(); i++) {
					insert.addOption(new TableColumnValueOption(values.get(i).getName(), values.get(i).getValue()));
				}

				statement = Build.getDatabaseManager().execute(databaseName, insert);

				//Assuming connection is dropped. We will try again.
				if (statement == null) {
					MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
					if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
						statement = Build.getDatabaseManager().execute(databaseName, insert);
				}

				if (statement != null)
					statement.terminate();

				break;
			}

			case DELETE_FROM: {

				/*
				 * Construct a select command for gathering player profile
				 */
				SelectCommand deleteCommand = new SelectCommand(tableName);
				deleteCommand.addOptions(conditions);

				/*
				 * Execute the constructed select command
				 */
				statement = Build.getDatabaseManager().execute(databaseName, deleteCommand);

				//Assuming connection is dropped. We will try again.
				if (statement == null) {
					MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
					if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
						statement = Build.getDatabaseManager().execute(databaseName, deleteCommand);
				}

				/*
				 * Check if a player record is found
				 */
				if (statement.getRowCount() > 0) {
					DeleteCommand delete = new DeleteCommand(tableName);


					if (getDeleteConditions() != null)
						delete.addOptions(getDeleteConditions());

					statement = Build.getDatabaseManager().execute(databaseName, delete);

					//Assuming connection is dropped. We will try again.
					if (statement == null) {
						MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
						if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
							statement = Build.getDatabaseManager().execute(databaseName, delete);
					}

					if (statement != null)
						statement.terminate();
				}
				break;
			}

			default:
				break;

		}
	}


	/**
	 * @param databaseName
	 * @param tableName
	 * @param values
	 * @param commandType
	 * @throws Throwable
	 */
	default void load(String databaseName, String tableName, List<MySQLColumn> values, MySQLCommand commandType) throws Throwable {

		switch (commandType) {

			case UPDATE:

				if (!Build.getDatabaseManager().isConnected(databaseName)) {
					System.out.println("Unable to establish connection [" + databaseName + " -> " + tableName + "]");
					return;
				}
				SelectCommand select = new SelectCommand(tableName);

				if (getUpdateConditions() != null)
					select.addOptions(getUpdateConditions());

				QueryResult statement = Build.getDatabaseManager().execute(databaseName, select);

				//Assuming connection is dropped. We will try again.
				if (statement == null) {
					MySQLDatabase db = Build.getDatabaseManager().getDatabases().get(databaseName).connect();
					if (db.getStatus() == MySQLDatabase.ConnectionStatus.CONNECTED)
						statement = Build.getDatabaseManager().execute(databaseName, select);
				}

				if (statement == null || statement.getRowCount() == 0)
					Logger.getGlobal().info("Unable to gather details from datatable.");
				else {

					ResultSet result = statement.getRow();

					for (MySQLColumn module : values) {

						switch (module.getColumnType()) {

							case INTEGER:
								module.parse("" + result.getInt(module.getName()));
								break;

							case VARCHAR:
								module.parse(result.getString(module.getName()));
								break;

							case DOUBLE:
								module.parse(result.getDouble(module.getName()));
								break;

							case SHORT:
								module.parse(result.getShort(module.getName()));
								break;

							case BOOLEAN:
								module.parse(result.getBoolean(module.getName()));
								break;
						}

					}
				}

				statement.terminate();

				break;

			default:
				break;
		}
	}

}
