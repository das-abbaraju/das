// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.message.service;

import com.klark.user.model.UserMessage;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class Test<T extends UserMessage> {

    private T t;

    public void set(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public String getString() {
        return t.getClass().toString();
    }

    public <U extends Number> void inspect(U u) {
        System.out.println("T: " + t.getClass().getName());
        System.out.println("U: " + u.getClass().getName());
    }

    public static void main(String[] args) {
        Test<UserMessage> integerBox = new Test<UserMessage>();
        // integerBox.set(new Integer(10));
        // integerBox.inspect(null); // error: this is still String!
    }
}