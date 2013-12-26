package com.picsauditing.service.addressverifier;

import com.picsauditing.exception.PicsException;


public class AddressVerificationException extends PicsException {
    private AddressHolder address;

    public AddressVerificationException(String message, Throwable cause, AddressHolder address) {
        super(message, cause);
        this.address = address;
    }

    public AddressVerificationException(String statusDescription, AddressHolder address) {
        super(statusDescription);
        this.address = address;
    }

    public AddressHolder getAddress() {
        return address;
    }
}
