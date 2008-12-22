package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;

@Transactional
@SuppressWarnings("unchecked")
public class OperatorAccountDAO extends PicsDAO {
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

	public void remove(OperatorAccount row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public OperatorAccount find(int id) {
		return em.find(OperatorAccount.class, id);
	}

	public List<OperatorAccount> findWhere(boolean includeCorporate, String where, Permissions permissions) {
		List<OperatorAccount> corporateList = new ArrayList<OperatorAccount>();

		if (where == null)
			where = "";

		if (permissions.isCorporate()) {
			Query query = em.createQuery("SELECT a FROM OperatorAccount a where a.id = :id");
			query.setParameter("id", permissions.getAccountId());
			corporateList = query.getResultList();

			if (where.length() > 0)
				where += "AND ";

			// Show corporate users operators in their facility
			where += "a IN (SELECT operator FROM Facility " + "WHERE corporate = " + permissions.getAccountId() + ")";

			includeCorporate = false;
		}
		if (permissions.isOperator()) {
			Query query = em.createQuery("select a.corporate from Facility a where a.operator.id = :id");
			query.setParameter("id", permissions.getAccountId());
			corporateList = query.getResultList();

			// Show operator users operators that share the same corporate
			// facility
			if (where.length() > 0)
				where += "AND ";

			where += "(a.id = " + permissions.getAccountId() + " OR a IN (SELECT operator FROM Facility "
					+ "WHERE corporate IN (SELECT corporate FROM Facility " + "WHERE operator.id = "
					+ permissions.getAccountId() + ")))";

			includeCorporate = false;
		}
		List<OperatorAccount> operatorList = findWhere(includeCorporate, where);

		if (corporateList.size() > 0) {
			corporateList.addAll(operatorList);
			return corporateList;
		}

		return operatorList;
	}

	public List<OperatorAccount> findWhere(boolean includeCorporate, String where) {

		if (where == null)
			where = "";

		if (includeCorporate == false)
			where = "a.type = 'Operator'" + ((where.length() > 0) ? " AND " + where : "");

		if (where.length() > 0)
			where = "WHERE " + where;

		Query query = em.createQuery("select a from OperatorAccount a " + where + " order by a.type, a.name");

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
			where += " AND workStatus = 'Y'";
		}

		Query query = em.createQuery("SELECT count(c) FROM ContractorAccount c " + "WHERE c.active = 'Y' "
				+ "AND c IN (SELECT contractorAccount FROM ContractorOperator WHERE " + where + ")");
		query.setParameter(1, operator);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public int getOperatorCounts(String where) {
		Query query = em.createQuery("SELECT count(o) FROM OperatorAccount o WHERE o.active = 'Y' AND " + where);
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

		query = em.createQuery("DELETE FROM FlagQuestionCriteria fq WHERE fq.operatorAccount.id = " + opID);
		query.executeUpdate();
		query = em.createQuery("DELETE FROM FlagOshaCriteria fo WHERE fo.operatorAccount.id = " + opID);
		query.executeUpdate();
		query = em.createQuery("DELETE FROM ContractorOperatorFlag cf WHERE cf.operatorAccount.id = " + opID);
		query.executeUpdate();
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
}
