package com.picsauditing.service.registration;


public class RegistrationSubmission {
    private final RegistrationService parentService;
    private String contractorName;
    private String userName;
    private String password;
    private String email;
    private String userFirstName;
    private String userLastName;
    private String address;
    private String phoneNumber;
    private String zip;
    private String countryISO;
    private String timeZone;
    private String vatID;

    RegistrationSubmission (
            RegistrationService s
    ) {
        this.parentService = s;
    }

    public void submit() {
        parentService.doRegistration(this);
    }


    public void setContractorName(String contractorName) {
        this.contractorName = contractorName;
    }

    public String getContractorName() {
        return contractorName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip() {
        return zip;
    }

    public void setCountryISO(String countryISO) {
        this.countryISO = countryISO;
    }

    public String getCountryISO() {
        return countryISO;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setVatID(String vatID) {
        this.vatID = vatID;
    }

    public String getVatID() {
        return vatID;
    }
}
