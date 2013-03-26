package com.picsauditing.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorFlagETL;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.ExceptionService;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.Anonymous;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.flags.ContractorScore;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.LcCorPhase;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.UserAssignmentType;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.mail.NoUsersDefinedException;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.messaging.FlagChange;
import com.picsauditing.messaging.Publisher;
import com.picsauditing.model.events.ContractorOperatorWaitingOnChangedEvent;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorCron extends PicsActionSupport {

	@Autowired
	private ContractorAccountDAO contractorDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private ContractorFlagETL contractorFlagETL;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private UserAssignmentDAO userAssignmentDAO;
	@Autowired
	private ContractorAuditDAO conAuditDAO;
	@Autowired
	private BillingCalculatorSingle billingService;
	@Autowired
	private ExceptionService exceptionService;
	@Autowired
	private FeatureToggle featureToggleChecker;

	// this is @Autowired at the setter because we need @Qualifier which does
	// NOT work
	// on the variable declaration; only on the method (I think this is a Spring
	// bug)
	private Publisher flagChangePublisher;

	static private Set<ContractorCron> manager = new HashSet<ContractorCron>();

	private FlagDataCalculator flagDataCalculator;
	private int conID = 0;
	private int opID = 0;
	private ContractorCronStep[] steps = null;
	private int limit = 10;
	final private Date startTime = new Date();
	private List<Integer> queue;
	private String redirectUrl;

	private final Logger logger = LoggerFactory.getLogger(ContractorCron.class);

	@Anonymous
	public String execute() throws Exception {
		if (steps == null) {
			return SUCCESS;
		}

		if (conID <= 0) {
			addActionError("You must supply a contractor id.");
			return SUCCESS;
		}

		ContractorAccount contractor = contractorDAO.find(conID);
		if (contractor == null) {
			addActionError("Could not find contractor #" + conID);
			return SUCCESS;
		}

		if (!shouldRunContractorCron(contractor.getStatus())) {
			addActionMessage("We don't run the cron on requested or declined contractors.");
			return SUCCESS;
		}

		logger.trace("Starting ContractorCron for {}", conID);
		try {
			run(contractor, opID);

			if (!Strings.isEmpty(redirectUrl)) {
				return setUrlForRedirect(redirectUrl);
			}
		} catch (Exception e) {
			String exceptionResult = handleException(e);
			if (Strings.isEmpty(exceptionResult)) {
				throw e;
			}

			return exceptionResult;
		}

		logger.trace("Finished ContractorCron");

		return SUCCESS;
	}

	private String handleException(Exception exception) {
		try {
			logger.trace("ContractorCron failed for conID {} because: {}", conID, exception.getMessage());
			if (Strings.isNotEmpty(redirectUrl)) {
				exceptionService.sendExceptionEmail(permissions, exception,
						"Error in Contractor Cron calculating account id #" + conID);
				return setUrlForRedirect(redirectUrl);
			}
		} catch (Exception e) {
			logger.error("An error occurred while sending exception email for ContractorCron. ConID = {}", conID, e);
		}

		return Strings.EMPTY_STRING;
	}

	@Anonymous
	public String listAjax() {
		List<Integer> ids = contractorDAO.findContractorsNeedingRecalculation(15, new HashSet<Integer>());
		output = Strings.implode(ids);
		contractorDAO.updateLastRecalculationToNow(output);
		return PLAIN_TEXT;
	}

	@Transactional
	protected void run(ContractorAccount contractor, int opID) throws Exception {
		try {
			runBilling(contractor);
			runAuditBuilder(contractor);
			runAuditCategory(contractor);
			runAssignAudit(contractor);
			runTradeETL(contractor);
			runContractorETL(contractor);

			if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_CSR_SINGLE_ASSIGNMENT) &&
					contractor.getCurrentCsr() == null) {
				runCSRAssignment(contractor);
			}

			flagDataCalculator = new FlagDataCalculator(contractor.getFlagCriteria());
			flagDataCalculator.setCorrespondingMultiYearCriteria(getCorrespondingMultiscopeCriteriaIds());

			if (runStep(ContractorCronStep.Flag) || runStep(ContractorCronStep.WaitingOn)
					|| runStep(ContractorCronStep.Policies) || runStep(ContractorCronStep.CorporateRollup)) {
				Set<OperatorAccount> corporateSet = new HashSet<OperatorAccount>();

				for (ContractorOperator co : contractor.getNonCorporateOperators()) {
					OperatorAccount operator = co.getOperatorAccount();
					// If the opID is 0, run through all the operators.
					// If the opID > 0, run through just that operator.
					if (opID == 0 || (opID > 0 && operator.getId() == opID)) {
						// for (FlagCriteriaOperator flagCriteriaOperator :
						// operator.getFlagCriteriaInherited()) {
						// PicsLogger.log(" flag criteria " +
						// flagCriteriaOperator.getFlag() + " for "
						// + flagCriteriaOperator.getCriteria().getCategory());
						// }

						if (runStep(ContractorCronStep.CorporateRollup)) {
							for (Facility facility : operator.getCorporateFacilities()) {
								corporateSet.add(facility.getCorporate());
							}
						}
						runFlag(co);
						runWaitingOn(co);

						if (opID > 0) {
							break;
						}
					}
				}
				runCorporateRollup(contractor, corporateSet);
			}

            // must be done before the save, or the calculation will be lost
            runContractorScore(contractor);

            if (steps != null && steps.length > 0) {
                if (opID == 0 && steps[0] == ContractorCronStep.All) {
                    contractor.setNeedsRecalculation(0);
                    contractor.setLastRecalculation(new Date());
                }
                contractorDAO.save(contractor);
                addActionMessage("Completed " + steps.length + " step(s) for " + contractor.toString()
						+ " successfully");
            }

            runPolicies(contractor);

		} catch (Exception continueUpTheStack) {
			setRecalculationToTomorrow(contractor);

			throw continueUpTheStack;
		}
	}

	private Map<Integer, List<Integer>> getCorrespondingMultiscopeCriteriaIds() {
		Database db = new Database();
		Map<Integer, List<Integer>> resultMap = new HashMap<Integer, List<Integer>>();

		SelectSQL sql = new SelectSQL("flag_criteria fc1");
		sql.addField("fc1.id as year1_id");
		sql.addField("fc2.id as year2_id");
		sql.addField("fc3.id as year3_id");
		sql.addJoin("left outer join flag_criteria fc2 on fc1.oshaType = fc2.oshaType AND fc1.oshaRateType = fc2.oshaRateType and fc2.multiYearScope = 'TwoYearsAgo' ");
		sql.addJoin("left outer join flag_criteria fc3 on fc1.oshaType = fc3.oshaType AND fc1.oshaRateType = fc3.oshaRateType and fc3.multiYearScope = 'ThreeYearsAgo' ");
		sql.addWhere("fc1.oshaType is not null and fc1.multiYearScope = 'LastYearOnly'");

		extractMultiyearCriteriaIdQueryResults(db, sql, resultMap);

		sql = new SelectSQL("flag_criteria fc1");
		sql.addField("fc1.id as year1_id");
		sql.addField("fc2.id as year2_id");
		sql.addField("fc3.id as year3_id");
		sql.addJoin("left outer join flag_criteria fc2 on fc1.questionID = fc2.questionID and fc2.multiYearScope = 'TwoYearsAgo' ");
		sql.addJoin("left outer join flag_criteria fc3 on fc1.questionID = fc3.questionID and fc3.multiYearScope = 'ThreeYearsAgo' ");
		sql.addWhere("fc1.questionID is not null and fc1.multiYearScope = 'LastYearOnly'");

		extractMultiyearCriteriaIdQueryResults(db, sql, resultMap);

		return resultMap;
	}

	private void extractMultiyearCriteriaIdQueryResults(Database db, SelectSQL sql,
			Map<Integer, List<Integer>> resultMap) {
		try {
			List<BasicDynaBean> resultBDB = db.select(sql.toString(), false);
			for (BasicDynaBean row : resultBDB) {
				Integer year1 = (Integer) row.get("year1_id");
				Integer year2 = (Integer) row.get("year2_id");
				Integer year3 = (Integer) row.get("year3_id");

				ArrayList<Integer> list = new ArrayList<Integer>();
				if (year1 != null) {
					list.add(year1);
				}

				if (year2 != null) {
					list.add(year2);
				}

				if (year3 != null) {
					list.add(year3);
				}

				if (year1 != null) {
					resultMap.put(year1, list);
				}

				if (year2 != null) {
					resultMap.put(year2, list);
				}

				if (year3 != null) {
					resultMap.put(year3, list);
				}
			}
		} catch (Exception e) {
			logger.error("Error while extracting multi-year criteria.", e);
		}
	}

	private void setRecalculationToTomorrow(ContractorAccount contractor) {
		if (contractor == null) {
			return;
		}

		try {
			contractor.setNeedsRecalculation(0);
			contractor.setLastRecalculation(DateBean.addDays(new Date(), 1));
			contractorDAO.save(contractor);
		} catch (Exception notMuchWeCanDoButLogIt) {
			logger.error("Error setting recalculation for tomorrow on contractor.");
			logger.error(notMuchWeCanDoButLogIt.getMessage());
		}
	}

	private void runBilling(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.Billing)) {
			return;
		}

		logger.trace("ContractorCron starting Billing");
		billingService.calculateContractorInvoiceFees(contractor);
		contractor.syncBalance();
	}

	private void runAuditBuilder(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditBuilder)) {
			return;
		}

		logger.trace("ContractorCron starting AuditBuilder");
		auditBuilder.buildAudits(contractor);

		checkLcCor(contractor);
		cancelScheduledImplementationAudits(contractor);
	}

	private void cancelScheduledImplementationAudits(ContractorAccount contractor) {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (isCancelImplementationAudit(audit)) {
				audit.setLatitude(0);
				audit.setLongitude(0);
				audit.setScheduledDate(null);
				dao.save(audit);

				Note note = createCanceledAuditNote(contractor, audit);
				dao.save(note);
			}
		}
	}

	private Note createCanceledAuditNote(ContractorAccount contractor, ContractorAudit audit) {
		Note note = new Note();

		int accountId = (audit.getAuditType().getAccount() != null) ? audit.getAuditType().getAccount().getId()
				: Account.EVERYONE;

		note.setAccount(contractor);
		note.setAuditColumns(new User(User.SYSTEM));
		note.setSummary("Implementation Audit canceled due to no visible CAOs");
		note.setPriority(LowMedHigh.Low);
		note.setNoteCategory(NoteCategory.Audits);
		note.setViewableById(accountId);
		note.setCanContractorView(true);
		note.setStatus(NoteStatus.Closed);

		return note;
	}

	private boolean isCancelImplementationAudit(ContractorAudit audit) {
		if (!audit.getAuditType().isImplementation()) {
			return false;
		}

		if (audit.hasOnlyInvisibleCaos() && audit.getScheduledDate() != null) {
			return true;
		}

		return false;
	}

	private void checkLcCor(ContractorAccount contractor) {
		if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_LCCOR)) {
			return;
		}

		boolean isLcCorNotify = false;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 180);
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditType.COR) {
				if (audit.getExpiresDate() != null && audit.getExpiresDate().before(cal.getTime())) {
					if (contractor.getLcCorPhase() == null) {
						isLcCorNotify = true;
					} else if (!contractor.getLcCorPhase().isAuditPhase()) {
						isLcCorNotify = true;
					} else if (contractor.getLcCorPhase().equals(LcCorPhase.Done)) {
						if (contractor.getLcCorNotification() == null) {
							isLcCorNotify = true;
						} else if (contractor.getLcCorNotification().before(audit.getExpiresDate())) {
							isLcCorNotify = true;
						}
					}
				}
			}
		}

		if (isLcCorNotify) {
			contractor.setLcCorPhase(LcCorPhase.RemindMeLaterAudit);
			contractor.setLcCorNotification(new Date());
			contractorDAO.save(contractor);
		}
	}

	private void runAuditCategory(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditCategory)) {
			return;
		}

		logger.trace("ContractorCron starting AuditCategory");
		for (ContractorAudit cAudit : contractor.getAudits()) {
			final Date lastRecalculation = cAudit.getLastRecalculation();
			if (lastRecalculation == null || DateBean.getDateDifference(lastRecalculation) < -14) {
				auditPercentCalculator.percentCalculateComplete(cAudit, true);
				cAudit.setLastRecalculation(new Date());
				cAudit.setAuditColumns();
				conAuditDAO.save(cAudit);
			}
		}
	}

	private void runTradeETL(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.TradeETL)) {
			return;
		}

		logger.trace("ContractorCron starting TradeETL");
		Set<ContractorTrade> trades = contractor.getTrades();
		List<String> selfPerform = new ArrayList<String>();
		List<String> subContract = new ArrayList<String>();

		for (ContractorTrade trade : trades) {
			if (trade.isSelfPerformed()) {
				selfPerform.add(trade.getTrade().getName().toString());
			} else {
				subContract.add(trade.getTrade().getName().toString());
			}
		}

		/*
		 * PICS-3313: This section is likely no longer needed, but this should
		 * prevent SQL errors when trying to save a contractor.
		 */
		String tradesSelf = Strings.implode(selfPerform, ";");
		if (tradesSelf.length() > 4000) {
			tradesSelf = tradesSelf.substring(0, 4000);
		}
		contractor.setTradesSelf(tradesSelf);

		String tradesSub = Strings.implode(subContract, ";");
		if (tradesSub.length() > 4000) {
			tradesSub = tradesSub.substring(0, 4000);
		}
		contractor.setTradesSub(tradesSub);

		boolean requiresOQ = false;
		boolean requiresCompetency = false;
		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().isRequiresOQ()) {
				requiresOQ = true;
			}
			if (co.getOperatorAccount().isRequiresCompetencyReview()) {
				requiresCompetency = true;
			}
		}

		contractor.setRequiresOQ(false);
		if (requiresOQ) {
			AuditData oqAuditData = auditDataDAO
					.findAnswerByConQuestion(contractor.getId(), AuditQuestion.OQ_EMPLOYEES);
			contractor.setRequiresOQ(oqAuditData == null || oqAuditData.getAnswer() == null
					|| oqAuditData.getAnswer().equals("Yes"));
		}

		contractor.setRequiresCompetencyReview(false);
		if (requiresCompetency) {
			for (ContractorTag tag : contractor.getOperatorTags()) {
				OperatorTag operatorTag = tag.getTag();

				if (operatorTag.getCategory().isCompetencyReview()) {
					contractor.setRequiresCompetencyReview(true);
				}
			}
		}
	}

	private void runContractorETL(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.ContractorETL)) {
			return;
		}
		logger.trace("ContractorCron starting ContractorETL");
		contractorFlagETL.calculate(contractor);
	}

	private void runContractorScore(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.PICSScore)) {
			return;
		}

		logger.trace("ContractorCron starting PicsScore");
		ContractorScore.calculate(contractor);
	}

	@SuppressWarnings("unchecked")
	private void runFlag(ContractorOperator co) {
		if (!runStep(ContractorCronStep.Flag)) {
			return;
		}

		logger.trace("ContractorCron starting Flags for {}", co.getOperatorAccount().getName());
		flagDataCalculator.setOperator(co.getOperatorAccount());
		flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagCriteriaInherited());

		Map<FlagCriteria, List<FlagDataOverride>> overridesMap = new HashMap<FlagCriteria, List<FlagDataOverride>>();

		Set<OperatorAccount> corporates = new HashSet<OperatorAccount>();
		for (Facility f : co.getOperatorAccount().getCorporateFacilities()) {
			corporates.add(f.getCorporate());
		}

		for (FlagDataOverride override : co.getContractorAccount().getFlagDataOverrides()) {
			if (override.getOperator().equals(co.getOperatorAccount())) {
				if (!overridesMap.containsKey(override.getCriteria())) {
					overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
				}
				((LinkedList<FlagDataOverride>) overridesMap.get(override.getCriteria())).addFirst(override);
			} else if (corporates.contains(override.getOperator())) {
				if (!overridesMap.containsKey(override.getCriteria())) {
					overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
				}
				((LinkedList<FlagDataOverride>) overridesMap.get(override.getCriteria())).addLast(override);
			}
		}
		flagDataCalculator.setOverrides(overridesMap);
		List<FlagData> changes = flagDataCalculator.calculate();

		// Save the FlagDetail to the ContractorOperator as a JSON string
		JSONObject flagJson = new JSONObject();
		for (FlagData data : changes) {
			JSONObject flag = new JSONObject();
			flag.put("category", data.getCriteria().getCategory());
			flag.put("label", data.getCriteria().getLabel().toString());
			flag.put("flag", data.getFlag().toString());

			flagJson.put(data.getCriteria().getId(), flag);
		}
		co.setFlagDetail(flagJson.toString());

		// Find overall flag color for this operator
		FlagColor overallColor = FlagColor.Green;
		String reason = "Contractor is no longer flagged on any criteria for this operator.";
		if (co.getContractorAccount().getAccountLevel().isBidOnly()
				|| co.getContractorAccount().getStatus().isPending()
				|| co.getContractorAccount().getStatus().isDeleted()
				|| co.getContractorAccount().getStatus().isDeactivated()) {
			overallColor = FlagColor.Clear;
			reason = "Contractor no longer tracked by flags.";
		}

		for (FlagData change : changes) {
			if (!change.getCriteria().isInsurance()) {
				FlagColor worst = FlagColor.getWorseColor(overallColor, change.getFlag());
				if (worst != overallColor) {
					reason = getFlagDataDescription(change, co.getOperatorAccount());
				}
				overallColor = worst;
			}
		}

		ContractorOperator conOperator = co.getForceOverallFlag();
		if (conOperator != null) { // operator has a forced flag
			co.setFlagColor(conOperator.getForceFlag());
			co.setFlagLastUpdated(new Date());
			if (co.getForceBegin() != null) {
				Calendar forceFlagCreatedOn = Calendar.getInstance();
				forceFlagCreatedOn.setTime(co.getForceBegin());
				Calendar yesterday = Calendar.getInstance();
				yesterday.add(Calendar.DATE, -1);
				Calendar tomorrow = Calendar.getInstance();
				tomorrow.add(Calendar.DATE, 1);
				if (!(forceFlagCreatedOn.after(tomorrow) || forceFlagCreatedOn.before(yesterday))) {
					if (overallColor.equals(conOperator.getForceFlag())) {
						co.setBaselineFlag(overallColor);
					}
				}
			}
		} else if (!overallColor.equals(co.getFlagColor())) {
			FlagChange flagChange = getFlagChange(co, overallColor);
			flagChangePublisher.publish(flagChange);

			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setNoteCategory(NoteCategory.Flags);
			note.setAuditColumns(new User(User.SYSTEM));
			note.setSummary("Flag color changed from " + co.getFlagColor() + " to " + overallColor + " for "
					+ co.getOperatorAccount().getName());
			note.setBody(reason);
			note.setCanContractorView(true);
			note.setViewableById(co.getOperatorAccount().getId());
			dao.save(note);
			if (co.getFlagColor() == FlagColor.Clear) {
				co.setBaselineFlag(overallColor);
				co.setBaselineFlagDetail(flagJson.toString());
			}
			co.setFlagColor(overallColor);
			co.setFlagLastUpdated(new Date());
		}

		// set baselineFlag to clear and baselineFlagDetail for null baselines
		if (co.getBaselineFlag() == null) {
			co.setBaselineFlag(FlagColor.Clear);
			co.setBaselineFlagDetail(flagJson.toString());
		}

		Iterator<FlagData> flagDataList = BaseTable.insertUpdateDeleteManaged(co.getFlagDatas(), changes).iterator();
		while (flagDataList.hasNext()) {
			FlagData flagData = flagDataList.next();
			co.getFlagDatas().remove(flagData);
			dao.remove(flagData);
		}
		co.setAuditColumns(new User(User.SYSTEM));
	}

	private FlagChange getFlagChange(ContractorOperator co, FlagColor overallColor) {
		FlagChange flagChange = new FlagChange();
		flagChange.setContractor(co.getContractorAccount());
		flagChange.setOperator(co.getOperatorAccount());
		flagChange.setFromColor(co.getFlagColor());
		flagChange.setToColor(overallColor);
		flagChange.setTimestamp(new Date());
		flagChange.setDetails(co.getFlagDetail());
		return flagChange;
	}

	private void runWaitingOn(ContractorOperator co) throws Exception {
		if (!runStep(ContractorCronStep.WaitingOn)) {
			return;
		}

		logger.trace("ContractorCron starting WaitingOn for {}", co.getOperatorAccount().getName());
		WaitingOn waitingOn = null;
		flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagAuditCriteriaInherited());
		waitingOn = flagDataCalculator.calculateWaitingOn(co);

		if (!waitingOn.equals(co.getWaitingOn())) {
			OperatorAccount operator = co.getOperatorAccount();
			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setNoteCategory(NoteCategory.Flags);
			note.setPriority(LowMedHigh.Low);
			note.setAuditColumns(new User(User.SYSTEM));
			if (waitingOn.isNone()) {
				note.setSummary("We are no longer \"Waiting On\" " + co.getWaitingOn()
						+ ". All required information has been gathered for " + operator.getName() + ".");
			} else if (co.getWaitingOn().isNone()) {
				note.setSummary("The \"Waiting On\" status for " + operator.getName() + " has changed to \""
						+ waitingOn + "\"");
			} else {
				note.setSummary("The \"Waiting On\" status for " + operator.getName() + " has changed from \""
						+ co.getWaitingOn() + "\" to \"" + waitingOn + "\"");
			}
			if (co.getProcessCompletion() != null) {
				note.setBody("The contractor first completed the PICS process on "
						+ co.getProcessCompletion().toString());
			}
			note.setCanContractorView(true);
			note.setViewableById(operator.getId());
			dao.save(note);

			if (co.getProcessCompletion() == null && waitingOn.isNone() && !co.getWaitingOn().isNone()) {
				EventSubscriptionBuilder.contractorFinishedEvent(subscriptionDAO, co);
				co.setProcessCompletion(new Date());
			}
			co.setWaitingOn(waitingOn);
			SpringUtils.publishEvent(new ContractorOperatorWaitingOnChangedEvent(co));
		}
	}

	/**
	 * Calculate and save the recommended flag color for policies
	 *
	 * @param contractor
	 * @throws IOException
	 * @throws EmailException
	 * @throws NoUsersDefinedException
	 */
	private void runPolicies(ContractorAccount contractor) throws EmailException, IOException, NoUsersDefinedException {
		if (!runStep(ContractorCronStep.Policies)) {
			return;
		}

		logger.trace("ContractorCron starting Policies");
		// TODO we might be able to move this to the new FlagCalculator method
		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			for (ContractorAudit audit : co.getContractorAccount().getAudits()) {
				if (audit.getAuditType().getClassType().isPolicy() && !audit.isExpired()) {
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.getStatus().after(AuditStatus.Pending)) {
							if (cao.hasCaop(co.getOperatorAccount().getId())) {
								FlagColor flagColor = flagDataCalculator.calculateCaoStatus(audit.getAuditType(),
										co.getFlagDatas());

								cao.setFlag(flagColor);
								dao.save(cao);
							}
						}
					}
				}
			}
		}

		Set<ContractorAudit> expiringPolicies = getExpiringPolicies(contractor);
		Set<EmailSubscription> unsentWeeklyInsuranceSubscriptions = getUnsentWeeklyInsuranceSubscriptions(contractor);

		if (!expiringPolicies.isEmpty() && !unsentWeeklyInsuranceSubscriptions.isEmpty()) {
			EventSubscriptionBuilder
					.sendExpiringCertificatesEmail(unsentWeeklyInsuranceSubscriptions, expiringPolicies);
		}
	}

	private Set<EmailSubscription> getUnsentWeeklyInsuranceSubscriptions(ContractorAccount contractor)
			throws NoUsersDefinedException {
		Set<EmailSubscription> unsentWeeklyInsuranceSubscriptions = new HashSet<EmailSubscription>();
		List<EmailSubscription> contractorInsuranceSubscriptions = subscriptionDAO.find(
				Subscription.InsuranceExpiration, contractor.getId());

		if (contractorInsuranceSubscriptions.isEmpty()) {
			EmailSubscription defaultUserSubscription = contractor
					.getFallbackSubscriptionForDefaultContact(Subscription.InsuranceExpiration);
			unsentWeeklyInsuranceSubscriptions.add(defaultUserSubscription);
		} else {
			for (EmailSubscription contractorInsuranceSubscription : contractorInsuranceSubscriptions) {
				if (!contractorInsuranceSubscription.getTimePeriod().equals(SubscriptionTimePeriod.None)
						&& (contractorInsuranceSubscription.getLastSent() == null || contractorInsuranceSubscription
								.getLastSent().before(SubscriptionTimePeriod.Weekly.getComparisonDate()))) {
					unsentWeeklyInsuranceSubscriptions.add(contractorInsuranceSubscription);
				}
			}
		}

		return unsentWeeklyInsuranceSubscriptions;
	}

	private Set<ContractorAudit> getExpiringPolicies(ContractorAccount contractor) {
		Set<ContractorAudit> expiringAudits = new HashSet<ContractorAudit>();
		Map<Integer, List<ContractorAudit>> policies = createAuditPolicyMap(contractor);

		for (Map.Entry<Integer, List<ContractorAudit>> entry : policies.entrySet()) {
			ContractorAudit expiringAudit = getExpiringAudit(entry.getValue());
			if (expiringAudit != null) {
				expiringAudits.add(expiringAudit);
			}
		}

		return expiringAudits;
	}

	/**
	 * Returns a map of Policies, where the key is the Audit Type ID and the
	 * value is a list of ContractorAudits for that Audit Type ID.
	 *
	 * @return
	 */
	private Map<Integer, List<ContractorAudit>> createAuditPolicyMap(ContractorAccount contractor) {
		Map<Integer, List<ContractorAudit>> policies = new HashMap<Integer, List<ContractorAudit>>();

		if (CollectionUtils.isNotEmpty(contractor.getAudits())) {
			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().getClassType().isPolicy()) {
					int key = audit.getAuditType().getId();
					List<ContractorAudit> audits;
					if (!policies.containsKey(audit.getAuditType().getId())) {
						audits = new ArrayList<ContractorAudit>();
					} else {
						audits = policies.get(key);
					}

					audits.add(audit);
					policies.put(key, audits);
				}
			}
		}

		return policies;
	}

	private ContractorAudit getExpiringAudit(List<ContractorAudit> audits) {
		ContractorAudit expiringAudit = null;

		if (CollectionUtils.isNotEmpty(audits)) {
			sortAuditsByExpirationDateDescending(audits);
			ContractorAudit audit = audits.get(0);

			if (isExpiringRenewableAudit(audit)) {
				expiringAudit = audit;
			} else if (audits.size() > 1) {
				ContractorAudit previousAudit = audits.get(1);
				if (isAuditExpiringSoon(previousAudit) && !audit.hasCaoStatusAfter(AuditStatus.Resubmitted)) {
					expiringAudit = previousAudit;
				}
			}
		}

		return expiringAudit;
	}

	private void sortAuditsByExpirationDateDescending(List<ContractorAudit> audits) {
		Collections.sort(audits, new Comparator<ContractorAudit>() {

			@Override
			public int compare(ContractorAudit audit1, ContractorAudit audit2) {
				if (audit1.getExpiresDate() == null && audit2.getExpiresDate() == null) {
					return 0;
				} else if (audit1.getExpiresDate() == null) {
					return -1;
				} else if (audit2.getExpiresDate() == null) {
					return 1;
				}

				return audit2.getExpiresDate().compareTo(audit1.getExpiresDate());
			}

		});
	}

	private boolean isExpiringRenewableAudit(ContractorAudit audit) {
		return audit.getAuditType().isRenewable() && isAuditExpiringSoon(audit);
	}

	private boolean isAuditExpiringSoon(ContractorAudit audit) {
		return (audit.willExpireWithinTwoWeeks() || audit.expiredUpToAWeekAgo());
	}

	private void runCorporateRollup(ContractorAccount contractor, Set<OperatorAccount> corporateSet) {
		if (!runStep(ContractorCronStep.CorporateRollup)) {
			return;
		}

		logger.trace("ContractorCron starting CorporateRollup");
		// // rolls up every flag color to root
		Map<OperatorAccount, FlagColor> corporateRollupData = new HashMap<OperatorAccount, FlagColor>();
		// existingCorpCOs is really two related, but separate sets mashed
		// together that will be used
		// for determining inserts from the CorporateSet (OperatorAccount)
		// and managed insert/up/del off the linked COs (ContractorOperator)
		Map<OperatorAccount, ContractorOperator> existingCorpCOchanges = new HashMap<OperatorAccount, ContractorOperator>();
		Queue<OperatorAccount> corporateUpdateQueue = new LinkedList<OperatorAccount>();
		Set<ContractorOperator> removalSet = new HashSet<ContractorOperator>();

		Set<ContractorOperator> dblinkedCOs = new HashSet<ContractorOperator>();
		dblinkedCOs.addAll(contractor.getOperators());

		// rolling up data to map, getOperators is linked
		Iterator<ContractorOperator> coIter = dblinkedCOs.iterator();

		while (coIter.hasNext()) {
			ContractorOperator coOperator = coIter.next();
			OperatorAccount operator = coOperator.getOperatorAccount();

			if (operator.isCorporate()) {
				if (!corporateSet.contains(operator)) {
					// if we have a corporate account in my operator list that
					// is not in the corporate rollup data map
					// it shouldn't be in my operator list and should be deleted
					removalSet.add(coOperator);
					coIter.remove();
				} else {
					// will remove existing from Primary CorporateSet to
					// determine inserts
					existingCorpCOchanges.put(operator, coOperator);
				}
			} else { // determining first level of corporate data, and setting
				// up queue to iterate over rest of corporate levels

				// CO stuff
				// roll up first-level Operator CO data
				if (operator.getCorporateFacilities() != null) {
					for (Facility facility : operator.getCorporateFacilities()) {
						OperatorAccount parent = facility.getCorporate();

						corporateUpdateQueue.add(parent);

						// if CO data does not already exist, assume green flag
						// then if CO data is found later, will be updated to
						// proper flag color
						FlagColor parentFacilityColor = (corporateRollupData.get(parent) != null) ? corporateRollupData
								.get(parent) : FlagColor.Green;
						FlagColor operatorFacilityColor = coOperator.getFlagColor();
						FlagColor worstColor = FlagColor.getWorseColor(parentFacilityColor, operatorFacilityColor);
						corporateRollupData.put(parent, worstColor);
					}
				} // otherwise operator has no one to roll up to
			}
		}

		// Breadth first update of all corporate levels
		while (!corporateUpdateQueue.isEmpty()) {
			// grab first element off of queue
			OperatorAccount corporate = corporateUpdateQueue.remove();
			OperatorAccount parent = corporate.getParent();

			if (parent != null) {
				FlagColor parentFacilityColor = (corporateRollupData.get(parent) != null) ? corporateRollupData
						.get(parent) : FlagColor.Green;
				FlagColor currentFacilityColor = corporateRollupData.get(corporate);

				FlagColor worstColor = FlagColor.getWorseColor(parentFacilityColor, currentFacilityColor);
				corporateRollupData.put(parent, worstColor);

				// putting parent at the end of the queue for later calculation
				corporateUpdateQueue.add(parent);
			} // otherwise just remove entry
		}

		// Corporate update should be successful, now write out entries
		// for all entries that exist in my existingCorpCOchanges (linked).
		// Update entries based off of corporateRollupData.
		for (OperatorAccount corporate : existingCorpCOchanges.keySet()) {
			existingCorpCOchanges.get(corporate).setFlagColor(corporateRollupData.get(corporate));
			corporateSet.remove(corporate);
		}

		// whats left in corporateSet is inserts
		for (OperatorAccount corporate : corporateSet) {
			ContractorOperator newCo = new ContractorOperator();
			newCo.setCreationDate(new Date());
			newCo.setUpdateDate(new Date());
			newCo.setFlagColor(corporateRollupData.get(corporate));
			newCo.setOperatorAccount(corporate);
			newCo.setContractorAccount(contractor);
			newCo.setCreatedBy(new User(User.SYSTEM));
			newCo.setUpdatedBy(new User(User.SYSTEM));
			newCo.setDefaultWorkStatus();
			contractorOperatorDAO.save(newCo);
		}

		// delete orphans++++-
		for (ContractorOperator removal : removalSet) {
			contractorOperatorDAO.remove(removal);
			contractor.getOperators().remove(removal);
		}
	}

	private void runCSRAssignment(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.CSRAssignment)) {
			return;
		}

		logger.trace("ContractorCron starting CsrAssignment");
		UserAssignment assignment = userAssignmentDAO.findByContractor(contractor);
		if (assignment != null) {
			if (!assignment.getUser().equals(contractor.getCurrentCsr())) {
				contractor.makeUserCurrentCsrExpireExistingCsr(assignment.getUser(), User.SYSTEM);
			}
		}
	}

	/**
	 *
	 * This is so audits like the HSE Competency Submittal can have an auditor
	 * automatically assigned
	 *
	 * @param contractor
	 */
	private void runAssignAudit(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AssignAudit)) {
			return;
		}
		logger.trace("ContractorCron starting AssignAudit");
		// See if the PQF is complete for manual audit auditor assignment
		boolean pqfCompleteSafetyManualVerified = false;
		// Save auditor for manual audit for HSE Competency Review
		for (ContractorAudit audit : contractor.getAudits()) {
			if (!audit.isExpired()) {
				if (audit.getAuditType().isPqf() && audit.hasCaoStatus(AuditStatus.Complete)) {
					for (AuditData d : audit.getData()) {
						if (d.getQuestion().getId() == AuditQuestion.MANUAL_PQF) {
							pqfCompleteSafetyManualVerified = d.getDateVerified() != null;
						}
					}
				}
			}
		}

		// Note: audit.setAuditor() is the final arbitor of which auditor is assigned to do an audit. UserAssignment is merely the intermediate "rules" for pre-determining the assignments.
		UserAssignment ua = null;
		for (ContractorAudit audit : contractor.getAudits()) {
			if (!audit.isExpired() && audit.getAuditor() == null) {
				switch (audit.getAuditType().getId()) {
				case (AuditType.WA_STATE_VERIFICATION):
					ua = userAssignmentDAO.findByContractor(contractor, audit.getAuditType());
					if (ua != null) {
						// Assign both auditor and closing auditor
						audit.setAuditor(ua.getUser());
						audit.setClosingAuditor(ua.getUser());
						audit.setAssignedDate(new Date());
					}
					break;
				case (AuditType.DESKTOP):
					if (audit.getAuditor() == null && pqfCompleteSafetyManualVerified
							&& contractor.isFinanciallyReadyForAudits()) {
						ua = userAssignmentDAO.findByContractor(contractor, audit.getAuditType());
						if (ua == null) {
							List<UserAssignment> uaList = userAssignmentDAO.findByType(UserAssignmentType.Auditor);
							if (uaList != null && uaList.size() > 0) {
								ua = uaList.get(0);
							}
						}
						if (ua != null) {
							audit.setAuditor(ua.getUser());
							audit.setAssignedDate(new Date());
						}
					}
					if (audit.getClosingAuditor() == null && audit.getAuditor() != null
							&& audit.hasCaoStatusAfter(AuditStatus.Pending)) {
						audit.setClosingAuditor(new User(audit.getIndependentClosingAuditor(audit.getAuditor())));
					}
					break;
				case (AuditType.BPIISNCASEMGMT):
					audit.setAuditor(userDAO.find(55603));
					break;
				case (AuditType.WELCOME):
					audit.setAuditor(contractor.getCurrentCsr());
					break;
				}
			}
		}
	}

	private String getFlagDataDescription(FlagData data, OperatorAccount operator) {
		String description = "";

		FlagCriteria fc = data.getCriteria();
		FlagCriteriaOperator matchingFco = null;
		ArrayList<FlagCriteriaOperator> fcos = new ArrayList<FlagCriteriaOperator>();
		fcos.addAll(operator.getFlagCriteria());
		fcos.addAll(operator.getFlagCriteriaInherited());
		for (FlagCriteriaOperator fco : fcos) {
			if (fco.getCriteria().getId() == fc.getId()) {
				matchingFco = fco;
				break;
			}
		}

		if (matchingFco != null) {
			description = matchingFco.getReplaceHurdle();
		}

		return description;
	}

	// TODO This should be in a service
	private boolean shouldRunContractorCron(AccountStatus contractorStatus) {
		if (contractorStatus == AccountStatus.Requested) {
			return false;
		}

		if (contractorStatus == AccountStatus.Declined) {
			return false;
		}

		return true;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public ContractorCronStep[] getStepValues() {
		return ContractorCronStep.values();
	}

	public ContractorCronStep[] getSteps() {
		return steps;
	}

	public void setSteps(ContractorCronStep[] steps) {
		this.steps = steps;
	}

	private boolean runStep(ContractorCronStep step) {
		if (step == null || steps == null) {
			return false;
		}
		for (ContractorCronStep candidate : steps) {
			if (candidate.equals(step) || candidate.equals(ContractorCronStep.All)) {
				return true;
			}
		}
		return false;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public static Set<ContractorCron> getManager() {
		return manager;
	}

	public Date getStartTime() {
		return startTime;
	}

	public List<Integer> getQueue() {
		return queue;
	}

	public void setRequestID(String requestID) {
		// We aren't actually using this. This solves a weird bug with duplicate
		// requests not being processed in parallel
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@Autowired
	@Qualifier("FlagChangePublisher")
	public void setFlagChangePublisher(Publisher flagChangePublisher) {
		this.flagChangePublisher = flagChangePublisher;
	}
}
