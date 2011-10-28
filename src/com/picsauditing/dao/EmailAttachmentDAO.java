package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailAttachment;

@SuppressWarnings("unchecked")
public class EmailAttachmentDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public EmailAttachment save(EmailAttachment o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		EmailAttachment row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(EmailAttachment row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public EmailAttachment find(int id) {
		return em.find(EmailAttachment.class, id);
	}
	
	
	public List<EmailAttachment> findByEmailID(int emailID) {
		Query query = em.createQuery("SELECT e from EmailAttachment e WHERE emailQueue.id = ?");
		query.setParameter(1, emailID);
		List<EmailAttachment> list = query.getResultList();
		
		return list;
	}
}
