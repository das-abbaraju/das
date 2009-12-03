package com.picsauditing.PICS;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

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
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.log.PicsLogger;

/**
 * Determine the Flag color for a single contractor at a given facility. This doesn't persist any data nor does it query
 * the database for the required data.
 * 
 * @author Trevor
 */
@Transactional
public class FlagCalculatorSingle {
	private boolean answerOnly = true;

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private List<ContractorAudit> conAudits;
	protected List<AuditCriteriaAnswer> acaList;
	protected boolean hasOqEmployees = false;
	protected boolean hasCOR = false;

	/**
	 * 1) Check to see all required audits are there 2) OSHA Data 3) AuditQuestions
	 * 
	 * @param contractor
	 *            The contractor jpa entity to calculate the flag for
	 * @param operator
	 *            The operator jpa entity to calculate the contractor flag for
	 * @param conAudits
	 *            A list of audits for this contractor that may be required by this operator
	 * @param auditAnswers
	 * @return
	 */
	public FlagColor calculate() {
		debug("FlagCalculator.calculate(" + contractor.getId() + "," + operator.getId() + ")");

		// Start positive and see if there are any violations
		FlagColor flagColor = FlagColor.Green;
		FlagColor overrideColor = null;
		boolean worksForOperator = false;
		// First see if there are any forced flags for this operator
		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().equals(operator)) {
				worksForOperator = true;
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

		for (AuditOperator audit : operator.getVisibleAudits()) {
			audit.setContractorFlag(null);
			boolean hasAudit = false;
			for (ContractorAudit conAudit : conAudits) {
				if (conAudit.getAuditType().equals(audit.getAuditType()) && !conAudit.getAuditStatus().isExpired()) {
					hasAudit = true;
					break;
				}
			}
			// Always start with Green
			if (hasAudit)
				audit.setContractorFlag(FlagColor.Green);
			// removed contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() (needs further review)
			boolean auditFlagRedOrAmber = audit.getRequiredForFlag() != null
					&& !audit.getRequiredForFlag().equals(FlagColor.Green);
			boolean contractorRiskLevelHighEnough = audit.getMinRiskLevel() > 0
					&& contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel();
			boolean adHocExists = false;
			// Setting the Flag Color to
			if (audit.getMinRiskLevel() == 0) {
				if (hasAudit)
					adHocExists = true;
				else
					audit.setContractorFlag(null);
			}

			if ((adHocExists || contractorRiskLevelHighEnough) && auditFlagRedOrAmber) {
				debug(" -- " + audit.getAuditType().getAuditName() + " - " + audit.getRequiredForFlag().toString());
				// The contractor requires this audit,
				// make sure they have an active one
				// If an active audit doesn't exist, then set
				// the contractor's flag to the required color
				
				if(!hasAudit 
						&& (contractor.isAcceptsBids() || !worksForOperator)) {
					audit.setContractorFlag(null);
				}
				else
					audit.setContractorFlag(audit.getRequiredForFlag());

				int annualAuditCount = 0;
				for (ContractorAudit conAudit : conAudits) {
					if (conAudit.getAuditType().equals(audit.getAuditType())) {
						if (conAudit.getAuditType().getClassType().isPolicy()) {
							// For Policies
							if(!worksForOperator) {
								audit.setContractorFlag(null);
							}
							if (!conAudit.getAuditStatus().isExpired()) {
								for (ContractorAuditOperator cao : conAudit.getOperators()) {
									if (cao.getOperator().equals(operator.getInheritInsurance())) {
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
						} else {
							// For PQF, Audit, IM
							boolean statusOK = false;
							boolean typeOK = false;
							if (conAudit.getAuditStatus().isActiveResubmittedExempt())
								statusOK = true;
							if (conAudit.getAuditStatus().isSubmitted()
									&& audit.getRequiredAuditStatus().isSubmitted())
								statusOK = true;
							if (conAudit.getAuditType().equals(audit.getAuditType()))
								typeOK = true;
							if (conAudit.getAuditType().getId() == AuditType.NCMS
									&& audit.getAuditType().isDesktop())
								typeOK = true;
							if(contractor.isAcceptsBids() 
									&& conAudit.getAuditStatus().isSubmitted()) {
								statusOK = true;
							}
							if (typeOK) {
								if (statusOK) {
									// We found a matching "valid" audit for this
									// contractor audit requirement
									debug(" ---- found");

									if (audit.getAuditType().isAnnualAddendum()) {
										// We actually require THREE annual addendums
										// before we consider this requirement complete
										annualAuditCount++;
									} else {
										// Regular audits (PQF, Desktop, Office, Field, IM, etc)
										audit.setContractorFlag(FlagColor.Green);
									}
								}
							}
						}
					} // end if auditType
				} // end for conAudit
				if (audit.getAuditType().isAnnualAddendum() && annualAuditCount >= 3) {
					// Make sure we have atleast three annual addendums
					audit.setContractorFlag(FlagColor.Green);
				}
				if(audit.getAuditType().getId() == AuditType.DA && !isHasOqEmployees()) {
					audit.setContractorFlag(null);
					debug(" ---- found D/A but OQ not required");
				}
				if(audit.getAuditType().getId() == AuditType.COR && !isHasCOR()) {
					audit.setContractorFlag(null);
					debug(" ---- found COR but is not required");
				}
				debug(" ---- flagColor=" + audit.getContractorFlag());
				flagColor = setFlagColor(flagColor, audit.getContractorFlag());
			}
			if (answerOnly && flagColor.equals(FlagColor.Red))
				// Things can't get worse, just exit
				return flagColor;
		} // end for (operator audit)

		debug(" post audit flagColor=" + flagColor);

		// Initialize the flag color to the default Green
		Map<String, OshaAudit> shaMap = contractor.getOshas().get(operator.getInheritFlagCriteria().getOshaType());

		if (shaMap != null) {
			for (OshaAudit oa : shaMap.values()) {
				oa.setFlagColor(FlagColor.Green);
			}
		}
		int year = DateBean.getCurrentYear() - 1;
		for (FlagOshaCriteria criteria : operator.getInheritFlagCriteria().getFlagOshaCriteria()) {
			if (criteria.isRequired()) {
				debug(" -- osha " + criteria.getFlagColor()); // Red or Amber

				if (shaMap != null) {
					for (String key : shaMap.keySet()) {
						OshaAudit osha = shaMap.get(key);
						if ((key.equals(OshaAudit.AVG) && criteria.getLwcr().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && 
										(!criteria.getLwcr().isLastYearOnly() ||
								(criteria.getLwcr().isLastYearOnly() && Integer.toString(year).equals(osha.getConAudit().getAuditFor()))
								))) {
							if (criteria.getLwcr().isFlagged(contractor.getNaics().getLwcr(), osha.getLostWorkCasesRate()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
							debug(" --- checking LWCR " + criteria.getLwcr() + " against value = "
									+ osha.getLostWorkCasesRate() + " color = " + osha.getFlagColor());
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getTrir().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && 
										(!criteria.getTrir().isLastYearOnly() ||
								(criteria.getTrir().isLastYearOnly() && Integer.toString(year).equals(osha.getConAudit().getAuditFor()))
								))) {
							if (criteria.getTrir().isFlagged(contractor.getNaics().getTrir(), osha.getRecordableTotalRate()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
							debug(" --- checking TRIR " + criteria.getTrir() + " against value = "
									+ osha.getRecordableTotalRate() + " color = " + osha.getFlagColor());
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getDart().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && 
										(!criteria.getDart().isLastYearOnly() ||
								(criteria.getDart().isLastYearOnly() && Integer.toString(year).equals(osha.getConAudit().getAuditFor()))
								))) {
							if (criteria.getDart().isFlagged(contractor.getNaics().getTrir(), osha.getRestrictedDaysAwayRate()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
							debug(" --- checking DART " + criteria.getDart() + " against value = "
									+ osha.getRestrictedDaysAwayRate() + " color = " + osha.getFlagColor());
						}

						if ((key.equals(OshaAudit.AVG) && criteria.getFatalities().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getFatalities().isTimeAverage())) {
							if (criteria.getFatalities().isFlagged(contractor.getNaics().getTrir(), osha.getFatalities()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
							debug(" --- checking Fatalities " + criteria.getFatalities() + " against value = "
									+ osha.getFatalities() + " color = " + osha.getFlagColor());
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getCad7().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getCad7().isTimeAverage())) {
							if (osha.getCad7() != null
									&& criteria.getCad7().isFlagged(contractor.getNaics().getTrir(), osha.getCad7()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
							debug(" --- checking Cad7 " + criteria.getCad7() + " against value = " + osha.getCad7()
									+ " color = " + osha.getFlagColor());
						}
						if ((key.equals(OshaAudit.AVG) && criteria.getNeer().isTimeAverage())
								|| (!key.equals(OshaAudit.AVG) && !criteria.getNeer().isTimeAverage())) {
							if (osha.getNeer() != null
									&& criteria.getNeer().isFlagged(contractor.getNaics().getTrir(), osha.getNeer()))
								osha.setFlagColor(setFlagColor(osha.getFlagColor(), criteria.getFlagColor()));
							debug(" --- checking Neer " + criteria.getNeer() + " against value = " + osha.getNeer()
									+ " color = " + osha.getFlagColor());
						}
						flagColor = setFlagColor(flagColor, osha.getFlagColor());
					}
				} else {
					// TODO what if they don't enter any OSHA/MSHA record?
					// I think we should auto set it to red
					flagColor = FlagColor.Red;
				}

				if (answerOnly && flagColor.equals(FlagColor.Red))
					// Things can't get worse, just exit
					return flagColor;
			}
		}
		debug(" post osha flagColor=" + flagColor);

		debug(" evaluating " + acaList.size() + " question criteria");
		for (AuditCriteriaAnswer aca : acaList) {
			if (!aca.getClassType().isPolicy())
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

		// If Bid Only Account
		if(contractor.isAcceptsBids()) {
			return WaitingOn.Operator;
		}
		
		// Operator Relationship Approval
		if (YesNo.Yes.equals(operator.getApprovesRelationships())) {
			if (co.isWorkStatusPending())
				// Operator needs to approve/reject this contractor
				return WaitingOn.Operator;
			if (co.isWorkStatusRejected())
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
		for (AuditOperator audit : operator.getInheritAudits().getAudits()) {
			if (contractor.getRiskLevel().ordinal() >= audit.getMinRiskLevel() && audit.getRequiredForFlag() != null
					&& !audit.getRequiredForFlag().equals(FlagColor.Green)) {

				for (ContractorAudit conAudit : conAudits) {
					AuditStatus auditStatus = conAudit.getAuditStatus();
					if (conAudit.getAuditType().equals(audit.getAuditType())) {
						// We found a matching audit type. Is it required?
						if (conAudit.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {

							if (!auditStatus.equals(AuditStatus.Exempt) && !auditStatus.equals(AuditStatus.Expired)) {
								// Pending, Submitted, Resubmitted, or Active Policy

								// This is a Policy, find the CAO for this operator
								for (ContractorAuditOperator cao : conAudit.getOperators()) {
									if (cao.getOperator().equals(operator) && cao.isVisible()) {

										// This policy is already approved by operator
										if (cao.getStatus().isApproved())
											return WaitingOn.None;

										if (cao.getStatus().isPending()) {
											return WaitingOn.Contractor;
										}

										if (cao.getStatus().isSubmitted()) {
											waitingOnPics = true;
										}

										if (cao.getStatus().isVerified()) {
											waitingOnOperator = true;
										}

										if (cao.getStatus().isRejected())
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
								if (conAudit.getAuditType().getClassType().isPqf()
										|| conAudit.getAuditType().isAnnualAddendum()) {
									if (auditStatus.isPending() || auditStatus.isIncomplete())
										// The contractor still needs to submit their PQF
										return WaitingOn.Contractor;
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

	public FlagColor calculateCaoRecommendedFlag(ContractorAuditOperator cao) {
		debug(" calculateCaoRecommendedFlag");

		FlagColor flagColor = null;

		if (cao.getValid() != null && !cao.getValid().isTrue())
			return FlagColor.Red;

		for (AuditCriteriaAnswer aca : acaList) {
			if (aca.getClassType() == AuditTypeClass.Policy && aca.getAnswer().getAudit().equals(cao.getAudit()))
				flagColor = setFlagColor(flagColor, aca.getResultColor());
		}

		if (flagColor == null)
			return null;

		if (flagColor.equals(FlagColor.Red))
			return FlagColor.Red;

		if (flagColor.equals(FlagColor.Amber))
			return FlagColor.Amber;

		if (flagColor.equals(FlagColor.Green))
			return FlagColor.Green;

		return null;
	}

	/**
	 * Set the flag color, but only let it get worse Green to Red but not reverse
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
			// System.out.println("WARNING: oldColor == null");
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

	public boolean isHasOqEmployees() {
		return hasOqEmployees;
	}

	public void setHasOqEmployees(boolean hasOqEmployees) {
		this.hasOqEmployees = hasOqEmployees;
	}

	public boolean isHasCOR() {
		return hasCOR;
	}

	public void setHasCOR(boolean hasCOR) {
		this.hasCOR = hasCOR;
	}
}
