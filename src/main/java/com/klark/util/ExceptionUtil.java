// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.util;

import org.apache.commons.lang.StringUtils;

/**
 * ExceptionUtil.java - Created by tammy on Oct 5, 2004
 * 
 * @author tammy
 * @version $Id: ExceptionUtil.java,v 1.8 2006-04-03 18:12:27 peter Exp $
 */
public class ExceptionUtil {

    /**
     * Returns name, message, and backtrace of the Throwable t as a String analoguos to what java.lang.Throwable.printStackTrace() prints to the standard error
     * stream.
     * 
     * @param t
     *            The Throwable to trace.
     * @return the text as <b>String</b>
     */
    public static String getStackTraceString(Throwable e) {
        StackTraceElement[] st = e.getStackTrace();
        StringBuffer traceStr = new StringBuffer();
        boolean repeat = false;
        do {
            repeat = e.getCause() != null;
            traceStr.append(e.getClass().getName());
            if (StringUtils.isNotBlank(e.getMessage())) {
                traceStr.append("\n");
                traceStr.append(e.getMessage());
            }
            for (int i = 0; i < st.length; i++) {
                traceStr.append("\n  ");
                traceStr.append(st[i].toString());
            }
            if (st.length == 0) {
                traceStr.append("\n");
            }
            if (repeat) {
                traceStr.append("\nCaused by: ");
                e = e.getCause();
            }
        } while (repeat);
        return traceStr.append("\n").toString();
    }
}
