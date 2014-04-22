package com.picsauditing.authentication.dao;

import com.picsauditing.employeeguard.entities.EmailHash;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class EmailHashDAO {

	@PersistenceContext
	protected EntityManager em;

	public EmailHash find(final int id) {
		return em.find(EmailHash.class, id);
	}

	public EmailHash findByHash(final String hash) {
		TypedQuery<EmailHash> query = em.createQuery("FROM EmailHash eh " +
				"WHERE eh.hash = :hash", EmailHash.class);

		query.setParameter("hash", hash);

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public EmailHash save(EmailHash o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}

		em.flush();

		return o;
	}
}
