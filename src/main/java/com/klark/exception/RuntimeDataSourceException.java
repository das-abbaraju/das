// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * A runtime data source exception. "For those times when you don't want to force them to catch it"
 * 
 * @since Created on Aug 16, 2005
 * @author peter
 * @version $Id: RuntimeDataSourceException.java,v 1.5 2006-05-11 18:02:39 tammy Exp $
 */
// TODO: move to mailutility core
public class RuntimeDataSourceException
        extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RuntimeDataSourceException(Throwable cause) {
        super(cause);
    }

}
