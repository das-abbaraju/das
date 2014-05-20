// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.common;

/**
 * Description here!
 *
 *
 * @author
 */

import org.springframework.http.MediaType;

/**
 * interface to hold common constants
 * 
 * 
 * @author
 */

public interface WebConstants {
    static final String DOCUMENT_PREFIX = "/doc";
    static final String PUBLIC_PREFIX = "/pub";
    static final String SECURE_PREFIX = "/sec";
    static final String NO_ARGUMENT_METHOD_PATH = "/";
    static final String METHOD_PATH_BY_ID = "/{id}";
    static final String METHOD_PATH_BY_USERNUM = "/{usernum}";

    static String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    // static String APPLICATION_JS = "application/x-javascript";
    static String APPLICATION_JS = "application/javascript";
    static String APPLICATION_XML = MediaType.APPLICATION_XML_VALUE;
}
