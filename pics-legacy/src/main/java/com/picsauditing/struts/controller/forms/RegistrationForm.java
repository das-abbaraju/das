package com.picsauditing.struts.controller.forms;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.registration.RegistrationService;
import com.picsauditing.service.registration.RegistrationSubmission;
import com.picsauditing.struts.validator.constraints.*;
import com.picsauditing.util.DataScrubber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Locale;
import java.util.TimeZone;


public class RegistrationForm {
    public static final String SPECIAL_CHAR_REGEX = "[^~!@#?$%^&*():;<>`!\"]*";
    public static final String USERNAME_REGEX = "[\\w+._@-]+";
    public static final int MAX_STRING_LENGTH_50 = 50;
    public static final int MIN_STRING_LENGTH_2 = 2;
    public static final String PHONE_NUMBER_REGEX_WITH_ASTERISK = "^(\\+?(?:\\(?[0-9]\\)?[-. ]{0,2}){9,14}[0-9])((\\s){0,4}(\\*|(?i)x|(?i)ext)(\\s){0,4}[\\d]{1,5})?$";
    public static final String REQUIRED_KEY = "JS.Validation.Required";
    public static final String MIN_2_CHARS_KEY = "JS.Validation.Minimum2Characters";
    public static final String MAX_50_CHARS_KEY = "JS.Validation.Maximum50Characters";
    public static final String MAX_100_CHARS_KEY = "JS.Validation.Maximum100Characters";
    public static final String NO_SPECIAL_CHARS_KEY = "JS.Validation.SpecialCharacters";
    public static final String COMPANY_NAME_EXISTS_KEY = "JS.Validation.CompanyNameAlreadyExists";
    public static final String INVALID_TAX_ID_KEY = "JS.Validation.InvalidVAT";
    public static final String INVALID_USERNAME_KEY = "JS.Validation.UsernameInvalid";
    public static final String USERNAME_TAKEN_KEY = "JS.Validation.UsernameIsTaken";
    public static final String INVALID_PHONE_FORMAT_KEY = "JS.Validation.InvalidPhoneFormat";
    public static final String INVALID_EMAIL_FORMAT_KEY = "JS.Validation.ValidEmail";
    public static final String INVALID_DATE_KEY = "AuditData.error.InvalidDate";
    public static final String INVALID_UK_POST_CODE_KEY = "JS.Validation.InvalidPostcode";
    public static final String PASSWORDS_MUST_MATCH_KEY = "JS.Validation.PasswordsMustMatch";
    public static final String PASSWORD_CANNOT_BE_USERNAME = "JS.Validation.CannotBeUsername";

    private AccountStatus status = AccountStatus.Pending;
    private Locale locale;

    @NotBlank(message = REQUIRED_KEY)
    @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY)
    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String city;

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
    // must contain letters & numbers
    @Length.List({
            @Length(max = MAX_STRING_LENGTH_50, message = MAX_50_CHARS_KEY),
            @Length(min = MIN_STRING_LENGTH_2, message = MIN_2_CHARS_KEY)
    })
    @NotNull(message = REQUIRED_KEY)
    @NotBlank(message = REQUIRED_KEY)
    private String password;

    @NotNull(message = REQUIRED_KEY)
    @NotBlank(message = REQUIRED_KEY)
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

    @Pattern(regexp = SPECIAL_CHAR_REGEX, message = NO_SPECIAL_CHARS_KEY)
    private String vatID;

    @NotBlank(message = REQUIRED_KEY)
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

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public static RegistrationForm fromContractor(final ContractorAccount input) {
        final User user = (input.getPrimaryContact() == null) ? new User() : input.getPrimaryContact();
        final RegistrationForm form = new RegistrationForm();

        form.status = input.getStatus();
        form.city = input.getCity();
        form.address = input.getAddress();
        form.address2 = input.getAddress2();
        form.legalName = input.getName();
        form.countryISOCode = (input.getCountry() != null)
                ? input.getCountry().getIsoCode()
                : null;
        form.countrySubdivision = (input.getCountrySubdivision() != null)
                ? input.getCountrySubdivision().getIsoCode()
                : null;
        form.email = user.getEmail();
        form.firstName = user.getFirstName();
        form.lastName = user.getLastName();
        form.locale = input.getLocale();
        form.zip = input.getZip();
        form.phone = user.getPhone();


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







    /*
        What follows is logic and classes specifically for form validation.
     */

    public PasswordPair getPasswordPair() {
        return new PasswordPair(getPassword(), getPasswordConfirmation(), getUsername());
    }

    public VATPair getVATPairing() {
        return new VATPair(getCountryISOCode(), getVatId());
    }

    public CountrySubdivisionPair getCountrySubdivisionPairing() {
        return new CountrySubdivisionPair(getCountryISOCode(), getCountrySubdivision(), getZip());
    }

    @PasswordsMatch(message = "registrationForm.passwordConfirmation::" + PASSWORDS_MUST_MATCH_KEY)
    @PasswordNotSameAsUserName(message =  "registrationForm.password::" + PASSWORD_CANNOT_BE_USERNAME)
    public static class PasswordPair {

        private final String first;
        private final String second;
        private final String username;

        private PasswordPair(String first, String second, String username) {
            this.first = first;
            this.second = second;
            this.username = username;
        }


        public String getFirstPassword() {
            return first;
        }

        public String getSecondPassword() {
            return second;
        }

        public String getUsername() {
            return username;
        }
    }

    @VatValidation(message = "registrationForm.vatId::" + INVALID_TAX_ID_KEY)
    public static class VATPair {
        private final String country;
        private final String vatCode;

        private VATPair(String country, String vat) {
            this.country = country;
            this.vatCode = vat;
        }

        public String getVatCode() {
            return vatCode;
        }

        public String getCountry() {
            return country;
        }
    }

    @ValidateSubdivision(message = "registrationForm.countrySubdivision::" + REQUIRED_KEY)
    public static class CountrySubdivisionPair {
        private final String country;
        private final String subdivision;
        private final String zip;

        private CountrySubdivisionPair(String country, String subdivision, String zip) {
            this.country = country;
            this.subdivision = subdivision;
            this.zip = zip;
        }

        public String getCountry() {
            return country;
        }

        public String getSubdivision() {
            return subdivision;
        }

        public String getZip() {
            return zip;
        }
    }
}
