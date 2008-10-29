package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailAttachment;

@Transactional
public class EmailAttachmentDAO extends PicsDAO {

	public EmailAttachment save(EmailAttachment o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		EmailAttachment row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(EmailAttachment row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public EmailAttachment find(int id) {
		return em.find(EmailAttachment.class, id);
	}
}
