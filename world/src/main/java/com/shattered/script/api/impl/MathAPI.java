package com.shattered.script.api.impl;

import com.shattered.engine.tasks.DelayedTaskTicker;
import com.shattered.script.ScriptManager;
import com.shattered.script.api.RelicEngineAPI;
import com.shattered.script.api.RelicMathAPI;

public class MathAPI extends RelicMathAPI {

    /**
     * Squares the value of a int
     *
     * @param value
     * @return the int value squared
     */
    @Override
    public int square(int value) {
        return value * value;
    }

    /**
     * Squares the value of a float
     * @param value
     * @return the float value squared
     */
    @Override
    public float square(final float value) {
        return value * value;
    }

    /**
     * Gets the specified percent of a specific variable
     *
     * @param percent
     * @param variable
     * @return the percent
     */
    @Override
    public int get_percent_of(final int percent, final int variable) {
        return (int) ((0.01 * percent) * variable);
    }

    /**
     * Gets the Angle of a Direction Vector
     *
     * @param x
     * @param y
     * @return the angle in degrees
     */
    @Override
    public double get_angle(final double x, final double y) {
        return (Math.atan2(y, x) / 3.141592653589793238) * 180;
    }
}
