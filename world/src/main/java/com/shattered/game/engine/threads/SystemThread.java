package com.shattered.game.engine.threads;


import com.shattered.BuildWorld;
import com.shattered.system.SystemCommand;
import com.shattered.system.SystemCommandRepository;
import com.shattered.system.SystemLogger;
import com.shattered.utilities.TimeUtility;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author JTlr Frost | Apr 28, 2018 : 12:15:32 PM
 */
public final class SystemThread extends Thread {

	/**
	 * Creates a new World Thread
	 */
	public SystemThread() {
		setPriority(Thread.MIN_PRIORITY);
		setName("System Thread");
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		while (!BuildWorld.getInstance().getEngine().isShuttingDown()) {
			try {
				//Listens for the system commands
				try {
					Scanner scanner = new Scanner(System.in);
					if (scanner.hasNext()) {
						String line = scanner.nextLine();
						String[] args = line.split(" ");
						SystemCommand command = SystemCommandRepository.forSyntax(args[0]);
						if (command != null)
							command.execute(args);
						else if (!line.isEmpty())
							SystemLogger.sendSystemErrMessage("'" + line + "' is not recognized as an internal or external command.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Throwable e) {
				e.printStackTrace();
			}


		}
	}

}
