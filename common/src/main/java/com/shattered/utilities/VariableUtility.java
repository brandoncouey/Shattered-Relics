package com.shattered.utilities;

import java.util.Random;

/**
 * @author JTlr Frost 7/22/18 : 3:34 PM
 */
public class VariableUtility {

    public static final char[] VALID_CHARS = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    /**
     *
     * @param l
     * @return string
     */
    public static final String convertLongToString(long l) {
        if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L)
            return null;
        if (l % 37L == 0L)
            return null;
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    /**
     *
     * @param toFormat
     * @return
     */
    public static String formatString(String toFormat) {
        if (toFormat == null)
            return "";
        toFormat = toFormat.replaceAll("_", " ");
        StringBuilder newName = new StringBuilder();
        boolean wasSpace = true;
        for (int i = 0; i < toFormat.length(); i++) {
            if (wasSpace) {
                newName.append(("" + toFormat.charAt(i)).toUpperCase());
                wasSpace = false;
            } else {
                newName.append(toFormat.charAt(i));
            }
            if (toFormat.charAt(i) == ' ') {
                wasSpace = true;
            }
        }
        return newName.toString();
    }

    /**
     * @param maxValue
     * @return
     */
    public static final int random(int maxValue) {
        if (maxValue <= 0) return 0;
        return new Random().nextInt(maxValue);
    }

    /**
     * @param min
     * @param max
     * @return
     */
    public static final int random(int min, int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random(n));
    }

    /**
     * Gets the specified percent of the variable
     * @param percent
     * @param variable
     * @return the percent
     */
    public static int getPercent(int percent, int variable) {
        return (int) ((0.01 * percent) * variable);
    }


}
