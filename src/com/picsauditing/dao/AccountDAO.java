package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class AccountDAO extends PicsDAO {
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
	
	public List<Account> findNoteRestrictionOperators(Permissions permissions) {
		String where = "AND a.status IN ('Active'";
		boolean needsClosingParen = true;
		
		if (permissions.isAdmin() || permissions.getAccountStatus().isDemo())
			where += ",'Demo'";

		if (permissions.isAdmin() || permissions.isAuditor())
			where += ",'Pending'";
		if (permissions.isOperatorCorporate()) {
			needsClosingParen = false;
			where += ",'Pending') AND a.id IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + ")";
		}
		if (needsClosingParen)
			where += ")";

		Query query;
		
		query = em.createQuery("FROM Account a WHERE (type IN ('Admin','Corporate') OR (type='Operator' AND (parent.id is NULL OR parent.id IN ("
						+ Strings.implode(Account.PICS_CORPORATE, ",") + ")))) AND a.id!=10403 " + where + " ORDER BY a.name");
		
		return query.getResultList();
	}
	
	public int findByID(String username) {
		Query query = em.createQuery("SELECT id FROM Account WHERE username='?'");
		query.setParameter(1, Strings.escapeQuotes(username));
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
