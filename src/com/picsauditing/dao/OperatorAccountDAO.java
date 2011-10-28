package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class OperatorAccountDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public OperatorAccount save(OperatorAccount o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OperatorAccount row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(OperatorAccount row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public OperatorAccount find(int id) {
		return em.find(OperatorAccount.class, id);
	}

	/**
	 * Return a list of Operators and Corporates if necessary Depending on who
	 * is asking (permissions), we may need to the return the Corporate list in
	 * a special way
	 * 
	 * @param includeCorporate
	 * @param where
	 * @param permissions
	 * @return
	 */
	public List<OperatorAccount> findWhere(boolean includeCorporate, String where, Permissions permissions) {
		// Get a list of corporate accounts if this a Corporate or Operator
		// account
		List<OperatorAccount> corporateList = new ArrayList<OperatorAccount>();
		if (includeCorporate) {
			if (permissions.isCorporate()) {
				Query query = em.createQuery("SELECT a FROM OperatorAccount a where a.id = :id");
				query.setParameter("id", permissions.getAccountId());
				corporateList = query.getResultList();
			}
			if (permissions.isOperator()) {
				Query query = em.createQuery("select a.corporate from Facility a where a.operator.id = :id");
				query.setParameter("id", permissions.getAccountId());
				corporateList = query.getResultList();
			}
		}

		// Now get the operator list
		if (where == null)
			where = "";

		if (where.length() > 0)
			where += " AND ";

		where += "a.status IN ('Active'";
		if (permissions.isAdmin())
			where += ",'Pending'";
		if (permissions.isAdmin() || permissions.getAccountStatus().isDemo())
			where += ",'Demo'";
		where += ") ";

		if (permissions.isCorporate()) {
			// Show corporate users operators in their facility
			where += "AND a IN (SELECT operator FROM Facility " + "WHERE corporate = " + permissions.getAccountId()
					+ ")";
			includeCorporate = false; // don't use the default findWhere to get
										// corporates
		}
		if (permissions.isOperator()) {
			// Show operator users operators that share the same corporate
			// facility
			where += "AND (a.id = " + permissions.getAccountId() + " OR a IN (SELECT operator FROM Facility "
					+ "WHERE corporate IN (SELECT corporate FROM Facility " + "WHERE operator.id = "
					+ permissions.getAccountId() + " AND corporate.id NOT IN ("
					+ Strings.implode(Account.PICS_CORPORATE, ",") + ") )))";
			includeCorporate = false; // don't use the default findWhere to get
										// corporates
		}
		List<OperatorAccount> operatorList = findWhere(includeCorporate, where);

		if (corporateList.size() > 0) {
			corporateList.addAll(operatorList);
			return corporateList;
		}

		return operatorList;
	}

	/**
	 * Alias a
	 * @param includeCorporate
	 * @param where
	 * @return
	 */
	public List<BasicDynaBean> findWhereNatively(boolean includeCorporate, String where) {
		SelectAccount select = new SelectAccount();
		if (includeCorporate)
			select.addWhere("a.type IN ('Operator','Corporate')");
		else
			select.addWhere("a.type = 'Operator'");

		select.addWhere(where);
		select.addOrderBy("a.name");
		

		try {
			Database db = new Database();
			return db.select(select.toString(), false);
		} catch (SQLException e) {
			return null;
		}
	}

	public List<OperatorAccount> findWhere(boolean includeCorporate, String where) {
		return findWhere(includeCorporate, where, 0);
	}

	public List<OperatorAccount> findWhere(boolean includeCorporate, String where, int maxResults) {

		if (where == null)
			where = "";

		if (includeCorporate == false)
			where = "a.type = 'Operator'" + ((where.length() > 0) ? " AND " + where : "");

		if (where.length() > 0)
			where = "WHERE " + where;

		Query query = em.createQuery("select a from OperatorAccount a " + where + " order by a.type, a.name");

		if (maxResults > 0)
			query.setMaxResults(maxResults);

		return query.getResultList();
	}

	public List<OperatorAccount> findOperators(List<Integer> opIds) {

		Query query = em.createQuery("select a from OperatorAccount a where a.id in (" + Strings.implode(opIds)
				+ ") order by a.type, a.name");

		return query.getResultList();
	}

	public int getContractorCount(int id, Permissions permissions) {
		Account operator = find(id);
		String where;

		if (operator.getType().equals("Corporate")) {
			where = "operatorAccount IN (SELECT operator FROM Facility WHERE corporate = ?)";
		} else {
			where = "operatorAccount = ?";
		}

		if (permissions.isApprovesRelationships() && !permissions.hasPermission(OpPerms.ViewUnApproved)) {
			where += " AND (operatorAccount.approvesRelationships = 'No' OR workStatus = 'Y')";
		}

		Query query = em
				.createQuery("SELECT count(c) FROM ContractorAccount c "
						+ "WHERE c.status "
						+ (permissions.getAccountStatus().equals(AccountStatus.Demo) ? "IN ('Active', 'Demo') "
								: "= 'Active' ") + "AND c IN (SELECT contractorAccount FROM ContractorOperator WHERE "
						+ where + ")");
		query.setParameter(1, operator);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public int getOperatorCounts(String where) {
		Query query = em
				.createQuery("SELECT count(o) FROM OperatorAccount o WHERE (o.status = 'Active' OR o.status = 'Demo') AND "
						+ where);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public boolean removeAllByOpID(OperatorAccount operatorAccount, String ftpDir) {
		int opID = operatorAccount.getId();

		Query query = em.createQuery("SELECT count(*) FROM ContractorAudit ca WHERE ca.requestingOpAccount = ?");
		query.setParameter(1, operatorAccount);
		if (Integer.parseInt(query.getSingleResult().toString()) > 0)
			return false;

		query = em.createQuery("SELECT count(*) FROM ContractorOperator co WHERE co.operatorAccount = ?");
		query.setParameter(1, operatorAccount);
		if (Integer.parseInt(query.getSingleResult().toString()) > 0)
			return false;

		query = em.createQuery("SELECT count(*) FROM Facility f WHERE f.corporate.id = " + opID);
		if (Integer.parseInt(query.getSingleResult().toString()) > 0)
			return false;

		query = em.createQuery("DELETE FROM AuditCatOperator ao WHERE ao.operatorAccount.id = " + opID);
		query.executeUpdate();
		query = em.createQuery("DELETE FROM AuditQuestionOperatorAccount aq WHERE aq.operatorAccount.id = " + opID);
		query.executeUpdate();
		query = em.createQuery("DELETE FROM Facility f WHERE f.operator.id = " + opID);

		OperatorFormDAO operatorFormDAO = (OperatorFormDAO) SpringUtils.getBean("OperatorFormDAO");
		if (!operatorFormDAO.deleteOperatorForms(opID, ftpDir))
			return false;

		remove(operatorAccount);
		return true;
	}

	public List<OperatorAccount> findInheritOperators(String field) {

		Query query = em.createQuery("select DISTINCT " + field + " from OperatorAccount a order by a.name");

		return query.getResultList();
	}

	public void incrementContractors(int id) {
		Query q = em.createNativeQuery("UPDATE contractor_info " +
										"SET    needsRecalculation = IF(needsRecalculation + 1 < 127, needsRecalculation + 1, 127) " +
										"WHERE  id IN (SELECT gc.subID " +
										              "FROM   generalcontractors gc JOIN accounts a ON a.id = gc.subID " +
										              "WHERE  gc.genID = " + id + " AND a.status = 'Active')");
		q.executeUpdate();
	}
}
