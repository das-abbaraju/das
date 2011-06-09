package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorEdit extends ContractorActionSupport implements Preparable {
	private File logo = null;
	private String logoFileName = null;
	private File brochure = null;
	private String brochureFileName = null;
	protected AuditQuestionDAO auditQuestionDAO;
	protected ContractorValidator contractorValidator;
	protected OperatorAccountDAO operatorAccountDAO;
	protected UserDAO userDAO;
	protected EmailQueueDAO emailQueueDAO;
	protected NoteDAO noteDAO;
	protected EmailSubscriptionDAO subscriptionDAO;
	protected UserSwitchDAO userSwitchDAO;
	protected List<Integer> operatorIds = new ArrayList<Integer>();
	protected Country country;
	protected State state;
	protected State billingState;
	protected int contactID;

	public ContractorEdit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO, ContractorValidator contractorValidator, UserDAO userDAO,
			OperatorAccountDAO operatorAccountDAO, EmailQueueDAO emailQueueDAO, NoteDAO noteDAO,
			EmailSubscriptionDAO subscriptionDAO, UserSwitchDAO userSwitchDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
		this.contractorValidator = contractorValidator;
		this.userDAO = userDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.emailQueueDAO = emailQueueDAO;
		this.noteDAO = noteDAO;
		this.subscriptionDAO = subscriptionDAO;
		this.userSwitchDAO = userSwitchDAO;

		this.currentStep = ContractorRegistrationStep.EditAccount;
	}

	public void prepare() throws Exception {
		getPermissions();
		if (permissions.isLoggedIn()) {
			int conID = 0;
			if (permissions.isContractor())
				conID = permissions.getAccountId();
			else {
				permissions.tryPermission(OpPerms.AllContractors);
				conID = getParameter("id");
			}
			if (conID > 0) {
				contractor = accountDao.find(conID);

				BillingCalculatorSingle.calculateAnnualFees(contractor);
				contractor.syncBalance();
				for (ContractorOperator conOperator : contractor.getNonCorporateOperators()) {
					operatorIds.add(conOperator.getOperatorAccount().getId());
				}
			}

			String[] countryIsos = (String[]) ActionContext.getContext().getParameters().get("country.isoCode");
			if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0]))
				country = getCountryDAO().find(countryIsos[0]);

			String[] stateIsos = (String[]) ActionContext.getContext().getParameters().get("state.isoCode");
			if (stateIsos != null && stateIsos.length > 0 && !Strings.isEmpty(stateIsos[0]))
				state = getStateDAO().find(stateIsos[0]);

			String[] billingStateIsos = (String[]) ActionContext.getContext().getParameters().get(
					"billingState.isoCode");
			if (billingStateIsos != null && billingStateIsos.length > 0 && !Strings.isEmpty(billingStateIsos[0]))
				billingState = getStateDAO().find(billingStateIsos[0]);
		}
	}

	public String execute() throws Exception {
		if (button != null) {
			String ftpDir = getFtpDir();

			if (button.equalsIgnoreCase("Save")) {
				if (permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorAccounts, OpType.Edit)) {
					if (logo != null) {
						String extension = logoFileName.substring(logoFileName.lastIndexOf(".") + 1);
						String[] validExtensions = { "jpg", "gif", "png" };

						if (!FileUtils.checkFileExtension(extension, validExtensions)) {
							addActionError("Logos must be a jpg, gif or png image");
							return SUCCESS;
						}
						String fileName = "logo_" + contractor.getId();
						FileUtils.moveFile(logo, ftpDir, "/logos/", fileName, extension, true);
						contractor.setLogoFile(fileName + "." + extension);
					}

					if (brochure != null) {
						String extension = brochureFileName.substring(brochureFileName.lastIndexOf(".") + 1);
						String[] validExtensions = { "jpg", "gif", "png", "doc", "pdf" };

						if (!FileUtils.checkFileExtension(extension, validExtensions)) {
							addActionError("Brochure must be a image, doc or pdf file");
							return SUCCESS;
						}
						String fileName = "brochure_" + contractor.getId();
						FileUtils.moveFile(brochure, ftpDir, "/files/brochures/", fileName, extension, true);
						contractor.setBrochureFile(extension);
					}

					if (country != null && !country.equals(contractor.getCountry())) {
						contractor.setCountry(country);
					}

					if (state != null && !state.equals(contractor.getState())) {
						contractor.setState(state);
					}

					if (billingState != null && !"".equals(billingState.getIsoCode())
							&& !billingState.equals(contractor.getBillingState())) {
						contractor.setBillingState(billingState);
					}

					Vector<String> errors = contractorValidator.validateContractor(contractor);

					if (contractor.getAccountLevel().equals(AccountLevel.ListOnly)) {
						// Now check if they have a product risk level
						if (!contractor.isMaterialSupplierOnly()
								|| (contractor.getProductRisk() != null && !contractor.getProductRisk().equals(
										LowMedHigh.Low))) {
							errors.addElement("Only Low Product Risk and Material Supplier Only (not Onsite or "
									+ "Offsite) contractor accounts can be set to List Only. Please verify contractor "
									+ "information before setting List Only status.");
						} else if (contractor.getProductRisk() == null) {
							// Contractor doesn't have a product risk. Set to material supplier and add open task to
							// answer the product questions on the PQF
							errors.addElement("This contractor does not have a Product Risk.");
							// TODO add an open task for the contractor?
						}
					}

					if (errors.size() > 0) {
						for (String error : errors)
							addActionError(error);
						// TODO I don't know if this is the right answer here, but we don't want to save anything if
						// there are errors.
						accountDao.refresh(contractor);
						return SUCCESS;
					}
					contractor.setQbSync(true);
					contractor.incrementRecalculation();
					contractor.setNameIndex();

					if (contactID > 0 && contactID != contractor.getPrimaryContact().getId()) {
						contractor.setPrimaryContact(userDAO.find(contactID));
					}
					// contractor.setNeedsIndexing(true);
					accountDao.save(contractor);

					addActionMessage("Successfully modified " + contractor.getName());
				}
			} else if (button.equalsIgnoreCase("Delete")) {
				permissions.tryPermission(OpPerms.RemoveContractors);
				findContractor();

				Iterator<ContractorAudit> auditList = contractor.getAudits().iterator();
				while (auditList.hasNext()) {
					ContractorAudit cAudit = auditList.next();
					if (!cAudit.hasCaoStatusAfter(AuditStatus.Pending)) {
						auditList.remove();
						auditDao.remove(cAudit);
					}
				}

				if (contractor.getAudits().size() > 0) {
					addActionError("Cannot Remove Contractor with Audits");
					return SUCCESS;
				}

				Iterator<User> userList = contractor.getUsers().iterator();

				while (userList.hasNext()) {
					User user = userList.next();
					userList.remove();
					userDAO.remove(user);
				}

				accountDao.remove(contractor, getFtpDir());

				return "ConList";
			} else if (button.equals("SendDeactivationEmail")) {
				permissions.tryPermission(OpPerms.EmailOperators);
				Set<String> emailAddresses = new HashSet<String>();
				if (operatorIds != null) {
					for (int operatorID : operatorIds) {
						OperatorAccount operator = operatorAccountDAO.find(operatorID);

						List<EmailSubscription> subscriptions = subscriptionDAO.find(
								Subscription.ContractorDeactivation, operatorID);

						OperatorAccount parent = operator.getParent();
						while (parent != null) { // adding corporate
							// subscriptions
							subscriptions.addAll(subscriptionDAO.find(Subscription.ContractorDeactivation, parent
									.getId()));
							parent = parent.getParent();
						}

						Set<String> emails = new HashSet<String>();
						boolean subscribed = false;
						for (EmailSubscription subscription : subscriptions) {
							if (!subscription.getTimePeriod().equals(SubscriptionTimePeriod.None)) {
								emails.add(subscription.getUser().getEmail());
								subscribed = true;
							}
						}

						// only want to access dao if no subscription exists
						// (very slow operation)
						// sending email to primary contact if no subscribers
						// exist
						if (!subscribed && operator != null && operator.getPrimaryContact() != null)
							emails.add(operator.getPrimaryContact().getEmail());

						if (emails.size() > 0)
							emailAddresses.addAll(emails);
						else
							addActionError("No primary contact or 'Contractor Registration' Subscriber for Operator ID: "
									+ operatorID);
					}

					if (emailAddresses.size() > 0) {
						if (!accountDao.isContained(contractor)) {
							contractor = accountDao.find(contractor.getId());
						}
						EmailBuilder emailBuilder = new EmailBuilder();
						emailBuilder.setTemplate(51); // Deactivation Email for
						// operators
						emailBuilder.setPermissions(permissions);
						emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
						emailBuilder.setBccAddresses(Strings.implode(emailAddresses, ","));
						emailBuilder.setCcAddresses("");
						emailBuilder.setToAddresses("billing@picsauditing.com");
						emailBuilder.setFromAddress("\"PICS Billing\"<billing@picsauditing.com>");
						EmailQueue email = emailBuilder.build();
						email.setViewableById(Account.PicsID);
						email.setPriority(50);
						emailQueueDAO.save(email);

						Note note = new Note();
						note.setAccount(contractor);
						note.setAuditColumns(permissions);
						note.setSummary("Deactivation Email Sent to the following email Addresses ");
						note.setBody(Strings.implode(emailAddresses, ", "));
						note.setPriority(LowMedHigh.Med);
						note.setNoteCategory(NoteCategory.General);
						note.setViewableById(Account.PicsID);
						note.setCanContractorView(false);
						note.setStatus(NoteStatus.Closed);
						noteDAO.save(note);

						this.addActionMessage("Successfully sent the email to operators");
					}
				}
			} else if (button.equals("copyPrimary")) {
				contractor.setBillingAddress(contractor.getAddress());
				contractor.setBillingCity(contractor.getCity());
				contractor.setBillingState(contractor.getState());
				contractor.setBillingZip(contractor.getZip());
				accountDao.save(contractor);
			} else {
				// Because there are anomalies between browsers and how they
				// pass
				// in the button values, this is a catch all so we can get
				// notified
				// when the button name isn't set correctly
				throw new Exception("no button action found called " + button);
			}
		}
		this.subHeading = "Contractor Edit";

		return SUCCESS;
	}

	public String deactivate() throws Exception {
		if (Strings.isEmpty(contractor.getReason())) {
			addActionError("Please select a deactivation reason before you cancel the account");
		} else {
			contractor.setRenew(false);
			if (contractor.isHasFreeMembership())
				contractor.setStatus(AccountStatus.Deactivated);

			String expiresMessage = "";
			if (contractor.getPaymentExpires().after(new Date()))
				expiresMessage = " This account will no longer be visible to operators after "
						+ contractor.getPaymentExpires();
			else {
				expiresMessage = " This account is no longer visible to operators.";
				contractor.setStatus(AccountStatus.Deactivated);
			}
			accountDao.save(contractor);

			this.addNote(contractor, "Closed contractor account." + expiresMessage);
			this.addActionMessage("Successfully closed this contractor account." + expiresMessage);
		}
		this.subHeading = "Contractor Edit";
		return SUCCESS;
	}

	public String reactivate() throws Exception {
		contractor.setRenew(true);
		if (contractor.isHasFreeMembership())
			contractor.setStatus(AccountStatus.Active);

		contractor.setReason("");
		accountDao.save(contractor);
		this.addNote(contractor, "Reactivated account");
		this.addActionMessage("Successfully reactivated this contractor account. "
				+ "<a href='BillingDetail.action?id=" + id + "'>Click to Create their invoice</a>");
		return SUCCESS;
	}

	public void setLogo(File logo) {
		this.logo = logo;
	}

	public void setBrochure(File brochure) {
		this.brochure = brochure;
	}

	public void setLogoContentType(String logoContentType) {
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}

	public void setBrochureContentType(String brochureContentType) {
	}

	public void setBrochureFileName(String brochureFileName) {
		this.brochureFileName = brochureFileName;
	}

	public List<Integer> getOperatorIds() {
		return operatorIds;
	}

	public void setOperatorIds(List<Integer> operatorIds) {
		this.operatorIds = operatorIds;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getBillingState() {
		return billingState;
	}

	public void setBillingState(State billingState) {
		this.billingState = billingState;
	}

	public List<Invoice> getUnpaidInvoices() {
		List<Invoice> unpaidInvoices = new ArrayList<Invoice>();
		if (!contractor.isRenew()) {
			for (Invoice item : contractor.getSortedInvoices()) {
				if (item.getStatus().isUnpaid()) {
					unpaidInvoices.add(item);
				}
			}

		}
		return unpaidInvoices;
	}

	public String[] getDeactivationReasons() {
		return ReportFilterContractor.DEACTIVATION_REASON;
	}

	public List<User> getUserList() {
		Set<User> primaryContactSet = new TreeSet<User>();

		primaryContactSet.addAll(userDAO.findByAccountID(contractor.getId(), "Yes", "No"));

		// Include users that can switch to groups
		Set<User> groupSet = new HashSet<User>();
		groupSet.addAll(userDAO.findByAccountID(contractor.getId(), "Yes", "Yes"));

		Set<User> switchToSet = new HashSet<User>();
		// Adding users that can switch to users on account
		for (User u : primaryContactSet)
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		// Adding users that can switch to groups on account
		for (User u : groupSet)
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		// Adding all SwitchTo users to primary contacts
		primaryContactSet.addAll(switchToSet);

		return new ArrayList<User>(primaryContactSet);
	}

	public int getContactID() {
		return contactID;
	}

	public void setContactID(int contactID) {
		this.contactID = contactID;
	}

	public boolean isCanEditRiskLevel() {
		if (contractor.getStatus().isDemo())
			return true;
		if (permissions.hasPermission(OpPerms.RiskRank))
			return true;
		return false;
	}

	/**
	 * During Registration ConRegistration becomes ConEdit after the first step has been completed.
	 */
	@Override
	public ContractorRegistrationStep getPreviousRegistrationStep() {
		return null;
	}

	@Override
	public String nextStep() throws Exception {
		redirect(ContractorRegistrationStep.Trades.getUrl(contractor.getId()));
		return SUCCESS;
	}

	@Override
	public String previousStep() throws Exception {
		redirect(ContractorRegistrationStep.EditAccount.getUrl(contractor.getId()));
		return SUCCESS;
	}
}
