package com.picsauditing.PICS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.User;

public class FlagDataCalculator {
	private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
	private Map<FlagCriteria, FlagCriteriaOperator> operatorCriteria = null;
	private Map<FlagCriteria, FlagDataOverride> overrides = null;
	private Map<Integer, List<ContractorAuditOperator>> caoMap;

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
				data.setContractor(contractorCriteria.get(key).getContractorAccount());
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

		// See if the criteria can be overridden
		if (criteria.isAllowCustomValue()) {
			// See if operators have a hurdle (override?) set
			if (opCriteria.getHurdle() != null)
				hurdle = opCriteria.getHurdle();
		}

		String answer = conCriteria.getAnswer();

		// Check to see if Criteria is a policy
		// Check if policy is not applicable or approved (green flag), else red flag
		if (criteria.getAuditType().getClassType().isPolicy()) {
			List<ContractorAuditOperator> caoList = caoMap.get(criteria.getAuditType().getId());
			if (caoList != null) {
				for (ContractorAuditOperator cao : caoList) {
					if (cao.getAudit().getAuditType().equals(criteria.getAuditType())) {
						if (cao.getStatus().isApproved() || cao.getStatus().isNotApplicable())
							return false;
						if (cao.getStatus().isRejected())
							return true;
					}
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

	public Map<Integer, List<ContractorAuditOperator>> getCaoMap() {
		return caoMap;
	}

	public void setCaoMap(Map<Integer, List<ContractorAuditOperator>> caoMap) {
		this.caoMap = caoMap;
	}
}
