package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;

@Transactional
@SuppressWarnings("unchecked")
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

	public List<EmailSubscription> findByUserId(int userID) {
		Query q = em.createQuery("SELECT e FROM EmailSubscription e WHERE e.user.id = ?");
		q.setParameter(1, userID);
		return q.getResultList();
	}

	public List<User> findUsersBySubscription(Subscription subscription) {
		Query query = em.createQuery("SELECT es.user FROM EmailSubscription es WHERE es.subscription = :sub");
		query.setParameter("sub", subscription);

		return query.getResultList();
	}

	public List<EmailSubscription> find(Subscription subscription, SubscriptionTimePeriod timePeriod) {
		Query query = em
				.createQuery("FROM EmailSubscription WHERE subscription = :sub AND timePeriod = :time AND user.account.status = 'Active'");
		query.setParameter("sub", subscription);
		query.setParameter("time", timePeriod);

		return query.getResultList();
	}

	public List<EmailSubscription> find(Subscription subscription, SubscriptionTimePeriod timePeriod, int opID) {
		Query query = em
				.createQuery("FROM EmailSubscription WHERE subscription = :sub AND timePeriod = :time AND user.account.id = :opID AND user.account.status = 'Active'");
		query.setParameter("sub", subscription);
		query.setParameter("time", timePeriod);
		query.setParameter("opID", opID);

		return query.getResultList();
	}

}
