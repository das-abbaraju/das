package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.ListType;

@SuppressWarnings("unchecked")
public class EmailTemplateDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public EmailTemplate save(EmailTemplate o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		EmailTemplate row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(EmailTemplate row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public EmailTemplate find(int id) {
		return em.find(EmailTemplate.class, id);
	}

	
	public List<EmailTemplate> findByAccountID(int accountID) {
		Query query = em.createQuery("FROM EmailTemplate WHERE accountID=? ORDER BY templateName");
		query.setParameter(1, accountID);
		return query.getResultList();
	}

	public List<EmailTemplate> findByAccountID(int accountID, ListType listType) {
		Query query = em.createQuery("FROM EmailTemplate WHERE accountID=? AND listType=? ORDER BY templateName");
		query.setParameter(1, accountID);
		query.setParameter(2, listType);
		return query.getResultList();
	}

	public List<EmailTemplate> findAll() {
		return (List<EmailTemplate>) super.findAll(EmailTemplate.class);
	}

}
