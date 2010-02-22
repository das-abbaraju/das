package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.OperatorAccount;

@Transactional
@SuppressWarnings("unchecked")
public class FlagDataOverrideDAO extends PicsDAO {

	public FlagDataOverride find(String id) {
		return em.find(FlagDataOverride.class, id);
	}

	public Map<FlagCriteria, FlagDataOverride> findByContractorAndOperator(ContractorAccount contractor,
			OperatorAccount operator) {
		String where = operator.getIdString();
		for (Facility facility : operator.getCorporateFacilities()) {
			where += "," + facility.getOperator().getId();
		}
		Query query = em.createQuery("FROM FlagDataOverride d WHERE contractor.id = ? AND operator.id IN (" + where
				+ ")");
		query.setParameter(1, contractor);
		Map<FlagCriteria, FlagDataOverride> map = new HashMap<FlagCriteria, FlagDataOverride>();
		List<FlagDataOverride> results = query.getResultList();
		if (operator.getCorporateFacilities().size() > 0) {
			// Put all the corporate overrides in
			for (FlagDataOverride override : results) {
				if (!override.getOperator().equals(operator)) {
					map.put(override.getCriteria(), override);
				}
			}
		}
		// Put all the operator overrides in
		for (FlagDataOverride override : results) {
			if (override.getOperator().equals(operator)) {
				map.put(override.getCriteria(), override);
			}
		}

		return map;
	}
}
