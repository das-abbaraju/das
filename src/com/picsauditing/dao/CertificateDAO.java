package com.picsauditing.dao;

import java.util.Calendar;
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
		Calendar calendar1 = Calendar.getInstance();
		Query query = em.createQuery("SELECT cr FROM Certificate cr WHERE"
				+ " (cr.lastSentDate < :Before21Days OR cr.lastSentDate IS NULL) AND"
				+ "(cr.expiration BETWEEN :Before35Days AND :After14Days)");
		calendar1.add(calendar1.WEEK_OF_YEAR, -3);
		query.setParameter("Before21Days", calendar1.getTime());
		calendar1.add(calendar1.WEEK_OF_YEAR, -2);
		query.setParameter("Before35Days", calendar1.getTime());
		calendar1.add(calendar1.WEEK_OF_YEAR, 7);
		query.setParameter("After14Days", calendar1.getTime());
		return query.getResultList();
	}
}
