package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
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

	public FlagDataOverride find(int id) {
		return em.find(FlagDataOverride.class, id);
	}

	public Map<FlagCriteria, List<FlagDataOverride>> findByContractorAndOperator(ContractorAccount contractor,
			OperatorAccount operator) {
		String where = operator.getIdString();
		for (Facility facility : operator.getCorporateFacilities()) {
			where += "," + facility.getCorporate().getId();
		}
		Query query = em.createQuery("FROM FlagDataOverride d WHERE contractor.id = ? AND operator.id IN (" + where
				+ ")");
		query.setParameter(1, contractor.getId());
		Map<FlagCriteria, List<FlagDataOverride>> map = new HashMap<FlagCriteria, List<FlagDataOverride>>();
		List<FlagDataOverride> results = query.getResultList();
		for (FlagDataOverride override : results) {
			if(map.get(override.getCriteria()) == null)
				map.put(override.getCriteria(), new ArrayList<FlagDataOverride>());
			map.get(override.getCriteria()).add(override);
		}

		return map;
	}
	
	public FlagDataOverride findByConAndOpAndCrit(int conID, int opID, int cID){
		try{
			Query query = em.createQuery("FROM FlagDataOverride fdo WHERE contractor.id = ? AND operator.id = ? AND " +
			"criteria.id = ?");
			query.setParameter(1, conID);
			query.setParameter(2, opID);
			query.setParameter(3, cID);
			
			return (FlagDataOverride) query.getSingleResult();	
			
		} catch(NoResultException e){
			return null;
		}
	}
}
