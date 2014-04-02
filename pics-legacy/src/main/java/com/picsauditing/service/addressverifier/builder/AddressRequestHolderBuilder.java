package com.picsauditing.service.addressverifier.builder;

import com.picsauditing.service.addressverifier.AddressRequestHolder;

public class AddressRequestHolderBuilder {

    private AddressRequestHolder addressRequestHolder = new AddressRequestHolder();

    public AddressRequestHolderBuilder addressBlob(String addressBlob) {
        addressRequestHolder.setAddressBlob(addressBlob);
        return this;
    }

    public AddressRequestHolderBuilder country(String countryISOCode) {
        addressRequestHolder.setCountry(countryISOCode);
        return this;
    }

    public AddressRequestHolderBuilder zipCode(String zip) {
        addressRequestHolder.setZipOrPostalCode(zip);
        return this;
    }

    public AddressRequestHolder build() {
        return addressRequestHolder;
    }
}
