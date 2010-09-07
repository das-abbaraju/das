package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
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
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class FlagDataCalculator {

	private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
	private Map<FlagCriteria, List<FlagCriteriaOperator>> operatorCriteria = null;
	private Map<FlagCriteria, List<FlagDataOverride>> overrides = null;
	private OperatorAccount operator = null;

	// private Map<AuditType, List<ContractorAuditOperator>> caoMap;
	// Assume this is true for the contractor in question
	private boolean worksForOperator = true;

	public FlagDataCalculator(Collection<FlagCriteriaContractor> contractorCriteria) {
		setContractorCriteria(contractorCriteria);
	}

	public FlagDataCalculator(FlagCriteriaContractor conCriteria, FlagCriteriaOperator opCriteria) {
		contractorCriteria = new HashMap<FlagCriteria, FlagCriteriaContractor>();
		contractorCriteria.put(conCriteria.getCriteria(), conCriteria);
		operatorCriteria = new HashMap<FlagCriteria, List<FlagCriteriaOperator>>();
		if (operatorCriteria.get(opCriteria.getCriteria()) == null)
			operatorCriteria.put(opCriteria.getCriteria(), new ArrayList<FlagCriteriaOperator>());
		operatorCriteria.get(opCriteria.getCriteria()).add(opCriteria);
	}

	public List<FlagData> calculate() {
		Map<FlagCriteria, FlagData> dataSet = new HashMap<FlagCriteria, FlagData>();

		for (FlagCriteria key : operatorCriteria.keySet()) {
			for (FlagCriteriaOperator fco : operatorCriteria.get(key)) {
				FlagColor flag = FlagColor.Green;
				if (contractorCriteria.containsKey(key)) {
					Boolean flagged = isFlagged(fco, contractorCriteria.get(key));
					if (flagged != null) {
						if (overrides != null && overrides.containsKey(key)) {
							FlagDataOverride override = hasForceDataFlag(overrides.get(key), operator);
							if (override != null)
								flag = override.getForceflag();
						} else if (flagged)
							flag = fco.getFlag();

						FlagData data = new FlagData();
						data.setCriteria(key);
						data.setContractor(contractorCriteria.get(key).getContractor());
						data.setCriteriaContractor(contractorCriteria.get(key));
						data.setOperator(operator);
						data.setFlag(flag);
						data.setAuditColumns(new User(User.SYSTEM));
						if (dataSet.get(key) == null)
							dataSet.put(key, data);
						else if (dataSet.get(key).getFlag().isWorseThan(flag))
							dataSet.put(key, data);
					}
				}
			}
		}

		return new ArrayList<FlagData>(dataSet.values());
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
			// TODO I don't think that requiring a risk level is going to work
			// for vendors
			if (conCriteria.getContractor().getRiskLevel() == null)
				return null;
			if (opCriteria.getMinRiskLevel().equals(LowMedHigh.None)) {
				return null;
			}
			if (opCriteria.getMinRiskLevel().ordinal() > conCriteria.getContractor().getRiskLevel().ordinal()) {
				return null;
			}
			if (!worksForOperator || conCriteria.getContractor().isAcceptsBids()) {
				// This is a check for if the contractor doesn't
				// work for the operator (Search for new), or is a bid only
				if (!criteria.getAuditType().isPqf() && !criteria.getAuditType().isAnnualAddendum())
					// Ignore all audit requirements other than PQF and AU
					return null;
			}

			if (conCriteria.getContractor().getAudits() == null)
				return null;

			if (criteria.getAuditType().isAnnualAddendum()) {
				// Annual Update Audit
				int count = 0;

				// Checking for at least 3 active annual updates
				for (ContractorAudit ca : conCriteria.getContractor().getAudits()) {
					if (ca.getAuditType().equals(criteria.getAuditType())) {
						for (ContractorAuditOperator cao : ca.getOperators()) {
							if (cao.getStatus().after(AuditStatus.Submitted))
								count++;
							else if (cao.getStatus().isSubmitted() && ca.getContractorAccount().isAcceptsBids())
								count++;
						}
					}
				}

				return count < 3;
			} else {
				// Any other audit, PQF, or Policy
				for (ContractorAudit ca : conCriteria.getContractor().getAudits()) {
					if (ca.getAuditType().equals(criteria.getAuditType()) && !ca.isExpired()) {
						for (ContractorAuditOperator cao : ca.getOperators()) {
							// TODO Make sure we identify the right operator or
							// corporate here
							if (opCriteria.getOperator().equals(cao.getOperator())) {
								if (cao.getStatus().after(AuditStatus.Submitted))
									return false;
								else if (!criteria.isValidationRequired() && cao.getStatus().isSubmitted())
									return false;
								else if (cao.getStatus().isSubmitted() && ca.getContractorAccount().isAcceptsBids())
									return false;

								if (!criteria.getAuditType().isHasMultiple())
									// There aren't any more so we might as well
									// return flagged right now
									return true;
							}
						}
					}
				}
				if (criteria.isFlaggableWhenMissing())
					// isFlaggableWhenMissing would be really useful for Manual
					// Audits or Implementation Audits
					return true;
			}
			return null;

		} else {

			if (criteria.isValidationRequired() && !conCriteria.isVerified())
				return true;

			final String dataType = criteria.getDataType();
			final String comparison = criteria.getComparison();

			try {
				if (dataType.equals("boolean")) {
					return (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
				}

				if (dataType.equals("number")) {
					float answer2 = Float.parseFloat(answer.replace(",", ""));
					float hurdle2 = Float.parseFloat(hurdle.replace(",", ""));
					if (criteria.getOshaRateType() != null && criteria.getOshaRateType().equals(OshaRateType.LwcrNaics)) {
						return answer2 > (Utilities.getIndustryAverage(true, conCriteria.getContractor().getNaics())* hurdle2)/100;
					}
					if (criteria.getOshaRateType() != null && criteria.getOshaRateType().equals(OshaRateType.TrirNaics)) {
						return answer2 > (Utilities.getIndustryAverage(false, conCriteria.getContractor().getNaics())* hurdle2)/100;
					}
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
						return Strings.isEmpty(answer);
					if (comparison.equalsIgnoreCase("contains"))
						return answer.contains(hurdle);
					if (comparison.equals("="))
						return hurdle.equals(answer);
				}

				if (dataType.equals("date")) {
					Date conDate = DateBean.parseDate(answer);
					Date opDate;

					if (hurdle.equals("Today"))
						opDate = new Date();
					else
						opDate = DateBean.parseDate(hurdle);

					if (comparison.equals("<"))
						return conDate.before(opDate);
					if (comparison.equals(">"))
						return conDate.after(opDate);
					if (comparison.equals("="))
						return conDate.equals(opDate);
				}
				return false;
			} catch (Exception e) {
				System.out.println("Datatype is " + dataType + " but values were not " + dataType + "s");
				return true;
			}
		}
	}

	public WaitingOn calculateWaitingOn(ContractorOperator co) {

		ContractorAccount contractor = co.getContractorAccount();
		OperatorAccount operator = co.getOperatorAccount();

		if (contractor.getRiskLevel() == null)
			return WaitingOn.Contractor;

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
			FlagCriteriaOperator fOperator = operatorCriteria.get(key).get(0);
			if (contractor.getRiskLevel().ordinal() >= fOperator.getMinRiskLevel().ordinal()
					&& !fOperator.getFlag().equals(FlagColor.Green)) {
				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (key.getAuditType().equals(conAudit.getAuditType())) {
						if (!conAudit.isExpired()) {

							ContractorAuditOperator cao = conAudit.getCao(operator.getOperatorHeirarchy());
							if (cao.isVisible()) {
								if (cao.getStatus().before(AuditStatus.Submitted)) {
									if (conAudit.getAuditType().isCanContractorEdit())
										return WaitingOn.Contractor;
									OpPerms editPerm = conAudit.getAuditType().getEditPermission();
									if (conAudit.getAuditType().getEditPermission() != null) {
										if (editPerm.isForOperator())
											waitingOnOperator = true;
										else if (editPerm.isForAdmin())
											waitingOnPics = true;
									}
								}

								AuditStatus requiredStatus = AuditStatus.Submitted;
								if (key.isValidationRequired())
									requiredStatus = AuditStatus.Complete;
								if (key.getAuditType().getClassType().isPolicy())
									// We may want to move this to
									// FlagOperatorCriteria
									requiredStatus = AuditStatus.Approved;

								if (cao.getStatus().before(requiredStatus)) {
									if (cao.getStatus().isComplete()) {
										waitingOnOperator = true;
									} else if (conAudit.getAuditType().getId() == AuditType.OFFICE) {
										// either needs to schedule the audit or
										// close out RQs
										return WaitingOn.Contractor;
									} else if (conAudit.getAuditType().getId() == AuditType.DESKTOP
											&& cao.getStatus().isSubmitted()) {
										// contractor needs to close out RQs
										return WaitingOn.Contractor;
									} else {
										waitingOnPics = true;
									}
								}
							}
						}
					}
				} // for
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
		for (FlagData flagData : flagDatas) {
			if (flagData.getCriteria().isInsurance()
					&& flagData.getCriteria().getQuestion().getAuditType().equals(auditType)) {
				flag = FlagColor.getWorseColor(flag, flagData.getFlag());
				if (flag.isRed()) {
					PicsLogger.log(" --- " + flagData.getFlag() + " " + flagData.getCriteria().getQuestion());
					return flag;
				}
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
		operatorCriteria = new HashMap<FlagCriteria, List<FlagCriteriaOperator>>();
		for (FlagCriteriaOperator value : list) {
			if (operatorCriteria.get(value.getCriteria()) == null)
				operatorCriteria.put(value.getCriteria(), new ArrayList<FlagCriteriaOperator>());
			operatorCriteria.get(value.getCriteria()).add(value);
		}
	}

	public void setOverrides(Map<FlagCriteria, List<FlagDataOverride>> overridesMap) {
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

	private FlagDataOverride hasForceDataFlag(List<FlagDataOverride> flList, OperatorAccount operator) {
		if (flList.size() > 0) {
			for (FlagDataOverride flagDataOverride : flList) {
				if (flagDataOverride.getOperator().equals(operator) && flagDataOverride.isInForce())
					return flagDataOverride;
			}
			if (flList.get(0).isInForce())
				return flList.get(0);
		}
		return null;
	}
}
