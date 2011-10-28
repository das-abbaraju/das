package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;

@SuppressWarnings("unchecked")
public class ContractorAuditOperatorWorkflowDAO extends PicsDAO {

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

	public List<ContractorAuditOperatorWorkflow> findbyAuditStatus(int auditID, AuditStatus status) {
		Query query = em.createQuery("FROM ContractorAuditOperatorWorkflow caow where caow.cao.audit.id = :auditID "
				+ "AND caow.status = :status AND caow.updateDate = "
				+ "(SELECT MAX(w.updateDate) from ContractorAuditOperatorWorkflow w WHERE w.cao.id = caow.cao.id)");

		query.setParameter("auditID", auditID);
		query.setParameter("status", status);

		return query.getResultList();
	}

}
