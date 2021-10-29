package com.shattered.system;

import com.shattered.database.DatabaseService;
import com.shattered.utilities.TimeUtility;

/**
 * @author JTlr Frost 7/15/18 : 1:20 AM
 */
public class SystemLogger {

    /**
     * @param message
     */
    public static void sendSystemMessage(String message) {
        System.out.println("System Message -> [Message='" + message + "', Time=" + TimeUtility.getYMDHMS() + "]");
    }

    /**
     * @param message
     */
    public static void sendSystemErrMessage(String message) {
        System.err.println("System Err Message -> [Message='" + message + "', Time=" + TimeUtility.getYMDHMS() + "]");
    }

    /**
     *
     * @param databaseService
     * @param message
     */
    public static void sendDatabaseInformation(DatabaseService databaseService, String message) {
        System.out.println("Database Information -> [Service='" + databaseService.name() + "']. [Message='" + message + "', Time=" + TimeUtility.getYMDHMS() + "]");
    }

    /**
     * @param databaseService
     * @param message
     */
    public static void sendDatabaseWarning(DatabaseService databaseService, String message) {
        System.out.println("Database Warning -> [Service='" + databaseService.name() + "']. [Message='" + message + "', Time=" + TimeUtility.getYMDHMS() + "]");
    }

    /**
     *
     * @param databaseService
     * @param message
     */
    public static void sendDatabaseErr(DatabaseService databaseService, String message) {
        System.err.println("Database Err -> [Service='" + databaseService.name() + "']. [Message='" + message + "', Time=" + TimeUtility.getYMDHMS() + "]");
    }


}
