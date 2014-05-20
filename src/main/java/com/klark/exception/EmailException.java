// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * @author tammy
 */
public class EmailException extends Exception {

    private static final long serialVersionUID = -3667878258410698635L;

    /**
     * @param message
     */
    public EmailException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public EmailException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

}
