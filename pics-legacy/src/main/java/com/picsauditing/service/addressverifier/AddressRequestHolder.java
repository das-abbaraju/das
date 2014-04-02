package com.picsauditing.service.addressverifier;

import com.picsauditing.service.addressverifier.builder.AddressRequestHolderBuilder;

public class AddressRequestHolder {
    private String addressBlob;
    private String zipOrPostalCode;
    private String country;

    @Override
    public String toString() {
        return "AddressRequestHolder{" +
                "addressBlob='" + addressBlob + '\'' +
                ", zipOrPostalCode='" + zipOrPostalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public String getAddressBlob() {
        return addressBlob;
    }

    public void setAddressBlob(String addressBlob) {
        this.addressBlob = addressBlob;
    }

    public String getZipOrPostalCode() {
        return zipOrPostalCode;
    }

    public void setZipOrPostalCode(String zipOrPostalCode) {
        this.zipOrPostalCode = zipOrPostalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public static AddressRequestHolderBuilder builder() {
        return new AddressRequestHolderBuilder();
    }
}
