package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.AuditType;
import com.picsauditing.auditbuilder.entities.ContractorDocument;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public class ContractorDocumentDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorDocument save(ContractorDocument o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public boolean isNeedsWelcomeCall(int conID) {
		Query query = em.createNativeQuery("SELECT a.id FROM accounts a " +
				"JOIN contractor_info ci ON ci.id = a.id " +
				"LEFT JOIN contractor_audit ca2 ON ca2.conID = a.id AND ca2.auditTypeID = 9 AND ca2.creationDate >= DATE_SUB(ci.membershipDate, INTERVAL 1 YEAR) " +
				"LEFT JOIN contractor_audit ca3 ON ca3.conID = a.id AND ca3.auditTypeID = 9 " +
				"WHERE ca2.id is null " +
				"AND a.type = 'Contractor' " +
				"AND a.status = 'Active' " +
				"AND ci.accountLevel = 'Full' " +
				"AND ci.membershipDate > DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
				"AND a.id=" + conID);

		List<Integer> list = query.getResultList();
		if (list.size() > 0) {
			return true;
		}

		return false;
	}

    public List<ContractorDocument> findAuditsByContractorAuditTypeAuditFors(int conID, int auditTypeID, List<String> auditFors) {
        String sql = "SELECT t FROM com.picsauditing.auditbuilder.entities.ContractorAudit t "
                + " WHERE t.contractorAccount.id = " + conID
                + " AND t.auditType.id = " + auditTypeID
                + " AND t.auditFor in (";
        boolean addComma = false;
        for (String auditFor:auditFors) {
            if (addComma)
                sql +=",";
            sql += "'" + auditFor + "'";
            addComma = true;
        }
        sql += ")";
        Query query = em.createQuery(sql);
        return query.getResultList();
    }

	public ContractorDocument find(int id) {
		return em.find(ContractorDocument.class, id);
	}

	public ContractorDocument findMostRecentAuditByContractorAuditType(int conId, int auditTypeId) {
		String hql = "SELECT ca FROM com.picsauditing.auditbuilder.entities.ContractorAudit ca " + "WHERE ca.contractorAccount.id = " + conId
				+ " AND ca.auditType.id = " + auditTypeId + " order by ca.expiresDate DESC";

		List<ContractorDocument> audits = em.createQuery(hql).getResultList();
		if (audits.size() > 0) {
			return audits.get(0);
		}
		return null;
	}

	public ContractorDocument findPreviousAudit(ContractorDocument audit) {
		ContractorDocument previousAudit = null;

		if (audit.getAuditType().isRenewable()) {
			return null;
		}

		if (audit.getAuditType().isHasMultiple() && audit.getAuditType().getId() != AuditType.ANNUALADDENDUM) {
			return null;
		}

		Query query = em.createQuery("SELECT t FROM com.picsauditing.auditbuilder.entities.ContractorAudit t " + "WHERE t.contractorAccount.id = :conId "
				+ " AND t.auditType.id = :auditTypeId AND t.creationDate < :creationDate "
				+ " ORDER BY t.creationDate DESC");
		query.setParameter("conId", audit.getContractorAccount().getId());
		query.setParameter("auditTypeId", audit.getAuditType().getId());
		query.setParameter("creationDate", audit.getCreationDate());
		List<ContractorDocument> list = query.getResultList();

		if (list.size() > 0)
			previousAudit = list.get(0);

		return previousAudit;
	}

	public List<ContractorDocument> findSubsequentAudits(ContractorDocument conAudit) {
		Query query = em.createQuery("SELECT ca FROM com.picsauditing.auditbuilder.entities.ContractorAudit ca WHERE ca.previousAudit = :conAudit " +
				"ORDER BY ca.creationDate DESC");
		query.setParameter("conAudit", conAudit);

		return query.getResultList();
	}
}