package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.User;
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

	protected UserDAO userDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	protected NoteDAO noteDAO;
	protected ContractorValidator contractorValidator;

	protected Country country;

	public ContractorRegistration(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO, ContractorValidator contractorValidator, NoteDAO noteDAO, UserDAO userDAO) {
		super(accountDao, auditDao);
		this.auditQuestionDAO = auditQuestionDAO;
		this.contractorValidator = contractorValidator;
		this.noteDAO = noteDAO;
		this.userDAO = userDAO;
		this.subHeading = "New Contractor Information";
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {

		if ("Create Account".equalsIgnoreCase(button)) {
			contractor.setType("Contractor");
			contractor.setActive('N');
			Vector<String> errors = contractorValidator.validateContractor(contractor, password, confirmPassword, user);
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

			// Default their current membership to 0
			contractor.setMembershipLevel(new InvoiceFee(InvoiceFee.FREE));
			contractor.setPaymentExpires(new Date());
			contractor.setAuditColumns(new User(User.CONTRACTOR));
			contractor.setNameIndex();
			contractor.setQbSync(true);
			contractor.setNaics(new Naics());
			contractor.getNaics().setCode("0");
			contractor.setNaicsValid(false);
			contractor = accountDao.save(contractor);
			user.setIsActive(YesNo.Yes);
			user.setAccount(contractor);
			user.setAuditColumns(new User(User.CONTRACTOR));
			user.setIsGroup(YesNo.No);
			user.setName(contractor.getContact());
			user.setEmail(contractor.getEmail());
			user = userDAO.save(user);
			// Need to perform a save to create a user id for seeding the
			// password.
			// Initial password is stored unencrypted.
			user.setEncryptedPassword(user.getPassword());
			userDAO.save(user);
			contractor.setUsers(new ArrayList<User>());
			contractor.getUsers().add(user);

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
			emailBuilder.setContractor(contractor);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(90);
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

			redirect("ContractorRegistrationServices.action?id=" + contractor.getId());
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
