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

public class ExecutionResult {

    private int statusCode;
    private String message;
    private Object data;

    public ExecutionResult() {
        statusCode = 500;
        message = "Error";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
