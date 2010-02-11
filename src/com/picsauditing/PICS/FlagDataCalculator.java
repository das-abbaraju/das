package com.picsauditing.PICS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;

public class FlagDataCalculator {
	private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
	private Map<FlagCriteria, FlagCriteriaOperator> operatorCriteria = null;
	private Map<FlagCriteria, FlagDataOverride> overrides = null;
	private Map<Integer, List<ContractorAuditOperator>> caoMap;
	
	public FlagDataCalculator(List<FlagCriteriaContractor> contractorCriteria, List<FlagCriteriaOperator> operatorCriteria) {
		setContractorCriteria(contractorCriteria);
		setOperatorCriteria(operatorCriteria);
	}

	public List<FlagData> calculate() {
		List<FlagData> list = new ArrayList<FlagData>();
		
		// Consider audit operator matrix and see if something is required or not
		
		for (FlagCriteria key : operatorCriteria.keySet()) {
			FlagColor flag = FlagColor.Green;
			if (contractorCriteria.containsKey(key)) {
				if (overrides != null && overrides.containsKey(key)) {
					// Check if the override is still effective
					if (overrides.get(key).getForceEnd().compareTo(new Date()) < 0)
						flag = overrides.get(key).getForceflag();
				} else {
					//flag = operatorCriteria.get(key).evaluate(contractorCriteria.get(key));
					// Moving the evaluate function here
					flag = evaluate(operatorCriteria.get(key), contractorCriteria.get(key));
				}
				
				if (!flag.equals(FlagColor.Green)) {
					FlagData data = new FlagData();
					// TODO set the data fields
					data.setContractor(contractorCriteria.get(key).getContractorAccount());
					data.setContractorCriteria(contractorCriteria.get(key));
					data.setOperator(operatorCriteria.get(key).getOperator());
					data.setOperatorCriteria(operatorCriteria.get(key));
					data.setFlag(flag);
					data.setCriteria(key);
					Date now = new Date();
					data.setCreationDate(now);
					data.setUpdateDate(now);
					
					list.add(data);
				}
			}
		}
		
		return list;
	}
	
	private FlagColor evaluate(FlagCriteriaOperator opCriteria, FlagCriteriaContractor conCriteria) {
		// Criteria should match
		FlagCriteria criteria = opCriteria.getCriteria();
		String opAnswer = criteria.getDefaultValue();
		String conAnswer = conCriteria.getAnswer();
		FlagColor flag = opCriteria.getFlag();
		
		// Check to see if Criteria is a policy
		// Check if policy is not applicable or approved (green flag), else red flag
		if (criteria.getAuditType().getClassType().isPolicy()) {
			List<ContractorAuditOperator> caoList = caoMap.get(new Integer(criteria.getAuditType().getId()));
			if (caoList.size() > 0) {
				for (ContractorAuditOperator cao : caoList) {
					if (cao.getAudit().getAuditType().equals(criteria.getAuditType())
							&& (cao.getStatus().equals(CaoStatus.Approved)
									|| cao.getStatus().equals(CaoStatus.NotApplicable))) {
						return flag;
					} else
						return FlagColor.Red;
				}
			}
		}
		
		// See if the criteria can be overridden
		if (criteria.isAllowCustomValue())
		{ // See if operators have a hurdle (override?) set
			if (opCriteria.getHurdle() != null)
				opAnswer = opCriteria.getHurdle();
		}
		
		try {
			if (criteria.getDataType().equals("boolean") && (Boolean.parseBoolean(conAnswer) == Boolean.parseBoolean(opAnswer)))
				return flag;
			else if (criteria.getDataType().equals("number")
					&& ( (criteria.getComparison().equals("=") && Float.parseFloat(conAnswer) == Float.parseFloat(opAnswer))
						|| (criteria.getComparison().equals(">") && Float.parseFloat(conAnswer) > Float.parseFloat(opAnswer))
						|| (criteria.getComparison().equals("<") && Float.parseFloat(conAnswer) < Float.parseFloat(opAnswer))
						|| (criteria.getComparison().equals(">=") && Float.parseFloat(conAnswer) >= Float.parseFloat(opAnswer))
						|| (criteria.getComparison().equals("<=") && Float.parseFloat(conAnswer) <= Float.parseFloat(opAnswer))
						|| (criteria.getComparison().equals("!=") && Float.parseFloat(conAnswer) != Float.parseFloat(opAnswer)) ))
				return flag;
			else if (criteria.getDataType().equals("string")
					&& ( (conAnswer.equals(opAnswer) && criteria.getComparison().equals("="))
					|| (!conAnswer.equals(opAnswer) && criteria.getComparison().equals("!=")) ) )
				return flag;
			else if (criteria.getDataType().equals("date")) {
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
				Date conDate;
				Date opDate;

				if (conAnswer.equals("Today"))
					conDate = new Date();
				else
					conDate = (Date) date.parse(conAnswer);

				if (opAnswer.equals("Today"))
					opDate = new Date();
				else
					opDate = (Date) date.parse(opAnswer);

				if ( (criteria.getComparison().equals("<") && conDate.before(opDate))
					|| (criteria.getComparison().equals("=") && conDate.equals(opDate))
					|| (criteria.getComparison().equals(">") && conDate.after(opDate)) )
					return flag;
			}

			return FlagColor.Red;
		} catch (Exception e) {
			System.out.println("Datatype is " + criteria.getDataType() + " but values were not " + criteria.getDataType() + "s");
			return FlagColor.Red;
		}
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
