package com.picsauditing.dao;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.mapper.AccountContactMapper;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.model.contractor.AccountContact;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unchecked")
public class AccountDAO extends PicsDAO {

    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);
    private static final String ACCOUNT_CONTACTS_BY_ROLE_SQL =
            "select u.id, u.name, u.email, u.phone, u.fax from users u " +
            "join useraccess ua on ua.userID = u.id " +
            "where ua.accessType = '%s' " +
            "and u.isActive = 'Yes' " +
            "and u.accountID = %d";

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

		query = em.createQuery("FROM Account a WHERE type IN ('Admin','Corporate', 'Operator') AND a.id!=10403 " + where + " ORDER BY a.name");

		return query.getResultList();
	}
	
	public int findByID(String username) {
		Query query = em.createQuery("SELECT id FROM Account WHERE username='?'");
		query.setParameter(1, Strings.escapeQuotesAndSlashes(username));
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

	public List<Account> findByIds(Collection<Integer> accountIds) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return Collections.emptyList();
        }

        TypedQuery<Account> query = em.createQuery("FROM Account a WHERE a.id IN ( :accountIds )", Account.class);
        query.setParameter("accountIds", accountIds);
		return query.getResultList();
	}

	public int findByUserID(int userID) {
		Query q = em.createNativeQuery("SELECT a.id FROM accounts a LEFT JOIN users u ON u.accountID = a.id WHERE u.id = :userID");
		q.setParameter("userID", userID);

		return (Integer) q.getSingleResult();
	}

    public Date findLastAccountLogin(int accountID) {
        Query q = em.createNativeQuery("SELECT MAX(u.lastLogin) FROM users u LEFT JOIN accounts a ON u.accountID = a.id WHERE a.id = :accountID");
        q.setParameter("accountID", accountID);

        return (Date) q.getSingleResult();
    }

    public List<AccountContact> findAccountContactsByRole(int accountID, OpPerms opPerms) {
        String sql = String.format(ACCOUNT_CONTACTS_BY_ROLE_SQL, opPerms.toString(), accountID);
        List<AccountContact> results = new ArrayList<>();
        Database db = new Database();
        try {
            results = db.select(sql, new AccountContactMapper());
        } catch (SQLException e) {
            logger.error("Unable to run the query {} for account contacts: {}", sql, e.getMessage());
        }
        return results;
    }

}
