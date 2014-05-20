// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * 
 */
public class GenericCheckedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public GenericCheckedException() {
    }

    /**
     * 
     *
     * @param message 
     */
    public GenericCheckedException(String message) {
        super(message);
    }

    /**
     * 
     *
     * @param cause 
     */
    public GenericCheckedException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     *
     * @param message 
     * @param cause 
     */
    public GenericCheckedException(String message, Throwable cause) {
        super(message, cause);
    }
}
