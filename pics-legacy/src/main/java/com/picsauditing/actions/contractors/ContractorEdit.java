package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.FeeService;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.*;
import com.picsauditing.util.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.validator.ContractorValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;



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
	protected EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	protected UserSwitchDAO userSwitchDAO;
	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;
    @Autowired
	private AccountStatusChanges accountStatusChanges;
    @Autowired
    protected BillingService billingService;
    @Autowired
    protected FeeService feeService;

	private SapAppPropertyUtil sapAppPropertyUtil;

	private File logo = null;
	private String logoFileName = null;
	private File brochure = null;
	private String brochureFileName = null;
	private CountrySubdivision countrySubdivision;
	private Country country;
	private String vatId;
	private int csrId;

	protected List<Integer> operatorIds = new ArrayList<Integer>();
	protected int contactID;
	private int insideSalesId;

	private List<ContractorType> conTypes = new ArrayList<ContractorType>();

	private String contractorTypeHelpText = "";
	private HttpServletRequest request;

	public void prepare() throws Exception {
		if (sapAppPropertyUtil == null) {
			sapAppPropertyUtil = SapAppPropertyUtil.factory();
		}
		if (permissions.isLoggedIn()) {
			int conID = 0;
			if (permissions.isContractor()) {
				conID = permissions.getAccountId();
			} else {
				permissions.tryPermission(OpPerms.AllContractors);
				conID = getParameter("id");
			}
			if (conID > 0) {
				contractor = contractorAccountDao.find(conID);

				feeService.calculateContractorInvoiceFees(contractor);
                billingService.syncBalance(contractor);
				for (ContractorOperator conOperator : contractor.getNonCorporateOperators()) {
					operatorIds.add(conOperator.getOperatorAccount().getId());
				}
			}

			String[] countryIsos = (String[]) ActionContext.getContext().getParameters()
					.get("contractor.country.isoCode");
			if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0])) {
				contractor.setCountry(countryDAO.find(countryIsos[0]));
			}

			String[] billingCountryIsos = (String[]) ActionContext.getContext().getParameters()
					.get("contractor.billingCountry.isoCode");
			if (billingCountryIsos != null && billingCountryIsos.length > 0 && !Strings.isEmpty(billingCountryIsos[0])) {
				contractor.setBillingCountry(countryDAO.find(billingCountryIsos[0]));
			}

			defaultConTypeHelpText();
		}

		if (contractor != null && !supportedLanguages.getVisibleLocales().contains(contractor.getLocale())) {
			contractor.setLocale(supportedLanguages.getClosestVisibleLocale(contractor.getLocale()));
		}
	}

	private void defaultConTypeHelpText() {
		if (contractor.isContractorTypeRequired(ContractorType.Onsite)) {
			contractorTypeHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Onsite.getI18nKey()), StringUtils.join(
					contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Onsite), ", "));
		}
		if (contractor.isContractorTypeRequired(ContractorType.Offsite)) {
			contractorTypeHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Offsite.getI18nKey()), StringUtils.join(
					contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Offsite), ", "));
		}
		if (contractor.isContractorTypeRequired(ContractorType.Supplier)) {
			contractorTypeHelpText += getTextParameterized("RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Supplier.getI18nKey()), StringUtils.join(
					contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Supplier), ", "));
		}
		if (contractor.isContractorTypeRequired(ContractorType.Transportation)) {
			contractorTypeHelpText += getTextParameterized(
					"RegistrationServiceEvaluation.OnlyServiceAllowed",
					getText(ContractorType.Transportation.getI18nKey()),
					StringUtils.join(
							contractor.getOperatorsNamesThatRequireContractorType(ContractorType.Transportation), ", "));
		}
	}

	@Before
	public void startup() throws Exception {
		this.subHeading = getText("ContractorEdit.subheading");

		findContractor();
		// Billing CountrySubdivision gets set to an empty string
		if (contractor.getBillingCountrySubdivision() != null
				&& Strings.isEmpty(contractor.getBillingCountrySubdivision().getIsoCode())) {
			contractor.setBillingCountrySubdivision(null);
		}
	}

	public String save() throws Exception {
		String ftpDir = getFtpDir();

		if (permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorAccounts, OpType.Edit)) {

			if (logo != null) {
				handleLogo(ftpDir);
			}
			if (brochure != null) {
				handleBrochure(ftpDir);
			}
			checkContractorTypes();
			checkListOnlyAcceptability();
			handleLocationChange();
			runContractorValidator();

			if (this.hasActionErrors()) {
				return SUCCESS;
			}

            addNotesForSavedContractor();
            // auditBuilder.buildAudits(contractor);
            if (!contractor.isDemo()) {
                contractor.setQbSync(true);
				if (sapAppPropertyUtil.isSAPBusinessUnitSetSyncTrueEnabledForObject(contractor)) {
                	contractor.setSapSync(true);
				}
            }

			contractor.incrementRecalculation();
			contractor.setNameIndex();

			if (contactID > 0 && contactID != contractor.getPrimaryContact().getId()) {
				contractor.setPrimaryContact(userDAO.find(contactID));
			}
			// contractor.setNeedsIndexing(true);

            if (permissions.hasPermission(OpPerms.UserZipcodeAssignment)) {
                if (csrId == 0) {
                    contractor.setDontReassign(false);
                } else {
                    contractor.setCurrentCsr(userDAO.find(csrId), permissions.getUserId());
                    contractor.setDontReassign(true);
                }

                if (insideSalesId > 0) {
                    User newRep = userDAO.find(insideSalesId);

                    if (newRep != null) {
                        contractor.setCurrentInsideSalesRepresentative(newRep, permissions.getUserId());
                    }
                }
            }

			contractorAccountDao.save(contractor);

			addActionMessage(this.getTextParameterized("ContractorEdit.message.SaveContractor", contractor.getName()));
		}

		return SUCCESS;
	}

    protected void checkListOnlyAcceptability() {
		if (AccountLevel.ListOnly == contractor.getAccountLevel()) {
			// Now check if they have a product risk level
			if (!contractor.isListOnlyEligible()) {
				addActionError(getText("ContractorEdit.error.ListOnlyRequirements"));
			}

			List<String> nonListOnlyOperators = new ArrayList<String>();
			for (ContractorOperator co : contractor.getNonCorporateOperators()) {
				if (!co.getOperatorAccount().isAcceptsList()) {
					nonListOnlyOperators.add(co.getOperatorAccount().getName());
				}
			}

			if (!nonListOnlyOperators.isEmpty()) {
				addActionError(this.getTextParameterized("ContractorEdit.error.OperatorsDoneAcceptList",
						Strings.implode(nonListOnlyOperators)));
			}
		}
	}

	protected void runContractorValidator() {
		if (vatId != null) {
			contractor.setVatId(vatId);
		}
		for (String error : contractorValidator.validateContractor(contractor)) {
			addActionError(error);
		}
	}

	protected void checkContractorTypes() {
		if (!permissions.isContractor()) {
			processContractorTypes();
			confirmConTypesOK();
		}
	}

	protected void handleBrochure(String ftpDir) throws Exception {
		String extension = brochureFileName.substring(brochureFileName.lastIndexOf(".") + 1);
		String[] validExtensions = {"jpg", "gif", "png", "doc", "pdf"};

		if (!FileUtils.checkFileExtension(extension, validExtensions)) {
			addActionError(getText("ContractorEdit.error.BrochureFormat"));
			return;
		}
		String fileName = "brochure_" + contractor.getId();
		FileUtils.moveFile(brochure, ftpDir, "/files/brochures/", fileName, extension, true);
		contractor.setBrochureFile(extension);
	}

	protected void handleLogo(String ftpDir) throws Exception {
		String extension = logoFileName.substring(logoFileName.lastIndexOf(".") + 1);
		String[] validExtensions = {"jpg", "gif", "png"};

		if (!FileUtils.checkFileExtension(extension, validExtensions)) {
			addActionError(getText("ContractorEdit.error.LogoFormat"));
			return;
		}
		String fileName = "logo_" + contractor.getId();
		FileUtils.moveFile(logo, ftpDir, "/logos/", fileName, extension, true);
		contractor.setLogoFile(fileName + "." + extension);
	}

	private void processContractorTypes() {
		// account for disabled checkboxes not coming though
		// but only if populated/presented
		for (ContractorType type : ContractorType.values()) {
			if (contractor.isContractorTypeRequired(type)) {
				conTypes.add(type);
			}
		}

		contractor.setAccountTypes(conTypes);
		contractor.resetRisksBasedOnTypes();
	}

	void handleLocationChange() {
		boolean countryHasChanged = country != null && !country.equals(contractor.getCountry());

		if (countryHasChanged) {
			contractor.setCountry(country);
		}

		if (!contractor.getCountry().isHasCountrySubdivisions()) {
			contractor.setCountrySubdivision(null);
			countrySubdivision = null;
		}

		boolean subdivisionHasChanged = (countrySubdivision != null)
				&& (!countrySubdivision.equals(contractor.getCountrySubdivision()));

		if (subdivisionHasChanged) {
			contractor.setCountrySubdivision(countrySubdivisionDAO.find(countrySubdivision.toString()));
		}

		if (countryHasChanged || subdivisionHasChanged) {
			contractorValidator.setOfficeLocationInPqfBasedOffOfAddress(contractor);
			stampContractorNoteAboutOfficeLocationChange();
		}
	}

    private void addNotesForSavedContractor() {
        addNoteWhenStatusChange();
        addNoteWhenSalesRepSalesForceIdChange();
    }

	private void addNoteWhenStatusChange() {
		request = ServletActionContext.getRequest();
		if (request.getParameter("currentStatus") != null) {
			if (!request.getParameter("currentStatus").equals(contractor.getStatus().toString())) {
				this.addNote(contractor, "Account Status changed from" + contractor.getStatus().toString() + " to "
						+ request.getParameter("currentStatus"));
			}
		}
	}

    private void addNoteWhenSalesRepSalesForceIdChange() {
        request = ServletActionContext.getRequest();
        String salesRepSalesForceIDParameter = request.getParameter("salesRepSalesForceID");
        if (salesRepSalesForceIDParameter != null) {
            if (!salesRepSalesForceIDParameter.equals(contractor.getSalesRepSalesForceID())) {
                this.addNote(contractor, "Sales Rep SalesForce ID changed from " + contractor.getSalesRepSalesForceID() + " to "
                        + salesRepSalesForceIDParameter);
            }
            contractor.setSalesRepSalesForceID(salesRepSalesForceIDParameter);
        }
    }

	private void stampContractorNoteAboutOfficeLocationChange() {
		User system = new User();
		system.setId(User.SYSTEM);
		Note pqfOfficeLocationChange = new Note(contractor, system, getText("AuditData.officeLocationSet.summary"));
		pqfOfficeLocationChange.setNoteCategory(NoteCategory.General);
		if (contractor.getCountry().isHasCountrySubdivisions() && countrySubdivision != null) {
			pqfOfficeLocationChange.setBody(getTextParameterized("AuditData.officeLocationSet",
					getText(countrySubdivision.getI18nKey())));
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
				if (!subscribed && operator != null && operator.getPrimaryContact() != null) {
					emails.add(operator.getPrimaryContact().getEmail());
				}

				if (emails.size() > 0) {
					emailAddresses.addAll(emails);
				} else {
					addActionError(getTextParameterized("ContractorEdit.error.NoPrimaryContact", operatorID));
				}
			}

			if (emailAddresses.size() > 0) {
				if (!contractorAccountDao.isContained(contractor)) {
					contractor = contractorAccountDao.find(contractor.getId());
				}
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(EmailTemplate.DEACTIVATION_FOR_OPERATORS_EMAIL_TEMPLATE); // Deactivation Email for
				// operators
				emailBuilder.setPermissions(permissions);
				emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
				emailBuilder.setBccAddresses(Strings.implode(emailAddresses, ","));
				emailBuilder.setCcAddresses("");
				@SuppressWarnings("deprecation")
				String billingEmail = EmailAddressUtils.getBillingEmail(contractor.getCurrency());
				emailBuilder.setToAddresses(billingEmail);
				emailBuilder.setFromAddress(billingEmail);
				EmailQueue email = emailBuilder.build();
				email.setSubjectViewableById(Account.PicsID);
				email.setBodyViewableById(Account.PicsID);
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
				noteDao.save(note);

				this.addActionMessage(getText("ContractorEdit.message.EmailSent"));
			}
		}

		return SUCCESS;
	}

	public String copyPrimary() throws Exception {
		contractor.setBillingAddress(contractor.getAddress());
		contractor.setBillingCity(contractor.getCity());
		contractor.setBillingCountrySubdivision(contractor.getCountrySubdivision());
		contractor.setBillingCountry(contractor.getCountry());
		contractor.setBillingZip(contractor.getZip());
		contractorAccountDao.save(contractor);

		return SUCCESS;
	}

	public String deactivate() throws Exception {
		subHeading = "Contractor Edit"; // TODO: This should be i18n

		if (Strings.isEmpty(contractor.getReason())) {
			addActionError(getText("ContractorEdit.error.DeactivationReason"));
			return SUCCESS;
		}

		// Contractors will be set not to renew regardless of their membership status
		setContractorNotToRenew();
		if (contractorHasNotExpiredYet() && !contractor.isHasFreeMembership()) {
			return leaveContractorAlone();
		}

		String expiresMessage = Strings.EMPTY_STRING;
		String expiresMessage_en = Strings.EMPTY_STRING;
		String reason = Strings.EMPTY_STRING;
		if (contractorHasNotExpiredYet()) {
			expiresMessage = this.getTextParameterized("ContractorEdit.message.AccountExpires",
					contractor.getPaymentExpires());
			expiresMessage_en = this.getTextParameterized(Locale.ENGLISH, "ContractorEdit.message.AccountExpires",
					contractor.getPaymentExpires());
			reason = AccountStatusChanges.ACCOUNT_ABOUT_TO_EXPIRE_REASON;
		} else {
			expiresMessage = getText("ContractorEdit.message.AccountDeactivated");
			expiresMessage_en = getText(Locale.ENGLISH, "ContractorEdit.message.AccountDeactivated");
			reason = AccountStatusChanges.ACCOUNT_EXPIRED_REASON;
		}

		accountStatusChanges.deactivateContractor(contractor, permissions, reason, expiresMessage_en);
		this.addActionMessage(this.getTextParameterized("ContractorEdit.message.AccountClosed", expiresMessage));

		return SUCCESS;
	}

	private void setContractorNotToRenew() {
		contractor.setRenew(false);
		contractorAccountDao.save(contractor);
	}

	private String leaveContractorAlone() {
		String expiresMessage = this.getTextParameterized("ContractorEdit.message.AccountExpires",
				contractor.getPaymentExpires());
		addNote(contractor, "Closed contractor account." + expiresMessage);
		addActionMessage(this.getTextParameterized("ContractorEdit.message.AccountClosed", expiresMessage));
		subHeading = "Contractor Edit";

		return SUCCESS;
	}

	private boolean contractorHasNotExpiredYet() {
		return contractor.getPaymentExpires() != null && contractor.getPaymentExpires().after(new Date());
	}

	public String reactivate() throws Exception {
		contractor.setRenew(true);
		if (contractor.isHasFreeMembership()) {
			contractor.setStatus(AccountStatus.Active);
		}

		contractor.setReason(Strings.EMPTY_STRING);
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

	public CountrySubdivision getCountrySubdivision() {
		return countrySubdivision;
	}

	public void setCountrySubdivision(CountrySubdivision countrySubdivision) {
		this.countrySubdivision = countrySubdivision;
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

		List<User> users = userDAO.findByAccountID(contractor.getId(), "Yes", "No");
		primaryContactSet.addAll(users);

		// Include users that can switch to groups
		Set<User> groupSet = new HashSet<User>();
		groupSet.addAll(userDAO.findByAccountID(contractor.getId(), "Yes", "Yes"));

		Set<User> switchToSet = new HashSet<User>();
		// Adding users that can switch to users on account
		for (User u : primaryContactSet) {
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		}
		// Adding users that can switch to groups on account
		for (User u : groupSet) {
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		}
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
		if (contractor.getStatus().isDemo()) {
			return true;
		}
		if (permissions.hasPermission(OpPerms.RiskRank)) {
			return true;
		}
		return false;
	}

	public boolean isHasImportPQFAudit() {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.IMPORT_PQF && !audit.isExpired()) {
				return true;
			}
		}

		return false;
	}

	public List<ContractorType> getConTypes() {
		return conTypes;
	}

	public void setConTypes(List<ContractorType> conTypes) {
		this.conTypes = conTypes;
	}

	public void confirmConTypesOK() {
		boolean meetsARequiredContractorTypeForThisOperator;
		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			meetsARequiredContractorTypeForThisOperator = false;
			for (ContractorType conType : operator.getAccountTypes()) {
				if (conTypes.contains(conType)) {
					meetsARequiredContractorTypeForThisOperator = true;
				}
			}

			if (!meetsARequiredContractorTypeForThisOperator) {
				String msg = operator.getName() + " requires you to select "
						+ StringUtils.join(operator.getAccountTypes(), " or ");
				addActionError(msg);
			}
		}
	}

	public String getContractorTypeHelpText() {
		return contractorTypeHelpText;
	}

	public void setContractorTypeHelpText(String contractorTypeHelpText) {
		this.contractorTypeHelpText = contractorTypeHelpText;
	}

	public void setVatId(String vatId) {
		this.vatId = vatId;
	}

	public List<User> getCsrList() {
		return userDAO.findWhere("u.isActive = 'Yes' and u.account.id = 1100 and u.assignmentCapacity > 0");
	}

	public List<User> getInsideSalesList() {
		List<User> insideSales = userDAO.findByGroup(User.GROUP_INSIDE_SALES);
		if (insideSales != null) {
			insideSales.addAll(getCsrList());
			Collections.sort(insideSales);
		}

		return insideSales;
	}

	public int getCsrId() {
		return csrId;
	}

	public void setCsrId(int csrId) {
		this.csrId = csrId;
	}

	public int getInsideSalesId() {
		return insideSalesId;
	}

	public void setInsideSalesId(int insideSalesId) {
		this.insideSalesId = insideSalesId;
	}

	public boolean showISRAssginment() {
		AccountStatus status = contractor.getStatus();

		if (status.equals(AccountStatus.Pending) ||
				status.equals(AccountStatus.Requested) ||
				status.equals(AccountStatus.Deactivated) ||
				status.equals(AccountStatus.Declined)) {
			return true;
		}
		return false;
	}

	public SapAppPropertyUtil getSapAppPropertyUtil() {
		return sapAppPropertyUtil;
	}

	public void setSapAppPropertyUtil(SapAppPropertyUtil sapAppPropertyUtil) {
		this.sapAppPropertyUtil = sapAppPropertyUtil;
	}
}
