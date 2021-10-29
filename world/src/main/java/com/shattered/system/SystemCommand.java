package com.shattered.system;


import com.shattered.system.SystemLogger;

/**
 * @author JTlr Frost | Mar 8, 2018 : 4:27:07 PM
 */
public interface SystemCommand {

	/**
	 * @return the Syntaxs
	 */
	String[] getSyntax();
	
	/**
	 * @param args
	 * @return
	 */
	boolean execute(String... args);

	/**
	 * @param syntax
	 */
	default void argsErr(String syntax) {
		SystemLogger.sendSystemErrMessage("Arguments are not recognized for " + syntax + " system command.");
	}
	

}
