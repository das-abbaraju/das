package com.picsauditing.user.controller;


import java.util.HashMap;

/**
 * Created by dasabbaraju on 5/8/14.
 */
public class NumberTest {


    public static String convert(long i) {
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
        if (i<1000000000){
            return convert(i / 1000000) + " Million " + ((i % 1000000 > 0) ? " " + convert(i % 1000000) : "");
        }
        if(i<1000000000000L){
            return convert(i / 1000000000) + " Billion " + ((i % 1000000000 > 0) ? " " + convert(i % 1000000000) : "");
        }
        if(i<1000000000000000L) {
            return convert(i / 1000000000000L) + " Trillion " + ((i % 1000000000000L > 0) ? " " + convert(i % 1000000000000L) : "");
        }
        return convert(i / 1000000000000000L) + " Quadrillion " + ((i % 1000000000000000L > 0) ? " " + convert(i % 1000000000000000L) : "");

    }

    private static HashMap<Long,String> getUnitsMap() {
        HashMap<Long,String> unitsMap = new HashMap<Long, String>();
        unitsMap.put(0L,"Zero");
        unitsMap.put(1L,"One");
        unitsMap.put(2L,"Two");
        unitsMap.put(3L,"Three");
        unitsMap.put(4L,"Four");
        unitsMap.put(5L,"Five");
        unitsMap.put(6L,"Six");
        unitsMap.put(7L,"Seven");
        unitsMap.put(8L,"Eight");
        unitsMap.put(9L,"Nine");
        unitsMap.put(10L,"Ten");
        unitsMap.put(11L,"Eleven");
        unitsMap.put(12L,"Twelve");
        unitsMap.put(13L,"Thirteen");
        unitsMap.put(14L,"Fourteen");
        unitsMap.put(15L,"Fifteen");
        unitsMap.put(16L,"Sixteen");
        unitsMap.put(17L,"Seventeen");
        unitsMap.put(18L,"Eighteen");
        unitsMap.put(19L,"Nineteen");
        return unitsMap;

    }

    private static HashMap<Long,String> getTensMap() {
        HashMap<Long,String> tensMap = new HashMap<Long, String>();
        tensMap.put(2L,"Twenty");
        tensMap.put(3L,"Thirty");
        tensMap.put(4L,"Forty");
        tensMap.put(5L,"Fifty");
        tensMap.put(6L,"Sixty");
        tensMap.put(7L,"Seventy");
        tensMap.put(8L,"Eighty");
        tensMap.put(9L,"Ninety");
        return tensMap;

    }

    public static void main(String s[]) {

        System.out.println(convert(5499229198533333L));
    }
}