package com.shattered.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author JTlr Frost 7/15/18 : 1:24 AM
 */
public class TimeUtility {

    /**
     * Gets the Year Month Day Hour Minute Second Format
     * @return date ymd hms
     */
    public static String getYMDHMS() {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return format.format(date);
    }

    /**
     *
     * @param unit
     * @param duration
     * @return
     */
    public static long duration(TimeUnit unit, int duration) {
        switch (unit) {
            case DAYS:
                return longToDays(duration);
            case HOURS:
                return longToHours(duration);
            case MINUTES:
                return longToHours(duration);
            case SECONDS:
                return longToSeconds(duration);
        }
        return -1;
    }

    /**
     * Converts a long to Amount of Days
     * @param days
     * @return
     */
    public static long longToDays(int days) {
        return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * days);
    }

    /**
     * Converts a long to Amount of Hours
     * @param hours
     * @return
     */
    public static long longToHours(int hours) {
        return System.currentTimeMillis() + (1000 * 60 * 60 * hours);
    }

    /**
     * Converts a long into Amount of Minutes
     * @param minutes
     * @return
     */
    public static long longToMinutes(int minutes) {
        return System.currentTimeMillis() + (1000 * 60 * minutes);
    }

    /**
     * Converts a long into Amount of Seconds
     * @param seconds
     * @return
     */
    public static long longToSeconds(int seconds) {
        return System.currentTimeMillis() + (1000 * seconds);
    }

    public static long longToSeconds(float seconds) {
        return (long) (System.currentTimeMillis() + (1000 * seconds));
    }

    public static float convertMilisToSeconds(long target) {
        long remaining = (target - System.currentTimeMillis());
        return (remaining / 1000);
    }

    public static float convertMilisToSeconds(long target, long fromTime) {
        long remaining = (target - fromTime);
        return (int) (remaining / 1000);
    }

}
