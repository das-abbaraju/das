package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.AuditType;
import com.picsauditing.auditbuilder.entities.ContractorAudit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public class ContractorAuditDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorAudit save(ContractorAudit o) {
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

    public List<ContractorAudit> findAuditsByContractorAuditTypeAuditFors(int conID, int auditTypeID, List<String> auditFors) {
        String sql = "SELECT t FROM ContractorAudit t "
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

	public ContractorAudit find(int id) {
		return em.find(ContractorAudit.class, id);
	}

	public ContractorAudit findMostRecentAuditByContractorAuditType(int conId, int auditTypeId) {
		String hql = "SELECT ca FROM ContractorAudit ca " + "WHERE ca.contractorAccount.id = " + conId
				+ " AND ca.auditType.id = " + auditTypeId + " order by ca.expiresDate DESC";

		List<ContractorAudit> audits = em.createQuery(hql).getResultList();
		if (audits.size() > 0) {
			return audits.get(0);
		}
		return null;
	}

	public ContractorAudit findPreviousAudit(ContractorAudit audit) {
		ContractorAudit previousAudit = null;

		if (audit.getAuditType().isRenewable()) {
			return null;
		}

		if (audit.getAuditType().isHasMultiple() && audit.getAuditType().getId() != AuditType.ANNUALADDENDUM) {
			return null;
		}

		Query query = em.createQuery("SELECT t FROM ContractorAudit t " + "WHERE t.contractorAccount.id = :conId "
				+ " AND t.auditType.id = :auditTypeId AND t.creationDate < :creationDate "
				+ " ORDER BY t.creationDate DESC");
		query.setParameter("conId", audit.getContractorAccount().getId());
		query.setParameter("auditTypeId", audit.getAuditType().getId());
		query.setParameter("creationDate", audit.getCreationDate());
		List<ContractorAudit> list = query.getResultList();

		if (list.size() > 0)
			previousAudit = list.get(0);

		return previousAudit;
	}

	public List<ContractorAudit> findSubsequentAudits(ContractorAudit conAudit) {
		Query query = em.createQuery("SELECT ca FROM ContractorAudit ca WHERE ca.previousAudit = :conAudit " +
				"ORDER BY ca.creationDate DESC");
		query.setParameter("conAudit", conAudit);

		return query.getResultList();
	}
}