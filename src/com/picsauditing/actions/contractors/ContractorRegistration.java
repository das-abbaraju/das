package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorRegistration extends ContractorActionSupport {
	protected ContractorAccount contractor;
	protected User user;
	protected String password;
	protected String confirmPassword;
	protected int requestID;

	protected UserDAO userDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	protected NoteDAO noteDAO;
	protected ContractorRegistrationRequestDAO requestDAO;
	protected ContractorValidator contractorValidator;
	protected FacilityChanger facilityChanger;

	protected Country country;

	public ContractorRegistration(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO, ContractorValidator contractorValidator, NoteDAO noteDAO,
			UserDAO userDAO, ContractorRegistrationRequestDAO requestDAO, FacilityChanger facilityChanger) {
		super(accountDao, auditDao);
		this.auditQuestionDAO = auditQuestionDAO;
		this.contractorValidator = contractorValidator;
		this.noteDAO = noteDAO;
		this.userDAO = userDAO;
		this.requestDAO = requestDAO;
		//this.subHeading = "New Contractor Information";
		this.facilityChanger = facilityChanger;
	}

	public String execute() throws Exception {
		loadPermissions(false);
		if (permissions.isLoggedIn()) {
			addActionError("You must logout before trying to register a new contractor account");
			return BLANK;
		}
		
		if ("request".equalsIgnoreCase(button)) {
			// check for basic rID...?
			if (getParameter("rID") > 0)
				requestID = getParameter("rID");
			// Check for new requestID
			if (requestID == 0)
				requestID = getParameter("requestID");
			
			if (requestID > 0) {
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

		if ("Create Account".equalsIgnoreCase(button)) {
			contractor.setType("Contractor");
			Vector<String> errors = contractorValidator.validateContractor(contractor);
			errors.addAll(contractorValidator.validateUser(password, confirmPassword, user));
			if (Strings.isEmpty(user.getPassword()))
				errors.add("Please fill in the Password field.");

			if (!contractorValidator.verifyTaxID(contractor)) {
				errors.add("The tax ID <b>" + contractor.getTaxId()
						+ "</b> already exists.  Please contact a PICS representative.");
			}

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
			contractor.setMembershipLevel(new InvoiceFee(InvoiceFee.FREE));
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
			userDAO.save(user);

			// agreeing to contractor agreement terms as stated at the end of
			// con_registration.jsp
			contractor.setAgreedBy(user);
			contractor.setAgreementDate(contractor.getCreationDate());
			contractor.setPrimaryContact(user);
			accountDao.save(contractor);

			// Create a blank PQF for this contractor
			ContractorAudit audit = new ContractorAudit();
			audit.setContractorAccount(contractor);
			audit.setAuditType(new AuditType(1));
			audit.setAuditColumns(new User(User.SYSTEM));
			addAuditCategories(audit, 2); // COMPANY INFORMATION
			addAuditCategories(audit, 8); // GENERAL INFORMATION
			addAuditCategories(audit, AuditCategory.SERVICES_PERFORMED);
			addAuditCategories(audit, 184); // SUPPLIER DIVERSITY
			auditDao.save(audit);

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(2); // Welcome Email
			emailBuilder.setUser(user);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(90);
			emailQueue.setFromAddress("PICS Mailer <info@picsauditing.com>");
			emailQueue.setViewableById(Account.EVERYONE);
			EmailSender.send(emailQueue);

			Note note = new Note();
			note.setAccount(contractor);
			note.setAuditColumns(new User(User.SYSTEM));
			note.setSummary("Welcome Email Sent");
			note.setPriority(LowMedHigh.Low);
			note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);

			Permissions permissions = new Permissions();
			permissions.login(user);
			ActionContext.getContext().getSession().put("permissions", permissions);

			// Update the Registration Request
			if (requestID > 0) {
				ContractorRegistrationRequest crr = requestDAO.find(requestID);
				crr.setContractor(contractor);
				crr.setMatchCount(1);
				crr.setOpen(false);
				crr.setAuditColumns();
				crr.setHandledBy(WaitingOn.Operator);
				crr.setNotes(maskDateFormat(new Date()) + " - " + contractor.getPrimaryContact().getName() + 
						" - Account created through completing a Registration Request\n\n" + crr.getNotes());
				requestDAO.save(crr);

				note = new Note();
				note.setAccount(contractor);
				note.setAuditColumns(new User(User.SYSTEM));
				note.setSummary("Requested Contractor Registered");
				note.setBody("Contractor '" + crr.getName() + "' requested by " + crr.getRequestedBy().getName()
						+ " has registered.");
				note.setPriority(LowMedHigh.Low);
				note.setViewableById(Account.EVERYONE);
				noteDAO.save(note);
			}
			
			if (contractor.isMaterialSupplier()) {
				redirect("ContractorFacilities.action?id=" + contractor.getId() + 
						(requestID > 0 ? "&requestID=" + requestID : ""));
			}

			redirect("ContractorRegistrationServices.action?id=" + contractor.getId() + 
					(requestID > 0 ? "&requestID=" + requestID : ""));
			return BLANK;
		}

		return SUCCESS;
	}

	public List<AuditQuestion> getTradeList() throws Exception {
		return auditQuestionDAO.findQuestionByType("Service");
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

	public Country getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = getCountryDAO().find(country);
	}

	public void setCountry(Country country) {
		this.country = country;
	}
	
	public int getRequestID() {
		return requestID;
	}
	
	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public void addAuditCategories(ContractorAudit audit, int CategoryID) {
		AuditCatData catData = new AuditCatData();
		catData.setCategory(new AuditCategory());
		catData.getCategory().setId(CategoryID);
		catData.setAudit(audit);
		catData.setApplies(YesNo.Yes);
		catData.setOverride(false);
		catData.setNumRequired(1);
		catData.setAuditColumns(new User(User.SYSTEM));
		audit.getCategories().add(catData);
	}
}
