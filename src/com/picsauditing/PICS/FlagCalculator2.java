package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;

/**
 * Business Engine used to calculate the flag 
 * color for a contractor at a given facility
 * 
 * @author Trevor
 *
 */
public class FlagCalculator2 {
	private boolean debug = false;
	
	private OperatorAccountDAO operatorDAO;
	private ContractorAccountDAO contractorDAO;
	private ContractorAuditDAO conAuditDAO;
	private AuditDataDAO auditDataDAO;
	private ContractorOperatorFlagDAO coFlagDAO;
	
	private List<OperatorAccount> operators = new ArrayList<OperatorAccount>(); // List of operators to be processed
	private List<Integer> contractorIDs = new ArrayList<Integer>(); // List of contractors to be processed
	
	public FlagCalculator2(OperatorAccountDAO operatorDAO, ContractorAccountDAO contractorDAO, 
		ContractorAuditDAO conAuditDAO, AuditDataDAO auditDataDAO, ContractorOperatorFlagDAO coFlagDAO) {
		this.operatorDAO = operatorDAO;
		this.contractorDAO = contractorDAO;
		this.conAuditDAO = conAuditDAO;
		this.auditDataDAO = auditDataDAO;
		this.coFlagDAO = coFlagDAO;
	}
	
	public void runAll() {
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
			operators = operatorDAO.findWhere("type='Operator'");
		if (contractorIDs.size() == 0) {
			List<ContractorAccount> contractors = contractorDAO.findWhere("");
			for(ContractorAccount contractor : contractors)
				contractorIDs.add(contractor.getId());
			// Clear the memory
			contractors.clear();
			contractors = null;
		}
		debug("...getting question for operators");
		
		List<Integer> questionIDs = new ArrayList<Integer>();
		// Create a list of questions that the operators want to ask
		for(OperatorAccount operator : operators) {
			// Read the operator data from database
			operator.getFlagOshaCriteria();
			operator.getAudits();
			questionIDs.addAll(operator.getQuestionIDs());
		}
		
		debug("FlagCalculator: Operator data ready...starting calculations");
		FlagCalculatorSingle calcSingle = new FlagCalculatorSingle();
		calcSingle.setDebug(debug);
		
		for(Integer conID : contractorIDs) {
			ContractorAccount contractor = contractorDAO.find(conID);
			
			calcSingle.setContractor(contractor);
			calcSingle.setConAudits(conAuditDAO.findNonExpiredByContractor(contractor.getId()));
			calcSingle.setAuditAnswers(auditDataDAO.findAnswersByContractor(contractor.getId(), questionIDs));
			
			for(OperatorAccount operator : operators) {
				calcSingle.setOperator(operator);
				
				// Calculate the color of the flag right here
				
				FlagColor color = calcSingle.calculate();
				debug(" - FlagColor returned: " + color);
				// Set the flag color on the object
				ContractorOperatorFlag coFlag = contractor.getFlags().get(operator);
				if (coFlag == null) {
					// Add a new flag
					coFlag = new ContractorOperatorFlag();
					coFlag.setFlagColor(color);
					coFlag.setContractorAccount(contractor);
					coFlag.setOperatorAccount(operator);
					coFlagDAO.save(coFlag);
					contractor.getFlags().put(operator, coFlag);
				} else {
					if (!color.equals(coFlag.getFlagColor())) {
						coFlag.setFlagColor(color);
						coFlag.setLastUpdate(new Date());
					}
				}
			}
			// Save the changes to the contractor
			contractorDAO.save(contractor);
		}
	}
	
	protected void debug(String message) {
		if (!debug)
			return;
		Date now = new Date();
		System.out.println(now.toString() + message);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
