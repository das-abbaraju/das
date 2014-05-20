// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * 
 */
public class GenericUncheckedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public GenericUncheckedException() {
    }

    /**
     * 
     *
     * @param message 
     */
    public GenericUncheckedException(String message) {
        super(message);
    }

    /**
     * 
     *
     * @param cause 
     */
    public GenericUncheckedException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     *
     * @param message 
     * @param cause 
     */
    public GenericUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }
}
