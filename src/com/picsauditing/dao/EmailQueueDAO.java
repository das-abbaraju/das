package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailQueue;

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

	@SuppressWarnings("unchecked")
	public List<EmailQueue> getPendingEmails(int limit) {
		return getPendingEmails("", limit);
	}

	@SuppressWarnings("unchecked")
	public List<EmailQueue> getPendingEmails(String where, int limit) {
		if (where == null)
			where = "1";
		where = "AND " + where;
		Query query = em.createQuery("FROM EmailQueue t WHERE t.status = 'Pending' " + where
				+ " ORDER BY t.priority DESC, t.id");
		query.setMaxResults(50);
		return query.getResultList();
	}
}
