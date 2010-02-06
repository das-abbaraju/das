package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;

public class FlagDataCalculator {
	private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
	private Map<FlagCriteria, FlagCriteriaOperator> operatorCriteria = null;
	
	public FlagDataCalculator(List<FlagCriteriaContractor> contractorCriteria, List<FlagCriteriaOperator> operatorCriteria) {
		setContractorCriteria(contractorCriteria);
		setOperatorCriteria(operatorCriteria);
	}

	public List<FlagData> calculate() {
		List<FlagData> list = new ArrayList<FlagData>();
		
		for (FlagCriteria key : operatorCriteria.keySet()) {
			if (contractorCriteria.containsKey(key)) {
				FlagColor flag = operatorCriteria.get(key).evaluate(contractorCriteria.get(key));
				if (!flag.equals(FlagColor.Green)) {
					FlagData data = new FlagData();
					// TODO set the data fields
					list.add(data);
				}
			}
		}
		
		return list;
	}
	
	public void setContractorCriteria(List<FlagCriteriaContractor> list) {
		contractorCriteria = new HashMap<FlagCriteria, FlagCriteriaContractor>();
		for (FlagCriteriaContractor value : list) {
			contractorCriteria.put(null, value);
		}
	}

	public void setOperatorCriteria(List<FlagCriteriaOperator> list) {
		operatorCriteria = new HashMap<FlagCriteria, FlagCriteriaOperator>();
		for (FlagCriteriaOperator value : list) {
			operatorCriteria.put(null, value);
		}
	}
}
