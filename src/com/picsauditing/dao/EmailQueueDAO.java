package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.search.Database;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class EmailQueueDAO extends PicsDAO {
	private Database database;

	@Transactional(propagation = Propagation.NESTED)
	public EmailQueue save(EmailQueue o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		EmailQueue row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(EmailQueue row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public EmailQueue find(int id) {
		return em.find(EmailQueue.class, id);
	}

	public List<EmailQueue> getPendingEmails(int limit) {
		return getPendingEmails("", limit);
	}

	public List<EmailQueue> getPendingEmails(String where, int limit) {
		if (!Strings.isEmpty(where))
			where = "AND " + where;
		Query query = em.createQuery("FROM EmailQueue t WHERE t.status = 'Pending' " + where
				+ " ORDER BY t.priority DESC, t.id");
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public EmailQueue getQuickbooksError() {
		Query query = em.createQuery("FROM EmailQueue t WHERE t.subject = 'QBWebConnector Errors'"
				+ " ORDER BY t.id DESC");
		query.setMaxResults(1);
		List<EmailQueue> list = query.getResultList();
		if (list == null || list.size() < 1)
			return null;
		return list.get(0);
	}

	public List<EmailQueue> findByContractorId(int id, Permissions permissions) {
		String permWhere;
		// Show the user's private notes
		permWhere = "(createdBy.id = " + permissions.getUserId() + " AND viewableBy.id = " + Account.PRIVATE + ")";
		// Show the note available to all users
		permWhere += " OR (viewableBy.id = " + Account.EVERYONE + ")";

		// Show intra-company notes users
		if (permissions.isOperatorCorporate())
			permWhere += " OR (viewableBy.id IN (" + Strings.implode(permissions.getVisibleAccounts(), ",") + "))";
		else
			permWhere += " OR (viewableBy IS NULL) OR (viewableBy.id > 2)";
		Query query = em.createQuery("FROM EmailQueue WHERE contractorAccount.id = :id AND (" + permWhere
				+ ") ORDER BY sentDate DESC");
		query.setMaxResults(25);
		query.setParameter("id", id);
		return query.getResultList();
	}

	public long findNumberOfEmailsSent(int timePeriodInMinutes) {
		String hql = "SELECT COUNT(*) FROM EmailQueue t WHERE t.sentDate > :lastSentDate";
		Query query = em.createQuery(hql);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -timePeriodInMinutes);
		query.setParameter("lastSentDate", calendar.getTime());

		return (Long) query.getSingleResult();
	}

	public long findNumberOfEmailsWithStatus(String status) {
		String hql = "SELECT COUNT(*) FROM EmailQueue t WHERE t.status = '" + status + "'";
		Query query = em.createQuery(hql);

		return (Long) query.getSingleResult();
	}

	public long findNumberOfEmailsWithStatusBeforeTime(String status, int creationTimeInMinutes) {
		String hql = "SELECT COUNT(*) FROM EmailQueue t WHERE t.status = '" + status + "'"
				+ "AND t.creationDate < :creationTime";
		Query query = em.createQuery(hql);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -creationTimeInMinutes);
		query.setParameter("creationTime", calendar.getTime());

		return (Long) query.getSingleResult();
	}

	public long findNumberOfEmailsWithStatusInTime(String status, int creationTimeInMinutes) {
		String hql = "SELECT COUNT(*) FROM EmailQueue t WHERE t.status = '" + status + "'"
				+ "AND t.creationDate >= :creationTime";
		Query query = em.createQuery(hql);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -creationTimeInMinutes);
		query.setParameter("creationTime", calendar.getTime());

		return (Long) query.getSingleResult();
	}

	public List<String> findPendingActivationEmails(String timeframe, int[] pendingEmailTemplates) {
		String sql = "SELECT DISTINCT toAddresses "
				+ "FROM email_queue eq "
				+ "WHERE eq.templateID IN (" + Strings.implode(pendingEmailTemplates) + ") "
				+ "AND eq.creationDate > DATE_SUB(CURDATE(), INTERVAL " + timeframe + ")";
		Query query = em.createNativeQuery(sql);
		return query.getResultList();
	}

	public List<String> findEmailAddressExclusions() {
		String sql = "SELECT DISTINCT email FROM email_exclusion ee ";
		Query query = em.createNativeQuery(sql);
		return query.getResultList();
	}

	public boolean findEmailAddressExclusionAlreadyExists(String email) {
		String sql = "SELECT DISTINCT email FROM email_exclusion ee WHERE email = '" + email + "'";
		Query query = em.createNativeQuery(sql);
		return (query.getResultList().size() > 0);
	}

	public void addEmailAddressExclusions(String email) throws SQLException {
		addEmailAddressExclusions(email, 1);
	}

	private Database database(){
		database = new Database();
		return database;
	}

	public void addEmailAddressExclusions(String email, int userID) throws SQLException {
		if (EmailAddressUtils.isValidEmail(email) && !findActiveUserEmail(email) && !findEmailAddressExclusionAlreadyExists(email)){
			String sql = "INSERT INTO email_exclusion (email,createdBy,creationDate,updatedBy,updateDate)" + " VALUES ('"
					+ email + "',"+ userID+", NOW(),"+ userID+", NOW())";
			database().executeInsert(sql);
		}
	}

	public void removeEmailAddressExclusions(String email) throws SQLException {
		if (EmailAddressUtils.isValidEmail(email)){
			String sql = "DELETE FROM email_exclusion " + " WHERE email = '" + email + "'";
			database().executeUpdate(sql);
		}
	}

	private boolean findActiveUserEmail(String email) throws SQLException{
		String sql = "SELECT DISTINCT email FROM users WHERE email = '" + email + "' and isActive='Yes' and isGroup='No'";
		Query query = em.createNativeQuery(sql);
		return (query.getResultList().size() > 0);
	}
}