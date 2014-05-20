// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * Exception when requested action is not supported
 * 
 * 
 * @author michaelc
 */
public final class NotSupportedException extends RuntimeException {

    private static final long serialVersionUID = 8469152963470744165L;

    public NotSupportedException(String message) {
        super(message);
    }

}
