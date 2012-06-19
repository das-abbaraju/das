package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorFee;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class Registration extends ContractorActionSupport {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private UserLoginLogDAO userLoginLogDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private EmailSenderSpring emailSender;

	private User user;
	private String username;
	private String confirmPassword;
	private int requestID;
	private State state;

	@Anonymous
	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		if (permissions.isLoggedIn() && !permissions.isDeveloperEnvironment()) {
			addActionError(getText("ContractorRegistration.error.LogoutBeforRegistering"));
			return SUCCESS;
		}

		if ("request".equalsIgnoreCase(button)) {
			// check for basic rID...?
			if (getParameter("rID") > 0)
				requestID = getParameter("rID");
			// Check for new requestID
			if (requestID == 0)
				requestID = getParameter("requestID");

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
					contractor.setState(crr.getState());
					contractor.setRequestedBy(crr.getRequestedBy());
					contractor.setTaxId(crr.getTaxID());

					user = new User();
					user.setName(crr.getContact());
					user.setEmail(crr.getEmail());
					user.setPhone(crr.getPhone());
				} else {
					addActionError(getText("ContractorRegistration.error.AlreadyRegistered"));
					return SUCCESS;
				}
			}
		}

		return SUCCESS;
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
		userDAO.save(user);

		// requires id for user to exist to seed the password properly
		user.setEncryptedPassword(user.getPassword());
		userDAO.save(user);

		contractor.setAgreedBy(user);
		contractor.setPrimaryContact(user);
		contractorAccountDao.save(contractor);

		Permissions permissions = logInUser();
		setLoginLog(permissions);

		sendWelcomeEmail();
		addNote(contractor, "Welcome Email Sent");

		if (requestID > 0) {
			ContractorRegistrationRequest crr = updateRegistrationRequest();

			Note note = addNote(contractor, "Requested Contractor Registered");
			note.setBody("Contractor '" + crr.getName() + "' requested by " + crr.getRequestedBy().getName()
					+ " has registered.");
			dao.save(note);
		}

		return redirect(getRegistrationStep().getUrl());
	}

	private ContractorRegistrationRequest updateRegistrationRequest() {
		ContractorRegistrationRequest crr = requestDAO.find(requestID);
		crr.setContractor(contractor);
		crr.setMatchCount(1);
		crr.setAuditColumns();
		crr.setNotes(maskDateFormat(new Date()) + " - " + contractor.getPrimaryContact().getName()
				+ " - Account created through completing a Registration Request\n\n" + crr.getNotes());
		requestDAO.save(crr);
		return crr;
	}

	private void sendWelcomeEmail() throws EmailException, UnsupportedEncodingException, IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(2);
		emailBuilder.setUser(user);
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
		user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
		String confirmLink = "http://www.picsorganizer.com/Login.action?username="
				+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
		emailBuilder.addToken("confirmLink", confirmLink);
		emailBuilder.addToken("contactName", user.getName());
		emailBuilder.addToken("userName", user.getUsername());

		EmailQueue emailQueue = emailBuilder.build();
		emailQueue.setVeryHighPriority();
		emailQueue.setViewableById(Account.EVERYONE);
		emailSender.send(emailQueue);
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
		Permissions permissions = new Permissions();
		permissions.login(user);
		ActionContext.getContext().getSession().put("permissions", permissions);
		return permissions;
	}

	private void setupUserData() {
		user.setActive(true);
		user.setAccount(contractor);
		user.setTimezone(contractor.getTimezone());
		user.setLocale(ActionContext.getContext().getLocale());
		user.setAuditColumns(new User(User.CONTRACTOR));
		user.setIsGroup(YesNo.No);
		user.addOwnedPermissions(OpPerms.ContractorAdmin, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorSafety, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorInsurance, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorBilling, User.CONTRACTOR);
		user.setLastLogin(new Date());
	}

	private void setupContractorData() {
		contractor.setType("Contractor");
		if (contractor.getName().contains("^^^")) {
			contractor.setStatus(AccountStatus.Demo);
			contractor.setName(contractor.getName().replaceAll("^", "").trim());
		}
		if (contractor.getCountry().isHasStates() && state != null)
			contractor.setState(state);
		contractor.setLocale(ActionContext.getContext().getLocale());
		contractor.setPhone(user.getPhone());
		contractor.setPaymentExpires(new Date());
		contractor.setAuditColumns(new User(User.CONTRACTOR));
		contractor.setNameIndex();
		contractor.setQbSync(true);
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

	public boolean isUsernameInUse() {
		return userDAO.duplicateUsername(user.getUsername(), 0);
	}

	public boolean isCompanyNameInUse() {
		String indexedContractorName = Strings.indexName(contractor.getName());
		List<ContractorAccount> duplicateAccounts = contractorAccountDAO.findWhere("a.nameIndex = '"
				+ indexedContractorName + "'");
		return !duplicateAccounts.isEmpty();
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}
}
