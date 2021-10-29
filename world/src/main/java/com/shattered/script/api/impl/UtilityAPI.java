package com.shattered.script.api.impl;

import com.shattered.script.api.RelicUtilityAPI;
import com.shattered.utilities.TimeUtility;
import com.shattered.utilities.VariableUtility;

public class UtilityAPI extends RelicUtilityAPI {

    /**
     * Formats a string for display
     *
     * @param toFormat
     * @return the formatted string
     */
    @Override
    public String format_string(String toFormat) {
        return VariableUtility.formatString(toFormat);
    }

    /**
     * Gets the current timestamp in the specified days from now
     *
     * @param days
     * @return the timestamp in the specified days
     */
    @Override
    public long timestamp_in_days(int days) {
        return TimeUtility.longToDays(days);
    }

    /**
     * Gets the current timestamp in the specified hours from now
     *
     * @param hours
     * @return the timestamp in the specified hours
     */
    @Override
    public long timestamp_in_hours(int hours) {
        return TimeUtility.longToHours(hours);
    }

    /**
     * Gets the current timestamp in the specified minutes from now
     *
     * @param minutes
     * @return the timestamp in the specified minutes
     */
    @Override
    public long timestamp_in_minutes(int minutes) {
        return TimeUtility.longToMinutes(minutes);
    }

    /**
     * Gets the current timestamp in the specified seconds from now
     *
     * @param seconds
     * @return the timestamp in the specified seconds
     */
    @Override
    public long timestamp_in_seconds(int seconds) {
        return TimeUtility.longToSeconds(seconds);
    }
}
