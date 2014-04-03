package com.picsauditing.service.addressverifier;

import com.picsauditing.exception.PicsException;


public class AddressVerificationException extends PicsException {
    private AddressRequestHolder address;

    public AddressVerificationException(String message, Throwable cause, AddressRequestHolder address) {
        super(message, cause);
        this.address = address;
    }

    public AddressVerificationException(String statusDescription, AddressRequestHolder address) {
        super(statusDescription);
        this.address = address;
    }

    public AddressRequestHolder getAddress() {
        return address;
    }
}
