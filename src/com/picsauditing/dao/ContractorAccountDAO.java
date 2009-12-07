package com.picsauditing.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
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
		String filename = "brochure_" + row.getId() + "." + row.getBrochureFile();
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

	public List<ContractorAccount> findWhere(String where, Object... params) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT a from ContractorAccount a " + where + " ORDER BY a.name");
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
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
		for (ContractorOperator co : list)
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

	public ContractorAccount findTaxID(String taxId, String country) {
		if (taxId == null)
			taxId = "";
		try {
			Query query = em.createQuery("SELECT a FROM ContractorAccount a WHERE taxId = :taxId AND country = :country");
			query.setParameter("taxId", taxId);
			query.setParameter("country", country);
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
		if (Strings.isEmpty(name))
			return null;

		try {
			Query query;

			query = em.createQuery("SELECT a FROM ContractorAccount a WHERE a.name = ?");
			query.setParameter(1, name);

			return (ContractorAccount) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Find ids for all active contractors who either need recalculation but
	 * haven't been calculated in the past 30 minutes or haven't been calculated
	 * in the past week
	 * 
	 * @return
	 */
	public List<Integer> findContractorsNeedingRecalculation() {
		String hql = "SELECT c.id FROM ContractorAccount c WHERE c.active = 'Y' AND ("
				+ "c.lastRecalculation < :lastRunDate " + "OR c.lastRecalculation IS NULL) "
				+ "ORDER BY c.needsRecalculation DESC, c.lastRecalculation";
		Query query = em.createQuery(hql);
		query.setMaxResults(10);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -10);
		query.setParameter("lastRunDate", calendar.getTime());

		return query.getResultList();
	}

	public void updateContractorByOperator(OperatorAccount operator) {
		String subSelect = "";
		if (operator.isOperator())
			subSelect += "SELECT gc.subID FROM generalcontractors gc WHERE gc.genID = " + operator.getId();
		else if (operator.isCorporate())
			subSelect += "SELECT gc.subID FROM generalcontractors gc JOIN facilities f on gc.genID = f.opID WHERE f.corporateID = "
					+ operator.getId();
		else
			return;

		String sql = "UPDATE contractor_info SET needsRecalculation = 1 " + "WHERE id IN (" + subSelect + ")";

		Query query = em.createNativeQuery(sql);
		query.executeUpdate();
	}

	public int findContractorsNeedingRecalculation(OperatorAccount operator) {
		String subSelect = "";
		if (operator.isOperator())
			subSelect += "SELECT gc.subID FROM generalcontractors gc WHERE gc.genID = " + operator.getId();
		else if (operator.isCorporate())
			subSelect += "SELECT gc.subID FROM generalcontractors gc JOIN facilities f on gc.genID = f.opID WHERE f.corporateID = "
					+ operator.getId();
		else
			return 0;

		String sql = "SELECT count(*) total FROM contractor_info " + "WHERE needsRecalculation = 1 AND id IN ("
				+ subSelect + ")";

		Query query = em.createNativeQuery(sql);
		Object result = query.getSingleResult();
		return Integer.parseInt(result.toString());
	}

	public List<ContractorAccount> findNewContractorsByOperator(int opID, Date start, Date end) {
		String query = "SELECT co.contractorAccount FROM ContractorOperator co WHERE co.operatorAccount.id = "
				+ ":opID AND co.contractorAccount.creationDate BETWEEN :start and :end AND co.contractorAccount.active = 'Y'";

		Query q = em.createQuery(query);
		q.setParameter("opID", opID);
		q.setParameter("start", start, TemporalType.TIMESTAMP);
		q.setParameter("end", end, TemporalType.TIMESTAMP);

		return q.getResultList();
	}

	public List<Invoice> findDelinquentContractors() {
		List<String> dates = new ArrayList<String>();
		Calendar calendar1 = Calendar.getInstance();
		SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");

		calendar1.add(Calendar.WEEK_OF_YEAR, 1);
		dates.add(DBFormat.format(calendar1.getTime()));// Before7Days

		calendar1.add(Calendar.DATE, -12);
		dates.add(DBFormat.format(calendar1.getTime()));// Before5Days

		calendar1.add(Calendar.MONTH, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// After30Days

		calendar1.add(Calendar.MONTH, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// After60Days

		calendar1.add(Calendar.MONTH, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// After90Days

		calendar1.add(Calendar.MONTH, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// After120Days

		calendar1.add(Calendar.MONTH, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// After150Days

		calendar1.add(Calendar.MONTH, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// After180Days

		String hql = "FROM Invoice i WHERE i.status = 'Unpaid' AND i.totalAmount > 0" + " AND i.dueDate IN ("
				+ Strings.implodeForDB(dates, ",") + ") " + " AND i.account.active = 'Y' ORDER BY i.dueDate ";
		Query query = em.createQuery(hql);
		return query.getResultList();
	}

	public List<ContractorAccount> findBidOnlyContractors() {
		List<String> dates = new ArrayList<String>();
		Calendar calendar1 = Calendar.getInstance();
		SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");

		calendar1.add(Calendar.WEEK_OF_YEAR, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// Before7Days

		calendar1.add(Calendar.WEEK_OF_YEAR, -1);
		dates.add(DBFormat.format(calendar1.getTime()));// Before14Days

		String hql = "SELECT c FROM ContractorAccount c WHERE c.active = 'Y' AND c.acceptsBids = 1 AND "
				+ " c.paymentExpires IN (" + Strings.implodeForDB(dates, ",") + ")";

		Query query = em.createQuery(hql);
		return query.getResultList();
	}
}
