package com.picsauditing.service.addressverifier;

public class AddressVerificationException extends Exception {
    private String message;
    public AddressVerificationException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
