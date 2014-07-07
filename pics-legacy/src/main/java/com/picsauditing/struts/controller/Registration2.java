package com.picsauditing.struts.controller;

import com.google.gson.*;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.account.RegistrationJsonException;
import com.picsauditing.actions.contractors.RegistrationAction;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.employeeguard.viewmodel.IdNameModel;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.menu.builder.MenuBuilder;
import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.model.viewmodel.RegistrationSignupForm;
import com.picsauditing.model.viewmodel.RegistrationSuccessResponse;
import com.picsauditing.service.account.AddressService;
import com.picsauditing.service.addressverifier.AddressRequestHolder;
import com.picsauditing.service.addressverifier.AddressResponseHolder;
import com.picsauditing.service.addressverifier.AddressVerificationService;
import com.picsauditing.service.addressverifier.ResultStatus;
import com.picsauditing.service.registration.RegistrationRequestService;
import com.picsauditing.service.registration.RegistrationResult;
import com.picsauditing.service.registration.RegistrationResult.RegistrationSuccess;
import com.picsauditing.service.registration.RegistrationService;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;
import com.picsauditing.struts.validator.RegistrationFormValidationWrapper;
import com.picsauditing.util.Strings;
import com.picsauditing.util.TimeZoneUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidatorFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings({"deprecation", "serial"})
public class Registration2 extends RegistrationAction {

    public static final String COUNTRY_SUBDIVISION_FIELD_KEY = "registrationForm.countrySubdivision";

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

    private String language;
    private String isoCode;
    private String registrationRequestKey;
    private String localeCode;

    private static final Logger logger = LoggerFactory.getLogger(Registration2.class);

	@Anonymous
    public String registrationFormJson() throws Exception {
        //getActionContext().setLocale(localeForm.getLocale()); // todo: revisit

        RegistrationForm registrationForm = (Strings.isNotEmpty(registrationRequestKey))
            ? RegistrationForm.fromContractor(regReqService.preRegistrationFromKey(registrationRequestKey))
            : new RegistrationForm();

        RegistrationSignupForm responsePayload = RegistrationSignupForm.builder().registrationForm(registrationForm).build();

        jsonString = new Gson().toJson(responsePayload);

        return JSON_STRING;
	}

    @Anonymous
    public String createAccount()  {
        jsonString = "{}";
        RegistrationSignupForm registrationSignupForm;

        try {
            registrationSignupForm = getModelFromJsonRequest();
        } catch (IOException e) {
            return handleServerError(e);
        } catch (RegistrationJsonException e) {
            return handleBadJson(e);
        }

        String timezoneId = registrationSignupForm.getRegistrationForm().getTimezoneId();
        if (timezoneId != null) {
            registrationSignupForm.getRegistrationForm().setTimezone(TimeZone.getTimeZone(timezoneId));
        }

        RegistrationForm registrationForm = registrationSignupForm.getRegistrationForm();
        RegistrationLocaleForm localeForm = registrationSignupForm.getLocaleForm();

        Map<String, List<String>> fieldErrors = validateRegistrationForm(registrationForm, localeForm);

        if (fieldErrors.isEmpty()) {
            String registrationKey = registrationSignupForm.getRegistrationRequestKey();
            try {
                jsonString = buildAccountFromForms(registrationForm, localeForm, registrationKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ServletActionContext.getResponse().setStatus(HttpStatus.SC_CREATED);
        } else {
            jsonString = new Gson().toJson(fieldErrors);
            ServletActionContext.getResponse().setStatus(HttpStatus.SC_NOT_ACCEPTABLE);
        }
        return JSON_STRING;
    }

    protected RegistrationSignupForm getModelFromJsonRequest() throws RegistrationJsonException, IOException {
        String body = getBodyFromRequest();

        RegistrationSignupForm registrationSignupForm = new RegistrationSignupForm();

        try {
            registrationSignupForm = new Gson().fromJson(body, RegistrationSignupForm.class);
        } catch (JsonSyntaxException e) {
            throw new RegistrationJsonException(HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST) + ": " + body, e);
        }

        return registrationSignupForm;
    }

    private String getBodyFromRequest() throws IOException {
        HttpServletRequest request = getRequest();
        return getBody(request);
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return stringBuilder.toString();
    }

    private Map<String, List<String>> validateRegistrationForm(RegistrationForm registrationForm, RegistrationLocaleForm localeForm) {
        Set<String> keysFromJson = getKeysFromJson(new Gson().toJson(registrationForm), "registrationForm");
        Set<String> localeFormKeys = getKeysFromJson(new Gson().toJson(localeForm), "localeForm");
        keysFromJson.addAll(localeFormKeys);

        RegistrationFormValidationWrapper registrationFormValidationWrapper =
                new RegistrationFormValidationWrapper(registrationForm, localeForm, validatorFactory.getValidator());

        registrationFormValidationWrapper.validate(getActionContext().getValueStack(), new DelegatingValidatorContext(this), keysFromJson);
        Map<String, List<String>> fieldErrors = this.getFieldErrors();

        if (Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive()) {
            removeFieldError(fieldErrors, COUNTRY_SUBDIVISION_FIELD_KEY);
        }
        return fieldErrors;
    }

    private String buildAccountFromForms(RegistrationForm registrationForm, RegistrationLocaleForm localeForm, String registrationKey) throws Exception {
        String jsonResponse = null;

        final RegistrationResult result = registrationForm.createSubmission(registrationService)
                .setLocale(localeForm.getLocale())
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

            AddressRequestHolder addressRequestHolder = buildAddressRequestHolder(registrationForm);
            AddressResponseHolder addressResponseHolder = addressVerificationService.verify(addressRequestHolder);

            RegistrationSuccessResponse registrationSuccessResponse = RegistrationSuccessResponse.builder()
                    .user(com.picsauditing.model.viewmodel.User.builder()
                            .loggedIn()
                            .build())
                    .build();

            if (isAddressVerificationEnabled()) {
                ResultStatus resultStatus = addressResponseHolder.getResultStatus();
                registrationSuccessResponse.setAddressVerificationResultStatus(resultStatus);
                registrationSuccessResponse.setRegistrationForm(registrationForm);

                if (resultStatus == ResultStatus.SUCCESS_DATA_CORRECT_ON_INPUT) {
                    addressService.saveAddressFieldsFromVerifiedAddress(contractor, addressResponseHolder, user);
                }  if (resultStatus == ResultStatus.SUCCESS_DATA_CORRECTED) {
                    registrationSuccessResponse.setAddressResponseHolder(addressResponseHolder);
                }

            }


            jsonResponse = new Gson().toJson(registrationSuccessResponse);
        } else {
            //FIXME: Find a better way to deal with this.
            //throw new Exception(((RegistrationFailure) result).getProblem());
            // TODO: figure out what this use case is, and respond return an appropriate json response
        }
        return jsonResponse;
    }


    private AddressRequestHolder buildAddressRequestHolder(RegistrationForm registrationForm) {
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


//    private RegistrationLocaleForm getLocaleFromRequestData() {
//        if (localeForm != null && Strings.isNotEmpty(localeForm.getLanguage())) return localeForm;
//
//        final RegistrationLocaleForm newForm = new RegistrationLocaleForm();
//        final ActionContext context = getActionContext();
//
//        if (context.getLocale() != null) {
//            final Locale preExistingLocale = context.getLocale();
//            newForm.setLocale(preExistingLocale);
//            newForm.setLanguage(preExistingLocale.getLanguage());
//            newForm.setDialect(preExistingLocale.getCountry());
//        } else {
//            ExtractBrowserLanguage languageUtility = new ExtractBrowserLanguage(getRequest(), supportedLanguages.getVisibleLanguages());
//            newForm.setLocale(languageUtility.getBrowserLocale());
//            newForm.setLanguage(languageUtility.getBrowserLanguage());
//            newForm.setDialect(languageUtility.getBrowserDialect());
//        }
//
//        return newForm;
//    }

    @Anonymous
	public String dialects() {
		return SUCCESS;
	}

//	@Anonymous
//	@SkipValidation
//	public String getCompanyAddressFields() {
//		if (isUKContractor()) {
//			return "GBAddressFields";
//		}
//		if (isAUContractor()) {
//			return "AUAddressFields";
//		} else {
//			return "defaultAddressFields";
//		}
//	}

//	private boolean isAUContractor() {
//        return registrationForm != null && registrationForm.getCountryISOCode() != null && registrationForm.getCountryISOCode().equals(Country.AUSTRALIA_ISO_CODE);
//	}
//
//	private boolean isUKContractor() {
//        return registrationForm != null && registrationForm.getCountryISOCode() != null && registrationForm.getCountryISOCode().equals(Country.UK_ISO_CODE);
//	}

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
//	public Validator getCustomValidator() {
//		return new RegistrationFormValidationWrapper(this, validatorFactory.getValidator());
//	}

	// For server-side validation
//	@Override
//	public void validate() {
//        RegistrationFormValidationWrapper registrationFormValidationWrapper = new RegistrationFormValidationWrapper(this, validatorFactory.getValidator());
//        registrationFormValidationWrapper.validate(getActionContext().getValueStack(), new DelegatingValidatorContext(this));
//        Map<String, List<String>> fieldErrors = this.getFieldErrors();
//
//        if (Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive()) {
//            removeFieldError(fieldErrors, COUNTRY_SUBDIVISION_FIELD_KEY);
//        }
//    }

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

//	@Override
//	public List<CountrySubdivision> getCountrySubdivisionList() {
//		if (registrationForm == null || Strings.isEmpty(registrationForm.getCountryISOCode()))
//			return getCountrySubdivisionList(Country.US_ISO_CODE);
//		else
//            return getCountrySubdivisionList(registrationForm.getCountryISOCode());
//	}
//
//	public String getCountrySubdivisionLabelFor() {
//		if (registrationForm == null || Strings.isEmpty(registrationForm.getCountryISOCode()))
//			return getCountrySubdivisionLabelFor(Country.US_ISO_CODE);
//		else
//            return getCountrySubdivisionLabelFor(registrationForm.getCountryISOCode());
//	}

    protected ActionContext getActionContext() {
        return ActionContext.getContext();
    }

    protected HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
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

    public boolean isAddressVerificationEnabled() {
        return Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive();
    }

    public Set<String> getKeysFromJson(String json, String formName) {
        Set<String> results = new TreeSet<>();
        collectKeys(json, results, formName);
        return results;
    }

    private void collectKeys(String json, Set<String> results, final String formName) {
        JsonParser p = new JsonParser();
        JsonObject result = p.parse(json).getAsJsonObject();
        Set<Map.Entry<String,JsonElement>> entrySet=result.entrySet();

        for(Map.Entry<String,JsonElement> entry : entrySet){
                results.add(formName + "." + entry.getKey());
        }
    }

    @Anonymous
    public String validateInputs() {
        jsonString = "{}";
        RegistrationSignupForm registrationSignupForm;
        try {
            registrationSignupForm = getModelFromJsonRequest();
        } catch (IOException e) {
            return handleServerError(e);
        } catch (RegistrationJsonException e) {
            return handleBadJson(e);
        }

        RegistrationForm registrationForm = registrationSignupForm.getRegistrationForm();
        RegistrationLocaleForm localeForm = registrationSignupForm.getLocaleForm();

        Map<String, List<String>> fieldErrors = validateRegistrationForm(registrationForm, localeForm);
        jsonString = new Gson().toJson(fieldErrors);

        return JSON_STRING;
    }

    @Anonymous
    public String languages() {
        List<KeyValue<String, String>> visibleLanguagesSansDialect = supportedLanguages.getVisibleLanguagesSansDialect();
        List idNameModels = new ArrayList<>();
        for (KeyValue<String, String> keyValue : visibleLanguagesSansDialect) {
            idNameModels.add(new IdNameModel.Builder().id(keyValue.getKey()).name(keyValue.getValue()).build());
        }
        jsonString = new Gson().toJson(idNameModels);
        return JSON_STRING;
    }

    @Anonymous
    public String countriesBasedOn() {
        Map<String, String> countriesBasedOn = supportedLanguages.getCountriesBasedOn(language);
        List idNameModels = new ArrayList<>();
        for (String language : countriesBasedOn.keySet()) {
            idNameModels.add(new IdNameModel.Builder().id(language).name(countriesBasedOn.get(language)).build());
        }
        jsonString = new Gson().toJson(idNameModels);
        return JSON_STRING;
    }

    @Anonymous
    public String countries() {
        List<Country> countryList = getCountryList();
        List idNameModels = new ArrayList<>();
        for (Country country : countryList) {
            idNameModels.add(new IdNameModel.Builder().id(country.getIsoCode()).name(country.getName()).build());
        }
        jsonString = new Gson().toJson(idNameModels);
        return JSON_STRING;
    }

    @Anonymous
    public String salesPhoneNumber() {
        String salesPhoneNumber = getSalesPhoneNumber(isoCode);
        IdNameModel idNameModel = new IdNameModel.Builder().id(isoCode).name(salesPhoneNumber).build();
        jsonString = new Gson().toJson(idNameModel);
        return JSON_STRING;
    }

    @Anonymous
    public String locale() {
        Locale locale = getLocale();
        jsonString = new Gson().toJson(locale);
        return JSON_STRING;
    }

    @Anonymous
    public String mibewBaseUrl() {
        Locale locale = new Locale(language);
        String mibewUrl = null;
        try {
            mibewUrl = MenuBuilder.getMibewURL(locale, permissions);
        } catch (UnsupportedEncodingException e) {
            handleBadJson(new Exception("Unable to build mibew url with locale: " + locale, e));
        }
        IdNameModel idNameModel = new IdNameModel.Builder().id(language).name(mibewUrl).build();
        jsonString = new Gson().toJson(idNameModel);
        return JSON_STRING;
    }


    private String handleBadJson(Exception e) {
        ServletActionContext.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
        logger.error("Bad request.", e);
        return JSON_STRING;
    }

    private String handleServerError(IOException e) {
        ServletActionContext.getResponse().setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        logger.error("Internal Server Error.", e);
        return JSON_STRING;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getRegistrationRequestKey() {
        return registrationRequestKey;
    }

    public void setRegistrationRequestKey(String registrationRequestKey) {
        this.registrationRequestKey = registrationRequestKey;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }
}
