package com.picsauditing.PICS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditOperator;
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
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.jpa.entities.YesNo;

public class FlagDataCalculator {
	private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
	private Map<FlagCriteria, FlagCriteriaOperator> operatorCriteria = null;
	private Map<FlagCriteria, FlagDataOverride> overrides = null;
	private Map<AuditType, List<ContractorAuditOperator>> caoMap;

	public FlagDataCalculator(List<FlagCriteriaContractor> contractorCriteria,
			List<FlagCriteriaOperator> operatorCriteria) {
		setContractorCriteria(contractorCriteria);
		setOperatorCriteria(operatorCriteria);
	}

	public List<FlagData> calculate() {
		List<FlagData> list = new ArrayList<FlagData>();

		for (FlagCriteria key : operatorCriteria.keySet()) {
			FlagColor flag = FlagColor.Green;
			if (contractorCriteria.containsKey(key)) {
				if (overrides != null && overrides.containsKey(key)) {
					final FlagDataOverride override = overrides.get(key);
					if (override.isInForce())
						flag = override.getForceflag();
				} else {
					boolean flagged = isFlagged(operatorCriteria.get(key), contractorCriteria.get(key));
					if (flagged)
						flag = operatorCriteria.get(key).getFlag();
				}

				FlagData data = new FlagData();
				data.setCriteria(key);
				data.setContractor(contractorCriteria.get(key).getContractor());
				data.setOperator(operatorCriteria.get(key).getOperator());
				data.setFlag(flag);
				data.setAuditColumns(new User(User.SYSTEM));
				list.add(data);
			}
		}

		return list;
	}

	private boolean isFlagged(FlagCriteriaOperator opCriteria, FlagCriteriaContractor conCriteria) {
		// Criteria should match

		FlagCriteria criteria = opCriteria.getCriteria();
		String hurdle = criteria.getDefaultValue();

		if (criteria.isAllowCustomValue() && opCriteria.getHurdle() != null) {
			hurdle = opCriteria.getHurdle();
		}

		String answer = conCriteria.getAnswer();

		if (criteria.getAuditType() != null) {
			for (AuditOperator auditOperator : opCriteria.getOperator().getVisibleAudits()) {
				if (auditOperator.isRequiredFor(conCriteria.getContractor())) {
					if (!criteria.getAuditType().getClassType().isPolicy()) {
						if (conCriteria.getAnswer().equals("false")) {
							return true;
						}
					} else {
						// TODO check to see if the policy is not expired when
						// adding caos to caoMap
						List<ContractorAuditOperator> caoList = caoMap.get(criteria.getAuditType());
						if (caoList != null) {
							for (ContractorAuditOperator cao : caoList) {
								if (cao.getStatus().isApproved() || cao.getStatus().isNotApplicable())
									return false;
								else
									return true;
							}
						}
						// If the policy doesn't exist, then flag it
					}
					return true;
				}
			}
		}
		// Check for questionID in (401, 755)
		if (criteria.getQuestion() != null) {
			int questionID = criteria.getQuestion().getId();
			if (questionID == 401 || questionID == 755) {
				// Check if answers have been verified... if yes, return false
				// (don't flag)
				return !conCriteria.isVerified();
			}
		}
		final String dataType = criteria.getDataType();
		final String comparison = criteria.getComparison();
		boolean isValid = true;
		try {
			if (dataType.equals("boolean")) {
				isValid = (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
			}

			if (dataType.equals("number")) {
				float answer2 = Float.parseFloat(answer);
				float hurdle2 = Float.parseFloat(hurdle);
				if (comparison.equals("="))
					isValid = answer2 == hurdle2;
				if (comparison.equals(">"))
					isValid = answer2 > hurdle2;
				if (comparison.equals("<"))
					isValid = answer2 < hurdle2;
				if (comparison.equals(">="))
					isValid = answer2 >= hurdle2;
				if (comparison.equals("<="))
					isValid = answer2 <= hurdle2;
				if (comparison.equals("!="))
					isValid = answer2 != hurdle2;
			}

			if (dataType.equals("string")) {
				if (comparison.equals("="))
					isValid = hurdle.equals(answer);
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
					isValid = conDate.before(opDate);
				if (comparison.equals(">"))
					isValid = conDate.after(opDate);
				if (comparison.equals("="))
					isValid = conDate.equals(opDate);
			}
		} catch (Exception e) {
			System.out.println("Datatype is " + dataType + " but values were not " + dataType + "s");
			return true;
		}
		if (isValid)
			return false;

		return true;
	}

	public WaitingOn calculateWaitingOn() {
		List<FlagData> flagDataList = calculate();

		if (flagDataList.size() > 0) {
			ContractorAccount contractor = flagDataList.get(0).getContractor();
			OperatorAccount operator = flagDataList.get(0).getOperator();

			ContractorOperator co = null;
			for (ContractorOperator co2 : contractor.getOperators()) {
				if (co2.getOperatorAccount().equals(operator)) {
					co = co2;
					break;
				}
			}

			if (co == null)
				return WaitingOn.None; // This contractor is not associated with
			// this operator, so nothing to do now

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

			for (FlagData flagData : flagDataList) {
				if (flagData.getCriteria().getAuditType() != null) {
					for (ContractorAudit conAudit : contractor.getAudits()) {
						if (conAudit.getAuditType().equals(flagData.getCriteria().getAuditType())) {
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
						} // end of flagData
					} // contractor.audits
				}
			}
			if (waitingOnPics)
				return WaitingOn.PICS;
			if (waitingOnOperator)
				// only show the operator if contractor and pics are all done
				return WaitingOn.Operator;
		}
		return WaitingOn.None;
	}

	public void setContractorCriteria(List<FlagCriteriaContractor> list) {
		contractorCriteria = new HashMap<FlagCriteria, FlagCriteriaContractor>();
		for (FlagCriteriaContractor value : list) {
			contractorCriteria.put(value.getCriteria(), value);
		}
	}

	public void setOperatorCriteria(List<FlagCriteriaOperator> list) {
		operatorCriteria = new HashMap<FlagCriteria, FlagCriteriaOperator>();
		for (FlagCriteriaOperator value : list) {
			operatorCriteria.put(value.getCriteria(), value);
		}
	}

	public Map<AuditType, List<ContractorAuditOperator>> getCaoMap() {
		return caoMap;
	}

	public void setCaoMap(Map<AuditType, List<ContractorAuditOperator>> caoMap) {
		this.caoMap = caoMap;
	}
}
