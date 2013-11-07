package com.picsauditing.service.addressverifier;

public abstract class AddressVerificationService {
    abstract public AddressHolder verify(AddressHolder address) throws AddressVerificationException;
}
