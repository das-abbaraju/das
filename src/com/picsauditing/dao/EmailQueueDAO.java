package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailQueue;

@Transactional
public class EmailQueueDAO extends PicsDAO {

	public EmailQueue save(EmailQueue o) {
		if (o.getEmailID() == 0) {
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
}
