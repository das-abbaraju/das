package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("unchecked")
public class EmailSubscriptionDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
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

	@Transactional(propagation = Propagation.NESTED)
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

    public List<EmailSubscription> findByUserIdReportId(int userID, int reportID) {
        Query q = em.createQuery("SELECT e FROM EmailSubscription e WHERE e.user.id = :userID AND e.report.id = :reportID");
        q.setParameter("userID", userID);
        q.setParameter("reportID", reportID);
        return q.getResultList();
    }

    public List<EmailSubscription> findByReportID(int reportID) {
        Query q = em.createQuery("SELECT e FROM EmailSubscription e WHERE e.report.id = :reportID");
        q.setParameter("reportID", reportID);
        return q.getResultList();
    }

	public List<EmailSubscription> find(Subscription subscription, int opID) {
		Query query = em
				.createQuery("FROM EmailSubscription e WHERE e.subscription = :sub AND e.user.account.id = :opID AND e.user.isActive = 'Yes'");
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
