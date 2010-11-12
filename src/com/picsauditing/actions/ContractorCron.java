package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorFlagETL;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.dao.UserAssignmentMatrixDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignmentMatrix;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.mail.SendMail;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorCron extends PicsActionSupport {

	static private Set<ContractorCron> manager = new HashSet<ContractorCron>();

	private PicsDAO dao;
	private ContractorAccountDAO contractorDAO;
	private ContractorOperatorDAO contractorOperatorDAO;
	private AuditDataDAO auditDataDAO;
	private EmailSubscriptionDAO subscriptionDAO;
	private FlagDataOverrideDAO flagDataOverrideDAO;

	private AuditPercentCalculator auditPercentCalculator;
	private AuditBuilderController auditBuilder;
	private ContractorFlagETL contractorFlagETL;
	private FlagDataCalculator flagDataCalculator;
	private AppPropertyDAO appPropertyDAO;
	private UserAssignmentMatrixDAO userAssignmentMatrixDAO;

	private int conID = 0;
	private int opID = 0;
	private ContractorCronStep[] steps = null;
	private int limit = 10;
	final private Date startTime = new Date();
	private List<Integer> queue;
	private String redirectUrl;

	public ContractorCron(ContractorAccountDAO contractorDAO,
			AuditDataDAO auditDataDAO, NoteDAO noteDAO,
			EmailSubscriptionDAO subscriptionDAO,
			AuditPercentCalculator auditPercentCalculator,
			AuditBuilderController auditBuilder,
			ContractorFlagETL contractorFlagETL,
			ContractorOperatorDAO contractorOperatorDAO,
			AppPropertyDAO appPropertyDAO,
			FlagDataOverrideDAO flagDataOverrideDAO,
			UserAssignmentMatrixDAO userAssignmentMatrixDAO) {
		this.dao = contractorDAO;
		this.contractorDAO = contractorDAO;
		this.auditDataDAO = auditDataDAO;
		this.subscriptionDAO = subscriptionDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
		this.contractorFlagETL = contractorFlagETL;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.appPropertyDAO = appPropertyDAO;
		this.flagDataOverrideDAO = flagDataOverrideDAO;
		this.userAssignmentMatrixDAO = userAssignmentMatrixDAO;
	}

	public String execute() throws Exception {
		if (steps == null)
			return SUCCESS;

		PicsLogger.start("ContractorCron");

		if (conID > 0) {
			run(conID, opID);
		} else {
			try {
				manager.add(this);

				double serverLoad = ServerInfo.getLoad();
				if (serverLoad > 3) {
					addActionError("Server Load is too high (" + serverLoad
							+ ")");
				} else {
					long totalQueueSize = contractorDAO
							.findNumberOfContractorsNeedingRecalculation();

					double limitDefault = Double.parseDouble(appPropertyDAO
							.find("ContractorCron.limit.default").getValue());
					double limitQueue = Double.parseDouble(appPropertyDAO.find(
							"ContractorCron.limit.queue").getValue());
					double limitServerLoad = Double
							.parseDouble(appPropertyDAO.find(
									"ContractorCron.limit.serverload")
									.getValue());

					// This is a formula based on a multiple regression analysis
					// of what we want. Not sure if it will work
					limit = (int) Math.round(limitDefault
							+ (totalQueueSize / limitQueue)
							- (serverLoad * limitServerLoad));

					if (limit > 0) {
						Set<Integer> contractorsToIgnore = new HashSet<Integer>();
						for (ContractorCron cron : manager) {
							if (!cron.equals(this))
								contractorsToIgnore.addAll(cron.getQueue());
						}
						queue = contractorDAO
								.findContractorsNeedingRecalculation(limit,
										contractorsToIgnore);

						for (Integer conID : queue) {
							run(conID, opID);
						}
						addActionMessage("ContractorCron processed "
								+ queue.size() + " record(s)");
					}
				}

			} catch (Exception e) {
				throw e;
			} finally {
				manager.remove(this);
			}
		}

		PicsLogger.stop();

		if (!Strings.isEmpty(redirectUrl)) {
			return redirect(redirectUrl);
		}

		return SUCCESS;
	}

	@Transactional
	private void run(int conID, int opID) {
		ContractorAccount contractor = contractorDAO.find(conID);

		try {
			runBilling(contractor);
			runAuditBuilder(contractor);
			runAuditCategory(contractor);
			runTradeETL(contractor);
			runContractorETL(contractor);
			runCSRAssignment(contractor);
			flagDataCalculator = new FlagDataCalculator(contractor
					.getFlagCriteria());

			if (runStep(ContractorCronStep.Flag)
					|| runStep(ContractorCronStep.WaitingOn)
					|| runStep(ContractorCronStep.Policies)
					|| runStep(ContractorCronStep.CorporateRollup)) {
				Set<OperatorAccount> corporateSet = new HashSet<OperatorAccount>();

				for (ContractorOperator co : contractor
						.getNonCorporateOperators()) {
					OperatorAccount operator = co.getOperatorAccount();
					// If the opID is 0, run through all the operators.
					// If the opID > 0, run through just that operator.
					if (opID == 0 || (opID > 0 && operator.getId() == opID)) {
						for (FlagCriteriaOperator flagCriteriaOperator : operator
								.getFlagCriteriaInherited()) {
							PicsLogger.log(" flag criteria "
									+ flagCriteriaOperator.getFlag()
									+ " for "
									+ flagCriteriaOperator.getCriteria()
											.getCategory());
						}

						if (runStep(ContractorCronStep.CorporateRollup)) {
							for (Facility facility : operator
									.getCorporateFacilities()) {
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
				dao.save(contractor);
				addActionMessage("Completed " + steps.length + " step(s) for "
						+ contractor.toString() + " successfully");
			}

			runPolicies(contractor);

		} catch (Throwable t) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));

			if (!isDebugging()) {
				addActionError("Error occurred on contractor " + conID + "<br>"
						+ t.getMessage());
				// In case this contractor errored out while running contractor
				// cron
				// we bump the last recalculation date to 1 day in future.
				dao.refresh(contractor);
				contractor.setNeedsRecalculation(0);
				contractor
						.setLastRecalculation(DateBean.addDays(new Date(), 1));
				dao.save(contractor);

				StringBuffer body = new StringBuffer();

				body
						.append("There was an error running ContractorCron for id=");
				body.append(conID);
				body.append("\n\n");

				body.append(t.getMessage());
				body.append("\n");

				body.append(sw.toString());
				sendMail(body.toString(), conID);
			} else {
				addActionError(sw.toString());
			}
		}
	}

	private void sendMail(String message, int conID) {
		try {
			SendMail sendMail = new SendMail();
			EmailQueue email = new EmailQueue();
			email.setToAddresses("errors@picsauditing.com");
			email.setFromAddress("PICS Mailer<info@picsauditing.com>");
			email.setSubject("Error in ContractorCron for conID = " + conID);
			email.setBody(message);
			email.setCreationDate(new Date());
			sendMail.send(email);
		} catch (Exception notMuchWeCanDoButLogIt) {
			System.out.println("Error sending email");
			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}
	}

	private void runBilling(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.Billing))
			return;
		InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		contractor.setNewMembershipLevel(fee);
		contractor.syncBalance();
	}

	private void runAuditCategory(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditCategory))
			return;
		for (ContractorAudit cAudit : contractor.getAudits()) {
			final Date lastRecalculation = cAudit.getLastRecalculation();
			if (lastRecalculation == null
					|| DateBean.getDateDifference(lastRecalculation) < -90) {
				auditPercentCalculator.percentCalculateComplete(cAudit, true);
				cAudit.setLastRecalculation(new Date());
				cAudit.setAuditColumns();
			}
		}
	}

	private void runAuditBuilder(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditBuilder))
			return;
		auditBuilder.buildAudits(contractor, getUser());
	}

	private void runTradeETL(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.TradeETL))
			return;
		List<AuditData> servicesPerformed = auditDataDAO
				.findServicesPerformed(contractor.getId());
		List<String> selfPerform = new ArrayList<String>();
		List<String> subContract = new ArrayList<String>();
		for (AuditData auditData : servicesPerformed) {
			if (auditData.getAnswer().startsWith("C"))
				selfPerform.add(auditData.getQuestion().getName());
			if (auditData.getAnswer().endsWith("S"))
				subContract.add(auditData.getQuestion().getName());
		}
		contractor.setTradesSelf(Strings.implode(selfPerform, ";"));
		contractor.setTradesSub(Strings.implode(subContract, ";"));

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
			AuditData oqAuditData = auditDataDAO.findAnswerByConQuestion(
					contractor.getId(), AuditQuestion.OQ_EMPLOYEES);
			contractor.setRequiresOQ(oqAuditData == null
					|| oqAuditData.getAnswer() == null
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
		int score = 120;
		for(ContractorAudit conAudit : contractor.getAudits()) {
			if(!conAudit.isExpired()) {
				for(ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
					if(cao.getStatus().isComplete() || cao.getStatus().isApproved())
						score += 100;
					else if (cao.getStatus().isSubmittedResubmitted())
						score += 80;
					else if (cao.getStatus().isIncomplete()) {
						
					}
					else score += -10;
				}
			}
		}
		List<AuditData> auditDatas = auditDataDAO.findAnswerByConQuestions(contractor.getId(), Arrays.asList(88,2447,5176,5179));
		for(AuditData auditData : auditDatas) {
			if(auditData.isAnswered()) {
				if(auditData.getQuestion().getId() == 88 && auditData.getAnswer().equals("Yes")) {
					score += -10;
				}
				else {
					score += auditData.getAnswer().replaceAll("[^0-9]","").length();
				}
			}
		}
		if(contractor.getMembershipLevel() != null) {
			score += contractor.getPayingFacilities()*10;
		}
		for (Invoice invoice :contractor.getInvoices()) {
			if(invoice.isOverdue())
				score += -25;
		}
	}
	
	private void runFlag(ContractorOperator co) {
		if (!runStep(ContractorCronStep.Flag))
			return;

		flagDataCalculator.setOperator(co.getOperatorAccount());
		flagDataCalculator.setOperatorCriteria(co.getOperatorAccount()
				.getFlagCriteriaInherited());

		Map<FlagCriteria, List<FlagDataOverride>> overridesMap = new HashMap<FlagCriteria, List<FlagDataOverride>>();

		Set<OperatorAccount> corporates = new HashSet<OperatorAccount>();
		for(Facility f : co.getOperatorAccount().getCorporateFacilities())
			corporates.add(f.getCorporate());
		
		for (FlagDataOverride override : co.getContractorAccount()
				.getFlagDataOverrides()) {
			if (override.getOperator().equals(co.getOperatorAccount())) {
				if (!overridesMap.containsKey(override.getCriteria()))
					overridesMap.put(override.getCriteria(),
							new LinkedList<FlagDataOverride>());
				((LinkedList<FlagDataOverride>)overridesMap.get(override.getCriteria())).addFirst(override);
			} else if(corporates.contains(co.getOperatorAccount())){
				if (!overridesMap.containsKey(override.getCriteria()))
					overridesMap.put(override.getCriteria(),
							new LinkedList<FlagDataOverride>());
				((LinkedList<FlagDataOverride>)overridesMap.get(override.getCriteria())).add(override);
			}
		}
		flagDataCalculator.setOverrides(overridesMap);
		List<FlagData> changes = flagDataCalculator.calculate();

		// Find overall flag color for this operator
		FlagColor overallColor = FlagColor.Green;
		if (co.getContractorAccount().isAcceptsBids()
				|| co.getContractorAccount().getStatus().isPending()
				|| co.getContractorAccount().getStatus().isDeleted())
			overallColor = FlagColor.Clear;

		for (FlagData change : changes) {
			if (!change.getCriteria().isInsurance())
				overallColor = FlagColor.getWorseColor(overallColor, change
						.getFlag());
		}

		ContractorOperator conOperator = co.getForceOverallFlag();
		if (conOperator != null) { // operator has a forced flag
			co.setFlagColor(conOperator.getForceFlag());
			co.setFlagLastUpdated(new Date());
		} else if (!overallColor.equals(co.getFlagColor())) {
			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setNoteCategory(NoteCategory.Flags);
			note.setAuditColumns(new User(User.SYSTEM));
			note.setSummary("Flag color changed from " + co.getFlagColor()
					+ " to " + overallColor + " for "
					+ co.getOperatorAccount().getName());
			note.setCanContractorView(true);
			note.setViewableById(co.getOperatorAccount().getId());
			dao.save(note);
			co.setFlagColor(overallColor);
			co.setFlagLastUpdated(new Date());
		}

		Iterator<FlagData> flagDataList = BaseTable.insertUpdateDeleteManaged(
				co.getFlagDatas(), changes).iterator();
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
		flagDataCalculator.setOperatorCriteria(co.getOperatorAccount()
				.getFlagAuditCriteriaInherited());
		waitingOn = flagDataCalculator.calculateWaitingOn(co);

		if (!waitingOn.equals(co.getWaitingOn())) {
			OperatorAccount operator = co.getOperatorAccount();
			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setNoteCategory(NoteCategory.Flags);
			note.setPriority(LowMedHigh.Low);
			note.setAuditColumns(new User(User.SYSTEM));
			if (waitingOn.isNone()) {
				note.setSummary("We are no longer \"Waiting On\" "
						+ co.getWaitingOn()
						+ ". All required information has been gathered for "
						+ operator.getName() + ".");
			} else if (co.getWaitingOn().isNone()) {
				note.setSummary("The \"Waiting On\" status for "
						+ operator.getName() + " has changed to \"" + waitingOn
						+ "\"");
			} else {
				note.setSummary("The \"Waiting On\" status for "
						+ operator.getName() + " has changed from \""
						+ co.getWaitingOn() + "\" to \"" + waitingOn + "\"");
			}
			if (co.getProcessCompletion() != null) {
				note
						.setBody("The contractor first completed the PICS process on "
								+ co.getProcessCompletion().toString());
			}
			note.setCanContractorView(true);
			note.setViewableById(operator.getId());
			dao.save(note);

			if (co.getProcessCompletion() == null && waitingOn.isNone()
					&& !co.getWaitingOn().isNone()) {
				EventSubscriptionBuilder.contractorFinishedEvent(
						subscriptionDAO, co);
				co.setProcessCompletion(new Date());
			}
			co.setWaitingOn(waitingOn);
		}
	}

	/**
	 * Calculate and save the recommended flag color for policies
	 * 
	 * @param contractor
	 */
	private void runPolicies(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.Policies))
			return;

		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			for (ContractorAudit audit : co.getContractorAccount().getAudits()) {
				if (audit.getAuditType().getClassType().isPolicy()
						&& !audit.isExpired()) {
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.getStatus().after(AuditStatus.Pending)) {
							if (cao.hasCaop(co.getOperatorAccount().getId())) {
								FlagColor flagColor = flagDataCalculator
										.calculateCaoStatus(audit
												.getAuditType(), co
												.getFlagDatas());

								cao.setFlag(flagColor);
								//dao.save(cao);
							}
						}
					}
				}
			}
		}
	}

	private void runCorporateRollup(ContractorAccount contractor,
			Set<OperatorAccount> corporateSet) {
		if (!runStep(ContractorCronStep.CorporateRollup))
			return;

		// // rolls up every flag color to root
		Map<OperatorAccount, FlagColor> corporateRollupData = new HashMap<OperatorAccount, FlagColor>();
		// existingCorpCOs is really two related, but separate sets mashed
		// together that will be used
		// for determining inserts from the CorporateSet (OperatorAccount)
		// and managed insert/up/del off the linked COs (ContractorOperator)
		Map<OperatorAccount, ContractorOperator> existingCorpCOchanges = new HashMap<OperatorAccount, ContractorOperator>();
		Queue<OperatorAccount> corporateUpdateQueue = new LinkedBlockingQueue<OperatorAccount>();
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
						FlagColor parentFacilityColor = (corporateRollupData
								.get(parent) != null) ? corporateRollupData
								.get(parent) : FlagColor.Green;
						FlagColor operatorFacilityColor = coOperator
								.getFlagColor();
						FlagColor worstColor = FlagColor.getWorseColor(
								parentFacilityColor, operatorFacilityColor);
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
				FlagColor parentFacilityColor = (corporateRollupData
						.get(parent) != null) ? corporateRollupData.get(parent)
						: FlagColor.Green;
				FlagColor currentFacilityColor = corporateRollupData
						.get(corporate);

				FlagColor worstColor = FlagColor.getWorseColor(
						parentFacilityColor, currentFacilityColor);
				corporateRollupData.put(parent, worstColor);

				// putting parent at the end of the queue for later calculation
				corporateUpdateQueue.add(parent);
			} // otherwise just remove entry
		}

		// Corporate update should be successful, now write out entries
		// for all entries that exist in my existingCorpCOchanges (linked).
		// Update entries based off of corporateRollupData.
		for (OperatorAccount corporate : existingCorpCOchanges.keySet()) {
			existingCorpCOchanges.get(corporate).setFlagColor(
					corporateRollupData.get(corporate));
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

		//contractorDAO.save(contractor);
	}

	private void runCSRAssignment(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.CSRAssignment))
			return;

		List<UserAssignmentMatrix> assignments = userAssignmentMatrixDAO
				.findByContractor(contractor);

		if (assignments.size() == 1) {
			contractor.setAuditor(assignments.get(0).getUser());
			contractorDAO.save(contractor);
		} else if (assignments.size() > 1) {
			// Manage Conflicts
		}
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
			if (candidate.equals(step)
					|| candidate.equals(ContractorCronStep.All))
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
