package com.picsauditing.struts.controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.*;
import com.picsauditing.actions.contractors.RegistrationAction;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.account.AddressService;
import com.picsauditing.service.addressverifier.AddressRequestHolder;
import com.picsauditing.service.addressverifier.AddressResponseHolder;
import com.picsauditing.service.addressverifier.AddressVerificationService;
import com.picsauditing.service.addressverifier.ResultStatus;
import com.picsauditing.service.registration.RegistrationRequestService;
import com.picsauditing.service.registration.RegistrationResult;
import com.picsauditing.service.registration.RegistrationResult.RegistrationFailure;
import com.picsauditing.service.registration.RegistrationResult.RegistrationSuccess;
import com.picsauditing.service.registration.RegistrationService;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;
import com.picsauditing.struts.validator.RegistrationFormValidationWrapper;
import com.picsauditing.util.Strings;
import com.picsauditing.util.TimeZoneUtil;
import com.picsauditing.validator.Validator;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidatorFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings({"deprecation", "serial"})
public class Registration extends RegistrationAction implements AjaxValidator, Preparable {

    public static final String COUNTRY_SUBDIVISION_FIELD_KEY = "registrationForm.countrySubdivision";
    private static final Logger logger = LoggerFactory.getLogger(Registration.class);

    private String registrationKey;
    private RegistrationForm registrationForm = new RegistrationForm();
    private RegistrationLocaleForm localeForm = new RegistrationLocaleForm();

	@Autowired
	private UserLoginLogDAO userLoginLogDAO;
	@Autowired
	protected PermissionBuilder permissionBuilder;
    @Autowired
    private RegistrationRequestService regReqService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ValidatorFactory validatorFactory;
    @Autowired
    private AddressVerificationService addressVerificationService;
    @Autowired
    private AddressService addressService;


	@Anonymous
	@Override
	public String execute() throws Exception {
        if (Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive()) {
            logger.warn("User navigated to the wrong registration action, perhaps via a bookmark? Redirecting...");
            setUrlForRedirect(LoginController.REGISTRATION_ACTION_ANGULAR);
            return REDIRECT;
        }

		if (loggedIn()) {
			addActionError(getText("ContractorRegistration.error.LogoutBeforRegistering"));
            return SUCCESS;
		}

        getActionContext().setLocale(createLocaleFromForms());

        registrationForm = (Strings.isNotEmpty(registrationKey))
            ? RegistrationForm.fromContractor(regReqService.preRegistrationFromKey(registrationKey))
            : new RegistrationForm();


        return SUCCESS;
	}

    private Locale createLocaleFromForms() {
        String isoCode = (localeForm.getDialect() != null)? localeForm.getDialect() : registrationForm.getCountryISOCode();
        if (isoCode != null)
            return new Locale(localeForm.getLanguage(), isoCode);
        else
            return new Locale(localeForm.getLanguage());
    }

    @Anonymous
    @SkipValidation
    public String confirmAddress() {
        return "confirmAddress";
    }

    @Anonymous
    @SkipValidation
    public String basicEdit() {
        return "basicEdit";
    }

    @Anonymous
    public String createAccount() throws Exception {

        if (loggedIn()) {
            addActionError(getText("ContractorRegistration.error.LogoutBeforRegistering"));
            return SUCCESS;
        }

        final RegistrationResult result = registrationForm.createSubmission(registrationService)
                .setLocale(createLocaleFromForms())
                .setRegistrationRequestHash(registrationKey)
                .submit();

        if (result instanceof RegistrationSuccess) {

            final RegistrationSuccess success = (RegistrationSuccess) result;
            contractor = success.getContractor();
            user = success.getUser();
            permissions = logInUser();
            addClientSessionCookieToResponse();
            setLoginLog(permissions);
            // they don't have an account yet so they won't get this as a default
            permissions.setSessionCookieTimeoutInSeconds(3600);

            AddressResponseHolder addressResponseHolder = addressVerificationService.verify(buildAddressRequestHolder());

            if (!addressVerifyFailed(addressResponseHolder)) {
                addressService.saveAddressFieldsFromVerifiedAddress(contractor, addressResponseHolder, user);
            }
            return setUrlForRedirect(getRegistrationStep().getUrl());

        } else {
            //FIXME: Find a better way to deal with this.
            throw new Exception(((RegistrationFailure) result).getProblem());
        }
    }

    private boolean addressVerifyFailed(AddressResponseHolder addressResponseHolder) {
        return addressResponseHolder.getResultStatus() == ResultStatus.DATA_NOT_FOUND
                || addressResponseHolder.getResultStatus() == ResultStatus.INVALID_INPUT
                || addressResponseHolder.getResultStatus() == ResultStatus.IGNORE;
    }


    private AddressRequestHolder buildAddressRequestHolder() {
        return AddressRequestHolder.builder()
                .addressBlob(registrationForm.getAddressBlob())
                .country(registrationForm.getCountryISOCode())
                .zipCode(registrationForm.getZip())
                .build();
    }

    //TODO: Extract this logic to an Interceptor Annotation
    protected boolean loggedIn() {
        loadPermissions(false);
        return permissions.isLoggedIn() && !permissions.isDeveloperEnvironment();
    }


    private RegistrationLocaleForm getLocaleFromRequestData() {
        if (localeForm != null && Strings.isNotEmpty(localeForm.getLanguage())) return localeForm;

        final RegistrationLocaleForm newForm = new RegistrationLocaleForm();
        final ActionContext context = getActionContext();

        if (context.getLocale() != null) {
            final Locale preExistingLocale = context.getLocale();
            newForm.setLocale(preExistingLocale);
            newForm.setLanguage(preExistingLocale.getLanguage());
            newForm.setDialect(preExistingLocale.getCountry());
        } else {
            ExtractBrowserLanguage languageUtility = new ExtractBrowserLanguage(getRequest(), supportedLanguages.getVisibleLanguages());
            newForm.setLocale(languageUtility.getBrowserLocale());
            newForm.setLanguage(languageUtility.getBrowserLanguage());
            newForm.setDialect(languageUtility.getBrowserDialect());
        }

        return newForm;
    }

    @Anonymous
	public String dialects() {
		return SUCCESS;
	}

	@Anonymous
	@SkipValidation
	public String getCompanyAddressFields() {
		if (isUKContractor()) {
			return "GBAddressFields";
		}
		if (isAUContractor()) {
			return "AUAddressFields";
		} else {
			return "defaultAddressFields";
		}
	}

	private boolean isAUContractor() {
        return registrationForm != null && registrationForm.getCountryISOCode() != null && registrationForm.getCountryISOCode().equals(Country.AUSTRALIA_ISO_CODE);
	}

	private boolean isUKContractor() {
        return registrationForm != null && registrationForm.getCountryISOCode() != null && registrationForm.getCountryISOCode().equals(Country.UK_ISO_CODE);
	}

	private void setLoginLog(Permissions permissions) {
		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setLoginDate(new Date());
		loginLog.setRemoteAddress(getRequest().getRemoteAddr());

		String serverName = getRequest().getLocalName();
		try {
			if (isLiveEnvironment()) {
				// Need computer name instead of www
				serverName = InetAddress.getLocalHost().getHostName();
			}
		} catch (UnknownHostException justUseRequestServerName) {
            //FIXME: Error swallowing.
		}

		loginLog.setServerAddress(serverName);

		loginLog.setSuccessful(permissions.isLoggedIn());
		loginLog.setUser(user);
		userLoginLogDAO.save(loginLog);
	}

	private Permissions logInUser() throws Exception {
		Permissions permissions = permissionBuilder.login(user);
		getActionContext().getSession().put(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
		return permissions;
	}

	// For the Ajax Validation
	public Validator getCustomValidator() {
		return new RegistrationFormValidationWrapper(this, validatorFactory.getValidator());
	}

	// For server-side validation
	@Override
	public void validate() {
        RegistrationFormValidationWrapper registrationFormValidationWrapper = new RegistrationFormValidationWrapper(this, validatorFactory.getValidator());
        registrationFormValidationWrapper.validate(getActionContext().getValueStack(), new DelegatingValidatorContext(this));
        Map<String, List<String>> fieldErrors = this.getFieldErrors();

        if (Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive()) {
            removeFieldError(fieldErrors, COUNTRY_SUBDIVISION_FIELD_KEY);
        }
    }

    private void removeFieldError(Map<String, List<String>> fieldErrors, String fieldName) {
        Iterator<Map.Entry<String, List<String>>> iterator = fieldErrors.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            if (fieldName.equals(entry.getKey())) {
                iterator.remove();
            }
        }
        this.setFieldErrors(fieldErrors);
	}

	public Map<String, String> getTimezones() {
		Map<String, String> timezones = new LinkedHashMap<String, String>();
		timezones.putAll(TimeZoneUtil.timeZones());

		for (String key : timezones.keySet()) {
			String value = timezones.get(key);
			timezones.put(key, getText(value));
		}

		return timezones;
	}

	@Override
	public List<CountrySubdivision> getCountrySubdivisionList() {
		if (registrationForm == null || Strings.isEmpty(registrationForm.getCountryISOCode()))
			return getCountrySubdivisionList(Country.US_ISO_CODE);
		else
            return getCountrySubdivisionList(registrationForm.getCountryISOCode());
	}

	public String getCountrySubdivisionLabelFor() {
		if (registrationForm == null || Strings.isEmpty(registrationForm.getCountryISOCode()))
			return getCountrySubdivisionLabelFor(Country.US_ISO_CODE);
		else
            return getCountrySubdivisionLabelFor(registrationForm.getCountryISOCode());
	}

    protected ActionContext getActionContext() {
        return ActionContext.getContext();
    }

    protected HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    public RegistrationForm getRegistrationForm() {
        return registrationForm;
    }

    public void setRegistrationForm(RegistrationForm registrationForm) {
        this.registrationForm = registrationForm;
    }

    public RegistrationLocaleForm getLocaleForm() {
        return localeForm;
    }

    public void setLocaleForm(RegistrationLocaleForm localeForm) {
        this.localeForm = localeForm;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    @Override
    public ContractorRegistrationStep getPreviousRegistrationStep() {
        return null;
    }

    @Override
    public ContractorRegistrationStep getNextRegistrationStep() {
        return null;
    }

    /**
     * This shouldn't be getting called
     */
    @Override
    public String previousStep() throws Exception {
        return SUCCESS;
    }

    /**
     * This shouldn't be getting called either. After the first step of
     * registration the Contractor account is created and this should redirect
     * to ConEdit
     */
    @Override
    public String nextStep() throws Exception {
        return SUCCESS;
    }


    @Override
    public void prepare() throws Exception {
        localeForm = getLocaleFromRequestData();
    }

    public boolean isAddressVerificationEnabled() {
        return Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive();
    }
}
