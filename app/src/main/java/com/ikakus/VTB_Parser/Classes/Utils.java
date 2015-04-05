package com.ikakus.VTB_Parser.Classes;

import java.util.ArrayList;

/**
 * Created by 404 on 4/5/2015.
 */
public class Utils {

    public static ArrayList myReverse(ArrayList arrayList) {
        int size = arrayList.size();
        ArrayList tempList = new ArrayList(size);

        for (int i = 0,j = size -1; i<size;i++,j-- ) {
            tempList.add(arrayList.get(j));

        }
        return tempList;
    }

}
