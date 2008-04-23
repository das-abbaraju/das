package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;

/**
 * Business Engine used to calculate the flag 
 * color for a contractor at a given facility
 * 
 * @author Trevor
 *
 */
public class FlagCalculator {
	OperatorAccountDAO operatorDAO;
	ContractorAccountDAO contractorDAO;
	ContractorAuditDAO conAuditDAO;
	
	List<OperatorAccount> operators = new ArrayList<OperatorAccount>(); // List of operators to be processed
	List<Integer> contractorIDs = new ArrayList<Integer>(); // List of contractors to be processed
	
	public FlagCalculator(OperatorAccountDAO operatorDAO, ContractorAccountDAO contractorDAO, ContractorAuditDAO conAuditDAO) {
		this.operatorDAO = operatorDAO;
		this.contractorDAO = contractorDAO;
		this.conAuditDAO = conAuditDAO;
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
		
		// Create a list of questions that the operators want to ask
		for(OperatorAccount operator : operators) {
			
		}
		List<Integer> questionIDs = new ArrayList<Integer>(); // List of audit questions to be asked of contractors
		
		for(Integer conID : contractorIDs) {
			ContractorAccount contractor = contractorDAO.find(conID);
			for(OperatorAccount operator : operators) {
				FlagColor color = calculate(contractor, operator);
				// Save the flag color HERE
			}
		}
	}
	
	public FlagColor calculate(ContractorAccount contractor, OperatorAccount operator) {
		FlagColor flagColor = FlagColor.Green;
		
		List<ContractorAudit> conAudits = conAuditDAO.findNonExpiredByContractor(contractor.getId());
		
		for(AuditOperator audit : operator.getAudits()) {
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() ) {
				boolean found = false;
				for(ContractorAudit conAudit : conAudits) {
					if (conAudit.getAuditType().equals(audit.getAuditType())) {
						// We found a matching audit for this contractor
						found = true;
					}
				}
				if (!found)
					setFlagColor(flagColor, audit.getRequiredForFlag());
			}
			if (flagColor.equals(FlagColor.Red))
				return flagColor;
		}
		
		for(FlagOshaCriteria criteria : operator.getFlagOshaCriteria()) {
			criteria.getLwcr();
			criteria.getTrir();
			criteria.getTrir();
		}
		// For each operator criteria, get the contractor's 
		// answer and see if it triggers the flag color
		for(FlagQuestionCriteria criteria : operator.getFlagQuestionCriteria()) {
			AuditData data = contractor.getAuditAnswers().get(criteria.getAuditQuestion());
			if (data != null)
				// The contractor has answered this question so it must be correct
				if (criteria.isFlagged(data.getAnswer()))
					flagColor = setFlagColor(flagColor, criteria.getFlagColor());
		}
		
		operator.getFlagQuestionCriteria();
		
		return flagColor;
	}
	
	/**
	 * Set the flag color, but only let it get worse
	 * Green to Red but not reverse
	 * @param oldColor
	 * @param newColor
	 * @return
	 */
	static private FlagColor setFlagColor(FlagColor oldColor, FlagColor newColor) {
		// Never set it to null, the default is Green
		if (newColor == null)
			newColor = FlagColor.Green;
		if (oldColor == null)
			return newColor;

		if (oldColor.ordinal() > newColor.ordinal())
			return oldColor;
		else
			return newColor;
	}

}
