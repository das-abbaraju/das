package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
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

	private ContractorAccountDAO contractorDAO;
	private ContractorAuditOperatorDAO caoDAO;
	private AuditDataDAO auditDataDAO;
	private NoteDAO noteDAO;
	private EmailSubscriptionDAO subscriptionDAO;
	private AuditPercentCalculator auditPercentCalculator;
	private AuditBuilder auditBuilder;
	private ContractorFlagETL contractorFlagETL;

	private int conID = 0;

	public ContractorCron(ContractorAccountDAO contractorAccountDAO) {
		this.contractorDAO = contractorAccountDAO;
	}

	public String execute() throws Exception {
		PicsLogger.start("ContractorCron");

		if (conID > 0) {
			run(conID);
		} else {
			List<Integer> list = contractorDAO.findContractorsNeedingRecalculation();

			if (list != null && list.size() > 0) {
				for (Integer conID : list) {
					run(conID);
				}
			}
			addActionMessage("ContractorCron processed " + list.size() + " record(s)");
		}

		PicsLogger.stop();
		return SUCCESS;
	}

	@Transactional
	private void run(int conID) {
		try {
			ContractorAccount contractor = contractorDAO.find(conID);
			runBilling(contractor);
			runAuditCategory(contractor);
			runAuditBuilder(contractor);
			runTradeETL(contractor);
			runContractorETL(contractor);

			Set<OperatorAccount> corporateSet = new HashSet<OperatorAccount>();
			for (ContractorOperator co : contractor.getOperators()) {
				OperatorAccount operator = co.getOperatorAccount();
				PicsLogger.log(" Starting FlagCalculator2 for conID: " + conID + " opID:" + operator.getId());
				for (FlagQuestionCriteria criteria : operator.getFlagQuestionCriteriaInherited())
					PicsLogger.log(" flag criteria " + criteria.getFlagColor() + " for "
							+ criteria.getAuditQuestion().getQuestion());

				for (FlagOshaCriteria criteria : operator.getInheritFlagCriteria().getFlagOshaCriteria())
					PicsLogger.log(" osha criteria " + criteria.getFlagColor());

				for (AuditOperator auditOperator : operator.getInheritInsurance().getAudits())
					PicsLogger.log(" has audits " + auditOperator.getAuditType().getAuditName());

				for (AuditOperator auditOperator : operator.getInheritAudits().getAudits())
					PicsLogger.log(" has audits " + auditOperator.getAuditType().getAuditName());

				for (AuditOperator auditOperator : operator.getVisibleAudits())
					PicsLogger.log(" can see audit " + auditOperator.getAuditType().getAuditName());

				for (Facility facility : operator.getCorporateFacilities()) {
					corporateSet.add(facility.getCorporate());
				}
				runFlag(co);
				runWaitingOn(co);

				contractorDAO.save(contractor);
			}
			addActionMessage("Completed contractor " + conID + " successfully");
		} catch (Throwable t) {
			addActionError("Error occurred on contractor " + conID + "<br>" + t.getMessage());

			StringBuffer body = new StringBuffer();

			body.append("There was an error running ContractorCron for id=");
			body.append(conID);
			body.append("\n\n");

			body.append(t.getMessage());
			body.append("\n");

			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			body.append(sw.toString());

			try {
				EmailQueue email = new EmailQueue();
				email.setToAddresses("errors@picsauditing.com");
				email.setPriority(30);
				email.setSubject("Flag calculation error for conID = " + conID);
				email.setBody(body.toString());
				email.setContractorAccount(new ContractorAccount(conID));
				email.setCreationDate(new Date());
				EmailSender.send(email);
			} catch (Exception notMuchWeCanDoButLogIt) {
				System.out.println("**********************************");
				System.out.println("Error calculating flags AND unable to send email");
				System.out.println("**********************************");

				System.out.println(notMuchWeCanDoButLogIt);
				notMuchWeCanDoButLogIt.printStackTrace();
			}
		}
	}

	private void runBilling(ContractorAccount contractor) {
		InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		contractor.setNewMembershipLevel(fee);
		contractor.syncBalance();
	}

	private void runAuditCategory(ContractorAccount contractor) {
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
		auditBuilder.buildAudits(contractor);
	}

	private void runTradeETL(ContractorAccount contractor) {
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
		contractorFlagETL.setContractor(contractor);
		List<FlagCriteriaContractor> contractorCriteria = contractorFlagETL.calculate();
	}

	private void runFlag(ContractorOperator co) {
		FlagDataCalculator flagDataCalculator = new FlagDataCalculator(contractorCriteria, co.getOperatorAccount().getCriteria());
		final List<FlagData> flagResults = flagDataCalculator.calculate();
		// TODO Save flagResults into table
		FlagColor color = FlagColor.Green;
		// TODO find overall flag color for this operator

		if (!color.equals(co.getFlagColor())) {
			Note note = new Note();
			note.setAccount(co.getContractorAccount());
			note.setNoteCategory(NoteCategory.Flags);
			note.setAuditColumns(new User(User.SYSTEM));
			note.setSummary("Flag color changed from " + co.getFlagColor() + " to " + color + " for "
					+ co.getOperatorAccount().getName());
			note.setCanContractorView(true);
			note.setViewableById(co.getOperatorAccount().getId());
			noteDAO.save(note);
			co.setFlagColor(color);
			co.setFlagLastUpdate(new Date());
		}
	}

	private void runWaitingOn(ContractorOperator co) {
		WaitingOn waitingOn = calcSingle.calculateWaitingOn();

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
			noteDAO.save(note);

			if (co.getProcessCompletion() == null && waitingOn.isNone() && !co.getWaitingOn().isNone()) {
				EventSubscriptionBuilder.contractorFinishedEvent(subscriptionDAO, co);
				co.setProcessCompletion(new Date());
			}
			co.setWaitingOn(waitingOn);
		}
	}

	private void runPolicies(ContractorOperator co) {
		for (ContractorAudit audit : co.getContractorAccount().getAudits()) {
			if (audit.getAuditType().getClassType().isPolicy()) {
				for (ContractorAuditOperator cao : audit.getOperators()) {
					if (cao.isVisible()) {
						if (cao.getOperator().equals(co.getOperatorAccount().getInheritInsurance())
								&& (cao.getStatus().isSubmitted() || cao.getStatus().isVerified())) {
							FlagColor flagColor = calcSingle.calculateCaoRecommendedFlag(cao);

							cao.setFlag(flagColor);
							caoDAO.save(cao);
						}
					}
				}
			}
		}
	}

	private void runCorporateRollup(ContractorAccount contractor) {
		for (OperatorAccount corporate : corporateSet) {
			FlagColor color = FlagColor.Green;
			for (Facility corpOperator : corporate.getOperatorFacilities()) {
				for (ContractorOperator conOperator : contractor.getOperators()) {
					if (corpOperator.getOperator().equals(conOperator.getOperatorAccount())) {
						// TODO finish
					}
				}
			}
			// TODO finish
		}

		contractor.setNeedsRecalculation(false);
		contractor.setLastRecalculation(new Date());
	}

	public void setConID(int conID) {
		this.conID = conID;
	}
}
