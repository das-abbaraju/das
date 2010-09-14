package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

public class ContractorAuditOperatorWorkflowDAO extends PicsDAO {

	@SuppressWarnings("unchecked")
	public List<ContractorAuditOperatorWorkflowDAO> findByCaoID(int caoID){
		Query query = em.createQuery("FROM ContractorAuditOperatorWorkflow c WHERE c.cao.id = ?");
		query.setParameter(1, "caoID");		
		
		return query.getResultList();
	}
	
}
