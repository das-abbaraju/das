package com.picsauditing.service.registration;


import java.util.Locale;
import java.util.TimeZone;

public class RegistrationSubmission {
    private final RegistrationService parentService;
    private String contractorName;
    private String userName;
    private String password;
    private String email;
    private String userFirstName;
    private String userLastName;
    private String address;
    private String address2;
    private String countrySubdivision;
    private String zip;
    private String city;
    private String countryISO;
    private String phoneNumber;
    private TimeZone timeZone;
    private String vatID;
    private Locale locale;
    private String registrationRequestHash;

    RegistrationSubmission ( RegistrationService s ) { this.parentService = s; }

    public RegistrationResult submit() {
        return parentService.doRegistration(this);
    }


    public RegistrationSubmission setContractorName(String contractorName) {
        this.contractorName = contractorName;
        return this;
    }

    public String getContractorName() {
        return contractorName;
    }

    public RegistrationSubmission setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public RegistrationSubmission setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegistrationSubmission setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RegistrationSubmission setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
        return this;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public RegistrationSubmission setUserLastName(String userLastName) {
        this.userLastName = userLastName;
        return this;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public RegistrationSubmission setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public RegistrationSubmission setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RegistrationSubmission setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public RegistrationSubmission setCountryISO(String countryISO) {
        this.countryISO = countryISO;
        return this;
    }

    public String getCountryISO() {
        return countryISO;
    }

    public RegistrationSubmission setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public RegistrationSubmission setVatID(String vatID) {
        this.vatID = vatID;
        return this;
    }

    public String getVatID() {
        return vatID;
    }

    public RegistrationSubmission setCountrySubdivision(String countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
        return this;
    }

    public String getCountrySubdivision() {
        return countrySubdivision;
    }

    public RegistrationSubmission setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public Locale getLocale() {
        return locale;
    }

    public RegistrationSubmission setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public String getRegistrationRequestHash() {
        return registrationRequestHash;
    }

    public String getCity() {
        return city;
    }

    public RegistrationSubmission setCity(String city) {
        this.city = city;
        return this;
    }

    public RegistrationSubmission setRegistrationRequestHash(String registrationRequestHash) {
        this.registrationRequestHash = registrationRequestHash;
        return this;
    }
}
