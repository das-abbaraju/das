package com.picsauditing.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ContractorAccountDAO extends PicsDAO {
	private final Logger logger = LoggerFactory.getLogger(ContractorAccountDAO.class);
	
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
		// I'm not sure we actually want to do this very often at all, I want to
		// know how long it takes
		long start = new Date().getTime();
		Query query = em.createQuery("SELECT id ContractorAccount WHERE status IN ('Active','Demo')");
		List<Integer> list = query.getResultList();
		long elapsed = new Date().getTime() - start;
		logger.debug("ContractorAccountDAO.findAll() found {} contractors ids in {} ms", list.size(), elapsed);
		return list;
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

	/**
	 * Return a list of Contractors
	 * 
	 * @param where
	 * @param permissions
	 * @return
	 */
	public List<ContractorAccount> findWhere(String where, Permissions permissions) {
		// Now get the contractor list
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

		where += "AND (a.id = " + permissions.getAccountId() + " )";

		List<ContractorAccount> contractorList = findWhere(where);

		return contractorList;
	}

	/**
	 * Alias a
	 * 
	 * @param includeCorporate
	 * @param where
	 * @return
	 */
	public List<BasicDynaBean> findWhereNatively(boolean includeCorporate, String where) {
		SelectAccount select = new SelectAccount();
		select.addWhere("a.type = 'Contractor'");
		select.addWhere(where);
		select.addOrderBy("a.name");

		try {
			Database db = new Database();
			return db.select(select.toString(), false);
		} catch (SQLException e) {
			return null;
		}
	}

	public List<ContractorOperator> findOperators(ContractorAccount contractor, Permissions permissions, String where) {
		if (where == null)
			where = "";

		if (permissions.isGeneralContractor()) {
			// Get gc Contractor's operators
			where += " AND operatorAccount IN (SELECT co1.operatorAccount FROM ContractorOperator co1 WHERE co1.contractorAccount = "
					+ "(SELECT co2.contractorAccount FROM ContractorOperator co2 WHERE co2.operatorAccount.id = "
					+ permissions.getAccountId() + " AND co2.type = 'GeneralContractor') AND co1.operatorAccount.type = 'Operator')";
		} else {
			if (permissions.isCorporate())
				// Show corporate users operators in their facility
				where += " AND operatorAccount IN (SELECT operator FROM Facility " + "WHERE corporate = "
						+ permissions.getAccountId() + ")";
			if (permissions.isOperator()) {
				// Show operator users operators that share the same corporate
				// facility
				where += " AND (operatorAccount.id = " + permissions.getAccountId()
						+ " OR operatorAccount IN (SELECT operator FROM Facility "
						+ "WHERE corporate IN (SELECT corporate FROM Facility " + "WHERE operator.id = "
						+ permissions.getAccountId() + " AND corporate.id NOT IN ( "
						+ Strings.implode(Account.PICS_CORPORATE, ",") + "))))";
			}
		}

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
		String q = "SELECT a.id from ContractorAccount a WHERE a IN"
				+ " (SELECT co.contractorAccount FROM ContractorOperator co WHERE co.operatorAccount = ? AND co.flagColor IN ('Red', 'Amber'))";
		Query query = em.createQuery(q);
		query.setParameter(1, opAccount);

		return query.getResultList();
	}

	public List<ContractorAccount> findNewContractors(Permissions permissions, int limit) {
		if (permissions == null)
			return new ArrayList<ContractorAccount>();

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
		String where = "1=1 ";
		if (permissions.hasGroup(User.GROUP_CSR)) {
			where = " auditor.id = " + permissions.getShadowedUserID();
		}
		String hql = "FROM ContractorAccount contractorAccount WHERE " + where + " " + qb.toString()
				+ " ORDER BY creationDate DESC";
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public ContractorAccount findTaxID(String taxId, String country) {
		if (taxId == null)
			taxId = "";
		try {
			Query query = em
					.createQuery("SELECT a FROM ContractorAccount a WHERE taxId LIKE :taxId AND country.isoCode = :country");
			query.setParameter("taxId", taxId + "%");
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
	 * Find ids for all active contractors who either need recalculation but haven't been calculated in the past 15
	 * minutes or haven't been calculated in the past week
	 * 
	 * @return
	 */
	public List<Integer> findContractorsNeedingRecalculation(int limit, Set<Integer> contractorsToIgnore) {
		String hql = "SELECT c.id FROM ContractorAccount c WHERE c.status IN ('Active','Pending','Demo') AND ("
				+ "c.lastRecalculation < :lastRunDate OR c.lastRecalculation IS NULL)";
		if (contractorsToIgnore.size() > 0)
			hql += " AND c.id NOT IN (" + Strings.implode(contractorsToIgnore) + ")";
		hql += " ORDER BY c.needsRecalculation DESC, c.lastRecalculation";
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -15);
		query.setParameter("lastRunDate", calendar.getTime());

		return query.getResultList();
	}

	public long findNumberOfContractorsNeedingRecalculation() {
		String hql = "SELECT COUNT(*) FROM ContractorAccount c "
				+ "WHERE c.status IN ('Active','Pending','Demo') AND c.needsRecalculation > 0";
		Query query = em.createQuery(hql);

		return (Long) query.getSingleResult();
	}

	public long findNumberOfContractorsProcessed(int timePeriodInMinutes) {
		String hql = "SELECT COUNT(*) FROM ContractorAccount c WHERE " + "c.lastRecalculation >= :lastRunDate ";
		Query query = em.createQuery(hql);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -timePeriodInMinutes);
		query.setParameter("lastRunDate", calendar.getTime());

		return (Long) query.getSingleResult();
	}

	@Transactional(propagation = Propagation.NESTED)
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
				+ ":opID AND co.contractorAccount.creationDate BETWEEN :start and :end AND co.contractorAccount.status IN ('Active') "
				+ "ORDER BY co.operatorAccount.name ASC";

		Query q = em.createQuery(query);
		q.setParameter("opID", opID);
		q.setParameter("start", start, TemporalType.TIMESTAMP);
		q.setParameter("end", end, TemporalType.TIMESTAMP);

		return q.getResultList();
	}

	public List<ContractorAccount> findRecentlyAddedContractorsByOperator(int opID, Date start, Date end) {
		String query = "SELECT co.contractorAccount FROM ContractorOperator co WHERE co.operatorAccount.id = "
				+ ":opID AND co.creationDate BETWEEN :start and :end AND co.contractorAccount.status IN ('Active')";

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
				+ Strings.implodeForDB(dates, ",") + ") " + " AND i.account.status IN ('Active') ORDER BY i.dueDate ";
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

		String hql = "SELECT c FROM ContractorAccount c WHERE c.status = 'Active' AND c.accountLevel = 'BidOnly' AND "
				+ " c.paymentExpires IN (" + Strings.implodeForDB(dates, ",") + ")";

		Query query = em.createQuery(hql);
		return query.getResultList();
	}

	public List<ContractorAccount> findByContractorIds(Set<Integer> conIDs) {
		if (conIDs == null || conIDs.size() == 0)
			return new ArrayList<ContractorAccount>();

		String ids = Strings.implodeForDB(conIDs, ",");
		Query query = em.createQuery("SELECT a FROM ContractorAccount a WHERE a.id in (" + ids + ")");
		return query.getResultList();
	}

	public List<ContractorAccount> findPendingAccounts(String whereClause) {
		String sql = "SELECT * FROM accounts a " + "JOIN contractor_info c ON a.id = c.id "
				+ "JOIN users u ON a.contactID = u.id " + "WHERE a.status = 'Pending' AND " + whereClause;
		Query query = em.createNativeQuery(sql, ContractorAccount.class);
		return query.getResultList();
	}

	public List<OperatorAccount> findPicsCountryCorporates(int conID) {
		// finds all PICS country based corporates (excludes PICS Global and PICS PSM)
		String sql = "select a.*, o.* from generalcontractors gc join accounts a on a.id = gc.genID join operators o ON o.id = a.id "
				+ "where gc.subID = " + conID + " and a.name like 'PICS%' and a.type = 'Corporate' "
				+ "and a.id not in (4,8);";
		Query q = em.createNativeQuery(sql, OperatorAccount.class);
		return q.getResultList();
	}
}
