package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionOperatorAccount;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaLogYear;
import com.picsauditing.jpa.entities.YesNo;

/**
 * Business Engine used to calculate the flag 
 * color for a contractor at a given facility
 * 
 * @author Trevor
 *
 */
public class FlagCalculator2 {
	boolean debug = false;
	
	OperatorAccountDAO operatorDAO;
	ContractorAccountDAO contractorDAO;
	ContractorAuditDAO conAuditDAO;
	AuditDataDAO auditDataDAO;
	ContractorOperatorFlagDAO coFlagDAO;
	
	List<OperatorAccount> operators = new ArrayList<OperatorAccount>(); // List of operators to be processed
	List<Integer> contractorIDs = new ArrayList<Integer>(); // List of contractors to be processed
	
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
			for(AuditQuestionOperatorAccount question : operator.getAuditQuestions()) {
				questionIDs.add(question.getAuditQuestion().getQuestionID());
			}
			for(FlagQuestionCriteria criteria : operator.getFlagQuestionCriteria()) {
				if (criteria.getChecked().equals(YesNo.Yes))
					questionIDs.add(criteria.getAuditQuestion().getQuestionID());
			}
			// Get the operator data
			for(FlagOshaCriteria criteria : operator.getFlagOshaCriteria()) {
				
			}
			for(AuditOperator audit : operator.getAudits()) {
				
			}
		}
		
		debug("FlagCalculator: Operator data ready...starting calculations");
		
		for(Integer conID : contractorIDs) {
			ContractorAccount contractor = contractorDAO.find(conID);
			
			// Get a list of current audits (actually we want Active audits, 
			// but we'll get them all and the filter them ( I'm not sure why)
			List<ContractorAudit> conAudits = conAuditDAO.findNonExpiredByContractor(contractor.getId());
			Map<Integer, AuditData> auditAnswers = auditDataDAO.findAnswersByContractor(contractor.getId(), questionIDs);

			for(OperatorAccount operator : operators) {
				// Calculate the color of the flag right here
				FlagColor color = calculate(contractor, operator, conAudits, auditAnswers);
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
	
	/**
	 * 1) Check to see all required audits are there
	 * 2) OSHA Data
	 * 3) AuditQuestions
	 * @param contractor
	 * @param operator
	 * @return
	 */
	private FlagColor calculate(ContractorAccount contractor, OperatorAccount operator, List<ContractorAudit> conAudits, Map<Integer, AuditData> auditAnswers) {
		debug("FlagCalculator.calculate("+contractor.getId()+","+operator.getId()+")");
		// First see if there are any forced flags for this operator
		for(ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().getId() == operator.getId())
				// Found the operator, is it forced?
				if (co.isForcedFlag())
					return co.getForceFlag();
		}

		// Start positive and see if there are any violations
		FlagColor flagColor = FlagColor.Green;
		
		for(AuditOperator audit : operator.getAudits()) {
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() ) {
				// The contractor requires this audit, make sure they have an active one
				boolean found = false;
				for(ContractorAudit conAudit : conAudits) {
					if (conAudit.getAuditType().equals(audit.getAuditType())
							&& conAudit.getAuditStatus().equals(AuditStatus.Active)) {
						// We found a matching active audit for this contractor
						found = true;
					}
				}
				// If an active audit doesn't exist, then set
				// the contractor's flag to the required color
				if (!found)
					setFlagColor(flagColor, audit.getRequiredForFlag());
			}
			if (flagColor.equals(FlagColor.Red))
				// Things can't get worse, just exit
				return flagColor;
		}
		
		for(FlagOshaCriteria criteria : operator.getFlagOshaCriteria()) {
			if (criteria.isRequired()) {
				boolean found = false;
				for(OshaLog osha : contractor.getOshas()) {
					if (osha.isCorporate()) {
						found = true;
						if (criteria.getLwcr().isTimeAverage()) {
							if (criteria.getLwcr().isFlagged(osha.getAverageLwcr()))
								flagColor = setFlagColor(flagColor, criteria.getFlagColor());
						}
						if (criteria.getTrir().isTimeAverage()) {
							if (criteria.getTrir().isFlagged(osha.getAverageTrir()))
								flagColor = setFlagColor(flagColor, criteria.getFlagColor());
						}
						if (criteria.getFatalities().isTimeAverage()) {
							if (criteria.getFatalities().isFlagged(osha.getAverageFatalities()))
								flagColor = setFlagColor(flagColor, criteria.getFlagColor());
						}
							
						// Make sure all three year pass the criteria
						flagColor = setFlagColor(flagColor, verifyOsha(osha.getYear1(), criteria));
						flagColor = setFlagColor(flagColor, verifyOsha(osha.getYear2(), criteria));
						flagColor = setFlagColor(flagColor, verifyOsha(osha.getYear3(), criteria));
					}
				}
				if (!found)
					// We didn't find the required Corporate Osha record, so flag this contractor 
					flagColor = setFlagColor(flagColor, criteria.getFlagColor());
				
				if (flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			}
		}
		
		// For each operator criteria, get the contractor's 
		// answer and see if it triggers the flag color
		for(FlagQuestionCriteria criteria : operator.getFlagQuestionCriteria()) {
			if (criteria.getChecked().equals(YesNo.Yes)) {
				// This question is required by the operator
				AuditData data;
				if (criteria.getAuditQuestion().getQuestionID() == AuditQuestion.EMR_AVG) {
					// Check the average of all three EMR years
					float emrRateTotal = 0;
					int years = 0;
					float emrRate;
					emrRate = getEmrRate(auditAnswers.get(AuditQuestion.EMR07));
					if (emrRate > 0) {
						years++;
						emrRateTotal = emrRateTotal + emrRate;
					}
					emrRate = getEmrRate(auditAnswers.get(AuditQuestion.EMR06));
					if (emrRate > 0) {
						years++;
						emrRateTotal = emrRateTotal + emrRate;
					}
					emrRate = getEmrRate(auditAnswers.get(AuditQuestion.EMR05));
					if (emrRate > 0) {
						years++;
						emrRateTotal = emrRateTotal + emrRate;
					}
					
					if (years > 0) {
						// The contractor has answered this question so it needs to be correct
						Float avgRate = emrRateTotal / years;
						if (criteria.isFlagged(avgRate.toString()))
							flagColor = setFlagColor(flagColor, criteria.getFlagColor());
					}
				} else {
					// Check all other audit data answers
					data = auditAnswers.get(criteria.getAuditQuestion().getQuestionID());
					if (data != null)
						// The contractor has answered this question so it needs to be correct
						if (criteria.isFlagged(data.getVerifiedAnswerOrAnswer()))
							flagColor = setFlagColor(flagColor, criteria.getFlagColor());
					
					if (flagColor.equals(FlagColor.Red))
						// Things can't get worse, just exit
						return flagColor;
				}
			}
		}
		return flagColor;
	}
	
	private float getEmrRate(AuditData auditData) {
		float NA = -1;
		if (auditData == null)
			return NA;
		String value = auditData.getVerifiedAnswerOrAnswer();
		if (value == null)
			return NA;
		
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return NA;
		}
	}

	private FlagColor verifyOsha(OshaLogYear osha, FlagOshaCriteria criteria) {
		if (osha.isApplicable()) {
			if (criteria.getLwcr().isFlagged(osha.getLostWorkCasesRate()))
				return criteria.getFlagColor();
			if (criteria.getTrir().isFlagged(osha.getRecordableTotalRate()))
				return criteria.getFlagColor();
			if (criteria.getFatalities().isFlagged(osha.getFatalitiesRate()))
				return criteria.getFlagColor();
		}
		return FlagColor.Green;
	}

	/**
	 * Set the flag color, but only let it get worse
	 * Green to Red but not reverse
	 * @param oldColor
	 * @param newColor
	 * @return
	 */
	static private FlagColor setFlagColor(FlagColor oldColor, FlagColor newColor) {
		if (oldColor == null)
			// Never set it to null, the default is Green
			oldColor = FlagColor.Green;
		
		if (newColor == null)
			// Don't change anything
			return oldColor;

		// If 2-Red is greater (worse) than 0-Green, then change the color 
		if (newColor.ordinal() > oldColor.ordinal())
			oldColor = newColor;
		return oldColor;
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
