package com.shattered.utilities.ecs;


import com.shattered.account.Account;
import com.shattered.database.mysql.MySQLEntry;
import com.shattered.database.mysql.MySQLFetch;
import lombok.Getter;
import lombok.Setter;

/**
 * @author JTlr Frost | Mar 7, 2018 : 8:28:14 PM
 */
public abstract class Component implements MySQLEntry, MySQLFetch {


	//TODO State Machine for ProcessComponent.
	//Registering a stoppable - continuation for a component ticking

	//Represents the ticks in milliseconds
	@Getter @Setter
	protected long nextFrame;

	/**
	 * Represents the {@link Account} Object
	 */
	@Setter
	protected Object gameObject;

	/**
	 * Creates a new constructor setting the {@link Account}
	 * @param gameObject
	 */
	public Component(Object gameObject) {
		setGameObject(gameObject);
	}

	/**
	 * Initializes the content.
	 * Used for 'Pre-Loading' data from Storage
	 */
	public void onStart() {}

	/**
	 * Used for using the data after storage load is finished.
	 */
	public void onWorldAwake() {}

	/**
	 * Called once per world cycle per each instance.
	 * @param deltaTime
	 */
	public void onTick(long deltaTime) { }

	/**
	 * Called once Actor is Finished
	 */
	public void onFinish() {}

	/**
	 * Inserts all of the Information into the Database
	 */
	public boolean insert() {
		return false;
	}

	/**
	 * Updates all of the Information to the Database
	 */
	public boolean update() {
		return false;
	}

	/**
	 * Fetches the Information for the Component from the Database
	 */
	public boolean fetch() {
		return false;
	}

}
