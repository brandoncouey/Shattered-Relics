package com.shattered.script.api;

public abstract class RelicUtilityAPI {

    /**
     * Formats a string for display
     * @param toFormat
     * @return the formatted string
     */
    public abstract String format_string(String toFormat);

    /**
     * Gets the current timestamp in the specified days from now
     * @param days
     * @return the timestamp in the specified days
     */
    public abstract long timestamp_in_days(final int days);

    /**
     * Gets the current timestamp in the specified hours from now
     * @param hours
     * @return the timestamp in the specified hours
     */
    public abstract long timestamp_in_hours(final int hours);

    /**
     * Gets the current timestamp in the specified minutes from now
     * @param minutes
     * @return the timestamp in the specified minutes
     */
    public abstract long timestamp_in_minutes(final int minutes);

    /**
     * Gets the current timestamp in the specified seconds from now
     * @param seconds
     * @return the timestamp in the specified seconds
     */
    public abstract long timestamp_in_seconds(final int seconds);


}
