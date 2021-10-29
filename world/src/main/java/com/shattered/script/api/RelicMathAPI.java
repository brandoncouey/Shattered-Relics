package com.shattered.script.api;

public abstract class RelicMathAPI {

    /**
     * Squares the value of a int
     * @param value
     * @return the int value squared
     */
    public abstract int square(final int value);

    /**
     * Squares the value of a float
     * @param value
     * @return the float value squared
     */
    public abstract float square(final float value);

    /**
     * Gets the specified percent of a specific variable
     * @param percent
     * @param variable
     * @return the percent
     */
    public abstract int get_percent_of(final int percent, final int variable);


    /**
     * Gets the Angle of a Direction Vector
     * @param x
     * @param y
     * @return the angle in degrees
     */
    public abstract double get_angle(final double x, final double y);
}
