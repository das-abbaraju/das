package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailTemplate;

@Transactional
public class EmailTemplateDAO extends PicsDAO {

	public EmailTemplate save(EmailTemplate o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		EmailTemplate row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public EmailTemplate find(int id) {
		return em.find(EmailTemplate.class, id);
	}
}
