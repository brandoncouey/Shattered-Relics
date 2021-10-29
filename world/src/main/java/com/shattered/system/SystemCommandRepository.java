package com.shattered.system;

import com.shattered.utilities.FileUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JTlr Frost | Mar 8, 2018 : 4:36:59 PM
 */
public class SystemCommandRepository {

	/**
	 * Represents the Command Repository
	 */
	public static List<SystemCommand> HANDLED_COMMANDS = new ArrayList<SystemCommand>();

	/**
	 * Represents the Command Event Identifier.
	 */
	public static final String COMMAND_START = new String("_");

	/**
	 * Starts up the Repository and Listener
	 */
	public static void startup() {
		parse();
	}

	/**
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void parse() {
		try {
			Class<? extends SystemCommand>[] commands = FileUtility.getClasses(SystemCommandRepository.class.getPackage().getName() + ".commands");
			for (Class<? extends SystemCommand> cmd : commands) {
				if (cmd.isAnonymousClass())
					continue;
				HANDLED_COMMANDS.add(cmd.newInstance());
			}
			SystemLogger.sendSystemMessage("Successfully registered " + HANDLED_COMMANDS.size() + " System Commands...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the {@link SystemCommand} by syntax
	 *
	 * @param syntax
	 * @return {@link SystemCommand}
	 */
	public static SystemCommand forSyntax(String syntax) {
		for (SystemCommand command : HANDLED_COMMANDS) {
			for (String s : command.getSyntax()) {
				if (s.equalsIgnoreCase(syntax)) {
					return command;
				}
			}
		}
		return null;
	}

	/**
	 * @return the commands
	 */
	public static List<SystemCommand> getCommands() {
		return HANDLED_COMMANDS;
	}
}