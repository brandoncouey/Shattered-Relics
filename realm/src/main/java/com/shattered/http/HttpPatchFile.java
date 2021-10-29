package com.shattered.http;

import com.shattered.system.SystemLogger;

import java.net.URL;
import java.util.Scanner;

/**
 * @author JTlr Frost - 11/22/2018 - 10:16 PM
 */
public class HttpPatchFile {

    public static void fetch() {
        try {
            URL remote = new URL("http://127.0.0.1/patch.php");
            Scanner scanner = new Scanner(remote.openStream());


            while (scanner.hasNext()) {

                String line = scanner.nextLine();
                //Adds exceptions to not be read.
                if (line.startsWith("//") || line.startsWith("#"))
                    line = scanner.nextLine();

                String[] arguments = line.split("=");
                String key = arguments[0];
                String value = arguments[1];

                //SystemLogger.sendSystemMessage("Locale: [" + key + "=" + value + "]");
            }
            SystemLogger.sendSystemMessage("Successfully fetched patch conf files.");
        } catch (Exception e) {
            SystemLogger.sendSystemErrMessage("Failed to fetch http patch conf files.");
        }
    }
}
