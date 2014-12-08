package com.picsauditing.actions.customerservice;

import java.util.HashMap;

/**
 * Created by dasabbaraju on 5/8/14.
 */
public class NumberTest {


    public static String convert(Integer i) {
        //
        if (i < 20) {
            return getUnitsMap().get(i);
        }
        if (i < 100) {
            return getTensMap().get(i / 10) + ((i % 10 > 0) ? " " + convert(i % 10) : "");
        }
        if (i < 1000) {
            return getUnitsMap().get(i / 100) + " Hundred" + ((i % 100 > 0) ? " and " + convert(i % 100) : "");
        }
        if (i < 1000000) {
            return convert(i / 1000) + " Thousand " + ((i % 1000 > 0) ? " " + convert(i % 1000) : "");
        }

        return convert(i / 1000000) + " Million " + ((i % 1000000 > 0) ? " " + convert(i % 1000000) : "");
    }

    private static HashMap<Integer,String> getUnitsMap() {
        HashMap<Integer,String> unitsMap = new HashMap<Integer, String>();
        unitsMap.put(0,"Zero");
        unitsMap.put(1,"One");
        unitsMap.put(2,"Two");
        unitsMap.put(3,"Three");
        unitsMap.put(4,"Four");
        unitsMap.put(5,"Five");
        unitsMap.put(6,"Six");
        unitsMap.put(7,"Seven");
        unitsMap.put(8,"Eight");
        unitsMap.put(9,"Nine");
        unitsMap.put(10,"Ten");
        unitsMap.put(11,"Eleven");
        unitsMap.put(12,"Twelve");
        unitsMap.put(13,"Thirteen");
        unitsMap.put(14,"Fourteen");
        unitsMap.put(15,"Fifteen");
        unitsMap.put(16,"Sixteen");
        unitsMap.put(17,"Seventeen");
        unitsMap.put(18,"Eighteen");
        unitsMap.put(19,"Nineteen");
        return unitsMap;

    }

    private static HashMap<Integer,String> getTensMap() {
        HashMap<Integer,String> tensMap = new HashMap<Integer, String>();
        tensMap.put(2,"Twenty");
        tensMap.put(3,"Thirty");
        tensMap.put(4,"Forty");
        tensMap.put(5,"Fifty");
        tensMap.put(6,"Sixty");
        tensMap.put(7,"Seventy");
        tensMap.put(8,"Eighty");
        tensMap.put(9,"Ninety");
        return tensMap;

    }

    public static void main(String s[]) {

        System.out.println(convert(99999));
    }
}