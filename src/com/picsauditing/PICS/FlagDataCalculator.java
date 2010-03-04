package com.picsauditing.PICS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class FlagDataCalculator {
	private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
	private Map<FlagCriteria, FlagCriteriaOperator> operatorCriteria = null;
	private Map<FlagCriteria, FlagDataOverride> overrides = null;
	private OperatorAccount operator = null;

	// private Map<AuditType, List<ContractorAuditOperator>> caoMap;
	// Assume this is true for the contractor in question
	private boolean worksForOperator = true;

	public FlagDataCalculator(Collection<FlagCriteriaContractor> contractorCriteria) {
		setContractorCriteria(contractorCriteria);
	}

	public List<FlagData> calculate() {
		List<FlagData> dataSet = new ArrayList<FlagData>();

		for (FlagCriteria key : operatorCriteria.keySet()) {
			FlagColor flag = FlagColor.Green;
			if (contractorCriteria.containsKey(key)) {
				Boolean flagged = isFlagged(operatorCriteria.get(key), contractorCriteria.get(key));
				if (flagged != null) {
					if (overrides != null && overrides.containsKey(key)) {
						final FlagDataOverride override = overrides.get(key);
						if (override.isInForce())
							flag = override.getForceflag();
					} else if (flagged)
						flag = operatorCriteria.get(key).getFlag();

					FlagData data = new FlagData();
					data.setCriteria(key);
					data.setContractor(contractorCriteria.get(key).getContractor());
					data.setOperator(operator);
					data.setFlag(flag);
					data.setAuditColumns(new User(User.SYSTEM));
					dataSet.add(data);
				}
			}
		}

		return dataSet;
	}

	/**
	 * 
	 * @param opCriteria
	 * @param conCriteria
	 * @return true if something is BAD
	 */
	private Boolean isFlagged(FlagCriteriaOperator opCriteria, FlagCriteriaContractor conCriteria) {
		if (!opCriteria.getCriteria().equals(conCriteria.getCriteria()))
			throw new RuntimeException("FlagDataCalculator: Operator and Contractor Criteria must be of the same type");

		FlagCriteria criteria = opCriteria.getCriteria();
		String hurdle = criteria.getDefaultValue();

		if (criteria.isAllowCustomValue() && !Strings.isEmpty(opCriteria.getHurdle())) {
			hurdle = opCriteria.getHurdle();
		}

		String answer = conCriteria.getAnswer();
		if (criteria.getAuditType() != null) {
			boolean hasAudit = false;
			if (opCriteria.getMinRiskLevel().compareTo(conCriteria.getContractor().getRiskLevel()) <= 0) {
				for (ContractorAudit conAudit : conCriteria.getContractor().getAudits()) {
					if (conAudit.getAuditType().equals(criteria.getAuditType())) {
						hasAudit = true;
						if (criteria.getAuditType().getClassType().isPolicy()) {
							// Calculating the Policy status is much more
							// complex
							if (!opCriteria.getOperator().getCanSeeInsurance().isTrue())
								// Don't flag for operators that don't subscribe
								// to InsureGUARD
								return false;
							if (!conAudit.getAuditStatus().isExpired()) {
								OperatorAccount insuranceParent = opCriteria.getOperator().getInheritInsurance();
								for (ContractorAuditOperator cao : conAudit.getOperators()) {
									if (cao.getOperator().equals(insuranceParent)) {
										// We've found the applicable cao
										if (cao.getStatus().isApproved() || cao.getStatus().isNotApplicable())
											return false;
										else
											return true;
									}
								}
							}
						} else {
							// All other Audits, PQF, etc, flag if it's missing
							return conCriteria.getAnswer().equals("false");
						}
					}
				}
				// We should not flag on Audits the contractors don't have
				if (!hasAudit) {
					// This is a check for if the contractor doesn't
					// work for the operator, or is a bid only
					if (!worksForOperator || conCriteria.getContractor().isAcceptsBids()) {
						return null;
					}
					// This is for adHocAudits added audits
					if (opCriteria.getMinRiskLevel() == LowMedHigh.None) {
						return null;
					}
				}
				return criteria.isFlaggableWhenMissing();
			}
			// The contractor's risk level is not high enough to flag on this
			// audit.
			return null;
		} else {

			// Check for License Verifications
			if (criteria.getQuestion() != null) {
				int questionID = criteria.getQuestion().getId();
				if (questionID == 401 || questionID == 755) {
					// Check if answers have been verified... if yes, return
					// false
					// (don't flag)
					return !conCriteria.isVerified();
				}
			}

			// TODO If criteria is AMBEST
			// if("AMBest".equals(questionType)) {
			// boolean flag1 = false;
			// boolean flag2 = false;
			// int ratings = Integer.parseInt(answer.substring(0,
			// answer.indexOf('|')));
			// int bestClass =
			// Integer.parseInt(answer.substring(value.indexOf('|')+
			// 1,answer.length()));
			// if(getAMBestRatings() > 0)
			// flag1 = ratings > getAMBestRatings();
			// if(getAMBestClass() > 0)
			// flag2 = bestClass < getAMBestClass();
			// if(flag1 || flag2)
			// return true;
			//						
			// return false;
			// }

			// public int getAMBestRatings() {
			// if(!Strings.isEmpty(value)) {
			// return Integer.parseInt(value.substring(0, value.indexOf('|')));
			// }
			// return 0;
			// }
			//
			// public int getAMBestClass() {
			// if(!Strings.isEmpty(value)) {
			// return Integer.parseInt(value.substring(value.indexOf('|')+
			// 1,value.length()));
			// }
			// return 0;
			// }

			final String dataType = criteria.getDataType();
			final String comparison = criteria.getComparison();
			try {
				if (dataType.equals("boolean")) {
					return (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
				}

				if (dataType.equals("number")) {
					float answer2 = Float.parseFloat(answer);
					float hurdle2 = Float.parseFloat(hurdle);
					if (comparison.equals("="))
						return answer2 == hurdle2;
					if (comparison.equals(">"))
						return answer2 > hurdle2;
					if (comparison.equals("<"))
						return answer2 < hurdle2;
					if (comparison.equals(">="))
						return answer2 >= hurdle2;
					if (comparison.equals("<="))
						return answer2 <= hurdle2;
					if (comparison.equals("!="))
						return answer2 != hurdle2;
				}

				if (dataType.equals("string")) {
					if (comparison.equals("NOT EMPTY"))
						return !Strings.isEmpty(answer);
					if (comparison.equals("="))
						return hurdle.equals(answer);
				}

				if (dataType.equals("date")) {
					SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
					Date conDate = (Date) date.parse(answer);
					Date opDate;

					if (hurdle.equals("Today"))
						opDate = new Date();
					else
						opDate = (Date) date.parse(hurdle);

					if (comparison.equals("<"))
						return conDate.before(opDate);
					if (comparison.equals(">"))
						return conDate.after(opDate);
					if (comparison.equals("="))
						return conDate.equals(opDate);
				}
			} catch (Exception e) {
				System.out.println("Datatype is " + dataType + " but values were not " + dataType + "s");
				return true;
			}
		}

		return false;
	}

	public WaitingOn calculateWaitingOn(ContractorOperator co) {

		ContractorAccount contractor = co.getContractorAccount();
		OperatorAccount operator = co.getOperatorAccount();

		if (!contractor.getStatus().isActiveDemo())
			return WaitingOn.Contractor; // This contractor is delinquent

		// If Bid Only Account
		if (contractor.isAcceptsBids()) {
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

		// If waiting on contractor, immediately exit, otherwise track the
		// other parties
		boolean waitingOnPics = false;
		boolean waitingOnOperator = false;

		for (FlagCriteria key : operatorCriteria.keySet()) {
			for (ContractorAudit conAudit : contractor.getAudits()) {
				if (key.getAuditType().equals(conAudit.getAuditType())) {
					AuditStatus auditStatus = conAudit.getAuditStatus();
					if (conAudit.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {
						if (!auditStatus.equals(AuditStatus.Expired)) {
							// This is a Policy, find the CAO for this
							// operator
							for (ContractorAuditOperator cao : conAudit.getOperators()) {
								if (cao.getOperator().equals(operator) && cao.isVisible()) {
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
										return WaitingOn.Contractor;
								} // if
							} // for cao
						} // end of policies
					} else {
						if (conAudit.getAuditType().getClassType().isPqf()
								|| conAudit.getAuditType().isAnnualAddendum()) {
							if (auditStatus.isPending() || auditStatus.isIncomplete())
								// The contractor still needs to submit
								// their PQF
								return WaitingOn.Contractor;
							waitingOnPics = true;
						} else if (conAudit.getAuditType().getId() == AuditType.OFFICE)
							// either needs to schedule the audit or
							// close out RQs
							return WaitingOn.Contractor;
						else if (conAudit.getAuditType().getId() == AuditType.DESKTOP) {
							if (auditStatus.equals(AuditStatus.Submitted))
								// contractor needs to close out RQs
								return WaitingOn.Contractor;
							waitingOnPics = true;
						}
					} // end of audits
				}
			}
		}
		if (waitingOnPics)
			return WaitingOn.PICS;
		if (waitingOnOperator)
			// only show the operator if contractor and pics are all done
			return WaitingOn.Operator;

		return WaitingOn.None;
	}

	public FlagColor calculateCaoStatus(AuditType auditType, Set<FlagData> flagDatas) {
		PicsLogger.log("Calculating recommendation for " + auditType);
		FlagColor flag = null;
		for(FlagData flagData : flagDatas) {
			if(flagData.getCriteria().getQuestion() != null 
					&& flagData.getCriteria().getQuestion().getAuditType().equals(auditType)) {
				flag = FlagColor.getWorseColor(flag, flagData.getFlag());
				if (flag.isRed())
					PicsLogger.log(" --- " + flagData.getFlag() + " " + flagData.getCriteria().getQuestion());
					return flag;
			}
		}
		if (flag == null)
			flag = FlagColor.Green;
		
		return flag;
	}

	private void setContractorCriteria(Collection<FlagCriteriaContractor> list) {
		contractorCriteria = new HashMap<FlagCriteria, FlagCriteriaContractor>();
		for (FlagCriteriaContractor value : list) {
			contractorCriteria.put(value.getCriteria(), value);
		}
	}

	public void setOperatorCriteria(Collection<FlagCriteriaOperator> list) {
		operatorCriteria = new HashMap<FlagCriteria, FlagCriteriaOperator>();
		for (FlagCriteriaOperator value : list) {
			operatorCriteria.put(value.getCriteria(), value);
		}
	}

	public void setOverrides(Set<FlagDataOverride> overridesSet) {
		Map<FlagCriteria, FlagDataOverride> overridesMap = new HashMap<FlagCriteria, FlagDataOverride>();
		for(FlagDataOverride override : overridesSet)
			overridesMap.put(override.getCriteria(), override);
		this.overrides = overridesMap;
	}

	public boolean isWorksForOperator() {
		return worksForOperator;
	}

	public void setWorksForOperator(boolean worksForOperator) {
		this.worksForOperator = worksForOperator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public OperatorAccount getOperator() {
		return operator;
	}
}
