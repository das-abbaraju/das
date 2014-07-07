package com.picsauditing.service.addressverifier.builder;

import com.picsauditing.service.addressverifier.AddressResponseHolder;
import com.picsauditing.service.addressverifier.ResultStatus;

public class AddressResponseHolderBuilder {
    private AddressResponseHolder addressResponseHolder = new AddressResponseHolder();


    public AddressResponseHolder build() {
        return addressResponseHolder;
    }

    public AddressResponseHolderBuilder address(String addressBlob) {
        addressResponseHolder.setAddressLine1(addressBlob);
        return this;
    }

    public AddressResponseHolderBuilder country(String country) {
        addressResponseHolder.setCountry(country);
        return this;
    }

    public AddressResponseHolderBuilder zipCode(String zipCode) {
        addressResponseHolder.setZipOrPostalCode(zipCode);
        return this;
    }

    public AddressResponseHolderBuilder addressLine1(String addressLine1) {
        addressResponseHolder.setAddressLine1(addressLine1);
        return this;
    }

    public AddressResponseHolderBuilder addressLine2(String addressLine2) {
        addressResponseHolder.setAddressLine2(addressLine2);
        return this;
    }

    public AddressResponseHolderBuilder city(String city) {
        addressResponseHolder.setCity(city);
        return this;
    }

    public AddressResponseHolderBuilder stateOrProvince(String stateOrProvince) {
        addressResponseHolder.setStateOrProvince(stateOrProvince);
        return this;
    }

    public AddressResponseHolderBuilder zipOrPostalCode(String zipOrPostalCode) {
        addressResponseHolder.setZipOrPostalCode(zipOrPostalCode);
        return this;
    }

    public AddressResponseHolderBuilder resultStatus(ResultStatus resultStatus) {
        addressResponseHolder.setResultStatus(resultStatus);
        return this;
    }

    public AddressResponseHolderBuilder statusDescription(String statusDescription) {
        addressResponseHolder.setStatusDescription(statusDescription);
        return this;
    }

    public AddressResponseHolderBuilder confidencePercentage(String confidencePercentage) {
        addressResponseHolder.setConfidencePercent(confidencePercentage);
        return this;
    }

    public AddressResponseHolderBuilder formattedAddressLines(String formattedAddressLines) {
        addressResponseHolder.setFormattedAddressLines(formattedAddressLines);
        return this;
    }
}
