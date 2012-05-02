package com.picsauditing.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ContractorOperatorDAO extends PicsDAO {

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

	public List<ContractorOperator> findNewContractorOperators(int opID, int limit) {
		if (limit < 0)
			limit = 1;
		/*
		 * This will show demo accounts if the operator is a Demo account, otherwise it will only show Active accounts.
		 */
		Query query = em.createQuery("FROM ContractorOperator WHERE operatorAccount.id = :opID "
				+ "AND contractorAccount.status IN ('Active', operatorAccount.status) ORDER BY creationDate DESC");
		query.setMaxResults(limit);
		query.setParameter("opID", opID);
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
		if (isCorporate) {
			where += "co.operatorAccount IN " + "(SELECT f.operator FROM Facility f WHERE f.corporate.id = " + opID
					+ ")";
		} else {
			where += "co.operatorAccount.id = " + opID;
		}
		where += " AND co.workStatus = 'P' AND co.contractorAccount.status IN ('Active','Demo')";

		if (includeBidding) {
			where += " AND co.contractorAccount.accountLevel = 'BidOnly'";
		} else {
			where += " AND co.contractorAccount.accountLevel = 'Full'";
		}
		if (!isCorporate)
			where += " GROUP BY co.contractorAccount ORDER BY co.creationDate DESC";
		Query query = em.createQuery(where);
		return query.getResultList();
	}

	public List<ContractorOperator> findExpiredForceFlags() {
		Query query = em.createQuery("FROM ContractorOperator WHERE forceFlag IS NOT NULL AND forceEnd < :now");
		query.setParameter("now", new Date());
		return query.getResultList();
	}
	
	public int getTotalFlagChanges() {
		String sql = "select count(*) from generalcontractors gc " + "join accounts a on a.id=gc.genId "
				+ "join accounts contractor on contractor.id = gc.subID " + "where gc.flag != gc.baselineFlag "
				+ "AND a.type='Operator' " + "AND (gc.baselineFlag != 'Clear') " + "AND (gc.flag != 'Clear') "
				+ "AND (gc.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK)) "
				+ "AND (gc.forceFlag IS NULL OR NOW() >= gc.forceEnd) " + "AND (contractor.type='Contractor') "
				+ "AND (contractor.status IN ('Active')) "
				+ "AND (contractor.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK))";
		Query query = em.createNativeQuery(sql);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public int getOperatorsAffectedByFlagChanges() {
		String sql = "select count(distinct gc.genid) from generalcontractors gc " + "join accounts a on a.id=gc.genId "
				+ "join accounts contractor on contractor.id = gc.subID " + "where gc.flag != gc.baselineFlag "
				+ "AND a.type='Operator' " + "AND (gc.baselineFlag != 'Clear') " + "AND (gc.flag != 'Clear') "
				+ "AND (gc.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK)) "
				+ "AND (gc.forceFlag IS NULL OR NOW() >= gc.forceEnd) " + "AND (contractor.type='Contractor') "
				+ "AND (contractor.status IN ('Active')) "
				+ "AND (contractor.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK))";
		Query query = em.createNativeQuery(sql);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public List<Integer> getContractorIdsForOperator(String where) {
		if (Strings.isEmpty(where))
			throw new IllegalArgumentException("The where clause cannot be an empty String.");
					
		try {
			Query query = em.createQuery("SELECT contractorAccount.id FROM ContractorOperator WHERE " + where);
			return query.getResultList();
		} catch (Exception e) {
			System.out.println("An error occurred in the ContractorOperatorDAO - " + e.getMessage());
		}
		
		return Collections.emptyList();
	}
	
}
