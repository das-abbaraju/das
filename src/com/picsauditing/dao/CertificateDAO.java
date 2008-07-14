package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Certificate;

@Transactional
@SuppressWarnings("unchecked")
public class CertificateDAO extends PicsDAO {
	public Certificate save(Certificate o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Certificate row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public Certificate find(int id) {
		Certificate a = em.find(Certificate.class, id);
		return a;
	}

}
