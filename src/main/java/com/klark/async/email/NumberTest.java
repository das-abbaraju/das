// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.async.email;

import java.util.HashMap;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class NumberTest {

    final private static String[] units = { "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen" };

    static HashMap<Integer, String> tensMap = new HashMap<Integer, String>();

    public void setTensMapVal() {
        tensMap.put(2, "twenty");
        tensMap.put(3, "thirty");
        tensMap.put(4, "fourty");
        tensMap.put(5, "fifty");
        tensMap.put(6, "sixty");
        tensMap.put(7, "sevnety");
        tensMap.put(8, "eighty");
        tensMap.put(9, "ninety");
    }

    public static String convert(Integer i) {
        //
        if (i < 20)
            return units[i];
        if (i < 100)
            return tensMap.get(i / 10) + ((i % 10 > 0) ? " " + convert(i % 10) : "");
        if (i < 1000)
            return units[i / 100] + " Hundred" + ((i % 100 > 0) ? " and " + convert(i % 100) : "");
        if (i < 1000000)
            return convert(i / 1000) + " Thousand " + ((i % 1000 > 0) ? " " + convert(i % 1000) : "");
        return convert(i / 1000000) + " Million " + ((i % 1000000 > 0) ? " " + convert(i % 1000000) : "");
    }

    public static void main(String s[]) {
        NumberTest test = new NumberTest();
        test.setTensMapVal();
        System.out.println(convert(25));
    }
}