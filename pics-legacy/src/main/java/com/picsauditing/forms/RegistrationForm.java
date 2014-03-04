package com.picsauditing.forms;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.registration.RegistrationService;
import com.picsauditing.service.registration.RegistrationSubmission;

import java.util.Locale;


public class RegistrationForm {

    private AccountStatus status = AccountStatus.Pending;
    private Locale locale;
    private String city;
    private String countrySubdivision;
    private String businessName;
    private String username;
    private String address;
    private String firstName;
    private String timeZone;
    private String lastName;
    private String password;
    private String passwordConfirmation;
    private String phone;
    private String countryISOCode;
    private String zip;
    private String vatID;
    private String email;

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = new Locale(locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(String countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryISOCode() {
        return countryISOCode;
    }

    public void setCountryISOCode(String countryISOCode) {
        this.countryISOCode = countryISOCode;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getVatID() {
        return vatID;
    }

    public void setVatID(String vatID) {
        this.vatID = vatID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static RegistrationForm fromContractor(final ContractorAccount input) {
        final User user = (input.getPrimaryContact() == null) ? new User() : input.getPrimaryContact();
        final RegistrationForm form = new RegistrationForm();

        form.status = input.getStatus();
        form.address = input.getAddress();
        form.businessName = input.getName();
        form.city = input.getCity();
        form.countryISOCode = (input.getCountry() != null)
                ? input.getCountry().getIsoCode()
                : null;
        form.countrySubdivision = (input.getCountrySubdivision() != null)
                ? input.getCountrySubdivision().getSimpleName()
                : null;
        form.email = user.getEmail();
        form.firstName = user.getFirstName();
        form.lastName = user.getLastName();
        form.locale = input.getLocale();
        form.phone = input.getPhone();

        return form;
    }

    public RegistrationSubmission createSubmission(RegistrationService service) {
        final RegistrationSubmission submission = service.newRegistration();

        //TODO

        return submission;
    }
}
