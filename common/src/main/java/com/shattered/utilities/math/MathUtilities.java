package com.shattered.utilities.math;

public class MathUtilities {

    /**
     * Squares the Value
     * @param value
     * @return the int value squared
     */
    public static int square(int value) {
        return value * value;
    }

    /**
     * Squares the value of a float
     * @param value
     * @return the float value squared
     */
    public static float square(float value) {
        return value * value;
    }

    /**
     * Squares the value of a double
     * @param value
     * @return the value squared
     */
    public static double square(double value) {
        return value * value;
    }

    /**
     * Gets the Angle of a Direction Vector
     * @param x
     * @param y
     * @return the angle in degrees
     */
    public static double getAngle(final double x, final double y) {
        return (Math.atan2(y, x) / 3.141592653589793238) * 180;
    }

    /**
     * Gets the percent
     * @param percent
     * @param variable
     * @return
     */
    public static int getPercentOf(final int percent, final int variable) {
        return (int) ((0.01 * percent) * variable);
    }

}
