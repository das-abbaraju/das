// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * EncryptionException
 * 
 * @author peter
 * @since July 21, 2005
 * @version $Id: EncryptionException.java,v 1.5 2006-05-02 22:04:44 ben Exp $
 */
public class EncryptionException extends RuntimeException {

    private static final long serialVersionUID = -6344438089915278178L;

    /**
     * @param cause
     */
    public EncryptionException(Throwable cause) {
        super(cause);
    }

}
