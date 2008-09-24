package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.UserLoginLog;

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
	
	@SuppressWarnings("unchecked")
	public List<EmailTemplate> findByAccountID(int accountID) {
		Query query = em.createQuery("FROM EmailTemplate WHERE accountID=?");
		query.setParameter(1, accountID);
		List<EmailTemplate> list = query.getResultList();
		return list;
	}
}
