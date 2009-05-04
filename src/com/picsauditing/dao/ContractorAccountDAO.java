package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class ContractorAccountDAO extends PicsDAO {
	public ContractorAccount save(ContractorAccount o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorAccount row = find(id);
		remove(row);
	}

	public void remove(ContractorAccount row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(ContractorAccount row, String ftpDir) {
		FileUtils.deleteFile(ftpDir + "/logos/" + row.getLogoFile());
		String filename = "brochure_" + row.getId()+"."+row.getBrochureFile();
		FileUtils.deleteFile(ftpDir + "/files/brochures/" + filename);
		remove(row);
	}

	public ContractorAccount find(int id) {
		return em.find(ContractorAccount.class, id);
	}

	public List<Integer> findAll() {
		Query query = em.createQuery("select a.id from ContractorAccount a WHERE a.active = 'Y'");
		return query.getResultList();
	}

	public List<ContractorAccount> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT a from ContractorAccount a " + where + " ORDER BY a.name");
		return query.getResultList();
	}

	public List<ContractorOperator> findOperators(ContractorAccount contractor, Permissions permissions, String where) {
		if (where == null)
			where = "";
		if (permissions.isCorporate())
			// Show corporate users operators in their facility
			where = "AND operatorAccount IN (SELECT operator FROM Facility " + "WHERE corporate = "
					+ permissions.getAccountId() + ")";
		if (permissions.isOperator())
			// Show operator users operators that share the same corporate
			// facility
			where = "AND (operatorAccount.id = " + permissions.getAccountId()
					+ " OR operatorAccount IN (SELECT operator FROM Facility "
					+ "WHERE corporate IN (SELECT corporate FROM Facility " + "WHERE operator.id = "
					+ permissions.getAccountId() + ")))";

		Query query = em.createQuery("FROM ContractorOperator WHERE contractorAccount = ? " + where
				+ " ORDER BY operatorAccount.name");
		query.setParameter(1, contractor);
		
		// Make sure we have the list of operators
		List<ContractorOperator> list = query.getResultList();
		for(ContractorOperator co : list)
			co.getOperatorAccount();
		
		return list;
	}

	public List<Integer> findIdsByOperator(OperatorAccount opAccount) {
		String where = "SELECT a.id from ContractorAccount a WHERE a IN (SELECT co.contractorAccount FROM ContractorOperator co WHERE co.operatorAccount = ?)";
		Query query = em.createQuery(where);
		query.setParameter(1, opAccount);

		return query.getResultList();
	}

	public List<ContractorAccount> findNewContractors(Permissions permissions, int limit) {
		if (permissions == null)
			return new ArrayList<ContractorAccount>();

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);

		String hql = "FROM ContractorAccount contractorAccount WHERE 1=1 " + qb.toString()
				+ " ORDER BY creationDate DESC";
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public ContractorAccount findTaxID(String taxId) {
		if (taxId == null)
			taxId = "";
		try {
			Query query = em.createQuery("SELECT a FROM ContractorAccount a WHERE taxId = " + "'" + taxId + "'");
			query.setMaxResults(1);
			return (ContractorAccount) query.getSingleResult();
		} catch (NoResultException e) {	
			return null;
		}	
	}
	
	public int getActiveContractorCounts(String where) {
		if (where.equals(""))
			where = "";
		else
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT count(c) FROM ContractorAccount c " + where);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public List<ContractorAccount> findRecentLoggedContractors() {
		Query query = em.createQuery("SELECT c FROM ContractorAccount c ORDER BY c.lastLogin DESC");
		query.setMaxResults(10);
		return query.getResultList();
	}

	public ContractorAccount findConID(String name) {
		try {
			Query query;
			if (Strings.isEmpty(name)){
				query = em.createQuery("SELECT a FROM ContractorAccount a");
			}
			else {
				query = em.createQuery("SELECT a FROM ContractorAccount a WHERE a.name = ?");
				query.setParameter(1, name);
			}
			
			return (ContractorAccount) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	/**
	 * Find ids for all active contractors who either need recalculation but haven't been calculated in the past 30 minutes or haven't been calculated in the past week
	 * @return
	 */
	public List<Integer> findContractorsNeedingRecalculation() {
		String hql = "SELECT c.id FROM ContractorAccount c " +
				"WHERE " +
				"c.lastRecalculation < :lastRunDate " +
				"OR c.lastRecalculation IS NULL " +
				"ORDER BY c.needsRecalculation DESC, c.lastRecalculation";
		Query query = em.createQuery(hql);
		query.setMaxResults(10);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -10);
		query.setParameter("lastRunDate", calendar.getTime());
		
		return query.getResultList();
	}
	
	public void updateContractorByOperator(OperatorAccount operator) {
		String where = "UPDATE ContractorAccount a SET a.needsRecalculation = 1 WHERE a.operators.operatorAccount.id = ?";
		Query query = em.createQuery(where);
		query.setParameter(1, operator.getId());
		query.executeUpdate();
	}

}
