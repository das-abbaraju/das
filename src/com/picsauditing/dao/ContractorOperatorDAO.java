package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class ContractorOperatorDAO extends PicsDAO {
	public ContractorOperator save(ContractorOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorOperator row = find(id);
		remove(row);
	}

	public void remove(ContractorOperator row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorOperator find(int id) {
		return em.find(ContractorOperator.class, id);
	}

	public ContractorOperator find(int conID, int opID) {
		try {
			Query query = em
					.createQuery("FROM ContractorOperator WHERE contractorAccount.id = ? AND operatorAccount.id = ?");
			query.setParameter(1, conID);
			query.setParameter(2, opID);
			return (ContractorOperator) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<ContractorOperator> findByContractor(int conID, Permissions permissions) {
		
		String where = "";
		if (permissions.isCorporate()) {
			String ids = Strings.implode(permissions.getOperatorChildren(), ",");
			if (ids.length() == 0)
				return null;
			where = " AND operatorAccount.id IN (" + ids + ")";
		}
		Query query = em.createQuery("FROM ContractorOperator WHERE contractorAccount.id = ? " + where + " ORDER BY operatorAccount.name");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public List<ContractorOperator> findForcedFlagsByOpID(int opID) {
		Query query = em.createQuery("FROM ContractorOperator WHERE operatorAccount.id = ? AND forceFlag IS NOT null");
		query.setParameter(1, opID);
		return query.getResultList();
	}
	
	public List<ContractorOperator> findPendingApprovalContractors(int opID, boolean includeBidding) {
		String where = "SELECT co FROM ContractorOperator co WHERE co.operatorAccount.id = "
				+ opID + " AND co.workStatus = 'P' AND co.contractorAccount.status = 'Active'";
		if(includeBidding) {
			where += " AND co.contractorAccount.acceptsBids = 1";
		}
		else {
			where += " AND co.contractorAccount.acceptsBids = 0";
		}
		where += " ORDER BY co.creationDate DESC";
		Query query = em.createQuery(where);
		query.setMaxResults(10);
		return query.getResultList();
	}
}
