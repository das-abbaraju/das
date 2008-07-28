package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

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

	public List<Certificate> findExpiredCertificate() {
		Query query = em.createQuery("SELECT cr FROM Certificate cr WHERE"
				+ " (cr.lastSentDate < DATE_SUB(now(), INTERVAL 21 DAY) OR cr.lastSentDate IS NULL) AND"
				+ "(cr.expiration BETWEEN DATE_ADD(NOW(), INTERVAL -35 DAY) AND DATE_ADD(NOW(), INTERVAL 14 DAY))");
		return query.getResultList();

	}

}
