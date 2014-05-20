// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * Exception when the request could not be completed due to a conflict with the
 * current state of the resource, i.e. an attempt to create already existing
 * resource.
 * 
 * 
 * @author vlad
 */
public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException() {
        super();
    }

    private static final long serialVersionUID = 1L;

    public ResourceConflictException(String message) {
        super(message);
    }

}
