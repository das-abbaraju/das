package com.picsauditing.actions.contractors;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
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
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorRegistration extends ContractorActionSupport {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private ContractorValidator contractorValidator;
	@Autowired
	private UserLoginLogDAO userLoginLogDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private EmailSenderSpring emailSender;

	protected User user;
	protected String password;
	protected String confirmPassword;
	protected int requestID;

	@Anonymous
	public String execute() throws Exception {
		loadPermissions(false);
		if (permissions.isLoggedIn() && !permissions.isDeveloperEnvironment()) {
			addActionError("You must logout before trying to register a new contractor account");
			return BLANK;
		}

		currentStep = ContractorRegistrationStep.Register;

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
					addActionError("You have already registered with PICS. <a href=\"Login.action\" title=\"Login to PICS\">"
							+ "Click here to log in</a>. If you forgot your login information, <a href=\"AccountRecovery.action\">"
							+ "click here to recover it</a>.");
					return SUCCESS;
				}
			}
		}

		return SUCCESS;
	}

	@Anonymous
	public String createAccount() throws Exception {
		contractor.setType("Contractor");
		Vector<String> errors = contractorValidator.validateContractor(contractor);
		errors.addAll(contractorValidator.validateUser(password, confirmPassword, user));
		if (Strings.isEmpty(user.getPassword()))
			errors.add("Please fill in the Password field.");

		errors.addAll(contractorValidator.verifyTaxID(contractor));

		if (!contractorValidator.verifyName(contractor)) {
			errors.add("The name <b>" + contractor.getName()
					+ "</b> already exists.  Please contact a PICS representative.");
		}

		if (errors.size() > 0) {
			for (String error : errors)
				addActionError(error);
			return SUCCESS;
		}

		if (contractor.getName().contains("^^^")) {
			contractor.setStatus(AccountStatus.Demo);
			contractor.setName(contractor.getName().replaceAll("^", "").trim());
		}

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

		contractor.setPaymentExpires(new Date());
		contractor.setAuditColumns(new User(User.CONTRACTOR));
		contractor.setNameIndex();
		contractor.setQbSync(true);
		contractor.setNaics(new Naics());
		contractor.getNaics().setCode("0");
		contractor.setNaicsValid(false);
		accountDao.save(contractor);

		user.setPhone(contractor.getPhone());
		user.setActive(true);
		user.setAccount(contractor);
		user.setTimezone(contractor.getTimezone());
		user.setAuditColumns(new User(User.CONTRACTOR));
		user.setIsGroup(YesNo.No);
		userDAO.save(user);
		// Initial password is stored unencrypted.
		// Need to perform a save to create a user id for
		// seeding the password.
		user.setEncryptedPassword(user.getPassword());
		userDAO.save(user);

		user.addOwnedPermissions(OpPerms.ContractorAdmin, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorSafety, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorInsurance, User.CONTRACTOR);
		user.addOwnedPermissions(OpPerms.ContractorBilling, User.CONTRACTOR);
		user.setLastLogin(new Date());
		userDAO.save(user);

		// Login the User
		Permissions permissions = new Permissions();
		permissions.login(user);
		ActionContext.getContext().getSession().put("permissions", permissions);

		// adding this user to the login log
		String remoteAddress = ServletActionContext.getRequest().getRemoteAddr();

		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setLoginDate(new Date());
		loginLog.setRemoteAddress(remoteAddress);
		loginLog.setSuccessful(permissions.isLoggedIn());
		loginLog.setUser(user);
		userLoginLogDAO.save(loginLog);

		// agreeing to contractor agreement terms as stated at the end of
		// con_registration.jsp
		contractor.setAgreedBy(user);
		contractor.setAgreementDate(contractor.getCreationDate());
		contractor.setPrimaryContact(user);
		// This should get taken care of by the xworks conversion
		// if (contractor.getState() != null)
		// contractor.setState(stateDAO.find(contractor.getState().getIsoCode()));
		// if (contractor.getCountry() != null)
		// contractor.setCountry(getCountryDAO().find(contractor.getCountry().getIsoCode()));
		accountDao.save(contractor);

		// Send the Welcome Email
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(2);
		emailBuilder.setUser(user);
		EmailQueue emailQueue = emailBuilder.build();
		emailQueue.setPriority(90);
		emailQueue.setViewableById(Account.EVERYONE);
		emailSender.send(emailQueue);
		addNote(contractor, "Welcome Email Sent");

		// Update the Registration Request
		if (requestID > 0) {
			ContractorRegistrationRequest crr = requestDAO.find(requestID);
			crr.setContractor(contractor);
			crr.setMatchCount(1);
			crr.setAuditColumns();
			crr.setHandledBy(WaitingOn.Operator);
			crr.setNotes(maskDateFormat(new Date()) + " - " + contractor.getPrimaryContact().getName()
					+ " - Account created through completing a Registration Request\n\n" + crr.getNotes());
			requestDAO.save(crr);

			Note note = addNote(contractor, "Requested Contractor Registered");
			note.setBody("Contractor '" + crr.getName() + "' requested by " + crr.getRequestedBy().getName()
					+ " has registered.");
			dao.save(note);
		}

		// Redirect to Step 2, usually Trades
		redirect(getRegistrationStep().getUrl(contractor.getId()));

		return BLANK;

	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	 * This shouldn't be getting called either. After the first step of registration the Contractor account is created
	 * and this should redirect to ConEdit
	 */
	@Override
	public String nextStep() throws Exception {
		return SUCCESS;
	}
}
