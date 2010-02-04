package com.picsauditing.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.util.Strings;

@Transactional
public class EmailQueueDAO extends PicsDAO {

	public EmailQueue save(EmailQueue o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		EmailQueue row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

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

	@SuppressWarnings("unchecked")
	public List<EmailQueue> getPendingEmails(String where, int limit) {
		if (!Strings.isEmpty(where))
			where = "AND " + where;
		Query query = em.createQuery("FROM EmailQueue t WHERE t.status = 'Pending' " + where
				+ " ORDER BY t.priority DESC, t.id");
		query.setMaxResults(limit);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public EmailQueue getQuickbooksError() {
		Query query = em.createQuery("FROM EmailQueue t WHERE t.subject = 'QBWebConnector Errors'"
				+ " ORDER BY t.id DESC");
		query.setMaxResults(1);
		List<EmailQueue> list = query.getResultList();
		if (list == null || list.size() < 1)
			return null;
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<EmailQueue> findByContractorId(int id) {
		Query query = em.createQuery("FROM EmailQueue WHERE contractorAccount.id = :id" + " ORDER BY sentDate DESC");
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
		String hql = "SELECT COUNT(*) FROM EmailQueue t WHERE t.status = '"+status+"'";
		Query query = em.createQuery(hql);

		return (Long) query.getSingleResult();
	}

	public long findNumberOfEmailsWithStatusBeforeTime(String status, int creationTimeInMinutes) {
		String hql = "SELECT COUNT(*) FROM EmailQueue t WHERE t.status = '"+status+"'"
				+ "AND t.creationDate < :creationTime";
		Query query = em.createQuery(hql);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -creationTimeInMinutes);
		query.setParameter("creationTime", calendar.getTime());

		return (Long) query.getSingleResult();
	}
	
	public long findNumberOfEmailsWithStatusInTime(String status, int creationTimeInMinutes) {
		String hql = "SELECT COUNT(*) FROM EmailQueue t WHERE t.status = '"+status+"'"
				+ "AND t.creationDate >= :creationTime";
		Query query = em.createQuery(hql);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -creationTimeInMinutes);
		query.setParameter("creationTime", calendar.getTime());

		return (Long) query.getSingleResult();
	}
}
