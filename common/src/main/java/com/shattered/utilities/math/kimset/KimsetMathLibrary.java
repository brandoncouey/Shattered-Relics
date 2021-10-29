package com.shattered.utilities.math.kimset;

public class KimsetMathLibrary {

    /**
     * Returns Value normalized to the given range.  (e.g. 20 normalized to the range 10->50 would result in 0.25)
     *
     * @param Value
     * @param rangeMin
     * @param rangeMax
     * @return
     */
    public static float normalizeToRange(float Value, float rangeMin, float rangeMax)
    {
        if (rangeMin == rangeMax)
        {
            if (Value < rangeMin)
            {
                return 0.f;
            }
            else
            {
                return 1.f;
            }
        }

        if (rangeMin > rangeMax)
        {
            float swap = rangeMin;
            rangeMin = rangeMax;
            rangeMax = swap;
        }
        return (Value - rangeMin) / (rangeMax - rangeMin);
    }
}
