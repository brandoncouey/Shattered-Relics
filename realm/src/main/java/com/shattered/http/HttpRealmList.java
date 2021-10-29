package com.shattered.http;

import com.shattered.system.SystemLogger;

import java.net.URL;
import java.util.Scanner;

/**
 * @author JTlr Frost - 11/22/2018 - 10:16 PM
 */
public class HttpRealmList {

    public static void fetch() {
        try {
            URL remote = new URL("http://127.0.0.1/realmlist.php");
            Scanner scanner = new Scanner(remote.openStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                //Adds exceptions to not be read.
                if (line.startsWith("//") || line.startsWith("#"))
                    line = scanner.nextLine();
                String[] attributes = line.split(",");
                int id = Integer.parseInt(attributes[0].split("=")[1]);
                String name = attributes[1].split("=")[1];
                String type = attributes[2].split("=")[1];
                String level = attributes[3].split("=")[1];
                String country = attributes[4].split("=")[1];
            }
            SystemLogger.sendSystemMessage("Successfully fetched realm list.");
        } catch (Exception e) {
            SystemLogger.sendSystemErrMessage("Failed to fetch realm list.");
        }

    }
}
