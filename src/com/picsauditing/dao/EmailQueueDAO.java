package com.picsauditing.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class EmailQueueDAO extends PicsDAO {
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

	public List<String> findPendingActivationEmails(String timeframe) {
		String sql = "SELECT DISTINCT toAddresses "
				+ "FROM email_queue eq "
				+ "WHERE eq.subject IN ('Incomplete Registration','Incomplete Client Site Registration','Incomplete Registration Reminder',"
				+ "'Reminder: Client Site Registration Incomplete','Registration Will Be Deleted for PICS Operator',"
				+ "'Registration Will Be Deleted for Client Site','Pending PICS Account Closed') "
				+ "AND eq.creationDate > DATE_SUB(CURDATE(), INTERVAL " + timeframe + ")";
		Query query = em.createNativeQuery(sql);
		return query.getResultList();
	}
}
