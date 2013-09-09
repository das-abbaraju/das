package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.*;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.dao.*;
import com.picsauditing.util.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.validator.RegistrationValidator;
import com.picsauditing.validator.Validator;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings({"deprecation", "serial"})
public class Registration extends RegistrationAction implements AjaxValidator {

	public static final String DEMO_CONTRACTOR_NAME_MARKER = "^^^";
    private User user;
	private String username;
	private String confirmPassword;
	private String registrationKey;
	private String language;
	private String dialect;
	private int requestID;
	private CountrySubdivision countrySubdivision;
	private Locale locale;

	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private ContractorTagDAO contractorTagDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private UserLoginLogDAO userLoginLogDAO;
	@Autowired
	protected PermissionBuilder permissionBuilder;
	@Autowired
	private RegistrationValidator registrationValidator;

	private SapAppPropertyUtil sapAppPropertyUtil;


	private static Logger logger = LoggerFactory.getLogger(Registration.class);

	public Registration() {
		if (sapAppPropertyUtil == null) {
			sapAppPropertyUtil = SapAppPropertyUtil.factory();
		}
	}

	@Anonymous
	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		if (permissions.isLoggedIn() && !permissions.isDeveloperEnvironment()) {
			addActionError(getText("ContractorRegistration.error.LogoutBeforRegistering"));
			return SUCCESS;
		}

		// check for basic rID...?
		if (getParameter("rID") > 0) {
			requestID = getParameter("rID");
		}
		// Check for new requestID
		if (requestID == 0) {
			requestID = getParameter("requestID");
		}

		if (requestID > 0) {
			// Set the session variable
			ActionContext.getContext().getSession().put("requestID", requestID);
			ContractorRegistrationRequest crr = requestDAO.find(requestID);

			if (crr.getContractor() == null) {
				contractor = new ContractorAccount();
				contractor.setName(crr.getName());
				contractor.setPhone(crr.getPhone());
				contractor.setTaxId(crr.getTaxID());
				contractor.setAddress(crr.getAddress());
				contractor.setCity(crr.getCity());
				contractor.setZip(crr.getZip());
				contractor.setCountry(crr.getCountry());
				if (crr.getCountrySubdivision() != null) {
					contractor.setCountrySubdivision(crr.getCountrySubdivision());
				} else {
					contractor.setCountrySubdivision(null);
				}

				contractor.setRequestedBy(crr.getRequestedBy());
				contractor.setTaxId(crr.getTaxID());

				user = new User();
				user.setName(crr.getContact());
				user.setEmail(EmailAddressUtils.validate(crr.getEmail()));
				user.setPhone(crr.getPhone());
			} else {
				addActionError(getText("ContractorRegistration.error.AlreadyRegistered"));
				return SUCCESS;
			}
		}

		if (!Strings.isEmpty(registrationKey)) {
			List<ContractorAccount> requestsByHash = contractorAccountDao.findWhere("a.registrationHash = '"
					+ registrationKey + "'");

			if (requestsByHash != null && !requestsByHash.isEmpty()) {
				ContractorAccount tempContractor = requestsByHash.get(0);
				if (!tempContractor.getStatus().isRequested()) {
					// Clear hash
					tempContractor.setRegistrationHash(null);
					contractorAccountDao.save(tempContractor);
				} else {
					// Fill out registration form based on registration request
					contractor = tempContractor;
					user = contractor.getPrimaryContact();
					user.setUsername(user.getEmail());
				}

			}
		}

		if (Strings.isEmpty(language)) {
			ExtractBrowserLanguage languageUtility = new ExtractBrowserLanguage(getRequest(), supportedLanguages
					.getVisibleLanguages());
			Locale locale = languageUtility.getBrowserLocale();
			language = languageUtility.getBrowserLanguage();
			dialect = languageUtility.getBrowserDialect();

			ActionContext context = ActionContext.getContext();
			if (context != null) {
				locale = context.getLocale();

				if (locale != null) {
					language = locale.getLanguage();
					dialect = locale.getCountry();
				}
			}

			ActionContext.getContext().setLocale(locale);
		}

		return SUCCESS;
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
		} else {
			return "defaultAddressFields";
		}
	}

	private boolean isUKContractor() {
		return contractor != null && contractor.getCountry() != null && contractor.getCountry().isUK();
	}

	@Anonymous
	public String createAccount() throws Exception {
		loadPermissions(false);
		if (permissions.isLoggedIn() && !permissions.isDeveloperEnvironment()) {
			addActionError(getText("ContractorRegistration.error.LogoutBeforRegistering"));
			return SUCCESS;
		}

		permissions = null;

		setupUserData();
		setupContractorData();
		contractorAccountDao.save(contractor);
		if (user.getEmail().length() > 0) {
			user.setEmail(EmailAddressUtils.validate(user.getEmail()));
		}

		userDAO.save(user);

		// requires id for user to exist to seed the password properly
		user.setEncryptedPassword(user.getPassword());
		userDAO.save(user);
		userDAO.refresh(user);

		contractor.setAgreedBy(user);
		contractor.setPrimaryContact(user);
		contractorAccountDao.save(contractor);

		permissions = logInUser();
		addClientSessionCookieToResponse();
		setLoginLog(permissions);
		// they don't have an account yet so they won't get this as a default
		permissions.setSessionCookieTimeoutInSeconds(3600);

		if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_SUPPRESS_WELCOME_EMAILS)) {
			sendWelcomeEmail();
			addNote(contractor, "Welcome Email Sent");
		} else {
			addNote(contractor, "Welcome Email NOT Sent");
		}

		addNoteThatRequestRegistered();

		return setUrlForRedirect(getRegistrationStep().getUrl());
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	@Override
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public String getRegistrationKey() {
		return registrationKey;
	}

	public void setRegistrationKey(String registrationKey) {
		this.registrationKey = registrationKey;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
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

	public void setCountrySubdivision(CountrySubdivision countrySubdivision) {
		this.countrySubdivision = countrySubdivision;
	}

	public CountrySubdivision getCountrySubdivision() {
		return countrySubdivision;
	}

	protected void sendWelcomeEmail() throws EmailException, UnsupportedEncodingException, IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(EmailTemplate.WELCOME_EMAIL_TEMPLATE);
		emailBuilder.setUser(user);
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
		// TODO move to EncodedKey
		user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
		String confirmLink = "http://www.picsorganizer.com/Login.action?username="
				+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
		emailBuilder.addToken("confirmLink", confirmLink);
		emailBuilder.addToken("contactName", user.getName());
		emailBuilder.addToken("userName", user.getUsername());

		EmailQueue emailQueue = emailBuilder.build();
		emailQueue.setHtml(true);
		emailQueue.setVeryHighPriority();
		emailQueue.setSubjectViewableById(Account.EVERYONE);
		emailQueue.setBodyViewableById(Account.EVERYONE);
		emailSender.send(emailQueue);
	}

	protected void setupContractorData() {
		contractor.setType("Contractor");
		if (contractor.getName().contains(DEMO_CONTRACTOR_NAME_MARKER)) {
			contractor.setStatus(AccountStatus.Demo);
			contractor.setName(contractor.getName().replaceAll("^", Strings.EMPTY_STRING).trim());
		}

		contractor.setCountrySubdivision(countrySubdivision);

		if (contractor.getStatus().isRequested()) {
			contractor.setStatus(AccountStatus.Pending);
		}
		
		contractor.setLocale(ActionContext.getContext().getLocale());
		contractor.setPhone(user.getPhone());
        contractor.setPaymentExpires(new Date());
		contractor.setAuditColumns(new User(User.CONTRACTOR));
		contractor.setNameIndex();
        if (!contractor.isDemo()) {
            contractor.setQbSync(true);
			if (sapAppPropertyUtil.isSAPBusinessUnitSetSyncTrueEnabledForObject(contractor)) {
				contractor.setSapSync(true);
			}
        }

		contractor.setNaics(new Naics());
		contractor.getNaics().setCode("0");
		contractor.setNaicsValid(false);
		contractor.setAgreementDate(contractor.getCreationDate());
		contractor.getUsers().add(user);

		// Default their current membership to 0
		List<FeeClass> feeClasses = Arrays.asList(FeeClass.BidOnly, FeeClass.ListOnly, FeeClass.DocuGUARD,
				FeeClass.AuditGUARD, FeeClass.InsureGUARD, FeeClass.EmployeeGUARD);
		for (FeeClass feeClass : feeClasses) {
			ContractorFee newConFee = new ContractorFee();
			newConFee.setAuditColumns(new User(User.CONTRACTOR));
			newConFee.setContractor(contractor);

			InvoiceFee currentFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(feeClass, 0);
			newConFee.setCurrentLevel(currentFee);
			newConFee.setNewLevel(currentFee);
			newConFee.setFeeClass(feeClass);
			contractor.getFees().put(feeClass, newConFee);
		}
		
		scrubContractorData(contractor);
	}

	private void scrubContractorData(ContractorAccount contractor) {
		if (contractor.getCountry().isUK()) {
			String zip = contractor.getZip();
			contractor.setZip(DataScrubber.cleanUKPostcode(zip));
		}
	}

	private void addNoteThatRequestRegistered() {
		if (requestID > 0) {
			ContractorRegistrationRequest crr = updateRegistrationRequest();

			Note note = addNote(contractor, "Requested Contractor Registered");
			note.setBody("Contractor '" + crr.getName() + "' requested by " + crr.getRequestedBy().getName()
					+ " has registered.");

			dao.save(note);
		}

		if (contractor.getStatus().isRequested()) {
			Note note = addNote(contractor, "Requested Contractor Registered");
			note.setBody("Contractor '" + contractor.getName() + "' requested by "
					+ contractor.getRequestedBy().getName() + " has registered.");
			dao.save(note);

			contractor.setStatus(AccountStatus.Pending);
			contractor.setRegistrationHash(null);
			contractorAccountDao.save(contractor);
		}
	}

	private ContractorRegistrationRequest updateRegistrationRequest() {
		ContractorRegistrationRequest crr = requestDAO.find(requestID);

		crr.setContractor(contractor);
		crr.setMatchCount(1);
		crr.setAuditColumns();
		crr.setNotes(maskDateFormat(new Date()) + " - " + contractor.getPrimaryContact().getName()
				+ " - Account created through completing a Registration Request\n\n" + crr.getNotes());

		requestDAO.save(crr);

		transferRegistrationRequestTags(crr);

		return crr;
	}

	private void transferRegistrationRequestTags(ContractorRegistrationRequest crr) {
		if (Strings.isEmpty(crr.getOperatorTags())) {
			return;
		}

		for (String tagID : crr.getOperatorTags().split(",")) {
			try {
				OperatorTag tag = operatorTagDAO.find(Integer.parseInt(tagID));

				if (tag.getOperator().getStatus().isActive()) {
					ContractorTag contractorTag = new ContractorTag();
					contractorTag.setTag(tag);
					contractorTag.setContractor(contractor);
					contractorTag.setAuditColumns(permissions);

					contractorTagDAO.save(contractorTag);
					contractor.getOperatorTags().add(contractorTag);
				}
			} catch (Exception exception) {
				logger.error("Error in transferring registration request tag {}\n{}", new Object[]{tagID, exception});
			}
		}
	}

	private void setLoginLog(Permissions permissions) {
		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setLoginDate(new Date());
		loginLog.setRemoteAddress(ServletActionContext.getRequest().getRemoteAddr());

		String serverName = ServletActionContext.getRequest().getLocalName();
		try {
			if (isLiveEnvironment()) {
				// Need computer name instead of www
				serverName = InetAddress.getLocalHost().getHostName();
			}
		} catch (UnknownHostException justUseRequestServerName) {
		}

		loginLog.setServerAddress(serverName);

		loginLog.setSuccessful(permissions.isLoggedIn());
		loginLog.setUser(user);
		userLoginLogDAO.save(loginLog);
	}

	private Permissions logInUser() throws Exception {
		Permissions permissions = permissionBuilder.login(user);
		ActionContext.getContext().getSession().put(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
		return permissions;
	}

	private void setupUserData() {
		user.setActive(true);
		user.setAccount(contractor);
		user.setTimezone(contractor.getTimezone());

		Locale locale = Locale.US;

		if (Strings.isNotEmpty(language)) {
			if (Strings.isNotEmpty(dialect)) {
				locale = new Locale(language, dialect);
			} else {
				locale = new Locale(language);
			}
		}

		user.setLocale(supportedLanguages.getClosestVisibleLocale(locale));

		user.setAuditColumns(new User(User.CONTRACTOR));
		user.setIsGroup(YesNo.No);
		user.addOwnedPermissions(OpPerms.ContractorAdmin, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorSafety, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorInsurance, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorBilling, User.CONTRACTOR);
		user.setLastLogin(new Date());
		user.updateDisplayNameBasedOnFirstAndLastName();
	}

	// For the Ajax Validation
	public Validator getCustomValidator() {
		return registrationValidator;
	}

	// For server-side validation
	@Override
	public void validate() {
		registrationValidator.validate(ActionContext.getContext().getValueStack(), new DelegatingValidatorContext(this));
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
		if (contractor == null || contractor.getCountry() == null) {
			return getCountrySubdivisionList(Country.US_ISO_CODE);
		}

		return getCountrySubdivisionList(contractor.getCountry().getIsoCode());
	}

    public String getCountrySubdivisionLabelFor() {
    	if (contractor == null || contractor.getCountry() == null) {
			return getCountrySubdivisionLabelFor(Country.US_ISO_CODE);
		}

		return getCountrySubdivisionLabelFor(contractor.getCountry().getIsoCode());
	}

	public SapAppPropertyUtil getSapAppPropertyUtil() {
		return sapAppPropertyUtil;
	}

	public void setSapAppPropertyUtil(SapAppPropertyUtil sapAppPropertyUtil) {
		this.sapAppPropertyUtil = sapAppPropertyUtil;
	}
}
