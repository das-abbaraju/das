package com.newcircle.classes;

import java.util.HashMap;

/**
 * Created by dasabbaraju on 02/12/14.
 */
public class H {

    public static final String T = "t";
    H(){
       // T = "t"
    }

    public static void main(String s[]){
       // System.out.println(indexFor(323444422,10000));

        String a = "abcd";
         a = "abcd2";

        System.out.println(a == a); //True

        Object o1 = new Object();
        Object o2 = new Object();
        System.out.println(o1.hashCode());
        System.out.println(o2.hashCode());
        HashMap m = new HashMap();
        m.put(o1,1);
        m.put(o2,2);
        System.out.println(T.hashCode());
        System.out.println(T.hashCode());
        System.out.println(m.keySet());


    }

    static int indexFor(int h, int length) {
        // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
        return h & (length-1);
    }
}
