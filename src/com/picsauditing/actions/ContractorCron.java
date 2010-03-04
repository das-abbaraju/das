package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorFlagETL;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorCron extends PicsActionSupport {

	private PicsDAO dao;
	private ContractorAccountDAO contractorDAO;
	private ContractorOperatorDAO contractorOperatorDAO;
	private AuditDataDAO auditDataDAO;
	private EmailSubscriptionDAO subscriptionDAO;
	private FlagDataDAO flagDataDAO;
	private FlagDataOverrideDAO flagDataOverrideDAO;

	private AuditPercentCalculator auditPercentCalculator;
	private AuditBuilder auditBuilder;
	private ContractorFlagETL contractorFlagETL;
	private FlagDataCalculator flagDataCalculator;

	private int conID = 0;
	private int opID = 0;
	private ContractorCronStep[] steps = null;

	public ContractorCron(ContractorAccountDAO contractorDAO, AuditDataDAO auditDataDAO, NoteDAO noteDAO,
			EmailSubscriptionDAO subscriptionDAO, AuditPercentCalculator auditPercentCalculator,
			AuditBuilder auditBuilder, ContractorFlagETL contractorFlagETL, FlagDataDAO flagDataDAO,
			FlagDataOverrideDAO flagDataOverrideDAO, ContractorOperatorDAO contractorOperatorDAO) {
		this.dao = contractorDAO;
		this.contractorDAO = contractorDAO;
		this.auditDataDAO = auditDataDAO;
		this.subscriptionDAO = subscriptionDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
		this.contractorFlagETL = contractorFlagETL;
		this.flagDataDAO = flagDataDAO;
		this.flagDataOverrideDAO = flagDataOverrideDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
	}

	public String execute() throws Exception {
		if (steps == null)
			return SUCCESS;

		if (isDebugging())
			PicsLogger.addRuntimeRule("ContractorCron");

		PicsLogger.start("ContractorCron");

		if (conID > 0) {
			run(conID, opID);
		} else {
			List<Integer> list = contractorDAO.findContractorsNeedingRecalculation();
			for (Integer conID : list) {
				run(conID, opID);
			}
			addActionMessage("ContractorCron processed " + list.size() + " record(s)");
		}

		PicsLogger.stop();
		return SUCCESS;
	}

	@Transactional
	private void run(int conID, int opID) {
		try {
			ContractorAccount contractor = contractorDAO.find(conID);
			flagDataCalculator = new FlagDataCalculator(contractor.getFlagCriteria());

			runBilling(contractor);
			runAuditCategory(contractor);
			runAuditBuilder(contractor);
			runTradeETL(contractor);
			runContractorETL(contractor);

			if (runStep(ContractorCronStep.Flag) || runStep(ContractorCronStep.WaitingOn)
					|| runStep(ContractorCronStep.Policies) || runStep(ContractorCronStep.CorporateRollup)) {
				Set<OperatorAccount> corporateSet = new HashSet<OperatorAccount>();
				for (ContractorOperator co : contractor.getNonCorporateOperators()) {
					OperatorAccount operator = co.getOperatorAccount();
					// If the opID is 0, run through all the operators.
					// If the opID > 0, run through just that operator.
					if (opID == 0 || (opID > 0 && operator.getId() == opID)) {
						for (FlagCriteriaOperator flagCriteriaOperator : operator.getFlagCriteriaInherited()) {
							PicsLogger.log(" flag criteria " + flagCriteriaOperator.getFlag() + " for "
									+ flagCriteriaOperator.getCriteria().getCategory());
						}

						for (AuditOperator auditOperator : operator.getVisibleAudits())
							PicsLogger.log(" can see audit " + auditOperator.getAuditType().getAuditName());

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
			runPolicies(contractor);

			if (steps != null && steps.length > 0) {
				contractor.setNeedsRecalculation(false);
				contractor.setLastRecalculation(new Date());
				dao.save(contractor);

				addActionMessage("Completed " + steps.length + " step(s) for " + contractor.toString()
						+ " successfully");
			}
		} catch (Throwable t) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));

			if (!isDebugging()) {
				addActionError("Error occurred on contractor " + conID + "<br>" + t.getMessage());

				StringBuffer body = new StringBuffer();

				body.append("There was an error running ContractorCron for id=");
				body.append(conID);
				body.append("\n\n");

				body.append(t.getMessage());
				body.append("\n");

				body.append(sw.toString());

				sendMail(body.toString());
			} else {
				addActionError(sw.toString());
			}
		}
	}

	private void sendMail(String message) {
		try {
			EmailQueue email = new EmailQueue();
			email.setToAddresses("errors@picsauditing.com");
			email.setPriority(30);
			email.setSubject("Error in ContractorCron for conID = " + conID);
			email.setBody(message);
			email.setCreationDate(new Date());
			EmailSender sender = new EmailSender();
			sender.sendNow(email);
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
			if (lastRecalculation == null || DateBean.getDateDifference(lastRecalculation) < -90) {
				auditPercentCalculator.percentCalculateComplete(cAudit, true);
				cAudit.setLastRecalculation(new Date());
				cAudit.setAuditColumns();
			}
		}
	}

	private void runAuditBuilder(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.AuditBuilder))
			return;
		auditBuilder.buildAudits(contractor);
	}

	private void runTradeETL(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.TradeETL))
			return;
		List<AuditData> servicesPerformed = auditDataDAO.findServicesPerformed(contractor.getId());
		List<String> selfPerform = new ArrayList<String>();
		List<String> subContract = new ArrayList<String>();
		for (AuditData auditData : servicesPerformed) {
			if (auditData.getAnswer().startsWith("C"))
				selfPerform.add(auditData.getQuestion().getQuestion());
			if (auditData.getAnswer().endsWith("S"))
				subContract.add(auditData.getQuestion().getQuestion());
		}
		contractor.setTradesSelf(Strings.implode(selfPerform, ";"));
		contractor.setTradesSub(Strings.implode(subContract, ";"));
	}

	private void runContractorETL(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.ContractorETL))
			return;
		contractorFlagETL.calculate(contractor);
	}

	private void runFlag(ContractorOperator co) {
		if (!runStep(ContractorCronStep.Flag))
			return;
		// get a list of overrides for this contractor and operator

		flagDataCalculator.setOperator(co.getOperatorAccount());
		flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagCriteriaInherited());
		Map<FlagCriteria, FlagDataOverride> overrides = flagDataOverrideDAO.findByContractorAndOperator(co
				.getContractorAccount(), co.getOperatorAccount());
		flagDataCalculator.setOverrides(overrides);

		List<FlagData> changes = flagDataCalculator.calculate();

		// Find overall flag color for this operator
		FlagColor overallColor = FlagColor.Green;
		for (FlagData change : changes) {
			if (change.getCriteria().getQuestion() == null || !change.getCriteria().getCategory().equals("InsureGUARD"))
				overallColor = FlagColor.getWorseColor(overallColor, change.getFlag());
		}

		if (!overallColor.equals(co.getFlagColor())) {
			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setNoteCategory(NoteCategory.Flags);
			note.setAuditColumns(new User(User.SYSTEM));
			note.setSummary("Flag color changed from " + co.getFlagColor() + " to " + overallColor + " for "
					+ co.getOperatorAccount().getName());
			note.setCanContractorView(true);
			note.setViewableById(co.getOperatorAccount().getId());
			dao.save(note);
			co.setFlagColor(overallColor);
			co.setFlagLastUpdated(new Date());
		}

		if (co.isForcedFlag()) { // operator has a forced flag
			co.setFlagColor(co.getForceFlag());
			co.setFlagLastUpdated(new Date());
		}

		List<FlagData> flagData = flagDataDAO.findByContractorAndOperator(co.getContractorAccount().getId(), co
				.getOperatorAccount().getId());

		BaseTable.insertUpdateDeleteExplicit(flagData, changes, flagDataDAO);

		co.setAuditColumns(new User(User.SYSTEM));
		contractorOperatorDAO.save(co);
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

	private void runPolicies(ContractorAccount contractor) {
		if (!runStep(ContractorCronStep.Policies))
			return;

		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			OperatorAccount inheritInsuranceCriteria = co.getOperatorAccount().getInheritInsuranceCriteria();
			flagDataCalculator.setOperatorCriteria(inheritInsuranceCriteria.getFlagCriteria());
			for (ContractorAudit audit : co.getContractorAccount().getAudits()) {
				if (audit.getAuditType().getClassType().isPolicy() && !audit.getAuditStatus().isExpired()) {
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.isVisible()) {
							if (cao.getOperator().equals(co.getOperatorAccount().getInheritInsurance())
									&& (cao.getStatus().isSubmitted() || cao.getStatus().isVerified())) {
								FlagColor flagColor = flagDataCalculator.calculateCaoStatus(audit.getAuditType());

								cao.setFlag(flagColor);
								dao.save(cao);
							}
						}
					}
				}
			}
		}
	}

	private void runCorporateRollup(ContractorAccount contractor, Set<OperatorAccount> corporateSet) {
		if (!runStep(ContractorCronStep.CorporateRollup))
			return;

		// rolls up every flag color to root
		Map<OperatorAccount, FlagColor> rollupData = new HashMap<OperatorAccount, FlagColor>();
		Map<OperatorAccount, ContractorOperator> corporateCOs = new HashMap<OperatorAccount, ContractorOperator>();

		// rolling up data to map
		Iterator<ContractorOperator> coIter = contractor.getOperators().iterator();
		while (coIter.hasNext()) {
			ContractorOperator coOperator = coIter.next();
			OperatorAccount operator = coOperator.getOperatorAccount();

			if (operator.isCorporate()) {
				if (!corporateSet.contains(operator)) {
					// if we have a corporate account in my operator list that
					// is not in the corporate rollup data map
					// it shouldn't be in my operator list and should be deleted
					coIter.remove();
				} else {
					// found a corporate CO
					corporateCOs.put(operator, coOperator);
					rollupData.put(operator, coOperator.getFlagColor());
				}
			} else { // just rollup data, will be checked later
				for (Facility facility : operator.getCorporateFacilities()) {
					OperatorAccount parent = facility.getCorporate();
					FlagColor worstColor = FlagColor.getWorseColor(coOperator.getFlagColor(), rollupData.get(parent));
					rollupData.put(parent, worstColor);
				}
			}
		}

		// ensuring that data is updated from hub to primaryCorporate in rollup
		for (OperatorAccount corporate : corporateCOs.keySet()) {
			// rollup data should contain all entries for corporate, including
			// those not in corporateSet
			OperatorAccount parent = corporate.getParent();
			if (parent != null) { // we have a corporate parent
				FlagColor worstColor = FlagColor.getWorseColor(rollupData.get(corporate), rollupData.get(parent));
				rollupData.put(parent, worstColor);
			}
		}

		// rollup finished, updating corporate values
		for (OperatorAccount corporate : corporateCOs.keySet())
			corporateCOs.get(corporate).setFlagColor(rollupData.get(corporate));

		// removing corporateSet entries based on contractor CO data
		for (OperatorAccount corporate : corporateCOs.keySet())
			corporateSet.remove(corporate);

		// whatever left in corporateSet needs to be inserted
		for (OperatorAccount corporate : corporateSet) {
			ContractorOperator newCo = new ContractorOperator();
			newCo.setCreationDate(new Date());
			newCo.setUpdateDate(new Date());
			newCo.setFlagColor(rollupData.get(corporate));
			newCo.setOperatorAccount(corporate);
			newCo.setContractorAccount(contractor);
			newCo.setCreatedBy(new User(User.SYSTEM));
			newCo.setUpdatedBy(new User(User.SYSTEM));
			contractorOperatorDAO.save(newCo);
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
			if (candidate.equals(step) || candidate.equals(ContractorCronStep.All))
				return true;
		return false;
	}
}
