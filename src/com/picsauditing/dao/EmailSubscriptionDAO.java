package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.search.SelectSQL;

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

	public List<EmailSubscription> findByAccountID(int accountID) {
		Query q = em.createQuery("SELECT e FROM EmailSubscription e WHERE e.user.account.id = ?");
		q.setParameter(1, accountID);
		return q.getResultList();
	}

	public List<EmailSubscription> findByUserId(int userID) {
		Query q = em.createQuery("SELECT e FROM EmailSubscription e WHERE e.user.id = ?");
		q.setParameter(1, userID);
		return q.getResultList();
	}

	public List<EmailSubscription> find(Subscription subscription, SubscriptionTimePeriod timePeriod) {
		Query query = em
				.createQuery("FROM EmailSubscription WHERE subscription = :sub AND timePeriod = :time AND user.account.status = 'Active' AND user.isActive = 'Yes'");
		query.setParameter("sub", subscription);
		query.setParameter("time", timePeriod);

		return query.getResultList();
	}

	public List<EmailSubscription> find(Subscription subscription, int opID) {
		Query query = em
				.createQuery("FROM EmailSubscription e WHERE  e.subscription = :sub AND e.user.account.id = :opID AND e.user.isActive = 'Yes'");
		query.setParameter("sub", subscription);
		query.setParameter("opID", opID);

		return query.getResultList();
	}

	public List<EmailSubscription> find(Subscription subscription, SubscriptionTimePeriod timePeriod, int opID) {
		Query query = em
				.createQuery("FROM EmailSubscription e WHERE e.subscription = :sub AND e.timePeriod = :time AND e.user.account.id = :opID AND e.user.account.status = 'Active' AND e.user.isActive = 'Yes'");
		query.setParameter("sub", subscription);
		query.setParameter("time", timePeriod);
		query.setParameter("opID", opID);

		return query.getResultList();
	}

	public List<Integer> findSubscriptionsToSend(int limit) {
		SelectSQL sql = new SelectSQL("email_subscription e");
		sql.addJoin("JOIN users u ON u.id = e.userID");
		sql.addJoin("JOIN accounts a ON a.id = u.accountID");
		sql.addWhere("u.isActive = 'Yes'");
		sql.addWhere("a.status = 'Active'");
		sql.addWhere("e.timePeriod NOT IN ('None', 'Event')");
		sql.addWhere("e.lastSent IS NULL OR e.lastSent < CASE e.timePeriod "
				+ "WHEN 'Daily' THEN DATE_SUB(:now, INTERVAL 1 DAY)"
				+ "WHEN 'Weekly' THEN DATE_SUB(:now, INTERVAL 1 WEEK)"
				+ "WHEN 'Monthly' THEN DATE_SUB(:now, INTERVAL 1 MONTH) END");
		sql.addOrderBy("e.lastSent");

		sql.addField("e.id");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("now", new Date(), TemporalType.TIMESTAMP);
		query.setMaxResults(limit);

		return query.getResultList();
	}
}
