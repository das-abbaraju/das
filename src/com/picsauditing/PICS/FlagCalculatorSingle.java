package com.picsauditing.PICS;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagOshaCriterion;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaLogYear;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;

/**
 * Determine the Flag color for a single contractor at a given facility. This
 * doesn't persist any data nor does it query the database for the required
 * data.
 * 
 * @author Trevor
 */
public class FlagCalculatorSingle {
	private boolean debug = false;
	private boolean answerOnly = true;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private List<ContractorAudit> conAudits;
	private Map<Integer, AuditData> auditAnswers;

	/**
	 * 1) Check to see all required audits are there 2) OSHA Data 3)
	 * AuditQuestions
	 * 
	 * @param contractor
	 *            The contractor jpa entity to calculate the flag for
	 * @param operator
	 *            The operator jpa entity to calculate the contractor flag for
	 * @param conAudits
	 *            A list of audits for this contractor that may be required by
	 *            this operator
	 * @param auditAnswers
	 * @return
	 */
	public FlagColor calculate() {
		debug("FlagCalculator.calculate(" + contractor.getId() + "," + operator.getId() + ")");
		debug(" answerOnly=" + answerOnly);
		
		// Start positive and see if there are any violations
		FlagColor flagColor = FlagColor.Green;
		FlagColor overrideColor = null;

		// First see if there are any forced flags for this operator
		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().equals(operator)) {
				// Found the operator, is it forced?
				debug(" isForcedFlag=" + co.isForcedFlag() + " " + co.getForceFlag());
				if (co.isForcedFlag())
					overrideColor = co.getForceFlag();
			}
		}
		if (answerOnly && overrideColor != null)
			// Things can't get worse, just exit
			return overrideColor;
		
		debug(" flagColor=" + flagColor);

		for (AuditOperator audit : operator.getAudits()) {
			// Always start with Green
			audit.setContractorFlag(FlagColor.Green);
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() 
					&& audit.getRequiredForFlag() != null) {
				debug(" -- " + audit.getAuditType().getAuditName() + " - " + audit.getRequiredForFlag().toString());
				// The contractor requires this audit, make sure they have an
				// active one
				audit.setContractorFlag(audit.getRequiredForFlag());
				for (ContractorAudit conAudit : conAudits) {
					if (conAudit.getAuditStatus().equals(AuditStatus.Active)
							|| conAudit.getAuditStatus().equals(AuditStatus.Exempt)) {
						if (conAudit.getAuditType().equals(audit.getAuditType())
								|| (conAudit.getAuditType().getAuditTypeID() == AuditType.NCMS && audit.getAuditType()
										.getAuditTypeID() == AuditType.DESKTOP)) {
							// We found a matching active audit for this
							// contractor
							debug(" ---- found");
							audit.setContractorFlag(FlagColor.Green);
						}
					}
				}
				// If an active audit doesn't exist, then set
				// the contractor's flag to the required color
				flagColor = setFlagColor(flagColor, audit.getContractorFlag());
			}
			if (answerOnly && flagColor.equals(FlagColor.Red))
				// Things can't get worse, just exit
				return flagColor;
		}

		debug(" flagColor=" + flagColor);

		for (OshaLog osha : contractor.getOshas()) {
			// The flag colors should always start Green, but sometimes they are still set from the previous operator's loop
			osha.setFlagColor(FlagColor.Green);
			osha.getYear1().setFlagColor(FlagColor.Green);
			osha.getYear2().setFlagColor(FlagColor.Green);
			osha.getYear3().setFlagColor(FlagColor.Green);
		}

		for (FlagOshaCriteria criteria : operator.getFlagOshaCriteria()) {
			if (criteria.isRequired()) {
				debug(" -- osha ");
				boolean found = false;
				for (OshaLog osha : contractor.getOshas()) {
					if (osha.isCorporate()) {
						found = true;
						// Calculate the flag color for Average calculations (if any)
						FlagColor oshaAvgFlag = FlagColor.Green;
						if (criteria.getLwcr().isTimeAverage()) {
							if (criteria.getLwcr().isFlagged(osha.getAverageLwcr()))
								oshaAvgFlag = setFlagColor(oshaAvgFlag, criteria.getFlagColor());
						}
						if (criteria.getTrir().isTimeAverage()) {
							if (criteria.getTrir().isFlagged(osha.getAverageTrir()))
								oshaAvgFlag = setFlagColor(oshaAvgFlag, criteria.getFlagColor());
						}
						if (criteria.getFatalities().isTimeAverage()) {
							if (criteria.getFatalities().isFlagged(osha.getAverageFatalities()))
								oshaAvgFlag = setFlagColor(oshaAvgFlag, criteria.getFlagColor());
						}
						
						// Set the flag color for the average years
						// and then add it to the "rolling total"
						oshaAvgFlag = setFlagColor(osha.getFlagColor(), oshaAvgFlag);
						osha.setFlagColor(oshaAvgFlag);
						
						flagColor = setFlagColor(flagColor, oshaAvgFlag);

						// Make sure all three years pass the criteria
						flagColor = setFlagColor(flagColor, verifyOsha(osha.getYear1(), criteria));
						flagColor = setFlagColor(flagColor, verifyOsha(osha.getYear2(), criteria));
						flagColor = setFlagColor(flagColor, verifyOsha(osha.getYear3(), criteria));

					}
				}
				if (!found)
					// We didn't find the required Corporate Osha record, so
					// flag this contractor
					// We may actually not want to do this if for some reason a
					// contractor
					// is allowed to pass their
					flagColor = setFlagColor(flagColor, criteria.getFlagColor());

				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			}
		}
		debug(" flagColor=" + flagColor);

		for (Integer dataID : auditAnswers.keySet()) {
			// The flag colors should always start Green, but sometimes they are still set from the previous operator's loop
			auditAnswers.get(dataID).setFlagColor(FlagColor.Green);
		}
		
		// For each operator criteria, get the contractor's
		// answer and see if it triggers the flag color
		for (FlagQuestionCriteria criteria : operator.getFlagQuestionCriteria()) {
			if (criteria.getChecked().equals(YesNo.Yes)) {
				// This question is required by the operator
				
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
						// The contractor has answered this question so it needs
						// to be correct
						Float avgRate = emrRateTotal / years;
						
						AuditData data = auditAnswers.get(AuditQuestion.EMR_AVG);
						if (data == null) {
							// Add a temporary auditdata record so we can display them on the flag page
							data = new AuditData();
							data.setQuestion(criteria.getAuditQuestion());
							int roundedRate1000 = Math.round(avgRate*1000);
							float roundedRate = ((float)roundedRate1000)/1000;
							data.setAnswer(Float.toString(roundedRate));
							data.setFlagColor(FlagColor.Green);
							auditAnswers.put(AuditQuestion.EMR_AVG, data);
						}
						
						if (criteria.isFlagged(avgRate.toString())) {
							data.setFlagColor(setFlagColor(data.getFlagColor(), criteria.getFlagColor()));
							flagColor = setFlagColor(flagColor, criteria.getFlagColor());
						}
					}
				} else {
					// Check all other audit data answers
					AuditData data = auditAnswers.get(criteria.getAuditQuestion().getQuestionID());
					if (data != null && data.getVerifiedAnswerOrAnswer() != null
							&& data.getVerifiedAnswerOrAnswer().length() > 0) {
						// The contractor has answered this question so it needs
						// to be correct
						boolean isFlagged = false;

						if (criteria.isValidationRequired() && !data.isVerified()) {
							isFlagged = true;
						}

						if (criteria.isFlagged(data.getVerifiedAnswerOrAnswer())) {
							isFlagged = true;
						}
						
						if (isFlagged) {
							data.setFlagColor(setFlagColor(data.getFlagColor(), criteria.getFlagColor()));
							flagColor = setFlagColor(flagColor, criteria.getFlagColor());
						}
					}
				}
				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			} // if criteria.isChecked...
		} // for
		debug(" flagColor=" + flagColor);

		// Calculate the insurance certificate flags colors
		if (operator.getCanSeeInsurance().equals(YesNo.Yes)) {
			FlagColor certFlagColor = null;
			
			for (Certificate certificate : contractor.getCertificates()) {
				if (certificate.getOperatorAccount().equals(operator)) {
					debug(" -- certificate" + certificate.getType() + " " + certificate.getOperatorAccount().getName());
					certFlagColor = setFlagColor(certFlagColor, certificate.getFlagColor());
					
					if (answerOnly && certFlagColor != null && certFlagColor.equals(FlagColor.Red))
						// Things can't get worse, just exit
						return certFlagColor;
				}
			}
			
			if (certFlagColor == null && !answerOnly) {
				certFlagColor = FlagColor.Red;
				Certificate certificate = new Certificate();
				certificate.setFlagColor(FlagColor.Red);
				certificate.setType("No Approved Certificates");
				certificate.setOperatorAccount(operator);
				certificate.setContractorAccount(contractor);
				certificate.setStatus(""); // This has no status because it's not a real cert
				contractor.getCertificates().add(certificate);
			}

			flagColor = setFlagColor(flagColor, certFlagColor);
			debug(" flagColor=" + flagColor);
		}

		debug(" flagColor=" + flagColor);
		
		if (overrideColor != null)
			return overrideColor;

		return flagColor;
	}
	
	public WaitingOn calculateWaitingOn() {
		debug("FlagCalculator.calculateWaitingOn(" + contractor.getId() + "," + operator.getId() + ")");
		
		ContractorOperator co = null;
		// First see if there are any forced flags for this operator
		for (ContractorOperator co2 : contractor.getOperators()) {
			if (co2.getOperatorAccount().equals(operator)) {
				co = co2;
				break;
			}
		}
		if (co == null)
			return WaitingOn.None; // This contractor is not associated with this operator, so nothing to do now
		
		if (!contractor.isActiveB())
			return WaitingOn.Contractor; // This contractor is delinquent

		// Operator Relationship Approval
		if (YesNo.Yes.equals(operator.getApprovesRelationships())) {
			if (co.getWorkStatus().equals("P"))
				return WaitingOn.Operator; // Operator needs to approve/reject this contractor
			if (co.getWorkStatus().equals("N"))
				return WaitingOn.None; // Operator has already rejected this contractor, and there's nothing else they can do
		}
		
		// Billing
		if (contractor.getAnnualAmountOwed() > 0)
			return WaitingOn.Contractor; // The contractor has an unpaid invoice due
		if (contractor.getUpgradeAmountOwed() > 0)
			return WaitingOn.Contractor; // The contractor has an unpaid invoice due
		
		// If waiting on contractor, immediately exit, otherwise track the other parties 
		boolean waitingOnPics = false;
		boolean waitingOnOperator = false;
		
		// PQF, Desktop & Office Audits
		for (AuditOperator audit : operator.getAudits()) {
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() 
					&& audit.getRequiredForFlag() != null) {
				
				for (ContractorAudit conAudit : conAudits) {
					if (conAudit.getAuditType().equals(audit.getAuditType()) &&
						(conAudit.getAuditStatus().equals(AuditStatus.Pending)
							|| conAudit.getAuditStatus().equals(AuditStatus.Submitted))
						) {
						// We found a matching pending or submitted audit for this contractor
						// Whose fault is it??
						debug(" ---- found");
						if (conAudit.getAuditType().isPqf()) {
							if (conAudit.getAuditStatus().equals(AuditStatus.Pending))
								return WaitingOn.Contractor; // The contractor still needs to submit their PQF
							if (conAudit.getPercentVerified() > 0)
								return WaitingOn.Contractor; // The contractor needs to send us updated information (EMR, OSHA, etc)
							
							// This PQF must be submitted and not verified yet at all, so it's PICS' fault
							waitingOnPics = true;
						}
						if (conAudit.getAuditType().getAuditTypeID() == AuditType.OFFICE)
							return WaitingOn.Contractor; // The contractor either needs to schedule the audit or close out RQs
						if (conAudit.getAuditType().getAuditTypeID() == AuditType.DESKTOP) {
							if (conAudit.getAuditStatus().equals(AuditStatus.Submitted))
								return WaitingOn.Contractor; // The contractor needs to close out RQs
							// This desktop still hasn't been performed by PICS
							waitingOnPics = true;
						}
						
					}
				}
			}
		}
		
		// Certificates
		if (operator.getCanSeeInsurance().equals(YesNo.Yes)) {
			
			int count = 0;
			for (Certificate certificate : contractor.getCertificates()) {
				if (certificate.getOperatorAccount().equals(operator)) {
					debug(" -- certificate" + certificate.getType() + " " + certificate.getOperatorAccount().getName());
					count++;
					if (certificate.getStatus().equals("Rejected"))
						return WaitingOn.Contractor; // The contractor should upload a new cert
					if (certificate.getStatus().equals("Expired"))
						return WaitingOn.Contractor; // The contractor should upload a new cert
					if (certificate.getStatus().equals("Pending"))
						waitingOnPics = true;
				}
			}

			if (count == 0)
				return WaitingOn.Contractor; // The contractor hasn't uploaded any certificates for this operator
		}

		
		// Conclusion
		if (waitingOnPics)
			return WaitingOn.Pics;
		if (waitingOnOperator)
			// only show the operator if contractor and pics are all done
			return WaitingOn.Operator;
		
		// If everything is done, then quit with waiting on = no one
		return WaitingOn.None;
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
		FlagColor oshaYearFlag = osha.getFlagColor();
		if (oshaYearFlag == null)
			oshaYearFlag = FlagColor.Green;

		debug(" ---- criteria.getFlagColor() " + criteria.getFlagColor() + osha.isApplicable());
		if (osha.isApplicable()) {
			boolean flagged = false;
			debug(" ---- LWCR ");
			flagged = isCriteriaFlagged(criteria.getLwcr(), osha.getLostWorkCasesRate(), flagged);
			debug(" ---- TRIR ");
			flagged = isCriteriaFlagged(criteria.getTrir(), osha.getRecordableTotalRate(), flagged);
			debug(" ---- Fatal ");
			flagged = isCriteriaFlagged(criteria.getFatalities(), osha.getFatalities(), flagged);
			
			if (flagged)
				oshaYearFlag = setFlagColor(oshaYearFlag, criteria.getFlagColor());
		}
		osha.setFlagColor(oshaYearFlag);
		debug(" -- oshaYearFlag = " + oshaYearFlag);
		return oshaYearFlag;
	}
	
	private boolean isCriteriaFlagged(FlagOshaCriterion criterion, float rate, boolean flagged) {
		if (!criterion.isTimeAverage()) {
			boolean tempFlagged = criterion.isFlagged(rate);
			if (tempFlagged)
				flagged = true;
			debug(" ----- " + tempFlagged + 
					" criteria:" + criterion.toString() + " value:" + rate);
		}
		return flagged;
	}

	/**
	 * Set the flag color, but only let it get worse Green to Red but not
	 * reverse
	 * 
	 * @param oldColor
	 * @param newColor
	 * @return
	 */
	static private FlagColor setFlagColor(FlagColor oldColor, FlagColor newColor) {
		if (newColor == null)
			// Don't change anything
			return oldColor;
		
		if (oldColor == null) {
			System.out.println("WARNING: oldColor == null");
			// Now we've changed this because of insurance
			//oldColor = FlagColor.Green;
			return newColor;
		}

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

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public List<ContractorAudit> getConAudits() {
		return conAudits;
	}

	public void setConAudits(List<ContractorAudit> conAudits) {
		this.conAudits = conAudits;
	}

	public Map<Integer, AuditData> getAuditAnswers() {
		return auditAnswers;
	}

	public void setAuditAnswers(Map<Integer, AuditData> auditAnswers) {
		this.auditAnswers = auditAnswers;
	}

	public boolean isAnswerOnly() {
		return answerOnly;
	}

	public void setAnswerOnly(boolean answerOnly) {
		this.answerOnly = answerOnly;
	}

}
