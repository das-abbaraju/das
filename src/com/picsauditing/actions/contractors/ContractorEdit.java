package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.PICS.data.ContractorDataEvent;
import com.picsauditing.PICS.data.DataEvent;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PICS.data.InvoiceDataEvent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
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
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorEdit extends ContractorActionSupport implements Preparable {
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;
	@Autowired
	protected ContractorValidator contractorValidator;
	@Autowired
	protected CountryDAO countryDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected EmailQueueDAO emailQueueDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	protected StateDAO stateDAO;
	@Autowired
	protected EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	protected UserSwitchDAO userSwitchDAO;
	@Autowired
	protected BillingCalculatorSingle billingService;
	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;
	@Autowired
	private DataObservable saleCommissionDataObservable;

	private File logo = null;
	private String logoFileName = null;
	private File brochure = null;
	private String brochureFileName = null;
	private State state;
	private Country country;

	protected List<Integer> operatorIds = new ArrayList<Integer>();
	protected int contactID;

	private List<ContractorType> conTypes = new ArrayList<ContractorType>();
	private String contractorTypeHelpText = "";

	private CountrySubdivision countrySubdivision;
	private HttpServletRequest request;

	public void prepare() throws Exception {
		if (permissions.isLoggedIn()) {
			int conID = 0;
			if (permissions.isContractor())
				conID = permissions.getAccountId();
			else {
				permissions.tryPermission(OpPerms.AllContractors);
				conID = getParameter("id");
			}
			if (conID > 0) {
				contractor = contractorAccountDao.find(conID);

				billingService.calculateAnnualFees(contractor);
				contractor.syncBalance();
				for (ContractorOperator conOperator : contractor.getNonCorporateOperators()) {
					operatorIds.add(conOperator.getOperatorAccount().getId());
				}
			}

			String[] countryIsos = (String[]) ActionContext.getContext().getParameters()
					.get("contractor.country.isoCode");
			if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0]))
				contractor.setCountry(countryDAO.find(countryIsos[0]));

			String[] billingCountryIsos = (String[]) ActionContext.getContext().getParameters()
					.get("contractor.billingCountry.isoCode");
			if (billingCountryIsos != null && billingCountryIsos.length > 0 && !Strings.isEmpty(billingCountryIsos[0]))
				contractor.setBillingCountry(countryDAO.find(billingCountryIsos[0]));

			defaultConTypeHelpText();
		}
	}

	private void defaultConTypeHelpText() {
		if (contractor.isContractorTypeRequired(ContractorType.Onsite))
			contractorTypeHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Onsite.getI18nKey()), StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Onsite), ", "));
		if (contractor.isContractorTypeRequired(ContractorType.Offsite))
			contractorTypeHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Offsite.getI18nKey()), StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Offsite), ", "));
		if (contractor.isContractorTypeRequired(ContractorType.Supplier))
			contractorTypeHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Supplier.getI18nKey()), StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Supplier), ", "));
		if (contractor.isContractorTypeRequired(ContractorType.Transportation))
			contractorTypeHelpText += getTextParameterized(
					"RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Transportation.getI18nKey()),
					StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Transportation), ", "));
	}

	@Before
	public void startup() throws Exception {
		this.subHeading = getText("ContractorEdit.subheading");

		findContractor();
		// Billing state gets set to an empty string
		if (contractor.getBillingState() != null && Strings.isEmpty(contractor.getBillingState().getIsoCode()))
			contractor.setBillingState(null);
	}

	public String save() throws Exception {
		String ftpDir = getFtpDir();

		if (permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorAccounts, OpType.Edit)) {
			if (logo != null) {
				String extension = logoFileName.substring(logoFileName.lastIndexOf(".") + 1);
				String[] validExtensions = { "jpg", "gif", "png" };

				if (!FileUtils.checkFileExtension(extension, validExtensions)) {
					addActionError(getText("ContractorEdit.error.LogoFormat"));
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
					addActionError(getText("ContractorEdit.error.BrochureFormat"));
					return SUCCESS;
				}
				String fileName = "brochure_" + contractor.getId();
				FileUtils.moveFile(brochure, ftpDir, "/files/brochures/", fileName, extension, true);
				contractor.setBrochureFile(extension);
			}

			// account for disabled checkboxes not coming though
			// but only if populated/presented
			if (!permissions.isContractor()) {
				if (contractor.isContractorTypeRequired(ContractorType.Onsite))
					conTypes.add(ContractorType.Onsite);
				if (contractor.isContractorTypeRequired(ContractorType.Offsite))
					conTypes.add(ContractorType.Offsite);
				if (contractor.isContractorTypeRequired(ContractorType.Supplier))
					conTypes.add(ContractorType.Supplier);
				if (contractor.isContractorTypeRequired(ContractorType.Transportation))
					conTypes.add(ContractorType.Transportation);

				contractor.setAccountTypes(conTypes);
				contractor.resetRisksBasedOnTypes();

				if (!conTypesOK()) {
					return SUCCESS;
				}
			}

			if ((!contractor.getCountry().equals(country) && country != null)
					|| (contractor.getState() !=null && !contractor.getState().equals(state) && state != null)) {
				contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);
				stampContractorNoteAboutOfficeLocationChange();
			}

			if (country != null && !country.equals(contractor.getCountry())) {
				contractor.setCountry(country);
			}

			if ((state != null && !state.equals(contractor.getState())) || (contractor.getState()==null&& state!=null)) {
				State contractorState = stateDAO.find(state.toString());
				contractor.setState(contractorState);
			}

			if (contractor.getCountry().isHasStates() && state != null) {
				updateStateAndCountrySubdivision();
			} else {
				contractor.setState(null);
				contractor.setCountrySubdivision(null);
			}

			addNoteWhenStatusChange();

			Vector<String> errors = contractorValidator.validateContractor(contractor);

			if (contractor.getAccountLevel().equals(AccountLevel.ListOnly)) {
				// Now check if they have a product risk level
				if (!contractor.isListOnlyEligible()) {
					errors.addElement(getText("ContractorEdit.error.ListOnlyRequirements"));
				}
				for (ContractorOperator co : contractor.getNonCorporateOperators()) {
					List<String> nonListOnlyOperators = new ArrayList<String>();
					if (!co.getOperatorAccount().isAcceptsList())
						nonListOnlyOperators.add(co.getOperatorAccount().getName());

					if (!nonListOnlyOperators.isEmpty())
						errors.addElement(this.getTextParameterized("ContractorEdit.error.OperatorsDoneAcceptList",
								Strings.implode(nonListOnlyOperators)));
				}
			}

			if (errors.size() > 0) {
				for (String error : errors)
					addActionError(error);

				return SUCCESS;
			}
			contractor.setQbSync(true);
			contractor.incrementRecalculation();
			contractor.setNameIndex();

			if (contactID > 0 && contactID != contractor.getPrimaryContact().getId()) {
				contractor.setPrimaryContact(userDAO.find(contactID));
			}
			// contractor.setNeedsIndexing(true);

			contractorAccountDao.save(contractor);

			addActionMessage(this.getTextParameterized("ContractorEdit.message.SaveContractor", contractor.getName()));
		}
		
		if (contractor.getStatus().isDeactivated()) {
			// we only care about deactivation, because reactivation can be determine through the contractor's billing status 
			notifyDataChange(new ContractorDataEvent(contractor, ContractorDataEvent.ContractorEventType.DEACTIVATION));
		}

		return SUCCESS;
	}

	private void addNoteWhenStatusChange() {
		request = ServletActionContext.getRequest();

		if (!request.getParameter("currentStatus").equals(contractor.getStatus().toString())) {
			this.addNote(contractor, "Account Status changed from" + request.getParameter("currentStatus") + " to "
					+ contractor.getStatus().toString());
		}
	}

	private void updateStateAndCountrySubdivision() {
		if (contractor.getCountry().equals(contractor.getState().getCountry())) {
			countrySubdivision = countrySubdivisionDAO.find(contractor.getCountry().getIsoCode() + "-"
					+ contractor.getState().getIsoCode());
			if (countrySubdivision != null) {
				contractor.setCountrySubdivision(countrySubdivision);
			} else {
				contractor.setCountrySubdivision(null);
			}
		} else {
			contractor.setState(null);
			contractor.setCountrySubdivision(null);
		}
	}

	private void stampContractorNoteAboutOfficeLocationChange() {
		User system = new User();
		system.setId(User.SYSTEM);
		Note pqfOfficeLocationChange = new Note(contractor, system, getText("AuditData.officeLocationSet.summary"));
		pqfOfficeLocationChange.setNoteCategory(NoteCategory.General);
		if (contractor.getCountry().isHasStates()) {
			pqfOfficeLocationChange.setBody(getTextParameterized("AuditData.officeLocationSet",
					getText(state.getI18nKey())));
		}
		pqfOfficeLocationChange.setId(0);
		pqfOfficeLocationChange.setCanContractorView(true);
		noteDao.save(pqfOfficeLocationChange);
	}

	@RequiredPermission(value = OpPerms.RemoveContractors)
	public String delete() throws Exception {
		for (User user : contractor.getUsers()) {
			user.setActive(false);
			userDAO.save(user);
		}

		contractor.setStatus(AccountStatus.Deleted);
		contractorAccountDao.save(contractor);
		
		notifyDataChange(new ContractorDataEvent(contractor, ContractorDataEvent.ContractorEventType.DEACTIVATION));

		return "ConList";
	}

	@RequiredPermission(value = OpPerms.EmailOperators)
	public String sendDeactivationEmail() throws Exception {
		Set<String> emailAddresses = new HashSet<String>();
		if (operatorIds != null) {
			for (int operatorID : operatorIds) {
				OperatorAccount operator = operatorAccountDAO.find(operatorID);

				List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.ContractorDeactivation,
						operatorID);

				OperatorAccount parent = operator.getParent();
				while (parent != null) { // adding corporate
					// subscriptions
					subscriptions.addAll(subscriptionDAO.find(Subscription.ContractorDeactivation, parent.getId()));
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
					addActionError(getTextParameterized("ContractorEdit.error.NoPrimaryContact", operatorID));
			}

			if (emailAddresses.size() > 0) {
				if (!contractorAccountDao.isContained(contractor)) {
					contractor = contractorAccountDao.find(contractor.getId());
				}
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(51); // Deactivation Email for
				// operators
				emailBuilder.setPermissions(permissions);
				emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
				emailBuilder.setBccAddresses(Strings.implode(emailAddresses, ","));
				emailBuilder.setCcAddresses("");
				String billingEmail = EmailAddressUtils.getBillingEmail(contractor.getCurrency());
				emailBuilder.setToAddresses(billingEmail);
				emailBuilder.setFromAddress(billingEmail);
				EmailQueue email = emailBuilder.build();
				email.setViewableById(Account.PicsID);
				email.setMediumPriority();
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

				this.addActionMessage(getText("ContractorEdit.message.EmailSent"));
			}
		}

		return SUCCESS;
	}

	public String copyPrimary() throws Exception {
		contractor.setBillingAddress(contractor.getAddress());
		contractor.setBillingCity(contractor.getCity());
		contractor.setBillingState(contractor.getState());
		contractor.setBillingCountry(contractor.getCountry());
		contractor.setBillingZip(contractor.getZip());
		contractorAccountDao.save(contractor);

		return SUCCESS;
	}

	public String deactivate() throws Exception {
		if (Strings.isEmpty(contractor.getReason())) {
			addActionError(getText("ContractorEdit.error.DeactivationReason"));
		} else {
			contractor.setRenew(false);
			if (contractor.isHasFreeMembership())
				contractor.setStatus(AccountStatus.Deactivated);

			String expiresMessage = "";
			if (contractor.getPaymentExpires().after(new Date()))
				expiresMessage = this.getTextParameterized("ContractorEdit.message.AccountExpires",
						contractor.getPaymentExpires());
			else {
				expiresMessage = getText("ContractorEdit.message.AccountDeactivated");
				contractor.setStatus(AccountStatus.Deactivated);
			}
			contractorAccountDao.save(contractor);

			this.addNote(contractor, "Closed contractor account." + expiresMessage);
			this.addActionMessage(this.getTextParameterized("ContractorEdit.message.AccountClosed", expiresMessage));
		}
		this.subHeading = "Contractor Edit";
		
		notifyDataChange(new ContractorDataEvent(contractor, ContractorDataEvent.ContractorEventType.DEACTIVATION));
		
		return SUCCESS;
	}

	public String reactivate() throws Exception {
		contractor.setRenew(true);
		if (contractor.isHasFreeMembership())
			contractor.setStatus(AccountStatus.Active);

		contractor.setReason("");
		contractorAccountDao.save(contractor);
		addNote(contractor, "Reactivated account");
		addActionMessage(this.getTextParameterized("ContractorEdit.message.AccountReactivated", id));
		return SUCCESS;
	}

	public String createImportPQF() throws Exception {
		billingService.addImportPQF(contractor, permissions);
		addActionMessage(getText("ContractorEdit.message.CreatedImportPQF"));

		return SUCCESS;
	}

	public String expireImportPQF() throws Exception {
		billingService.removeImportPQF(contractor);
		addActionMessage(getText("ContractorEdit.message.RemovedImportPQF"));

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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
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

	public Map<String, String> getDeactivationReasons() {
		return ReportFilterContractor.getDeactivationReasons();
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

	public boolean isHasImportPQFAudit() {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired())
				return true;
		}

		return false;
	}

	public List<ContractorType> getConTypes() {
		return conTypes;
	}

	public void setConTypes(List<ContractorType> conTypes) {
		this.conTypes = conTypes;
	}

	public boolean conTypesOK() {
		boolean conTypesOK = true;
		boolean meetsOperatorsRequirements = false;
		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			meetsOperatorsRequirements = false;
			for (ContractorType conType : operator.getAccountTypes()) {
				if (conTypes.contains(conType))
					meetsOperatorsRequirements = true;
			}

			if (!meetsOperatorsRequirements) {
				String msg = operator.getName() + " requires you to select "
						+ StringUtils.join(operator.getAccountTypes(), " or ");
				addActionError(msg);
				conTypesOK = false;
			}
		}
		return conTypesOK;
	}

	public String getContractorTypeHelpText() {
		return contractorTypeHelpText;
	}

	public void setContractorTypeHelpText(String contractorTypeHelpText) {
		this.contractorTypeHelpText = contractorTypeHelpText;
	}
	
	private <T> void notifyDataChange(DataEvent<T> dataEvent) {
		saleCommissionDataObservable.setChanged();
		saleCommissionDataObservable.notifyObservers(dataEvent);
	}
}
