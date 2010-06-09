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
	
	public List<ContractorOperator> findNewContractorOperators(int opID, int limit){
		if(limit < 0)
			limit = 1;
		Query query = em.createQuery("FROM ContractorOperator WHERE genID = " + opID + " ORDER BY creationDate DESC");
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<ContractorOperator> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("FROM ContractorOperator " + where + " ORDER BY contractorAccount.name");
		return query.getResultList();
	}

	public List<ContractorOperator> findByContractor(int conID, Permissions permissions) {

		String where = "";
		if (permissions.isCorporate()) {
			String ids = Strings.implode(permissions.getOperatorChildren(), ",");
			if (ids.length() == 0)
				return null;
			where = " AND operatorAccount.id IN (" + ids + ")";
		}
		Query query = em
				.createQuery("FROM ContractorOperator WHERE contractorAccount.id = ? AND operatorAccount.type IN ('Operator')"
						+ where + " ORDER BY operatorAccount.name");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public List<ContractorOperator> findForcedFlagsByOpID(int opID) {
		Query query = em.createQuery("FROM ContractorOperator WHERE operatorAccount.id = ? AND forceFlag IS NOT null");
		query.setParameter(1, opID);
		return query.getResultList();
	}

	public List<ContractorOperator> findPendingApprovalContractors(int opID, boolean includeBidding, boolean isCorporate) {
		String where = "SELECT co FROM ContractorOperator co WHERE "; 
		if(isCorporate) {
			where += "co.operatorAccount IN "
			+ "(SELECT f.operator FROM Facility f WHERE f.corporate.id = " + opID+ ")";
		}
		else {
			where += "co.operatorAccount.id = " + opID;
		}
		where += " AND co.workStatus = 'P' AND co.contractorAccount.status IN ('Active','Demo')";

		if (includeBidding) {
			where += " AND co.contractorAccount.acceptsBids = 1";
		} else {
			where += " AND co.contractorAccount.acceptsBids = 0";
		}
		where += " GROUP BY co.contractorAccount ORDER BY co.creationDate DESC";
		Query query = em.createQuery(where);
		query.setMaxResults(10);
		return query.getResultList();
	}
}
