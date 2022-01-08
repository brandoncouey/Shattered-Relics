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
     * Gets a timestamp forward of the current time determined by the specified unit
     * @param unit
     * @param amount
     * @return the timestamp set to the desired unit of time ahead
     */
    public static long duration(TimeUnit unit, int amount) {
        switch (unit) {
            case DAYS:
                return longToDays(amount);
            case HOURS:
                return longToHours(amount);
            case MINUTES:
                return longToMinutes(amount);
            case SECONDS:
                return longToSeconds(amount);
        }
        return -1;
    }

    /**
     * Converts a long to Amount of Days
     * @param days
     * @return the timestamp set into the desired amount of days ahead.
     */
    public static long longToDays(int days) {
        return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * days);
    }

    /**
     * Converts a long to Amount of Hours
     * @param hours
     * @return the timestamp set into the desired amount of hours ahead
     */
    public static long longToHours(int hours) {
        return System.currentTimeMillis() + (1000 * 60 * 60 * hours);
    }

    /**
     * Converts a long into Amount of Minutes
     * @param minutes
     * @return the timestamp into the desired amount of minutes ahead
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

    /**
     * Converts miliseconds to seconds
     * @param time
     * @return the converted ms to seconds
     */
    public static float convertMilisToSeconds(long time) {
        long remaining = (time - System.currentTimeMillis());
        return (remaining / 1000);
    }

    /**
     * Converts the miliseconds to seconds
     * @param from
     * @param to
     * @return the converted ms to seconds
     */
    public static float convertMilisToSeconds(long from, long to) {
        long remaining = (from - to);
        return (int) (remaining / 1000);
    }

    /**
     * Gets a previous time and calculates how many years ago it was from the current time
     * @param time
     * @return the amount of years
     */
    public static int getYearsFromTime(long time) {
        return getMonthsFromTime(time) / 12;
    }

    /**
     * Gets a previous time and calculates how many months ago it was from the current time
     * @param time
     * @return the amount of months
     */
    public static int getMonthsFromTime(long time) {
        return getDaysFromTime(time) / 30;
    }

    /**
     * Gets a previous time and calculates how many days ago it was from the current time
     * @param time
     * @return the amount of days
     */
    public static int getDaysFromTime(long time) {
        return getHoursFromTime(time) / 24;
    }

    /**
     * Gets a previous time and calculates how many hours ago it was from the current time
     * @param time
     * @return the amount of hours
     */
    public static int getHoursFromTime(long time) {
        long timeOffline = System.currentTimeMillis() - time;
        return (int) (timeOffline / 3_600_000);
    }

    /**
     * Gets a previous time and calculates how many minutes it was from the current time
     * @param time
     * @return the amount of minutes
     */
    public static int getMinutesFromTime(long time) {
        long timeOffline = System.currentTimeMillis() - time;
        int hours = getHoursFromTime(time);
        return (int) (hours == 0 ? (timeOffline / 60_000) : ((timeOffline - 3_600_000 * hours) / 60_000));
    }

    /**
     * Gets the highest possible time of hour, day, or minutes that is > 1 and will convert
     *      it to a message of 'x days' or 'y hours' or 'z minutes'
     * @param time
     * @return the highest time unit to a string
     */
    public static String getHighestTimeOnly(long time) {
        int days = getDaysFromTime(time);
        int hours = getHoursFromTime(time);
        int minutes = getMinutesFromTime(time);
        int months = getMonthsFromTime(time);
        int years = getYearsFromTime(time);
        if (years > 0)
            return years + (years == 1 ? " year" : " years");
        else if (months > 0)
            return months + (months == 1 ? " month" : " months");
        else if (days > 0)
            return days + (days == 1 ? " day" : " days");
        else if (hours > 0)
            return hours + (hours == 1 ? " hour" : " hours");

        if (minutes == 0)
            return "less than a minute";
        return minutes + (minutes == 1 ? " minute" : " minutes");
    }

}
