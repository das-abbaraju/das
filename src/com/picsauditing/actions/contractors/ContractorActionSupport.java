package com.picsauditing.actions.contractors;

import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.PermissionToViewContractor;
import org.apache.struts2.ServletActionContext;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("serial")
public class ContractorActionSupport extends AccountActionSupport {
	private final static Logger logger = LoggerFactory.getLogger(ContractorActionSupport.class);

	protected ContractorAccount contractor;
	private List<ContractorAudit> contractorNonExpiredAudits = null;

	@Autowired
	protected ContractorAccountDAO contractorAccountDao;
	@Autowired
	protected ContractorAuditDAO auditDao;
	@Autowired
	private CertificateDAO certificateDAO;
	@Autowired
	protected OperatorAccountDAO operatorDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	protected AuditDataDAO auditDataDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private FeatureToggle featureToggle;

	private List<ContractorOperator> operators;
	protected boolean limitedView = false;
	protected List<ContractorOperator> activeOperators;
	protected Map<ContractorAudit, AuditStatus> contractorAuditWithStatuses = null;
	protected List<ContractorAudit> employeeGuardAudits;

	protected List<Certificate> certificates = null;

	private Map<ContractorTrade, String> tradeCssMap;

	// TODO cleanup the PermissionToViewContractor duplicate code here
	private PermissionToViewContractor permissionToViewContractor = null;

	protected ContractorRegistrationStep currentStep = null;
	protected Set<AuditType> manuallyAddAudits = null;
	@Autowired
	protected AuditTypeRuleCache auditTypeRuleCache;
	@Autowired
	protected AuditDecisionTableDAO auditRuleDAO;
	private Map<Integer, List<Integer>> certIdToOp;

	public String execute() throws Exception {
		findContractor();
		return SUCCESS;
	}

	protected void findContractor() throws Exception {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		contractor = contractorAccountDao.find(id);
		account = contractor;
		if (contractor == null)
			throw new RecordNotFoundException("Contractor " + id);

		if (!checkPermissionToView())
			throw new NoRightsException("No Rights to View this Contractor");
	}

	protected boolean checkPermissionToView() {
		loadPermissions();
		if (id == 0 || permissions == null)
			return false;

		if (permissionToViewContractor == null) {
			permissionToViewContractor = new PermissionToViewContractor(id, permissions);
			permissionToViewContractor.setActiveAudits(getActiveAudits());
			permissionToViewContractor.setOperators(getOperators());
		}

		return permissionToViewContractor.check(limitedView);
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	protected void resetActiveAudits() {
		contractorNonExpiredAudits = null;
	}

	public Map<ContractorAudit, AuditStatus> getActiveAuditsStatuses() {
		if (contractorAuditWithStatuses == null) {
			contractorAuditWithStatuses = new HashMap<ContractorAudit, AuditStatus>();
			List<ContractorAudit> list = contractor.getAudits();
			for (ContractorAudit contractorAudit : list) {
				// .isPicsPqf may be wrong here. Consider using
				// contractorAudit.getAuditType().isRenewable() instead
				if (contractorAudit.getAuditType().isPicsPqf() || !contractorAudit.isExpired()) {
					// We're dealing with a non-archived document
					if (permissions.isContractor()) {
						if (contractorAudit.getAuditType().isCanContractorView()) {
							contractorAuditWithStatuses.put(contractorAudit, null);
						}
					} else if (permissions.isPicsEmployee()) {
						contractorAuditWithStatuses.put(contractorAudit, null);
					} else if (contractorAudit.getAuditType().isCanOperatorView()) {
						for (ContractorAuditOperator cao : contractorAudit.getOperators()) {
							if (cao.isVisibleTo(permissions)) {
								contractorAuditWithStatuses.put(contractorAudit, cao.getStatus());
							}
						}
					}
				}
			}
		}
		return contractorAuditWithStatuses;
	}

	public List<ContractorAudit> getActiveAudits() {
		if (contractorNonExpiredAudits == null) {
			contractorNonExpiredAudits = new ArrayList<ContractorAudit>();
			List<ContractorAudit> list = getAudits();
			for (ContractorAudit contractorAudit : list) {
				if (contractorAudit.getAuditType().isPicsPqf() || !contractorAudit.isExpired()) {
					if (contractorAudit.isVisibleTo(permissions))
						contractorNonExpiredAudits.add(contractorAudit);
				}
			}
		}
		return contractorNonExpiredAudits;
	}

	public List<MenuComponent> getMenu() {
		// Create the menu
		List<MenuComponent> menu = new ArrayList<MenuComponent>();
		MenuComponent item = null;
		getRequestString();

		ContractorRegistrationStep step = ContractorRegistrationStep.getStep(contractor);

		// setup initial registration
		if (step == ContractorRegistrationStep.Register) {
			item = new MenuComponent(getText("Registration.title"), "Registration.action", "conRegisterLink");
			if (requestURL.contains("Registration.action")) {
				item.setCurrent(true);
			}
			menu.add(item);
			menu.add(new MenuComponent(getText("RegistrationAddClientSite.title"), null, "conFacilitiesLink")); // Facilities
			menu.add(new MenuComponent(getText("RegistrationServiceEvaluation.title"), null, "conServicesLink")); // Services
			menu.add(new MenuComponent(getText("RegistrationMakePayment.title"), null, "conPaymentLink")); // Payment
		} else {
			// setup account editing

			// Edit Details
			item = new MenuComponent(getText("ContractorEdit.title"), "ContractorEdit.action?id=" + id,
					"edit_contractor");
			if (requestURL.contains("ContractorEdit.action"))
				item.setCurrent(true);
			menu.add(item);

			// Services
			MenuComponent itemServices = new MenuComponent(getText("RegistrationServiceEvaluation.title"), null,
					"conServicesLink");
			if (requestURL.contains("RegistrationServiceEvaluation.action")) {
				itemServices.setCurrent(true);
			}
			menu.add(itemServices);

			// Clients
			MenuComponent itemFacilities = new MenuComponent(getText("RegistrationAddClientSite.title"), null,
					"conFacilitiesLink");
			if (requestURL.contains("RegistrationAddClientSite.action")) {
				itemFacilities.setCurrent(true);
			}
			menu.add(itemFacilities);

			// Payment Options
			MenuComponent itemPaymentOptions = new MenuComponent(getText("RegistrationMakePayment.title"), null,
					"conPaymentLink");
			if (requestURL.contains("RegistrationMakePayment.action")) {
				itemPaymentOptions.setCurrent(true);
			}
			menu.add(itemPaymentOptions);

			// set urls based on step
			switch (step) {
				case Done:
				case Payment:
					itemPaymentOptions.setUrl("RegistrationMakePayment.action?id=" + id);
				case Risk:
					itemServices.setUrl("RegistrationServiceEvaluation.action?id=" + id);
				case Clients:
					itemFacilities.setUrl("RegistrationAddClientSite.action?id=" + id);
			}
		}

		// number menu steps
		int counter = 0;
		for (MenuComponent menuItem : menu) {
			counter++;
			menuItem.setName(counter + ") " + menuItem.getName());
		}

		return menu;
	}

	/**
	 * Build a Menu (List<MenuComponent>) with the following:<br>
	 * * PQF<br>
	 * * Annual Update<br>
	 * * InsureGUARD<br>
	 * * Audits<br>
	 *
	 * @return
	 */
	public List<MenuComponent> getAuditMenu() {
		Set<ContractorAudit> auditList = getActiveAuditsStatuses().keySet();
		logger.info("Found [{}] total active audits", auditList.size());

		Logger profiler = LoggerFactory.getLogger("org.perf4j.DebugTimingLogger");
		StopWatch stopwatch = new Slf4JStopWatch(profiler);
		stopwatch.start("ContractorActionSupport.getAuditMenu");

		AuditMenuBuilder auditMenuBuilder = new AuditMenuBuilder(contractor, permissions);
		auditMenuBuilder.setManuallyAddedAuditTypes(getManuallyAddAudits());
		auditMenuBuilder.setClientReviewsUnderAuditGUARD(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_SHOW_REVIEW_DOC_IN_AUDITGUARD));
		List<MenuComponent> menu = auditMenuBuilder.buildAuditMenuFrom(auditList);

		stopwatch.stop();
		resetActiveAudits();
		return menu;
	}

	/**
	 * TODO: Find out if this comment is useful. Is the method it is for
	 * missing. Only show the COR/SECOR link for contractors who have answered
	 * Yes to that question and linked to an operator that subscribes to COR
	 */

	public boolean isShowHeader() {
		if (permissions.isContractor())
			return true;
		if (!permissions.hasPermission(OpPerms.ContractorDetails))
			return false;
		if (permissions.isOperator())
			return isCheckPermissionForOperator();
		if (permissions.isCorporate())
			return isCheckPermissionForCorporate();
		if (permissions.isOnlyAuditor()) {
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditor() != null && audit.getAuditor().getId() == permissions.getUserId())
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.getStatus().before(AuditStatus.Complete))
							return true;
					}
			}
			return false;
		}
		return true;
	}

	public boolean isCheckPermissionForOperator() {
		for (ContractorOperator operator : getOperators())
			if (operator.getOperatorAccount().getId() == permissions.getAccountId())
				return true;

		return false;
	}

	public boolean isCheckPermissionForCorporate() {
		for (ContractorOperator co : getOperators()) {
			int opID = co.getOperatorAccount().getId();
			if (permissions.getOperatorChildren().contains(opID))
				return true;
		}
		return false;
	}

	// TODO change this to List<OperatorAccount> instead or figure out why we're
	// getting an exception on isRequiresInsurance()

	public List<ContractorOperator> getOperators() {
		if (operators == null)
			operators = contractorAccountDao.findOperators(contractor, permissions,
					" AND operatorAccount.type IN ('Operator')");
		return operators;
	}

	public List<ContractorOperator> getActiveOperators() {
		if (activeOperators == null)
			activeOperators = contractorAccountDao.findOperators(contractor, permissions,
					" AND status IN ('Active','Demo') AND operatorAccount.type IN ('Operator')");
		return activeOperators;
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		return operatorDAO.findWhere(false, "", permissions);
	}

	/**
	 * @return a list of the certificates, if the user is an operator/corporate
	 *         then this does the appropriate checking to remove the certs that
	 *         they shouldn't be able to see
	 */
	public List<Certificate> getCertificates() {
		if (certificates == null) {
			certificates = certificateDAO.findByConId(contractor.getId(), permissions, true);
			List<Integer> certIds = new ArrayList<Integer>();
			for (Certificate cert : certificates)
				certIds.add(cert.getId());

			certIdToOp = certificateDAO.findOpsMapByCert(certIds);
		}

		if (permissions.isOperatorCorporate()) {
			int topID = permissions.getTopAccountID();
			OperatorAccount opAcc = operatorDAO.find(topID);

			List<Integer> allowedList = new ArrayList<Integer>();
			allowedList = opAcc.getOperatorHeirarchy();

			for (OperatorAccount tmpOp : opAcc.getOperatorChildren())
				allowedList.add(tmpOp.getId());

			Iterator<Certificate> itr = certificates.iterator();

			while (itr.hasNext()) {
				Certificate c = itr.next();
				int certID = c.getId();

				boolean remove = true;

				if (certIdToOp.get(certID) != null) {
					for (Integer i : certIdToOp.get(certID)) {
						if (allowedList.contains(i)) {
							remove = false;
							break;
						}
					}
				}

				if (remove)
					itr.remove();
			}
		}

		return certificates;
	}

	public List<OperatorAccount> getOperatorsUsingCertificate(int certId) {
		List<OperatorAccount> operatorsUsingCert = new ArrayList<OperatorAccount>();

		getCertificates();

		List<Integer> opIds = certIdToOp.get(certId);
		if (opIds != null) {
			for (int opId : opIds) {
				for (ContractorOperator conOp : operators) {
					if (conOp.getOperatorAccount().getId() == opId)
						operatorsUsingCert.add(conOp.getOperatorAccount());
				}
			}
		}

		return operatorsUsingCert;
	}

	/**
	 * Get a list of Audits that the current user can see Operators can't see
	 * each other's audits Contractors can't see the Welcome Call This is a bit
	 * complicated but needs to look at permissions
	 *
	 * @return
	 */
	public List<ContractorAudit> getAudits() {

		List<ContractorAudit> temp = new ArrayList<ContractorAudit>();
		try {
			// Is this ever used? We should just make sure findContractor() has
			// already been called
			if (!contractorAccountDao.isContained(contractor))
				findContractor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (ContractorAudit contractorAudit : contractor.getAudits()) {
			if (contractorAudit.isVisibleTo(permissions)) {
				temp.add(contractorAudit);
			}
		}
		return temp;
	}

	public LowMedHigh[] getRiskLevelList() {
		return LowMedHigh.values();
	}

	public ContractorRegistrationStep getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(ContractorRegistrationStep currentStep) {
		this.currentStep = currentStep;
	}

	public ContractorRegistrationStep getRegistrationStep() {
		return ContractorRegistrationStep.getStep(contractor);
	}

	/**
	 * We're assuming that the ording in the enum is the standard order of
	 * contractor registration.
	 *
	 * @return Previous ContractorRegistrationStep, according to the
	 *         ContractorRegistrationStep enum order
	 */
	public ContractorRegistrationStep getPreviousRegistrationStep() {
		if (currentStep != null && currentStep.isHasPrevious())
			return ContractorRegistrationStep.values()[currentStep.ordinal() - 1];

		return null;
	}

	/**
	 * @return Next ContractorRegistrationStep, according to the
	 *         ContractorRegistrationStep enum order
	 */
	public ContractorRegistrationStep getNextRegistrationStep() {
		if (currentStep != null && currentStep.isHasNext())
			return ContractorRegistrationStep.values()[currentStep.ordinal() + 1];

		return null;
	}

	public String previousStep() throws Exception {
		findContractor();
		setUrlForRedirect(getPreviousRegistrationStep().getUrl());
		return SUCCESS;
	}

	public String nextStep() throws Exception {
		findContractor();
		if (getNextRegistrationStep() != null)
			setUrlForRedirect(getNextRegistrationStep().getUrl());
		return SUCCESS;
	}

	public Map<ContractorTrade, String> getTradeCssMap() {
		if (tradeCssMap == null) {
			/**
			 * the power to raise the activityPercent. Larger numbers mean that
			 * 9s (most of the time) are less prone to dilution when other
			 * trades are added
			 */
			final float factor = 1.8f;

			tradeCssMap = new HashMap<ContractorTrade, String>();
			int sumTrades = 0;
			for (ContractorTrade trade : contractor.getTrades()) {
				sumTrades += (int) Math.round(Math.pow(trade.getActivityPercent(), factor));
			}

			// assign style mappings
			for (ContractorTrade trade : contractor.getTrades()) {
				int activityPercent = (int) Math.round(Math.pow(trade.getActivityPercent(), factor));

				int tradePercent = Math.round(10f * activityPercent / sumTrades);

				switch (trade.getActivityPercent()) {
					case 1:
						tradePercent = cap(tradePercent, 1, 6);
						break;
					case 3:
						tradePercent = cap(tradePercent, 2, 7);
						break;
					case 5:
						tradePercent = cap(tradePercent, 3, 8);
						break;
					case 7:
						tradePercent = cap(tradePercent, 4, 9);
						break;
					case 9:
						tradePercent = cap(tradePercent, 5, 10);
						break;

					default:
						tradePercent = cap(tradePercent, 1, 10);
				}
				tradeCssMap.put(trade, "" + tradePercent);

			}
		}

		return tradeCssMap;
	}

	private int cap(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	protected boolean redirectIfNotReadyForThisStep() throws IOException {
		ContractorRegistrationStep highestStepReached = ContractorRegistrationStep.getStep(contractor);
		if (highestStepReached.ordinal() < this.currentStep.ordinal()
				|| highestStepReached == ContractorRegistrationStep.Done) {
			ServletActionContext.getResponse().sendRedirect(highestStepReached.getUrl());
			return true;
		}
		return false;
	}

	public int getLastStepCompleted() {
		ContractorRegistrationStep step = ContractorRegistrationStep.getStep(contractor);
		if (step.equals(ContractorRegistrationStep.Clients))
			return 1;
		else if (step.equals(ContractorRegistrationStep.Risk))
			return 2;
		else
			return 3;
	}

	public List<ContractorAudit> getEmployeeGuardAudits() {
		return employeeGuardAudits;
	}

	public boolean isManuallyAddAudit() {
		if (getManuallyAddAudits().size() > 0) {
			if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit))
				return true;
			if (permissions.isOperatorCorporate()) {
				return true;
			}
		}
		return false;
	}

	public Set<AuditType> getManuallyAddAudits() {
		if (manuallyAddAudits == null) {
			manuallyAddAudits = new HashSet<AuditType>();
			List<AuditTypeRule> applicableAuditRules = auditTypeRuleCache.getRules(contractor);

			for (AuditTypeRule auditTypeRule : applicableAuditRules) {
				if (isValidManualAuditType(auditTypeRule)) {
					manuallyAddAudits.add(auditTypeRule.getAuditType());
				}
			}
		}

		return manuallyAddAudits;
	}

	public boolean isValidManualAuditType(int auditTypeId) {
		if (manuallyAddAudits == null)
			getManuallyAddAudits();

		AuditType auditType = new AuditType(auditTypeId);

		return manuallyAddAudits.contains(auditType);
	}

	public boolean isValidManualAuditType(AuditTypeRule auditTypeRule) {
		if (!auditTypeRule.isInclude() || auditTypeRule.getAuditType() == null
				|| auditTypeRule.getAuditType().isAnnualAddendum() || auditTypeRule.getAuditType().isExtractable()) {
			return false;
		}

		if (!auditTypeRule.getAuditType().isHasMultiple() && !auditTypeRule.isManuallyAdded()) {
			return false;
		}

		if (!auditTypeRule.getAuditType().isHasMultiple()) {
			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().getId() == auditTypeRule.getAuditType().getId() && !audit.isExpired()) {
					return false;
				}
			}
		}

		if (auditTypeRule.getAuditType().isHasMultiple() || auditTypeRule.isManuallyAdded()) {
			if (permissions.isAdmin())
				return true;
			else if (permissions.isOperator()) {
				if (auditTypeRule.getOperatorAccount() != null
						&& (permissions.getCorporateParent().contains(auditTypeRule.getOperatorAccount().getId()) || permissions
						.getAccountId() == auditTypeRule.getOperatorAccount().getId())) {
					return true;
				}
			} else if (permissions.isCorporate()) {
				if (auditTypeRule.getOperatorAccount() != null
						&& auditTypeRule.getOperatorAccount().getId() == permissions.getAccountId()) {
					return true;
				}
			}
		}

		return false;
	}

	public void reviewCategories(EventType eventType) {
		ContractorAudit pqf = auditDao.findPQF(contractor.getId());

		if (pqf != null && pqf.hasCaoStatusAfter(AuditStatus.Incomplete)) {
			List<AuditData> eventQuestions = findEventQuestions(eventType, pqf);

			if (eventQuestions != null && eventQuestions.size() > 0) {
				boolean changeCAOStatus = clearAnswers(eventQuestions, pqf);

				if (changeCAOStatus) {
					changeCAOStatuses(eventType, pqf);
					addAccountNote(eventType);
				}

				recalculatePQF(pqf);

				auditBuilder.buildAudits(contractor);
			}
		}
	}

	private void recalculatePQF(ContractorAudit pqf) {
		auditPercentCalculator.percentCalculateComplete(pqf, true);
		pqf.setLastRecalculation(new Date());
		auditDao.save(pqf);
	}

	private void addAccountNote(EventType eventType) {
		Note note = new Note();
		note.setAccount(contractor);
		note.setAuditColumns(permissions);
		note.setSummary("set PQF status to resubmit to revisit " + eventType.toString() + " section");
		note.setNoteCategory(NoteCategory.Audits);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	private void changeCAOStatuses(EventType eventType, ContractorAudit pqf) {
		for (ContractorAuditOperator cao : pqf.getViewableOperators(permissions)) {
			if (cao.isVisible() && cao.getStatus().after(AuditStatus.Incomplete)) {
				ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Resubmit, permissions);
				if (caow != null) {
					caow.setNotes("Set status to resubmit to revisit " + eventType.toString() + " section");
					caow.setCreatedBy(new User(User.SYSTEM));
					auditDao.save(caow);
				}
			}
		}
	}

	private List<AuditData> findEventQuestions(EventType eventType, ContractorAudit pqf) {
		String where = "d.audit.contractorAccount.id = " + contractor.getId() + " AND d.question.uniqueCode = '"
				+ eventType.getUniqueCode() + "' AND d.audit.id = " + pqf.getId();
		return auditDataDAO.findWhere(where);
	}

	private boolean clearAnswers(List<AuditData> questions, ContractorAudit pqf) {
		boolean changeStatus = false;
		for (AuditData pqfData : questions) {
			if (questionNeedsClearing(pqf, pqfData)) {
				pqfData.setAnswer(null);
				pqfData.setDateVerified(null);
				changeStatus = true;
			}
		}
		return changeStatus;
	}

	private boolean questionNeedsClearing(ContractorAudit pqf, AuditData pqfData) {
		return pqfData.isAnswered() && pqfData.getQuestion().isVisibleInAudit(pqf)
				&& pqfData.getQuestion().getExpirationDate().after(new Date());
	}
}
