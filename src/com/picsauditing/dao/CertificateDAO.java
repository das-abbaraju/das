package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Certificate;

@Transactional
public class CertificateDAO extends PicsDAO {

	public Certificate find(String id) {
		return em.find(Certificate.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Certificate> findByConId(int conID) {
		Query q = em.createQuery("SELECT c FROM certificate WHERE contractor.id = ?");

		q.setParameter(1, conID);
		
		return q.getResultList();
	}
}
