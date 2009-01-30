package com.picsauditing.PICS;

import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.log.PicsLogger;

/**
 * Determine the Flag color for a single contractor at a given facility. This
 * doesn't persist any data nor does it query the database for the required
 * data.
 * 
 * @author Trevor
 */
public class FlagCalculatorSingle {
	private boolean answerOnly = true;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private List<ContractorAudit> conAudits;
	protected List<AuditCriteriaAnswer> acaList;

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

		debug(" post override flagColor=" + flagColor);

		for (AuditOperator audit : operator.getAudits()) {
			audit.setContractorFlag(null);
			if (audit.isCanSee()) {
				// Always start with Green
				audit.setContractorFlag(FlagColor.Green);
				// removed contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() (needs further review)
				if ( (audit.getMinRiskLevel() == 0 || contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel())
						&& audit.getRequiredForFlag() != null && !audit.getRequiredForFlag().equals(FlagColor.Green)) {
					debug(" -- " + audit.getAuditType().getAuditName() + " - " + audit.getRequiredForFlag().toString());
					// The contractor requires this audit,
					// make sure they have an active one
					// If an active audit doesn't exist, then set
					// the contractor's flag to the required color
					audit.setContractorFlag(audit.getRequiredForFlag());
					
					int annualAuditCount = 0;
					for (ContractorAudit conAudit : conAudits) {
						if (conAudit.getAuditType().equals(audit.getAuditType())) {
						
							if (conAudit.getAuditType().getClassType().isAudit()) {
								boolean statusOK = false;
								boolean typeOK = false;
								if (conAudit.getAuditStatus() == AuditStatus.Active)
									statusOK = true;
								if (conAudit.getAuditStatus() == AuditStatus.Resubmitted)
									statusOK = true;
								if (conAudit.getAuditStatus() == AuditStatus.Exempt)
									statusOK = true;
								if (conAudit.getAuditStatus() == AuditStatus.Submitted
										&& audit.getRequiredAuditStatus() == AuditStatus.Submitted)
									statusOK = true;
								if (conAudit.getAuditType().equals(audit.getAuditType()))
									typeOK = true;
								if (conAudit.getAuditType().getId() == AuditType.NCMS
										&& audit.getAuditType().getId() == AuditType.DESKTOP)
									typeOK = true;
								
								if (typeOK) {
									if (statusOK) {
										// We found a matching "valid" audit for this 
										// contractor audit requirement
										debug(" ---- found");
				
										if (audit.getAuditType().getId() == AuditType.ANNUALADDENDUM) {
											// We actually require THREE annual addendums 
											// before we consider this requirement complete
											annualAuditCount++;
										} else {
											// Regular audits (PQF, Desktop, Office, Field, IM, etc)
											audit.setContractorFlag(FlagColor.Green);
										}
									}
								}
								
							} else {
								// For Policies
								for(ContractorAuditOperator cao : conAudit.getOperators()) {
									if (cao.getOperator().equals(operator)) {
										if (CaoStatus.NotApplicable.equals(cao.getStatus())) {
											audit.setContractorFlag(null);
											debug(" ---- found N/A");
										}
										if (CaoStatus.Approved.equals(cao.getStatus())) {
											audit.setContractorFlag(FlagColor.Green);
											debug(" ---- found");
										}
									}
								}
							}
						} // end if auditType
					} // end for conAudit
					if (audit.getAuditType().getId() == AuditType.ANNUALADDENDUM && annualAuditCount >= 3) {
						// Make sure we have atleast three annual addendums
						audit.setContractorFlag(FlagColor.Green);
					}
					debug(" ---- flagColor=" + audit.getContractorFlag());
					flagColor = setFlagColor(flagColor, audit.getContractorFlag());
				}
				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			} // end if operator canSee audit
		} // end for (operator audit)

		debug(" post audit flagColor=" + flagColor);

		// Initialize the flag color to the default Green
		for( OshaType oshaType : contractor.getOshas().keySet() ) {
			Map<String, OshaAudit> theseOshas = contractor.getOshas().get(oshaType);
			
			if( theseOshas != null ) {
				for (OshaAudit oa : theseOshas.values() ) {
					oa.setFlagColor(FlagColor.Green);
				}
			}
		}
		for (FlagOshaCriteria criteria : operator.getFlagOshaCriteria()) {
			if (criteria.isRequired()) {
				debug(" -- osha " + criteria.getFlagColor()); // Red or Amber

				if (contractor.getOshas() != null 
						&& contractor.getOshas().get(OshaType.OSHA) != null) {
					for (String key : contractor.getOshas().get(OshaType.OSHA).keySet()) {
						OshaAudit osha = contractor.getOshas().get(OshaType.OSHA).get(key);
						if ((key.equals(OshaAudit.AVG) && criteria.getLwcr().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getLwcr().isTimeAverage())) {
							if (criteria.getLwcr().isFlagged(osha.getLostWorkCasesRate()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getTrir().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getTrir().isTimeAverage())) {
							if (criteria.getTrir().isFlagged(osha.getRecordableTotalRate()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getFatalities().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getFatalities().isTimeAverage())) {
							if (criteria.getFatalities().isFlagged(osha.getFatalities()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
						}
						flagColor = setFlagColor(flagColor, osha.getFlagColor());
					}
				}

				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			}
		}
		debug(" post osha flagColor=" + flagColor);
		
		
		debug(" evaluating " + acaList.size() + " question criteria");
		for(AuditCriteriaAnswer aca : acaList) {
			if (aca.getClassType() == AuditTypeClass.Audit)
				flagColor = setFlagColor(flagColor, aca.getResultColor());
		}
		debug(" post aca flagColor=" + flagColor);

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
			return WaitingOn.None; // This contractor is not associated with
		// this operator, so nothing to do now

		if (!contractor.isActiveB())
			return WaitingOn.Contractor; // This contractor is delinquent

		// Operator Relationship Approval
		if (YesNo.Yes.equals(operator.getApprovesRelationships())) {
			if (co.getWorkStatus().equals("P"))
				// Operator needs to approve/reject this contractor
				return WaitingOn.Operator;
			if (co.getWorkStatus().equals("N"))
				// Operator has already rejected this
				// contractor, and there's nothing else
				// they can do
				return WaitingOn.None;
		}

		// Billing
		if (contractor.isPaymentOverdue())
			return WaitingOn.Contractor; // The contractor has an unpaid
		// invoice due

		// If waiting on contractor, immediately exit, otherwise track the other
		// parties
		boolean waitingOnPics = false;
		boolean waitingOnOperator = false;

		// PQF, Desktop & Office Audits
		for (AuditOperator audit : operator.getAudits()) {
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() 
					&& audit.getRequiredForFlag() != null
					&& !audit.getRequiredForFlag().equals(FlagColor.Green) ) {

				for (ContractorAudit conAudit : conAudits) {
					AuditStatus auditStatus = conAudit.getAuditStatus();
					if (conAudit.getAuditType().equals(audit.getAuditType())) {
						// We found a matching audit type. Is it required?
						if (conAudit.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {
							
							if (!auditStatus.equals(AuditStatus.Exempt) && !auditStatus.equals(AuditStatus.Expired)) {
								// Pending, Submitted, Resubmitted, or Active Policy
								
								// This is a Policy, find the CAO for this operator
								for(ContractorAuditOperator cao : conAudit.getOperators()) {
									if (cao.getOperator().equals(operator) 
											&& !CaoStatus.NotApplicable.equals(cao.getRecommendedStatus())) {
										// This Policy is required by the operator
										if (!auditStatus.isComplete(audit.getRequiredAuditStatus()))
											// The contractor needs to still submit it
											return WaitingOn.Contractor;
										
										if (audit.getRequiredAuditStatus().equals(AuditStatus.Active) 
												&& (auditStatus.isPendingSubmittedResubmitted()))
											// This policy requires verification by PICS
											waitingOnPics = true;
										
										if (auditStatus.equals(AuditStatus.Active))
											// This is active, the operator needs to verify it
											if (cao.getStatus().equals(CaoStatus.Awaiting))
												waitingOnOperator = true;
										
										if (CaoStatus.Rejected.equals(cao.getStatus()))
											// The operator rejected their certificate, 
											// they should fix it and resubmit it
											return WaitingOn.Contractor;
									} // if
								} // for cao
							}
							// end of policies
						} else {
							// This is a audit
							if (!auditStatus.isComplete(audit.getRequiredAuditStatus())) {
								// We found a matching pending or submitted audit still not finished
								// Whose fault is it??
								debug(" ---- still required");
								if (conAudit.getAuditType().isPqf()) {
									if (auditStatus.equals(AuditStatus.Pending))
										// The contractor still needs to submit their PQF
										return WaitingOn.Contractor;
									if (conAudit.getPercentVerified() > 0)
										// contractor needs to send us updated
										// information (EMR, OSHA, etc)
										// This PQF must be submitted and not verified yet
										// at all, so it's PICS' fault
										return WaitingOn.Contractor; // The
									waitingOnPics = true;
								} else if (conAudit.getAuditType().getId() == AuditType.OFFICE)
									// either needs to
									// schedule the
									// audit or close
									// out RQs
									return WaitingOn.Contractor; // The contractor
								else if (conAudit.getAuditType().getId() == AuditType.DESKTOP) {
									if (auditStatus.equals(AuditStatus.Submitted))
										// contractor
										// needs to
										// close out RQs
										// This desktop still hasn't been performed by PICS
										return WaitingOn.Contractor; // The
									waitingOnPics = true;
								}
							} // if stillRequired
						}
					} // if auditType
				} // for conAudits
			} // if op.audit required
		} // for operator.audits

		// Conclusion
		if (waitingOnPics)
			return WaitingOn.PICS;
		if (waitingOnOperator)
			// only show the operator if contractor and pics are all done
			return WaitingOn.Operator;

		// If everything is done, then quit with waiting on = no one
		return WaitingOn.None;
	}
	
	public CaoStatus calculateCaoRecommendedStatus(ContractorAuditOperator cao) {
		debug(" calculateCaoRecommendedStatus");
		
		FlagColor flagColor = null;
		for(AuditCriteriaAnswer aca : acaList) {
			if (aca.getClassType() == AuditTypeClass.Policy && aca.getAnswer().getAudit().equals( cao.getAudit() ) )
				flagColor = setFlagColor(flagColor, aca.getResultColor());
		}
		
		if( flagColor == null )
			return CaoStatus.Awaiting;

		if (flagColor.equals(FlagColor.Red))
			return CaoStatus.Rejected;
		
		if (flagColor.equals(FlagColor.Green))
			return CaoStatus.Approved;
		
		return CaoStatus.Awaiting;
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
			// oldColor = FlagColor.Green;
			return newColor;
		}

		// If 2-Red is greater (worse) than 0-Green, then change the color
		if (newColor.ordinal() > oldColor.ordinal())
			oldColor = newColor;
		return oldColor;
	}

	protected void debug(String message) {
		PicsLogger.log(message);
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

	public boolean isAnswerOnly() {
		return answerOnly;
	}

	public void setAnswerOnly(boolean answerOnly) {
		this.answerOnly = answerOnly;
	}

	public void setAcaList(List<AuditCriteriaAnswer> acaList) {
		this.acaList = acaList;
	}
}
