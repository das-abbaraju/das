// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * Exception to be thrown in Service Provider Layer. NOTE: being thrown by mailutility core
 * 
 * Created on May 6, 2005
 * 
 * @author peter
 * @version $Id: ServiceProviderException.java,v 1.2 2005-08-05 21:30:44 tammy Exp $
 */
// TODO: move to mailutility core
public class ServiceProviderException
        extends Exception {

    private static final long serialVersionUID = 3341850167016515339L;

    public ServiceProviderException(String message) {
        super(message);
    }

    public ServiceProviderException(String message, Throwable cause) {
        super(message, cause);
    }

}
