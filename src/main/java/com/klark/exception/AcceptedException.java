// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * Exception when the request has been accepted for processing, but the processing has not been completed.
 * 
 * 
 * @author vlad
 */
public class AcceptedException extends RuntimeException {

    public AcceptedException() {
        super();
    }

    private static final long serialVersionUID = 1L;

    public AcceptedException(String message) {
        super(message);
    }

}
