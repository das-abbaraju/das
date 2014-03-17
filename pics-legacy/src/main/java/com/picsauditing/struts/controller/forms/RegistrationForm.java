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
import java.util.TimeZone;


public class RegistrationForm {
    private static final String SPECIAL_CHAR_REGEX = "[^~!@#?$%^&*():;<>`!\"]*";
    private static final String USERNAME_REGEX = "[\\w+._@-]+";
    private static final int MAX_STRING_LENGTH_50 = 50;
    private static final String PHONE_NUMBER_REGEX_WITH_ASTERISK = "^(\\+?(?:\\(?[0-9]\\)?[-. ]{0,2}){9,14}[0-9])((\\s){0,4}(\\*|(?i)x|(?i)ext)(\\s){0,4}[\\d]{1,5})?$";
    private static final String REQUIRED_KEY = "JS.Validation.Required";
    private static final String MIN_2_CHARS_KEY = "JS.Validation.Minimum2Characters";
    private static final String MAX_50_CHARS_KEY = "JS.Validation.Maximum50Characters";
    private static final String MAX_100_CHARS_KEY = "JS.Validation.Maximum100Characters";
    private static final String NO_SPECIAL_CHARS_KEY = "JS.Validation.SpecialCharacters";
    private static final String COMPANY_NAME_EXISTS_KEY = "JS.Validation.CompanyNameAlreadyExists";
    private static final String INVALID_TAX_ID_KEY = "JS.Validation.InvalidTaxID";
    private static final String INVALID_USERNAME_KEY = "JS.Validation.UsernameInvalid";
    private static final String USERNAME_TAKEN_KEY = "JS.Validation.UsernameIsTaken";
    private static final String INVALID_PHONE_FORMAT_KEY = "JS.Validation.InvalidPhoneFormat";
    private static final String INVALID_EMAIL_FORMAT_KEY = "JS.Validation.ValidEmail";
    private static final String INVALID_DATE_KEY = "AuditData.error.InvalidDate";
    private static final String INVALID_UK_POST_CODE_KEY = "JS.Validation.InvalidPostcode";
    private static final String PASSWORDS_MUST_MATCH_KEY = "JS.Validation.PasswordsMustMatch";

    private AccountStatus status = AccountStatus.Pending;
    private Locale locale;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String city;

    //TODO Custom Validation Constraint for countrySubdivision
    private String countrySubdivision = "";


    @NotBlank(message = REQUIRED_KEY)
    @UniqueContractorName(message = COMPANY_NAME_EXISTS_KEY)
    private String legalName;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = USERNAME_REGEX, message = INVALID_USERNAME_KEY)
    @UniqueUserName(message = USERNAME_TAKEN_KEY)
    private String username;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String address;

    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String address2;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, flags = Pattern.Flag.CASE_INSENSITIVE, message = NO_SPECIAL_CHARS_KEY)
    private String firstName;

    @NotNull(message = REQUIRED_KEY)
    private TimeZone timezone;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String lastName;

    //TODO - custom validator -
    // cannot be username
    // must contain letters & numbers
    // cannot be current password -- (hopefully) impossible during registration
    private String password;

    //TODO - check that it matches entered password
    private String passwordConfirmation;

    @Pattern(regexp = PHONE_NUMBER_REGEX_WITH_ASTERISK, message = INVALID_PHONE_FORMAT_KEY)
    private String phone;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String countryISOCode;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String zip;

    //TODO - tax validator
    private String vatID;

    @NotNull(message = REQUIRED_KEY)
    @Email(message = INVALID_EMAIL_FORMAT_KEY)
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

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
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

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
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

    public String getVatId() {
        return vatID;
    }

    public void setVatId(String vatID) {
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
        form.address2 = input.getAddress2();
        form.legalName = input.getName();
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

        submission.setContractorName(this.getLegalName())
            .setUserName(this.getUsername())
            .setPassword(this.getPassword())
            .setUserFirstName(this.getFirstName())
            .setUserLastName(this.getLastName())
            .setEmail(this.getEmail())
            .setAddress(this.getAddress())
            .setAddress2(this.getAddress2())
            .setZip(this.getZip())
            .setPhoneNumber(this.getPhone())
            .setTimeZone(this.getTimezone())
            .setVatID(this.getVatId())
            .setCountrySubdivision(this.getCountrySubdivision())
            .setCountryISO(this.getCountryISOCode());

        return submission;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
}
