package com.picsauditing.service.addressverifier;

import com.picsauditing.service.addressverifier.builder.AddressResponseHolderBuilder;

public class AddressResponseHolder {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateOrProvince;
    private String zipOrPostalCode;
    private String country;
    private ResultStatus resultStatus;
    public String statusDescription;
    private String confidencePercent;
    private String formattedAddressLines;

    @Override
    public String toString() {
        return "AddressResponseHolder{" +
                "addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", stateOrProvince='" + stateOrProvince + '\'' +
                ", zipOrPostalCode='" + zipOrPostalCode + '\'' +
                ", country='" + country + '\'' +
                ", resultStatus=" + resultStatus +
                ", statusDescription='" + statusDescription + '\'' +
                ", confidencePercentage='" + confidencePercent + '\'' +
                '}';
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
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

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public void setConfidencePercent(String confidencePercent) {
        this.confidencePercent = confidencePercent;
    }

    public String getConfidencePercent() {
        return confidencePercent;
    }

    public String getFormattedAddressLines() {
        return formattedAddressLines;
    }

    public void setFormattedAddressLines(String formattedAddressLines) {
        this.formattedAddressLines = formattedAddressLines;
    }

    public static AddressResponseHolderBuilder builder() {
        return new AddressResponseHolderBuilder();
    }
}
