package com.picsauditing.PICS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.cron.CronMetricsAggregator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.util.AnswerMapByAudits;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

/**
 * Business Engine used to calculate the flag color for a contractor at a given
 * facility
 * 
 * @author Trevor
 * 
 */
public class FlagCalculator2 {

	// protected EntityManager em = null;

	private OperatorAccountDAO operatorDAO;
	private ContractorAccountDAO contractorDAO;
	private ContractorAuditOperatorDAO caoDAO;
	private AuditDataDAO auditDataDAO;
	private ContractorOperatorFlagDAO coFlagDAO;
	private NoteDAO noteDAO;
	private EmailSubscriptionDAO subscriptionDAO;
	private AuditPercentCalculator auditPercentCalculator;

	private AuditBuilder auditBuilder;
	private CronMetricsAggregator cronMetrics;

	// List of operators to be processed
	private List<OperatorAccount> operators = new ArrayList<OperatorAccount>();

	// List of contractors to be processed
	private List<Integer> contractorIDs = new ArrayList<Integer>();

	public FlagCalculator2(OperatorAccountDAO operatorDAO, ContractorAccountDAO contractorDAO,
			AuditDataDAO auditDataDAO, ContractorOperatorFlagDAO coFlagDAO,
			ContractorAuditOperatorDAO caoDAO, AuditBuilder auditBuilder, NoteDAO noteDAO,
			EmailSubscriptionDAO subscriptionDAO, AuditPercentCalculator auditPercentCalculator) {
		this.operatorDAO = operatorDAO;
		this.contractorDAO = contractorDAO;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDAO = coFlagDAO;
		this.caoDAO = caoDAO;
		this.auditBuilder = auditBuilder;
		this.noteDAO = noteDAO;
		this.subscriptionDAO = subscriptionDAO;
		this.auditPercentCalculator = auditPercentCalculator;
	}

	public void runAll() {
		execute();
	}

	public void runByOperatorLimited(int opID) {
		OperatorAccount operator = operatorDAO.find(opID);
		operators.add(operator);

		contractorIDs = contractorDAO.findIdsByOperator(operator);
		execute();
	}

	public void runByOperator(int opID) {
		OperatorAccount operator = operatorDAO.find(opID);
		operators.add(operator);
		execute();
	}

	public void runByContractor(int conID) {
		contractorIDs.add(conID);
		execute();
	}

	public void runByContractors(List<Integer> cons) {
		contractorIDs.addAll(cons);
		execute();
	}

	public void run(int[] cons, int[] ops) {
		if (ops != null)
			for (Integer opID : ops) {
				OperatorAccount operator = operatorDAO.find(opID);
				operators.add(operator);
			}
		for (Integer conID : cons)
			contractorIDs.add(conID);

		execute();
	}

	public void runOne(int conID, int opID) {
		int[] cons = { conID };
		int[] ops = { opID };
		run(cons, ops);
	}

	@Transactional
	private void execute() {
		PicsLogger.log("FlagCalculator.execute()");
		// Load ALL operators and contractors by default
		if (operators.size() == 0)
			operators = operatorDAO.findWhere(false, "type='Operator' and status IN ('Active','Demo','Pending')");
		if (contractorIDs.size() == 0) {
			contractorIDs = contractorDAO.findAll();
		}

		// Create a list of questions that the operators want to ask
		List<Integer> questionIDs = new ArrayList<Integer>();

		PicsLogger.start("OperatorCache", "...getting data cache for operators");
		for (OperatorAccount operator : operators) {
			PicsLogger.log("----------- " + operator.getName());

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

			questionIDs.addAll(operator.getQuestionIDs());
		}
		PicsLogger.stop();

		int errorCount = 0;

		for (Integer conID : contractorIDs) {

			try {
				long conStart = System.currentTimeMillis();
				PicsLogger.start("Flag.calculate", "for : " + conID);
				runCalc(questionIDs, conID);

				if (cronMetrics != null) {
					cronMetrics.addContractor(conID, System.currentTimeMillis() - conStart);
				}

			} catch (Throwable t) {
				t.printStackTrace();
				StringBuffer body = new StringBuffer();

				body.append("There was an error calculating flags for contractor ");
				body.append(conID.toString());
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

				if (++errorCount == 10) {
					break;
				}
			} finally {
				PicsLogger.stop();
			}
		}
	}

	protected void runCalc(List<Integer> questionIDs, Integer conID) {
		// long startTime = System.currentTimeMillis();

		ContractorAccount contractor = contractorDAO.find(conID);

		
		/////////////////////////////////////////////
		//                  BILLING                //
		/////////////////////////////////////////////
		InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
		contractor.setNewMembershipLevel(fee);
		contractor.syncBalance();

		
		/////////////////////////////////////////////
		//             RECALC AUDIT CATEGORY %     //
		/////////////////////////////////////////////
		for (ContractorAudit cAudit : contractor.getAudits()) {
			final Date lastRecalculation = cAudit.getLastRecalculation();
			if (lastRecalculation == null || DateBean.getDateDifference(lastRecalculation) < -90) {
				auditPercentCalculator.percentCalculateComplete(cAudit, true);
				cAudit.setLastRecalculation(new Date());
				cAudit.setAuditColumns();
			}
		}

		
		/////////////////////////////////////////////
		//                AuditBuilder             //
		/////////////////////////////////////////////
		auditBuilder.buildAudits(contractor);

		
		/////////////////////////////////////////////
		//                FlagCalculator           //
		/////////////////////////////////////////////
		// debug("FlagCalculator: Operator data ready...starting calculations");
		FlagCalculatorSingle calcSingle = new FlagCalculatorSingle();

		// List<ContractorAudit> nonExpiredByContractor =
		// conAuditDAO.findNonExpiredByContractor(contractor.getId());

		calcSingle.setContractor(contractor);

		calcSingle.setConAudits(contractor.getAudits());
		calcSingle.setHasOqEmployees(contractor.isOqEmployees(auditDataDAO));
		calcSingle.setHasCOR(contractor.isCOR(auditDataDAO));

		AnswerMapByAudits answerMapByAudits = auditDataDAO.findAnswersByAudits(contractor.getAudits(), questionIDs);
		List<ContractorOperator> conOperators = contractor.getOperators();

		
		// Calculating the 3 Year Avg for EMR and OSHA
		OshaAudit oshaAvg = contractor.getOshas().get(OshaType.OSHA).get(OshaAudit.AVG);
		AuditData emrAvg = contractor.getEmrs().get(OshaAudit.AVG);
		if(emrAvg != null && !Strings.isEmpty(emrAvg.getAnswer()))
			contractor.setEmrAverage(Float.valueOf(emrAvg.getAnswer()).floatValue());
		if (oshaAvg != null) {
			contractor.setTrirAverage(oshaAvg.getRecordableTotalRate());
			contractor.setLwcrAverage(oshaAvg.getLostWorkCasesRate());
		}
		
		// Updating the Trades Column on the Contractor
		List<AuditData> servicesPerformed = auditDataDAO.findServicesPerformed(contractor.getId());
		List<String> selfPerform = new ArrayList<String>();
		List<String> subContract = new ArrayList<String>();
		for(AuditData auditData : servicesPerformed) {
			if(auditData.getAnswer().startsWith("C"))
				selfPerform.add(auditData.getQuestion().getQuestion());
			if(auditData.getAnswer().endsWith("S"))
				subContract.add(auditData.getQuestion().getQuestion());
		}
		contractor.setTradesSelf(Strings.implode(selfPerform,";"));
		contractor.setTradesSub(Strings.implode(subContract,";"));
		
		
		// //since the @Transactional annotation is on this method (and it seems
		// for good reason), and not on the class
		// //level, the runCalc method actually runs from within a different
		// transaction than the transaction in which
		// //the operator was initially loaded, which means we can't traverse
		// it's graph.
		// //Reloading the operator on the first line here will give us a
		// connected operator in scope.
		// for (OperatorAccount opFromDifferentTransaction : operators) {
		//			
		// OperatorAccount operator =
		// operatorDAO.find(opFromDifferentTransaction.getId());
		for (OperatorAccount operator : operators) {
			PicsLogger.log(" Starting FlagCalculator2 for conID: " + conID + " opID:" + operator.getId());

			// prune our answermapMAP for this operator (take out audits they
			// can't see, and answers to questions they shouldn't see)
			// also note that this uses the copy constructor, so our local
			// variable answerMapByAUdits is not affected by pruning
			// on each run through the "operators" list.
			AnswerMapByAudits answerMapForOperator = new AnswerMapByAudits(answerMapByAudits, operator);
			PicsLogger.log(" Found " + answerMapForOperator.getAuditSet().size() + " audits in answerMapForOperator");
			AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(answerMapForOperator, operator
					.getFlagQuestionCriteriaInherited());
			calcSingle.setAcaList(acaBuilder.getAuditCriteriaAnswers());

			calcSingle.setOperator(operator);
			// Calculate the color of the flag right here
			FlagColor color = calcSingle.calculate();
			debug(" - FlagColor returned: " + color);

			// Set the flag color on the object
			// em.refresh(contractor);
			ContractorOperatorFlag coFlag = null;

			WaitingOn waitingOn = calcSingle.calculateWaitingOn();

			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().getClassType().isPolicy()) {
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.isVisible()) {
							if (cao.getOperator().equals(operator.getInheritInsurance())
									&& (cao.getStatus().isSubmitted() || cao.getStatus().isVerified())) {
								FlagColor flagColor = calcSingle.calculateCaoRecommendedFlag(cao);

								cao.setFlag(flagColor);
								caoDAO.save(cao);
							}
						}
					}
				}
			}

			try {
				coFlag = contractor.getFlags().get(operator);
			} catch (Exception e) {
				System.out.println(e);
			}

			if (coFlag == null) {
				for (ContractorOperatorFlag cof : contractor.getFlags().values()) {
					try {
						if (operator.getIdString().equals(cof.getOperatorAccount().getIdString())) {
							coFlag = cof;
						}
					} catch (Exception e) {
						if (!(e instanceof EntityNotFoundException)) {
							System.out.println(e);
						}

					}
				}
			}

			if (coFlag == null) {
				// For New Contractors
				coFlag = new ContractorOperatorFlag();
				coFlag.setFlagColor(color);
				coFlag.setWaitingOn(waitingOn);
				coFlag.setContractorAccount(contractor);
				coFlag.setOperatorAccount(operator);
				coFlag.setLastUpdate(new Date());

				coFlagDAO.save(coFlag);
				contractor.getFlags().put(operator, coFlag);
			} else {
				// For Existing Contractors
				boolean isLinked = false;
				ContractorOperator co = null;
				for (ContractorOperator cOperator : conOperators) {
					if (cOperator.getOperatorAccount().equals(operator)) {
						isLinked = true;
						co = cOperator;
					}
				}
				if (color == null || !color.equals(coFlag.getFlagColor())) {
					if (isLinked) {
						try {
							Note note = new Note();
							note.setAccount(contractor);
							note.setNoteCategory(NoteCategory.Flags);
							note.setAuditColumns(new User(User.SYSTEM));
							note.setSummary("Flag color changed from " + coFlag.getFlagColor() + " to " + color
									+ " for " + operator.getName());
							note.setCanContractorView(true);
							note.setViewableById(operator.getId());
							noteDAO.save(note);
						} catch (Exception e) {
							System.out.println("ERROR: failed to save note because - " + e.getMessage());
						}
					}
					coFlag.setFlagColor(color);
					coFlag.setLastUpdate(new Date());
				}
				if (waitingOn == null || !waitingOn.equals(coFlag.getWaitingOn())) {
					if (isLinked) {
						try {
							Note note = new Note();
							note.setAccount(contractor);
							note.setNoteCategory(NoteCategory.Other);
							note.setPriority(LowMedHigh.Low);
							note.setAuditColumns(new User(User.SYSTEM));
							if (waitingOn.isNone()) {
								note.setSummary("We are no longer \"Waiting On\" " + coFlag.getWaitingOn() + 
									". All required information has been gathered for " + operator.getName() + ".");
							} else if (coFlag.getWaitingOn().isNone()) {
								note.setSummary("The \"Waiting On\" status for " + operator.getName() +
										" has changed to \"" + waitingOn + "\"");
							} else {
								note.setSummary("The \"Waiting On\" status for " + operator.getName() +
										" has changed from \"" + coFlag.getWaitingOn() + "\" to \"" + waitingOn + "\"");
							}
							if(co.getProcessCompletion() != null) {
								note.setBody("The contractor first completed the PICS process on " + co.getProcessCompletion().toString());
							}
							note.setCanContractorView(true);
							note.setViewableById(operator.getId());
							noteDAO.save(note);
						} catch (Exception e) {
							System.out.println("ERROR: failed to save note because - " + e.getMessage());
						}

						try {
							if (co.getProcessCompletion() == null 
									&& waitingOn.equals(WaitingOn.None) 
									&& !coFlag.getWaitingOn().equals(WaitingOn.None)) {
								EventSubscriptionBuilder.contractorFinishedEvent(subscriptionDAO, co);
								co.setProcessCompletion(new Date());
							}
						} catch (Exception e) {
							System.out.println("ERROR: failed to send subscription email - " + e.getMessage());
						}
					}
					coFlag.setWaitingOn(waitingOn);
					coFlag.setLastUpdate(new Date());
					
					// End of Waiting On
				}
				
				// End of Existing Contractors
			}
			
			// End of this Operator
		}

		contractor.setNeedsRecalculation(false);
		contractor.setLastRecalculation(new Date());

		contractorDAO.save(contractor);
	}

	protected void debug(String message) {
		PicsLogger.log(message);
	}

	public CronMetricsAggregator getCronMetrics() {
		return cronMetrics;
	}

	public void setCronMetrics(CronMetricsAggregator cronMetrics) {
		this.cronMetrics = cronMetrics;
	}
}
