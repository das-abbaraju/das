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
		String hurdle = opCriteria.criteriaValue();

		String answer = conCriteria.getAnswer();

		// Check to see if Criteria is a policy
		// Check if policy is not applicable or approved (green flag), else red flag
		if (criteria.getAuditType().getClassType().isPolicy()) {
			List<ContractorAuditOperator> caoList = caoMap.get(criteria.getAuditType());
			if (caoList != null) {
				for (ContractorAuditOperator cao : caoList) {
					if (cao.getStatus().isApproved() || cao.getStatus().isNotApplicable())
						return false;
					if (cao.getStatus().isRejected())
						return true;
				}
			}
			// If the policy doesn't exist, then flag it
			return true;
		}

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
				return false;
			}

			if (dataType.equals("string")) {
				if (comparison.equals("="))
					return hurdle.equals(answer);
				else
					return !hurdle.equals(answer);
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
		return false;
	}
	
	public WaitingOn calculateWaitingOn() {
		// Assuming that the contractors in the contractorCriteria map are the same, and similar for operators
		ContractorAccount contractor = null;
		OperatorAccount operator = null;
		
		for (FlagCriteria key : contractorCriteria.keySet()) {
			FlagCriteriaContractor conFlags = contractorCriteria.get(key);
			contractor = conFlags.getContractor();
			break;
		}
		
		for (FlagCriteria key : operatorCriteria.keySet()) {
			FlagCriteriaOperator opFlags = operatorCriteria.get(key);
			operator = opFlags.getOperator();
			break;
		}
		
		// Taken more or less verbatim from FlagCalculatorSingle
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

		// If waiting on contractor, immediately exit, otherwise track the other
		// parties
		boolean waitingOnPics = false;
		boolean waitingOnOperator = false;
		List<ContractorAudit> conAudits = contractor.getAudits();

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
								// Pending, Submitted, Resubmitted, or Active
								// Policy

								// This is a Policy, find the CAO for this
								// operator
								for (ContractorAuditOperator cao : conAudit.getOperators()) {
									if (cao.getOperator().equals(operator) && cao.isVisible()) {

										// This policy is already approved by
										// operator
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
								// We found a matching pending or submitted
								// audit still not finished. Whose fault is it??
								if (conAudit.getAuditType().getClassType().isPqf()
										|| conAudit.getAuditType().isAnnualAddendum()) {
									if (auditStatus.isPending() || auditStatus.isIncomplete())
										// The contractor still needs to submit their PQF
										return WaitingOn.Contractor;
									waitingOnPics = true;
								} else if (conAudit.getAuditType().getId() == AuditType.OFFICE)
									// either needs to schedule the audit or close out RQs
									return WaitingOn.Contractor; // The contractor
								else if (conAudit.getAuditType().getId() == AuditType.DESKTOP) {
									if (auditStatus.equals(AuditStatus.Submitted))
										// contractor needs to close out RQs
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
