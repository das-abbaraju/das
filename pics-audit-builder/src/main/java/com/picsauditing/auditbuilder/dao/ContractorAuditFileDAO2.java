package com.picsauditing.auditbuilder.dao;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

@SuppressWarnings("unchecked")
public class ContractorAuditFileDAO2 extends PicsDAO {
    @Transactional(propagation = Propagation.NESTED)
    public void removeAllByAuditID(int auditID) {
        Query query = em.createQuery("DELETE FROM ContractorAuditFile c "
                + "WHERE c.audit.id = :auditID");
        query.setParameter("auditID", auditID);
        query.executeUpdate();
    }
}