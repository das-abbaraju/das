package com.picsauditing.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorFlagETL;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.flags.ContractorScore;
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
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.mail.NoUsersDefinedException;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.util.ExpireUneededAnnualUpdates;
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

	static private Set<ContractorCron> manager = new HashSet<ContractorCron>();

	private FlagDataCalculator flagDataCalculator;
	private int conID = 0;
	private int opID = 0;
	private ContractorCronStep[] steps = null;
	private int limit = 10;
	final private Date startTime = new Date();
	private List<Integer> queue;
	private String redirectUrl;

	@Anonymous
	public String execute() throws Exception {
		if (steps == null)
			return SUCCESS;

		// PicsLogger.start("ContractorCron");

		if (conID > 0) {
			run(conID, opID);
		} else {
			addActionError("You must supply a contractor id.");
		}

		// PicsLogger.stop();

		if (!Strings.isEmpty(redirectUrl)) {
			return redirect(redirectUrl);
		}

		return SUCCESS;
	}

	@Anonymous
	public String listAjax() {
		List<Integer> ids = contractorDAO.findContractorsNeedingRecalculation(15, new HashSet<Integer>());
		output = Strings.implode(ids);
		return PLAIN_TEXT;
	}

	@Transactional
	private void run(int conID, int opID) throws Exception {
		ContractorAccount contractor = contractorDAO.find(conID);

		try {
			runBilling(contractor);
			runRemoveExtraAnnualUpdates(contractor);
			runAuditBuilder(contractor);
			runAuditCategory(contractor);
			runAssignAudit(contractor);
			runTradeETL(contractor);
			runContractorETL(contractor);
			runCSRAssignment(contractor);
			flagDataCalculator = new FlagDataCalculator(contractor.getFlagCriteria());

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

						if (opID > 0)
							break;
					}
				}
				runCorporateRollup(contractor, corporateSet);
			}

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
			runContractorScore(contractor);

		} catch (Exception continueUpTheStack) {
			setRecalculationToTomorrow(contractor);

			throw continueUpTheStack;
		}
	}

	private void setRecalculationToTomorrow(ContractorAccount contractor) {
		try {
			contractor.setNeedsRecalculation(0);
			contractor.setLastRecalculation(DateBean.addDays(new Date(), 1));
			contractorDAO.save(contractor);
		} catch (Exception notMuchWeCanDoButLogIt) {
			System.out.println("Error sending email");
			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}
	}

	private void runBilling(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.Billing))
			return;
		billingService.calculateAnnualFees(contractor);
		contractor.syncBalance();
	}

	private void runRemoveExtraAnnualUpdates(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.RemoveExtraAnnualUpdates))
			return;

		ExpireUneededAnnualUpdates.calculate(contractor);
	}

	private void runAuditBuilder(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditBuilder))
			return;
		auditBuilder.buildAudits(contractor);
	}

	private void runAuditCategory(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditCategory))
			return;
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
		if (!runStep(ContractorCronStep.TradeETL))
			return;

		Set<ContractorTrade> trades = contractor.getTrades();
		List<String> selfPerform = new ArrayList<String>();
		List<String> subContract = new ArrayList<String>();

		for (ContractorTrade trade : trades) {
			if (trade.isSelfPerformed())
				selfPerform.add(trade.getTrade().getName().toString());
			else
				subContract.add(trade.getTrade().getName().toString());
		}

		/*
		 * PICS-3313: This section is likely no longer needed, but this should prevent SQL errors when trying to save a
		 * contractor.
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
			if (co.getOperatorAccount().isRequiresOQ())
				requiresOQ = true;
			if (co.getOperatorAccount().isRequiresCompetencyReview())
				requiresCompetency = true;
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
				if (tag.getTag().getId() == OperatorTag.SHELL_COMPETENCY_REVIEW)
					contractor.setRequiresCompetencyReview(true);
			}
		}
	}

	private void runContractorETL(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.ContractorETL))
			return;
		contractorFlagETL.calculate(contractor);
	}

	private void runContractorScore(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.PICSScore))
			return;

		ContractorScore.calculate(contractor);
	}

	@SuppressWarnings("unchecked")
	private void runFlag(ContractorOperator co) {
		if (!runStep(ContractorCronStep.Flag))
			return;

		flagDataCalculator.setOperator(co.getOperatorAccount());
		flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagCriteriaInherited());

		Map<FlagCriteria, List<FlagDataOverride>> overridesMap = new HashMap<FlagCriteria, List<FlagDataOverride>>();

		Set<OperatorAccount> corporates = new HashSet<OperatorAccount>();
		for (Facility f : co.getOperatorAccount().getCorporateFacilities())
			corporates.add(f.getCorporate());

		for (FlagDataOverride override : co.getContractorAccount().getFlagDataOverrides()) {
			if (override.getOperator().equals(co.getOperatorAccount())) {
				if (!overridesMap.containsKey(override.getCriteria()))
					overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
				((LinkedList<FlagDataOverride>) overridesMap.get(override.getCriteria())).addFirst(override);
			} else if (corporates.contains(override.getOperator())) {
				if (!overridesMap.containsKey(override.getCriteria()))
					overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
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
					if (overallColor.equals(conOperator.getForceFlag()))
						co.setBaselineFlag(overallColor);
				}
			}
		} else if (!overallColor.equals(co.getFlagColor())) {
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

	private void runWaitingOn(ContractorOperator co) throws Exception {
		if (!runStep(ContractorCronStep.WaitingOn))
			return;

		WaitingOn waitingOn = null; // calcSingle.calculateWaitingOn();
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
		if (!runStep(ContractorCronStep.Policies))
			return;

		// TODO we might be able to move this to the new FlagCalculator method
		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			for (ContractorAudit audit : co.getContractorAccount().getAudits()) {
				if (audit.getAuditType().getClassType().isPolicy() && !audit.isExpired()) {
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.getStatus().after(AuditStatus.Pending)) {
							if (cao.hasCaop(co.getOperatorAccount().getId())) {
								FlagColor flagColor = flagDataCalculator.calculateCaoStatus(audit.getAuditType(), co
										.getFlagDatas());

								cao.setFlag(flagColor);
							}
						}
					}
				}
			}
		}

		Set<ContractorAudit> expiringPolicies = getExpiringPolicies(contractor);
		Set<EmailSubscription> unsentWeeklyInsuranceSubscriptions = getUnsentWeeklyInsuranceSubscriptions(contractor);

		if (!expiringPolicies.isEmpty() && !unsentWeeklyInsuranceSubscriptions.isEmpty())
			EventSubscriptionBuilder
					.sendExpiringCertificatesEmail(unsentWeeklyInsuranceSubscriptions, expiringPolicies);
	}

	private Set<EmailSubscription> getUnsentWeeklyInsuranceSubscriptions(ContractorAccount contractor)
			throws NoUsersDefinedException {
		Set<EmailSubscription> unsentWeeklyInsuranceSubscriptions = new HashSet<EmailSubscription>();
		List<EmailSubscription> contractorInsuranceSubscriptions = subscriptionDAO.find(
				Subscription.InsuranceExpiration, contractor.getId());

		if (contractorInsuranceSubscriptions.isEmpty()) {
			EmailSubscription defaultUserSubscription = createSubscriptionForDefaultContact(contractor);
			unsentWeeklyInsuranceSubscriptions.add(defaultUserSubscription);
		} else {
			for (EmailSubscription contractorInsuranceSubscription : contractorInsuranceSubscriptions) {
				if (contractorInsuranceSubscription.getLastSent() == null
						|| contractorInsuranceSubscription.getLastSent().before(
								SubscriptionTimePeriod.Weekly.getComparisonDate()))
					unsentWeeklyInsuranceSubscriptions.add(contractorInsuranceSubscription);
			}
		}

		return unsentWeeklyInsuranceSubscriptions;
	}

	private EmailSubscription createSubscriptionForDefaultContact(ContractorAccount contractor)
			throws NoUsersDefinedException {
		EmailSubscription defaultContactSubscription = new EmailSubscription();
		defaultContactSubscription.setAuditColumns();
		defaultContactSubscription.setSubscription(Subscription.InsuranceExpiration);
		defaultContactSubscription.setTimePeriod(SubscriptionTimePeriod.Event);

		if (contractor.getPrimaryContact() != null) {
			defaultContactSubscription.setUser(contractor.getPrimaryContact());
		} else if (!contractor.getUsersByRole(OpPerms.ContractorAdmin).isEmpty()) {
			defaultContactSubscription.setUser(contractor.getUsersByRole(OpPerms.ContractorAdmin).get(0));
		} else if (!contractor.getUsers().isEmpty()) {
			defaultContactSubscription.setUser(contractor.getUsers().get(0));
		} else {
			throw new NoUsersDefinedException();
		}

		subscriptionDAO.save(defaultContactSubscription);
		return defaultContactSubscription;
	}

	private Set<ContractorAudit> getExpiringPolicies(ContractorAccount contractor) {
		Set<ContractorAudit> expiringPolicies = new HashSet<ContractorAudit>();

		for (ContractorAudit audit : contractor.getAudits())
			if (audit.getAuditType().getClassType().isPolicy()
					&& (audit.willExpireWithinTwoWeeks() || audit.expiredUpToAWeekAgo()))
				expiringPolicies.add(audit);

		return expiringPolicies;
	}

	private void runCorporateRollup(ContractorAccount contractor, Set<OperatorAccount> corporateSet) {
		if (!runStep(ContractorCronStep.CorporateRollup))
			return;

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
			contractorOperatorDAO.save(newCo);
		}

		// delete orphans++++-
		for (ContractorOperator removal : removalSet) {
			contractorOperatorDAO.remove(removal);
			contractor.getOperators().remove(removal);
		}
	}

	private void runCSRAssignment(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.CSRAssignment))
			return;

		UserAssignment assignment = userAssignmentDAO.findByContractor(contractor);
		if (assignment != null) {
			if (!assignment.getUser().equals(contractor.getAuditor()))
				contractor.setAuditor(assignment.getUser());
		}
	}

	/**
	 * 
	 * This is so audits like the HSE Competency Submittal can have an auditor automatically assigned
	 * 
	 * @param contractor
	 */
	private void runAssignAudit(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AssignAudit))
			return;
		// See if the PQF is complete for manual audit auditor assignment
		boolean pqfCompleteSafetyManualVerified = false;
		// Save auditor for manual audit for HSE Competency Review
		for (ContractorAudit audit : contractor.getAudits()) {
			if (!audit.isExpired()) {
				if (audit.getAuditType().isPqf() && audit.hasCaoStatus(AuditStatus.Complete)) {
					for (AuditData d : audit.getData()) {
						if (d.getQuestion().getId() == AuditQuestion.MANUAL_PQF)
							pqfCompleteSafetyManualVerified = d.getDateVerified() != null;
					}
				}
			}
		}

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
				case (AuditType.WELCOME):
					audit.setAuditor(contractor.getAuditor());
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
		if (step == null || steps == null)
			return false;
		for (ContractorCronStep candidate : steps)
			if (candidate.equals(step) || candidate.equals(ContractorCronStep.All))
				return true;
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
