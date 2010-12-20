package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;

public class ContractorAuditOperatorWorkflowDAO extends PicsDAO {

	@SuppressWarnings("unchecked")
	public List<ContractorAuditOperatorWorkflow> findByCaoID(int caoID) {
		Query query = em
				.createQuery("FROM ContractorAuditOperatorWorkflow c WHERE c.cao.id = ? ORDER BY c.creationDate DESC");
		query.setParameter(1, caoID);

		return query.getResultList();
	}
	
	public ContractorAuditOperatorWorkflow findByCaoNoteID(int caoID, int noteID) {
		Query query = em
				.createQuery("FROM ContractorAuditOperatorWorkflow c WHERE c.cao.id = :caoID AND c.id = :noteID");
		query.setParameter("caoID", caoID);
		query.setParameter("noteID", noteID);

		return (ContractorAuditOperatorWorkflow) query.getSingleResult();
	}
	
}
