package com.picsauditing.actions;

import com.picsauditing.PICS.*;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.cron.AuditCategoryJobException;
import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.dao.*;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.flagcalculator.FlagCalculator;
import com.picsauditing.flagcalculator.FlagData;
import com.picsauditing.flags.ContractorScore;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.*;
import com.picsauditing.messaging.MessagePublisherService;
import com.picsauditing.model.events.ContractorOperatorWaitingOnChangedEvent;
import com.picsauditing.models.audits.InsurancePolicySuggestionCalculator;
import com.picsauditing.rbic.RulesRunner;
import com.picsauditing.service.AuditService;
import com.picsauditing.service.account.WaitingOnService;
import com.picsauditing.service.employeeGuard.EmployeeGuardRulesService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("serial")
public class ContractorCron extends PicsActionSupport {

	@Autowired
	private ContractorAccountDAO contractorDAO;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private FlagCriteriaDAO flagCriteriaDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private UserAssignmentDAO userAssignmentDAO;
	@Autowired
	private ContractorAuditDAO conAuditDAO;
	@Autowired
	private BillingService billingService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private ExceptionService exceptionService;
    @Autowired
    private FeeService feeService;
	@Autowired
	private FeatureToggle featureToggleChecker;
    @Autowired
    private RulesRunner rulesRunner;
    @Autowired
    private EmployeeGuardRulesService employeeGuardRulesService;
    @Autowired
    private MessagePublisherService messageService;
    @Autowired
    private AuditBuilderFactory auditBuilderFactory;
    @Autowired
    private FlagCalculatorFactory flagCalculatorFactory;
    @Autowired
    private WaitingOnService waitingOnService;

	static private Set<ContractorCron> manager = new HashSet<>();

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

		logger.trace("Starting ContractorCron for {}", conID);
		try {
			run(contractor, opID);

			if (!Strings.isEmpty(redirectUrl)) {
				return setUrlForRedirect(redirectUrl);
			}
		} catch (Exception e) {
			String exceptionResult = handleException(e, contractor);
			if (Strings.isEmpty(exceptionResult)) {
				throw e;
			}

			return exceptionResult;
		}

		logger.trace("Finished ContractorCron");

		return SUCCESS;
	}

	private String handleException(Exception exception, ContractorAccount contractor) {
		logger.error("ContractorCron failed for conID " + conID, exception);
		setRecalculationToTomorrow(contractor);

		if (Strings.isNotEmpty(redirectUrl)) {
			try {
				exceptionService.sendExceptionEmail(permissions, exception, "Error in Contractor Cron calculating account id #" + conID);
				return setUrlForRedirect(redirectUrl);  // todo: Investigate. Why do we return the redirectUrl only if the email succeeds?
			} catch (Exception e) {
				logger.error("An error occurred while sending exception email for ContractorCron. ConID " + conID, e);
			}
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
			runBilling(contractor);
			runAuditBuilder(contractor);
			runAuditCategory(contractor);
			runAssignAudit(contractor);
			runTradeETL(contractor);
			runContractorETL(contractor);
			runCSRAssignment(contractor);
            runRulesBasedInsuranceCriteria(contractor);
            runEmployeeGuardRules(contractor);

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
                addActionMessage(getTextParameterized(this.getLocale(),"ContractorCron.message.SuccessRefresh",contractor.toString()));
			}

			runPolicies(contractor);
	}

    private void runEmployeeGuardRules(ContractorAccount contractor) {
        if (runStep(ContractorCronStep.EmployeeGuardRules) && Features.USE_NEW_EMPLOYEE_GUARD_RULES.isActive()) {
            employeeGuardRulesService.runEmployeeGuardRules(contractor);
        }
    }

    private void runRulesBasedInsuranceCriteria(ContractorAccount contractor) {
        if (!runStep(ContractorCronStep.RulesBasedInsurance)) {
            return;
        }
        rulesRunner.setContractor(contractor);

        contractor.setInsuranceCriteriaContractorOperators(new ArrayList<InsuranceCriteriaContractorOperator>());
        dao.deleteData(InsuranceCriteriaContractorOperator.class, "t.contractorAccount.id = " + contractor.getId());

        for (ContractorOperator contractorOperator : contractor.getOperators()) {
            OperatorAccount operatorAccount = contractorOperator.getOperatorAccount();
            try {
                rulesRunner.runInsuranceCriteriaRulesForOperator(operatorAccount);
            } catch (RuntimeException e) {
                logger.error(e.getMessage() + " for contractor " + contractor.getName()
                        + " working for " + operatorAccount.getName());
                //continue;
            }
        }

    }

	private void setRecalculationToTomorrow(ContractorAccount contractor) {
		if (contractor == null) {
			return;
		}

		try {
			contractorDAO.refresh(contractor);// fix Hibernate exception: Found two representations of same collection

			contractor.setNeedsRecalculation(0);
			contractor.setLastRecalculation(DateBean.addDays(new Date(), 1));
			contractorDAO.save(contractor);
		} catch (Exception e) {
			logger.error("Error setting recalculation for tomorrow on contractor: " + e.getMessage());
		}
	}

	private void runBilling(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.Billing)) {
			return;
		}

		logger.trace("ContractorCron starting Billing");
		feeService.calculateContractorInvoiceFees(contractor);
		billingService.syncBalance(contractor);
	}

	private void runAuditBuilder(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditBuilder)) {
			return;
		}

		logger.trace("ContractorCron starting AuditBuilder");
        auditBuilderFactory.buildAudits(contractor);
        contractorDAO.refresh(contractor);

		checkLcCor(contractor);
		cancelScheduledImplementationAudits(contractor);
        auditService.checkSla(contractor, permissions);
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
		featureToggleChecker.addToggleVariable("contractor", contractor);
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

	private void runAuditCategory(ContractorAccount contractor) throws AuditCategoryJobException {
		if (!runStep(ContractorCronStep.AuditCategory)) {
			return;
		}

		logger.trace("ContractorCron starting AuditCategory");
		for (ContractorAudit cAudit : contractor.getAudits()) {
			try {
				final Date lastRecalculation = cAudit.getLastRecalculation();
				if (lastRecalculation == null || DateBean.getDateDifference(lastRecalculation) < -14) {
                    auditBuilderFactory.percentCalculateComplete(cAudit);
				}
			} catch (Exception e) {
				throw new AuditCategoryJobException("Failed to run audit category for contractorAccount id: " + contractor.getId()
						+ " and audit id: " + cAudit.getId(), e);
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

			if (contractor.hasOperatorWithCompetencyRequiringDocumentation() &&
					!hasContractorTagRemovingEmployeeGUARD(contractor)) {
				contractor.setRequiresCompetencyReview(true);
			}
		}
	}

	private boolean hasContractorTagRemovingEmployeeGUARD(ContractorAccount contractor) {
		for (ContractorTag contractorTag : contractor.getOperatorTags()) {
			OperatorTagCategory operatorTagCategory = contractorTag.getTag().getCategory();
			if (operatorTagCategory != null && operatorTagCategory.isRemoveEmployeeGUARD()) {
				return true;
			}
		}

		return false;
	}

	private void runContractorETL(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.ContractorETL)) {
			return;
		}
		logger.trace("ContractorCron starting ContractorETL");
        flagCalculatorFactory.runContractorFlagETL(contractor);
	}

	private void runContractorScore(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.PICSScore)) {
			return;
		}

		logger.trace("ContractorCron starting PicsScore");
		ContractorScore.calculate(contractor);
	}

	@SuppressWarnings("unchecked")
	private void runFlag(ContractorOperator contractorOperator) throws Exception {
		if (!runStep(ContractorCronStep.Flag)) {
			return;
		}

		logger.trace("ContractorCron starting Flags for {}", contractorOperator.getOperatorAccount().getName());

        FlagColor previousFlagColor = contractorOperator.getFlagColor();

        FlagCalculator flagCalculator = flagCalculatorFactory.flagCalculator(contractorOperator, messageService);
		List<com.picsauditing.flagcalculator.FlagData> changes = flagCalculator.calculate();
        addLabelsToChanges(changes);
        boolean needNote = flagCalculator.saveFlagData(changes);
        dao.refresh(contractorOperator);

        if (needNote) {
            FlagColor overallColor = FlagColor.Green;
            String reason = "Contractor is no longer flagged on any criteria for this operator.";
            if (contractorOperator.getContractorAccount().getAccountLevel().isBidOnly()
                    || contractorOperator.getContractorAccount().getStatus().isPending()
                    || contractorOperator.getContractorAccount().getStatus().isDeleted()
                    || contractorOperator.getContractorAccount().getStatus().isDeclined()
                    || contractorOperator.getContractorAccount().getStatus().isDeactivated()) {
                overallColor = FlagColor.Clear;
                reason = "Contractor no longer tracked by flags.";
            }

            for (com.picsauditing.flagcalculator.FlagData change : changes) {
                FlagColor changeFlag = FlagColor.valueOf(change.getFlagColor());
                if (!change.isInsurance()) {
                    FlagColor worst = FlagColor.getWorseColor(overallColor, changeFlag);
                    if (worst != overallColor) {
                        reason = getFlagDataDescription(change, contractorOperator.getOperatorAccount());
                    }
                    overallColor = worst;
                }
            }

            Note note = new Note();
            note.setAccount(contractorOperator.getContractorAccount());
            note.setNoteCategory(NoteCategory.Flags);
            note.setAuditColumns(new User(User.SYSTEM));
            note.setSummary("Flag color changed from " + previousFlagColor + " to " + contractorOperator.getFlagColor() + " for "
                    + contractorOperator.getOperatorAccount().getName());
            note.setBody(reason);
            note.setCanContractorView(true);
            note.setViewableBy(contractorOperator.getOperatorAccount());
            dao.save(note);
        }
    }

    private void addLabelsToChanges(List<com.picsauditing.flagcalculator.FlagData> changes) {
        for (com.picsauditing.flagcalculator.FlagData flagData : changes) {
            com.picsauditing.flagcalculator.entities.FlagData flagDatum = (com.picsauditing.flagcalculator.entities.FlagData) flagData;
            FlagCriteria flagCriteria = flagCriteriaDAO.find(flagDatum.getCriteria().getId());
            flagDatum.setCriteriaLabel(flagCriteria.getLabel());
        }
    }

    private String getFlagDataDescription(FlagData data, OperatorAccount operator) {
        String description = "";

        FlagCriteriaOperator matchingFco = null;
        ArrayList<FlagCriteriaOperator> fcos = new ArrayList<FlagCriteriaOperator>();
        fcos.addAll(operator.getFlagCriteria());
        fcos.addAll(operator.getFlagCriteriaInherited());
        for (FlagCriteriaOperator fco : fcos) {
            if (fco.getCriteria().getId() == data.getCriteriaID()) {
                matchingFco = fco;
                break;
            }
        }

        if (matchingFco != null) {
            description = matchingFco.getReplaceHurdle();
        }

        return description;
    }

    private boolean isOverrideApplicableToOperator(FlagDataOverride override, OperatorAccount operator) {
		for (FlagCriteriaOperator fco:operator.getFlagCriteriaInherited()) {
			if (override.getCriteria().equals(fco.getCriteria())) {
				// need to check if audit is not expired
				if (!Strings.isEmpty(override.getYear())) {
					for (OshaAudit audit: override.getContractor().getOshaAudits()) {
						if (override.getYear().equals(audit.getAuditFor())) {
							return true;
						}
					}
					return false;
				}
				return true;
			}
		}
		return false;
	}

	private void runWaitingOn(ContractorOperator co) throws Exception {
		if (!runStep(ContractorCronStep.WaitingOn)) {
			return;
		}

		logger.trace("ContractorCron starting WaitingOn for {}", co.getOperatorAccount().getName());
		WaitingOn waitingOn = null;
        waitingOn = waitingOnService.calculateWaitingOn(co);

		if (!waitingOn.equals(co.getWaitingOn()) && !co.getWorkStatus().isNo()) {
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
	private void runPolicies(ContractorAccount contractor) throws EmailException, IOException, NoUsersDefinedException, EmailBuildErrorException {
		if (!runStep(ContractorCronStep.Policies)) {
			return;
		}

		logger.trace("ContractorCron starting Policies");

        InsurancePolicySuggestionCalculator.calculateSuggestionForAllPolicies(contractor);
        dao.save(contractor);

		Set<ContractorAudit> expiringPolicies = getExpiringPolicies(contractor);
		Set<EmailSubscription> unsentWeeklyInsuranceSubscriptions = getUnsentWeeklyInsuranceSubscriptions(contractor);

		if (!expiringPolicies.isEmpty() && !unsentWeeklyInsuranceSubscriptions.isEmpty()) {
			EventSubscriptionBuilder.sendExpiringCertificatesEmail(unsentWeeklyInsuranceSubscriptions, expiringPolicies);
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
		Map<OperatorAccount, FlagColor> corporateRollupFlag = new HashMap<OperatorAccount, FlagColor>();
		Map<OperatorAccount, WaitingOn> corporateRollupWaitingOn = new HashMap<OperatorAccount, WaitingOn>();
		// existingCorpCOs is really two related, but separate sets mashed
		// together that will be used
		// for determining inserts from the CorporateSet (OperatorAccount)
		// and managed insert/up/del off the linked COs (ContractorOperator)
		Map<OperatorAccount, ContractorOperator> existingCorpCOchanges = new HashMap<OperatorAccount, ContractorOperator>();
		Queue<OperatorAccount> corporateUpdateQueue = new LinkedList<OperatorAccount>();
		Set<ContractorOperator> removalSet = new HashSet<ContractorOperator>();

		if (!shouldRunContractorCron(contractor.getStatus())) {
			for (ContractorOperator co : contractor.getOperators()) {
				co.setFlagColor(FlagColor.Clear);
				co.setBaselineFlag(FlagColor.Clear);
			}
			return;
		}

		Set<ContractorOperator> dblinkedCOs = new HashSet<ContractorOperator>();
		dblinkedCOs.addAll(contractor.getOperators());

		// rolling up data to map, getOperators is linked
		Iterator<ContractorOperator> coIter = dblinkedCOs.iterator();

		while (coIter.hasNext()) {
			ContractorOperator contractorOperator = coIter.next();
			OperatorAccount operator = contractorOperator.getOperatorAccount();

			if (operator.isCorporate()) {
				if (!corporateSet.contains(operator)) {
					// if we have a corporate account in my operator list that
					// is not in the corporate rollup data map
					// it shouldn't be in my operator list and should be deleted
					removalSet.add(contractorOperator);
					coIter.remove();
				} else {
					// will remove existing from Primary CorporateSet to
					// determine inserts
					existingCorpCOchanges.put(operator, contractorOperator);
				}
			} else {
				// determining first level of corporate data, and setting
				// up queue to iterate over rest of corporate levels

				// CO stuff
				// roll up first-level Operator CO data
				if (operator.getStatus().isActiveOrDemo() && operator.getCorporateFacilities() != null) {
					FlagColor operatorFlag = contractorOperator.getFlagColor();
					WaitingOn operatorWaitingOn = contractorOperator.getWaitingOn();

					for (Facility facility : operator.getCorporateFacilities()) {
						OperatorAccount parent = facility.getCorporate();
						FlagColor parentFlag = findFlagOrDefaultToGreen(corporateRollupFlag, parent);
						WaitingOn parentWaitingOn = findWaitingOnOrDefaultToNone(corporateRollupWaitingOn, parent);

						corporateUpdateQueue.add(parent);
						corporateRollupFlag.put(parent, worstFlag(parentFlag, operatorFlag));
						corporateRollupWaitingOn.put(parent, worstWaitingOn(parentWaitingOn, operatorWaitingOn));
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
				FlagColor corporateFlag = findFlagOrDefaultToGreen(corporateRollupFlag, corporate);
				WaitingOn corporateWaitingOn = findWaitingOnOrDefaultToNone(corporateRollupWaitingOn, corporate);

				FlagColor parentFlag = findFlagOrDefaultToGreen(corporateRollupFlag, parent);
				WaitingOn parentWaitingOn = findWaitingOnOrDefaultToNone(corporateRollupWaitingOn, parent);

				corporateRollupFlag.put(parent, worstFlag(parentFlag, corporateFlag));
				corporateRollupWaitingOn.put(parent, worstWaitingOn(parentWaitingOn, corporateWaitingOn));

				// putting parent at the end of the queue for later calculation
				corporateUpdateQueue.add(parent);
			} // otherwise just remove entry
		}

		// Corporate update should be successful, now write out entries
		// for all entries that exist in my existingCorpCOchanges (linked).
		// Update entries based off of corporateRollupData.
		for (OperatorAccount corporate : existingCorpCOchanges.keySet()) {
			FlagColor corporateFlag = findFlagOrDefaultToGreen(corporateRollupFlag, corporate);
			WaitingOn corporateWaitingOn = findWaitingOnOrDefaultToNone(corporateRollupWaitingOn, corporate);

			existingCorpCOchanges.get(corporate).setFlagColor(corporateFlag);
			existingCorpCOchanges.get(corporate).setWaitingOn(corporateWaitingOn);

			corporateSet.remove(corporate);
		}

		// whats left in corporateSet is inserts
		for (OperatorAccount corporate : corporateSet) {
			FlagColor flag = findFlagOrDefaultToGreen(corporateRollupFlag, corporate);

			ContractorOperator newCo = new ContractorOperator();
			newCo.setFlagColor(flag);
			WaitingOn waitingOn = corporateRollupWaitingOn.get(corporate);
			if (waitingOn == null)
				waitingOn = WaitingOn.None;
			newCo.setWaitingOn(waitingOn);
			newCo.setOperatorAccount(corporate);
			newCo.setContractorAccount(contractor);

			newCo.setAuditColumns(new User(User.SYSTEM));

			if (corporate.isAutoApproveRelationships()) {
				newCo.setWorkStatus(ApprovalStatus.Approved);
			}
			newCo.setDefaultWorkStatus();

			contractorOperatorDAO.save(newCo);
		}

		// delete orphans++++-
		for (ContractorOperator removal : removalSet) {
			contractorOperatorDAO.remove(removal);
			contractor.getOperators().remove(removal);
		}
	}

	private FlagColor worstFlag(FlagColor parentFlag, FlagColor operatorFlag) {
		FlagColor worstColor = FlagColor.getWorseColor(parentFlag, operatorFlag);
		if (worstColor != null) {
			return worstColor;
		}

		return FlagColor.Green;
	}

	private FlagColor findFlagOrDefaultToGreen(Map<OperatorAccount, FlagColor> corporateRollupFlag, OperatorAccount parent) {
		if (corporateRollupFlag.get(parent) != null) {
			return corporateRollupFlag.get(parent);
		}

		return FlagColor.Green;
	}

	private WaitingOn worstWaitingOn(WaitingOn parentOperatorWaitingOn, WaitingOn operatorWaitingOn) {
		WaitingOn worstWaitingOn = WaitingOn.getWorseWaitingOn(parentOperatorWaitingOn, operatorWaitingOn);
		if (worstWaitingOn != null) {
			return worstWaitingOn;
		}

		return WaitingOn.None;
	}

	private WaitingOn findWaitingOnOrDefaultToNone(Map<OperatorAccount, WaitingOn> corporateRollupWaitingOn,
												   OperatorAccount parent) {
		if (corporateRollupWaitingOn.get(parent) != null) {
			return corporateRollupWaitingOn.get(parent);
		}

		return WaitingOn.None;
	}

	private void rollUpCorporateFlags(Map<OperatorAccount, FlagColor> corporateRollupData,
									  Queue<OperatorAccount> corporateUpdateQueue,
									  ContractorOperator coOperator, OperatorAccount operator) {
		if (operator.getStatus() != AccountStatus.Active && operator.getStatus() != AccountStatus.Demo) {
			return;
		}

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
			if (worstColor == null)
				worstColor = FlagColor.Green;
			corporateRollupData.put(parent, worstColor);
		}
	}

	private void runCSRAssignment(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.CSRAssignment)) {
			return;
		}

		if (contractor.getStatus().isActive() && contractor.getCurrentCsr() == null) {
			logger.trace("ContractorCron starting CsrAssignment");
			messageService.getCsrAssignmentSinglePublisher().publish(contractor.getId());
		}

	}

	/**
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
		// Note: audit.setAuditor() is the final arbitor of which auditor is
		// assigned to do an audit. UserAssignment is merely the intermediate
		// "rules" for pre-determining the assignments.
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
					case (AuditType.MANUAL_AUDIT):
						if (audit.getSlaDate() != null) { 
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

                                Note note = new Note();
                                note.setAccount(audit.getContractorAccount());
                                note.setAuditColumns(permissions);
                                note.setSummary("Auto assign Audit #" + audit.getId() + " was assigned to " + ua.getUser());
                                note.setNoteCategory(NoteCategory.Audits);
                                note.setViewableById(Account.PICS_ID);
                                noteDAO.save(note);
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

	// TODO This should be in a service
	private boolean shouldRunContractorCron(AccountStatus contractorStatus) {
		if (contractorStatus == AccountStatus.Requested) {
			return false;
		}

		if (contractorStatus == AccountStatus.Pending) {
			return false;
		}

		if (contractorStatus == AccountStatus.Deactivated) {
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
}
