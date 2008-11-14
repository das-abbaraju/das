package com.picsauditing.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PermissionQueryBuilder;

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
		Query query = em.createQuery("select a.id from ContractorAccount a");
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
		return query.getResultList();
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
				+ " ORDER BY dateCreated DESC";
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public ContractorAccount findName(String userName) {
		if (userName == null)
			userName = "";
		Query query = em.createQuery("SELECT a FROM ContractorAccount a WHERE username = " + "'" + userName + "'");
		return (ContractorAccount) query.getSingleResult();
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

	public List<ContractorAccount> findDelinquentContractors(Permissions permissions, int limit) {
		if (permissions == null)
			return new ArrayList<ContractorAccount>();

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
		qb.setActiveContractorsOnly(false);
		String hql = "FROM ContractorAccount contractorAccount WHERE DATEDIFF(NOW(),lastInvoiceDate) > 75 AND (lastPayment IS NULL OR lastPayment < lastInvoiceDate) AND active = 'Y' AND mustPay = 'Yes'"
				+ qb.toString() + " ORDER BY lastInvoiceDate ASC";
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);
		return query.getResultList();

	}

	public ContractorAccount findConID(String name) {
		if (name == null)
			name = "";
		if (name.length() > 0)
			name = "WHERE a.name = '" + name + "'";
		Query query = em.createQuery("SELECT a from ContractorAccount a " + name);
		return (ContractorAccount) query.getSingleResult();
	}

}
