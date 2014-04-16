package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAuditFile;

@SuppressWarnings("unchecked")
public class ContractorAuditFileDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorAuditFile save(ContractorAuditFile o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public ContractorAuditFile find(int id) {
		return em.find(ContractorAuditFile.class, id);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		ContractorAuditFile row = find(id);
		if (row != null)
			remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(ContractorAuditFile row) {
		if (row != null)
			em.remove(row);
	}
	
	public List<ContractorAuditFile> findByAudit(int auditID) {
		Query q = em.createQuery("SELECT caf FROM ContractorAuditFile caf WHERE caf.audit.id = ? ORDER BY caf.description");
		q.setParameter(1, auditID);
		return q.getResultList();
	}

    @Transactional(propagation = Propagation.NESTED)
    public void removeAllByAuditID(int auditID) {
        Query query = em.createQuery("DELETE FROM ContractorAuditFile c "
                + "WHERE c.audit.id = :auditID");
        query.setParameter("auditID", auditID);
        query.executeUpdate();
    }
}
