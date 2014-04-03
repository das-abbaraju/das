package com.picsauditing.service.addressverifier;

public abstract class AddressVerificationService {
    public abstract AddressResponseHolder verify(AddressRequestHolder addressRequestHolder) throws AddressVerificationException;
}
