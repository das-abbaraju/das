package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.email.Subscription;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;

@Transactional
public class EmailSubscriptionDAO extends PicsDAO {

	public EmailSubscription save(EmailSubscription o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public EmailSubscription find(int id) {
		return em.find(EmailSubscription.class, id);
	}

	public void remove(int id) {
		EmailSubscription row = find(id);
		if (row != null)
			remove(row);
	}

	public void remove(EmailSubscription row) {
		if (row != null)
			em.remove(row);
	}

	@SuppressWarnings("unchecked")
	public List<User> findUsersBySubscription(Subscription subscription) {
		Query query = em.createQuery("SELECT es.user FROM EmailSubscription es WHERE es.subscription = :sub");
		query.setParameter("sub", subscription);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<EmailSubscription> findBySubscription(Subscription subscription) {
		Query query = em.createQuery("FROM EmailSubscription es WHERE es.subscription = :sub");
		query.setParameter("sub", subscription);

		return query.getResultList();

	}
}
