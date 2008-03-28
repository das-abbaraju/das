package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;

@Transactional
public class ContractorAuditDAO extends PicsDAO {
	public ContractorAudit save(ContractorAudit o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		ContractorAudit row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	@SuppressWarnings("unchecked")
    public List<ContractorAudit> findByContractor(int conID) {
        Query query = em.createQuery("select t FROM ContractorAudit t WHERE t.contractorAccount.id = ?");
        query.setParameter(1, conID);
        return query.getResultList();
    }
	
    public ContractorAudit findContractorActiveAudit(int conID, int auditTypeID) {
        Query query = em.createQuery("SELECT t FROM ContractorAudit t " +
        		"WHERE t.contractorAccount.id = ? AND auditType.auditTypeID = ? " +
        		"AND auditStatus = ?");
        query.setParameter(1, conID);
        query.setParameter(2, auditTypeID);
        query.setParameter(3, AuditStatus.Active);
        return (ContractorAudit)query.getSingleResult();
    }

	public ContractorAudit find(int id) {
        return em.find(ContractorAudit.class, id);
    }
}
