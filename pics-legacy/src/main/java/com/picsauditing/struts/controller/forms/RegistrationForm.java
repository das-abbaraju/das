package com.picsauditing.struts.controller.forms;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.registration.RegistrationService;
import com.picsauditing.service.registration.RegistrationSubmission;
import com.picsauditing.struts.validator.constraints.UniqueContractorName;
import com.picsauditing.struts.validator.constraints.UniqueUserName;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Locale;


public class RegistrationForm {
    private static final String SPECIAL_CHAR_REGEX = "(?s).*[;<>`\"].*";
    private static final String USERNAME_REGEX = "[\\w+._@-]+";
    private static final int MAX_STRING_LENGTH_50 = 50;
    private static final String PHONE_NUMBER_REGEX_WITH_ASTERISK = "^(\\+?(?:\\(?[0-9]\\)?[-. ]{0,2}){9,14}[0-9])((\\s){0,4}(\\*|(?i)x|(?i)ext)(\\s){0,4}[\\d]{1,5})?$";


    private AccountStatus status = AccountStatus.Pending;
    private Locale locale;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH_50)
    @Pattern(regexp = SPECIAL_CHAR_REGEX)
    private String city;

    //TODO Custom Validation Constraint for countrySubdivision
    private String countrySubdivision;


    @NotBlank
    @UniqueContractorName
    private String businessName;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH_50)
    @Pattern(regexp = USERNAME_REGEX)
    @UniqueUserName
    private String username;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH_50)
    @Pattern(regexp = SPECIAL_CHAR_REGEX)
    private String address;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH_50)
    @Pattern(regexp = SPECIAL_CHAR_REGEX)
    private String firstName;

    @NotNull
    private String timeZone;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH_50)
    @Pattern(regexp = SPECIAL_CHAR_REGEX)
    private String lastName;

    //TODO - custom validator -
    // cannot be username
    // must contain letters & numbers
    // cannot be current password
    private String password;

    //TODO - check that it matches entered password
    private String passwordConfirmation;

    @Pattern(regexp = PHONE_NUMBER_REGEX_WITH_ASTERISK)
    private String phone;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH_50)
    @Pattern(regexp = SPECIAL_CHAR_REGEX)
    private String countryISOCode;

    @NotBlank
    @Length(max = MAX_STRING_LENGTH_50)
    @Pattern(regexp = SPECIAL_CHAR_REGEX)
    private String zip;

    //TODO - tax validator
    private String vatID;

    @NotNull
    @Email
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
