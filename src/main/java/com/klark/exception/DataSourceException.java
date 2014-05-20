// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

import java.sql.SQLException;

/**
 * 
 */
public class DataSourceException extends Exception {
    private static final long serialVersionUID = -2267726936110868766L;
    private static final int ERROR_CODE_CONSTRAINT_VIOLATION = 1;
    private static final String SQL_STATE_CONSTRAINT_VIOLATION = "23000";

    private int errCode = -1;
    private String sqlState = "";

    public DataSourceException(String message) {
        super(message);
    }

    public DataSourceException(String message, Throwable cause) {
        super(message, cause);
        setCause(cause);
    }

    public DataSourceException(Throwable cause) {
        super(cause);
        setCause(cause);
    }

    private void setCause(Throwable cause) {
        if (cause instanceof SQLException) {
            errCode = ((SQLException) cause).getErrorCode();
            sqlState = ((SQLException) cause).getSQLState();
        }
    }

    /**
     * Checks for constraint violation errors
     * 
     * @return boolean
     */
    public boolean isCausedByConstraintViolation() {
        return errCode == ERROR_CODE_CONSTRAINT_VIOLATION && sqlState.equals(SQL_STATE_CONSTRAINT_VIOLATION);
    }
}
