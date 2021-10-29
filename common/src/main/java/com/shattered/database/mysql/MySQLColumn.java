package com.shattered.database.mysql;

/**
 * @author JTlr Frost | Mar 13, 2018 : 9:05:28 PM
 */
public class MySQLColumn {
	
	/** Represents the Column Name */
	private String name;
	
	/** Represents the Column Value */
	private Object value;
	
	/** Represents the Column Type */
	private MySQLColumnType type;
	
	/**
	 * @param name
	 * @param value
	 * @param type
	 */
	public MySQLColumn(String name, Object value) {
		setName(name);
		setValue(value);
		setColumnType(value);
	}
	
	/**
	 * @param data
	 */
	public void parse(Object data) {
		
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the columnType
	 */
	public MySQLColumnType getColumnType() {
		return type;
	}

	/**
	 * @param columnType the columnType to set
	 */
	public void setColumnType(Object value) {
		if (value instanceof Long)
			this.type = MySQLColumnType.VARCHAR;
		else if (value instanceof Integer)
			this.type = MySQLColumnType.INTEGER;
		else if (value instanceof Double)
			this.type = MySQLColumnType.DOUBLE;
		else if (value instanceof Short)
			this.type = MySQLColumnType.INTEGER;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
