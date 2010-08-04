package com.picsauditing.dao;

import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

import java.util.List;

@Transactional
@SuppressWarnings("unchecked")
public class AccountDAO extends IndexableDAO {
	public Account save(Account o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Account row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public Account find(int id) {
		Account a = em.find(Account.class, id);
		return a;
	}

	public Account find(int id, String type) {
		if ("Contractor".equals(type))
			return em.find(ContractorAccount.class, id);
		
		if ("Operator".equals(type) || "Corporate".equals(type))
			return em.find(OperatorAccount.class, id);
		
		return em.find(Account.class, id);
	}

	public List<Account> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("select a from Account a " + where + " order by a.name");
		return query.getResultList();
	}
	
	public Account findRandomAssessmentCenter() {
		Query query = em.createQuery("SELECT a FROM Account a WHERE type = 'Assessment' ORDER BY RAND()");
		query.setMaxResults(1);
		
		return (Account) query.getSingleResult();
	}
	
	public List<Account> findViewableOperators(Permissions permissions) {
		String where = "AND a.status IN ('Active'";
		
		if (permissions.isAdmin() || permissions.getAccountStatus().isDemo())
			where += ",'Demo'";

		if (permissions.isAdmin() || permissions.isAuditor())
			where += ",'Pending')";
		if (permissions.isContractor())
			where += ")";
		if (permissions.isOperatorCorporate())
			where += ",'Pending') AND a.id IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + ")";
		
		Query query = em.createQuery("FROM Account a WHERE type IN ('Admin','Corporate','Operator') " + where + " ORDER BY a.type, a.name");
		return query.getResultList();
	}
	
	public int findByID(String username) {
		Query query = em.createQuery("SELECT id FROM Account WHERE username='?'");
		query.setParameter(1, Utilities.escapeQuotes(username));
		return Integer.parseInt(query.getSingleResult().toString());
	}
	
	public List<Account> findSetWhere(String where, int limit) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		
		Query query = em.createQuery("FROM Account a " + where + " ORDER BY a.id");
		query.setMaxResults(limit);
		return query.getResultList();
	}	
}
