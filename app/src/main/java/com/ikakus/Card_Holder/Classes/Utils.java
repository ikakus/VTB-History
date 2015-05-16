package com.ikakus.Card_Holder.Classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by 404 on 4/5/2015.
 */
public class Utils {

    public static ArrayList myReverse(ArrayList arrayList) {
        int size = arrayList.size();
        ArrayList tempList = new ArrayList(size);

        for (int i = 0, j = size - 1; i < size; i++, j--) {
            tempList.add(arrayList.get(j));

        }
        return tempList;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float getMax(float[] array) {
        float max = 0;
        for (float f : array) {
            if (f > max) {
                max = f;
            }
        }
        return max;
    }
}
