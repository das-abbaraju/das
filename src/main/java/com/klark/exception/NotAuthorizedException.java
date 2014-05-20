// =======================================================
// Copyright Mylife.com Inc., 2011. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class NotAuthorizedException extends RuntimeException {

    private static final long serialVersionUID = -6028649875333691035L;

    public NotAuthorizedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NotAuthorizedException(final Throwable cause) {
        super(cause);
    }

    public NotAuthorizedException(final String message) {
        super(message);
    }

    public NotAuthorizedException() {
        super();
    }

}
