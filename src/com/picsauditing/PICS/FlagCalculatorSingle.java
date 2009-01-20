package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
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
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.AnswerMapByAudits;

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
	private AnswerMapByAudits answerMapByAudits;

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
			audit.setContractorFlag(null);
			if (audit.isCanSee()) {
				// Always start with Green
				audit.setContractorFlag(FlagColor.Green);
				if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() && audit.getRequiredForFlag() != null) {
					debug(" -- " + audit.getAuditType().getAuditName() + " - " + audit.getRequiredForFlag().toString());
					// The contractor requires this audit,
					// make sure they have an active one
					// If an active audit doesn't exist, then set
					// the contractor's flag to the required color
					audit.setContractorFlag(audit.getRequiredForFlag());
					
					int annualAuditCount = 0;
					for (ContractorAudit conAudit : conAudits) {
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
						if (conAudit.getAuditType().getAuditTypeID() == AuditType.NCMS
								&& audit.getAuditType().getAuditTypeID() == AuditType.DESKTOP)
							typeOK = true;
	
						if (typeOK) {
							if (statusOK) {
								// We found a matching "valid" audit for this 
								// contractor audit requirement
								debug(" ---- found");
		
								if (audit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM) {
									// We actually require THREE annual addendums 
									// before we consider this requirement complete
									annualAuditCount++;
								} else if(audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {
									// Policies have a different type of requirement
									for(ContractorAuditOperator cao : conAudit.getOperators()) {
										if (cao.getOperator().equals(operator) 
												&& !CaoStatus.NotApplicable.equals(cao.getRecommendedStatus())
												&& CaoStatus.Approved.equals(cao.getStatus())) {
											// If the cao is applicable, then it must be approved for this operator
											audit.setContractorFlag(FlagColor.Green);
										}
									}
								} else {
									// Regular audits (PQF, Desktop, Office, Field, IM, etc)
									audit.setContractorFlag(FlagColor.Green);
								}
							}
							
							// There is one exception to the status issue
							// If the operator says a Policy is N/A, then that 
							// requirement is fine no matter what the auditStatus is
							if (audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {
								for(ContractorAuditOperator cao : conAudit.getOperators()) {
									if (cao.getOperator().equals(operator)) {
										if (CaoStatus.NotApplicable.equals(cao.getRecommendedStatus())) {
											// The computer believes this cao is no longer necessary
											// This could be because the auditType is no longer visible
											// to the operator or because the operator said it was N/A
											audit.setContractorFlag(null);
										}
									}
								}
							}
						}
					} // end conAudit
					if (audit.getAuditType().getAuditTypeID() == AuditType.ANNUALADDENDUM && annualAuditCount >= 3) {
						// Make sure we have atleast three annual addendums
						audit.setContractorFlag(FlagColor.Green);
					}
					flagColor = setFlagColor(flagColor, audit.getContractorFlag());
				}
				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			} // end if operator canSee audit
		} // end for (operator audit)

		debug(" flagColor=" + flagColor);

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
		debug(" flagColor=" + flagColor);
		
		answerMapByAudits.resetFlagColors();
		
		// For each operator criteria, get the contractor's
		// answer and see if it triggers the flag color
		for (FlagQuestionCriteria criteria : operator.getFlagQuestionCriteria()) {
			if (criteria.getChecked().equals(YesNo.Yes)) {
				// This question is required by the operator

				FlagColor criteriaColor = null;
				AuditQuestion criteriaQuestion = criteria.getAuditQuestion();
				AuditType criteriaAuditType = criteriaQuestion.getSubCategory().getCategory().getAuditType();
				
				List<ContractorAudit> matchingConAudits = answerMapByAudits.getAuditSet(criteriaAuditType);
				
				if (matchingConAudits.size() > 0) {
					MultiYearScope scope = criteria.getMultiYearScope();
					if( scope != null ) {
						if (MultiYearScope.LastYearOnly.equals(scope)) {
							AuditData data = null;

							// Get the most recent year
							int mostRecentYear = 0;
							for (ContractorAudit conAudit : matchingConAudits) {
								try {
									int year = Integer.parseInt(conAudit.getAuditFor());
									if (year > mostRecentYear) {
										mostRecentYear = year;
										data = answerMapByAudits.get(conAudit).get(criteriaQuestion.getId());
									}
								} catch (Exception e) {
									System.out.println("Ignoring answer with year key: " + conAudit.getAuditFor());
								}
							}
							criteriaColor = flagData(criteriaColor, criteria, data);

						} else if (MultiYearScope.AllThreeYears.equals(scope)) {
							for (ContractorAudit conAudit : matchingConAudits) {
								AuditData data = answerMapByAudits.get(conAudit).get(criteriaQuestion.getId());
								criteriaColor = flagData(criteriaColor, criteria, data);
							}

						} else if (MultiYearScope.ThreeYearAverage.equals(scope)) {
							List<AuditData> dataList = new ArrayList<AuditData>();
							for (ContractorAudit conAudit : matchingConAudits) {
								AuditData data = answerMapByAudits.get(conAudit).get(criteriaQuestion.getId());
								dataList.add(data);
							}
							AuditData data = AuditData.addAverageData(dataList);
							criteriaColor = flagData(criteriaColor, criteria, data);

						}

					} else {
						if (criteria.getMultiYearScope() == null && matchingConAudits.size() > 1)
							System.out.println("WARNING! Found more than one " + criteriaAuditType.getAuditName() 
									+ " for conID=" + contractor.getId());
						AnswerMap answerMap = answerMapByAudits.get(matchingConAudits.get(0));
						
						if(criteriaQuestion.isAllowMultipleAnswers() ) {
							// this question is an anchor question that can have multiple answers
							// figure out if we should be optimistic or pessimistic here
							// I'm not going to spend much time on this 
							// because there are no existing use cases of using this yet
							for(AuditData data : answerMap.getAnswerList(criteriaQuestion.getId()))
								criteriaColor = flagData(criteriaColor, criteria, data);
							
						} else if( criteriaQuestion.getParentQuestion() != null ) {
							// These questions are "child" questions, so we must first find their parent
							for(AuditData parentData : answerMap.getAnswerList(criteriaQuestion.getParentQuestion().getId())) {
								// For each row, get the child answer and evaluate it
								AuditData data = answerMap.get(criteriaQuestion.getId(), parentData.getId());
								criteriaColor = flagData(criteriaColor, criteria, data);
							}

						} else {
							// DEFAULT : this is a normal (root/non child/non multiple) question
							AuditData data = answerMap.get(criteriaQuestion.getId());
							criteriaColor = flagData(criteriaColor, criteria, data);
						}
					}
				} // if matchingConAudits.size() > 0
				
				// Criteria for Policies don't affect the flag color, 
				// but the ContractorAuditOperator.recommendedStatus instead
				boolean isPolicyCriteria = criteriaAuditType.getClassType().equals(AuditTypeClass.Policy);

				if (isPolicyCriteria) {
					// Policy Criteria are only "suggestions," see calculateCaoRecommendedStatus()
				} else {
					// Update the Flag color
					flagColor = setFlagColor(flagColor, criteriaColor);
				}
				
				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			} // if criteria.isChecked...
		} // for
		debug(" flagColor=" + flagColor);

		if (overrideColor != null)
			return overrideColor;

		return flagColor;
	}

	private FlagColor flagData(FlagColor flagColor, FlagQuestionCriteria criteria, AuditData data) {
		if (data != null && data.getAnswer() != null && data.getAnswer().length() > 0) {
			// The contractor has answered this question
			// so it needs to be correct
			boolean isFlagged = false;

			if (criteria.isValidationRequired() && !data.isVerified()) {
				isFlagged = true;
			}

			if (criteria.isFlagged(data.getAnswer())) {
				isFlagged = true;
			}

			if (isFlagged) {
				data.setFlagColor(setFlagColor(data.getFlagColor(), criteria.getFlagColor()));
				flagColor = setFlagColor(flagColor, criteria.getFlagColor());
			} else {
				data.setFlagColor(FlagColor.Green);
			}
		}
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
							
							if (auditStatus.equals(AuditStatus.Exempt) || auditStatus.equals(AuditStatus.Expired)) {
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
											if (cao.getStatus().equals(CaoStatus.Missing))
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
								} else if (conAudit.getAuditType().getAuditTypeID() == AuditType.OFFICE)
									// either needs to
									// schedule the
									// audit or close
									// out RQs
									return WaitingOn.Contractor; // The contractor
								else if (conAudit.getAuditType().getAuditTypeID() == AuditType.DESKTOP) {
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
		AnswerMap answerMap = answerMapByAudits.get(cao.getAudit());
		if (answerMap == null)
			return CaoStatus.Missing;

		if (!cao.getAudit().getAuditType().getClassType().equals(AuditTypeClass.Policy))
			// This shouldn't happen ever, but just to make sure...
			return null;

		CaoStatus caoStatus = CaoStatus.Missing;
		answerMap.resetFlagColors();
		
		// For each operator criteria, get the contractor's
		// answer and see if it triggers the flag color
		for (FlagQuestionCriteria criteria : operator.getFlagQuestionCriteria()) {
			if (criteria.getChecked().equals(YesNo.Yes)) {
				// This question is required by the operator

				FlagColor criteriaColor = null;
				AuditQuestion criteriaQuestion = criteria.getAuditQuestion();
				AuditType criteriaAuditType = criteriaQuestion.getSubCategory().getCategory().getAuditType();
				
				if (criteriaAuditType.equals(cao.getAudit().getAuditType())) {
					// only evaluate the criteria for this AuditType
					
					if(criteriaQuestion.isAllowMultipleAnswers() ) {
						// this question is an anchor question that can have multiple answers
						// figure out if we should be optimistic or pessimistic here
						// I'm not going to spend much time on this 
						// because there are no existing use cases of using this yet
						for(AuditData data : answerMap.getAnswerList(criteriaQuestion.getId()))
							criteriaColor = flagData(criteriaColor, criteria, data);
						
					} else if( criteriaQuestion.getParentQuestion() != null ) {
						// These questions are "child" questions, so we must first find their parent
						for(AuditData parentData : answerMap.getAnswerList(criteriaQuestion.getParentQuestion().getId())) {
							// For each row, get the child answer and evaluate it
							AuditData data = answerMap.get(criteriaQuestion.getId(), parentData.getId());
							criteriaColor = flagData(criteriaColor, criteria, data);
						}

					} else {
						// DEFAULT : this is a normal (root/non child/non multiple) question
						AuditData data = answerMap.get(criteriaQuestion.getId());
						criteriaColor = flagData(criteriaColor, criteria, data);
					}

					// Update the CAO Recommended Status
					if (criteriaColor.equals(FlagColor.Red))
						caoStatus = CaoStatus.Rejected;
					if (!caoStatus.equals(CaoStatus.Rejected) && criteriaColor.equals(FlagColor.Green))
						caoStatus = CaoStatus.Approved;
					
				}
			} // if criteria.isChecked...
		} // for
		
		return caoStatus;
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

	public boolean isAnswerOnly() {
		return answerOnly;
	}

	public void setAnswerOnly(boolean answerOnly) {
		this.answerOnly = answerOnly;
	}

	public AnswerMapByAudits getAnswerMapByAudits() {
		return answerMapByAudits;
	}

	public void setAnswerMapByAudits(AnswerMapByAudits answerMapByAudits) {
		this.answerMapByAudits = answerMapByAudits;
	}
}
