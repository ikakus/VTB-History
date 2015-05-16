package com.ikakus.Card_Holder.Classes;

/**
 * Created by 404 on 5/3/2015.
 */
public class MathUtils {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
