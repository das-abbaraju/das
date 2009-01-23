package com.picsauditing.PICS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.AnswerMapByAudits;
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
	private ContractorAuditDAO conAuditDAO;
	private ContractorAuditOperatorDAO caoDAO;
	private AuditDataDAO auditDataDAO;
	private ContractorOperatorFlagDAO coFlagDAO;

	// List of operators to be processed
	private List<OperatorAccount> operators = new ArrayList<OperatorAccount>();

	// List of contractors to be processed
	private List<Integer> contractorIDs = new ArrayList<Integer>();

	public FlagCalculator2(OperatorAccountDAO operatorDAO, ContractorAccountDAO contractorDAO,
			ContractorAuditDAO conAuditDAO, AuditDataDAO auditDataDAO, ContractorOperatorFlagDAO coFlagDAO,
			ContractorAuditOperatorDAO caoDAO ) {
		this.operatorDAO = operatorDAO;
		this.contractorDAO = contractorDAO;
		this.conAuditDAO = conAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDAO = coFlagDAO;
		this.caoDAO = caoDAO;
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

	public void runOne(int conID, int opID) {
		OperatorAccount operator = operatorDAO.find(opID);
		operators.add(operator);
		contractorIDs.add(conID);
		execute();
	}

	private void execute() {
		debug("FlagCalculator.execute()");
		// Load ALL operators and contractors by default
		if (operators.size() == 0)
			operators = operatorDAO.findWhere(false, "type='Operator'");
		if (contractorIDs.size() == 0) {
			contractorIDs = contractorDAO.findAll();
		}
		debug("...getting question for operators");

		List<Integer> questionIDs = new ArrayList<Integer>();
		// Create a list of questions that the operators want to ask
		for (OperatorAccount operator : operators) {
			// Read the operator data from database
			operator.getFlagOshaCriteria();
			operator.getAudits();
			questionIDs.addAll(operator.getQuestionIDs());
		}

		int count = 1;
		int testValue = 5;
		float lastAvg = 1000000;
		long startTime = System.currentTimeMillis();

		int errorCount = 0;

		for (Integer conID : contractorIDs) {

			try {
				PicsLogger.start("Flag.calculate", "for : " + conID);
				runCalc(questionIDs, conID);
			} catch (Throwable t) {
				System.out.println("Error: " + t.getMessage());

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

			if ((count++ % testValue) == 0) {
				questionIDs = null;


				operatorDAO.close();
				contractorDAO.close();
				conAuditDAO.close();
				caoDAO.close();
				auditDataDAO.close();
				coFlagDAO.close();

				System.gc();

				questionIDs = new ArrayList<Integer>();

				for (OperatorAccount operator : operators) {
					// Read the operator data from database
					operator.getFlagOshaCriteria();
					operator.getAudits();
					questionIDs.addAll(operator.getQuestionIDs());
				}

				long endTime = System.currentTimeMillis();

				float thisAvg = (endTime - startTime) / testValue;

				System.out.println("one iteration finished:  batch size: " + testValue + " average time : " + thisAvg);

				if (thisAvg > lastAvg) {
					testValue -= 3;

					if (testValue < 5) {
						testValue = 5;
					}
				} else {
					testValue += 5;

					if (testValue > 70) {
						testValue = 20;
					}

				}
				lastAvg = thisAvg;
				count = 1;
				startTime = System.currentTimeMillis();

			}

		}
	}

	@Transactional
	protected void runCalc(List<Integer> questionIDs, Integer conID) {
		// long startTime = System.currentTimeMillis();

		ContractorAccount contractor = contractorDAO.find(conID);

		// debug("FlagCalculator: Operator data ready...starting calculations");
		FlagCalculatorSingle calcSingle = new FlagCalculatorSingle();

		List<ContractorAudit> nonExpiredByContractor = conAuditDAO.findNonExpiredByContractor(contractor.getId());
		
		calcSingle.setContractor(contractor);
		
		calcSingle.setConAudits(nonExpiredByContractor);
		
		AnswerMapByAudits answerMapByAudits = auditDataDAO.findAnswersByAudits( nonExpiredByContractor, questionIDs );
		
		//since the @Transactional annotation is on this method (and it seems for good reason), and not on the class 
		//level, the runCalc method actually runs from within a different transaction than the transaction in which 
		//the operator was initially loaded, which means we can't traverse it's graph.  
		//Reloading the operator on the first line here will give us a connected operator in scope.
		for (OperatorAccount opFromDifferentTransaction : operators) {  
			
			OperatorAccount operator = operatorDAO.find(opFromDifferentTransaction.getId());
			
			//prune our answermapMAP for this operator (take out audits they can't see, and answers to questions they shouldn't see)
			//also note that this uses the copy constructor, so our local variable answerMapByAUdits is not affected by pruning 
			//on each run through the "operators" list.
			AnswerMapByAudits answerMapForOperator = new AnswerMapByAudits(answerMapByAudits, operator);
			AuditCriteriaAnswerBuilder acaBuilder = new AuditCriteriaAnswerBuilder(answerMapForOperator, operator.getFlagQuestionCriteria());
			calcSingle.setAcaList(acaBuilder.getAuditCriteriaAnswers());

			calcSingle.setOperator(operator);
			// Calculate the color of the flag right here
			FlagColor color = calcSingle.calculate();
			debug(" - FlagColor returned: " + color);
			
			// Set the flag color on the object
			// em.refresh(contractor);
			ContractorOperatorFlag coFlag = null;

			WaitingOn waitingOn = calcSingle.calculateWaitingOn();

			for( ContractorAudit audit : contractor.getAudits() ) {
				if( audit.getAuditType().getClassType() == AuditTypeClass.Policy ) {
					for (ContractorAuditOperator cao : audit.getOperators()) {
						if (cao.getStatus() == CaoStatus.Awaiting) {
							CaoStatus recommendedStatus = calcSingle
									.calculateCaoRecommendedStatus(cao);
							cao.setRecommendedStatus(recommendedStatus);
							caoDAO.save(cao);
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
				// Add a new flag
				coFlag = new ContractorOperatorFlag();
				coFlag.setFlagColor(color);
				coFlag.setWaitingOn(waitingOn);
				coFlag.setContractorAccount(contractor);
				coFlag.setOperatorAccount(operator);
				coFlag.setLastUpdate(new Date());

				coFlagDAO.save(coFlag);
				contractor.getFlags().put(operator, coFlag);
			} else {
				if (color == null || !color.equals(coFlag.getFlagColor())) {
					coFlag.setFlagColor(color);
					coFlag.setLastUpdate(new Date());
				}
				if (waitingOn == null || !waitingOn.equals(coFlag.getWaitingOn())) {
					coFlag.setWaitingOn(waitingOn);
					coFlag.setLastUpdate(new Date());
				}
			}
		}
		contractorDAO.save(contractor);

		// destroy with a vengence!!!!
		// for(OperatorAccount operator : contractor.getFlags().keySet()) {
		// ContractorOperatorFlag flag = contractor.getFlags().get(operator);
		// flag = null;
		// }
		// contractor.getFlags().clear();
		// contractor = null;

		// System.out.println( "" + conID + " " + (System.currentTimeMillis() -
		// startTime) );
	}

	protected void debug(String message) {
		PicsLogger.log(message);
	}

}
