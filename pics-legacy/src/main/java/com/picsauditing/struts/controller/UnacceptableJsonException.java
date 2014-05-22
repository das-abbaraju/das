package com.picsauditing.struts.controller;

import java.io.IOException;

public class UnacceptableJsonException extends IOException {

    public UnacceptableJsonException() {
        super();
    }

    public UnacceptableJsonException(String message, Exception cause) {
        super(message, cause);
    }

    public UnacceptableJsonException(String message) {
        super(message);
    }

}
