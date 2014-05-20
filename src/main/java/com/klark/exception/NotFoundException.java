// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * Exception when requested item is not found
 * 
 * 
 * @author michaelc
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    private static final long serialVersionUID = 8469152963470744165L;

    public NotFoundException(String message) {
        super(message);
    }

}
