// =======================================================
// Copyright Mylife.com Inc., 2010. All rights reserved.
//
// =======================================================

package com.klark.exception;

/**
 * 
 */
public final class ApplicationExceptionInfo {
    private final String exceptionInfo;
    private final String sender;
    private final String recipient;
    private final String title;

    public ApplicationExceptionInfo(String exceptionInfo, String recipient, String title) {
        super();
        this.exceptionInfo = exceptionInfo;
        this.recipient = recipient;
        this.title = title;
        this.sender = null;
    }

    public ApplicationExceptionInfo(String exceptionInfo, String sender, String recipient, String title) {
        super();
        this.exceptionInfo = exceptionInfo;
        this.recipient = recipient;
        this.sender = sender;
        this.title = title;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getTitle() {
        return title;
    }
}
