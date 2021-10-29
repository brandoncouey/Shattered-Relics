package com.shattered.database.mysql;

public enum MySQLCommand {
	
	/**
	 * An insert query.
	 */
	INSERT("INSERT INTO `<table>` (<column>) VALUES (<value>) "),
	
	/**
	 * Clear table request.
	 */
	DELETE_FROM("DELETE FROM `<table>` WHERE <condition>"),

	/**
	 * Empty/Truncate a Table
	 */
	TRUNCATE("TRUNCATE `<table>`"),
	
	/**
	 * Select all columns request.
	 */
	SELECT_ALL("SELECT * FROM `<table>` WHERE <condition>"),
	
	/**
	 * Update request.
	 */
	UPDATE("UPDATE `<table>` SET <data> WHERE <condition>");
	
	/**
	 * The query itself.
	 */
	private String query;
	
	/**
	 * Constructs an SQL Query.
	 * 
	 * @param query
	 * 			The request to query.
	 */
	MySQLCommand(String query) {
		this.setQuery(query);
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

}