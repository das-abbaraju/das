package com.picsauditing.dao;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditData;
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

	public List<Certificate> findAll() {
		Query q = em.createQuery("from Certificate");
		return q.getResultList();
	}
	
	/**
	 * This is for getting a list of certificates that we need to send emails on.
	 * The final result of all of this logic below is that we send emails:
	 * 14 days before it expires
	 * 7 days after it expires
	 * 28 days after it expires
	 * for a total of 3 emails
	 * @return
	 */
	public List<Certificate> findExpiredCertificate() {
		Query query = em.createQuery("SELECT cr FROM Certificate cr WHERE"
				+ " (cr.lastSentDate < :Before21Days OR cr.lastSentDate IS NULL) AND"
				+ "(cr.expiration BETWEEN :Before35Days AND :After14Days)");
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(calendar1.WEEK_OF_YEAR, -3);
		query.setParameter("Before21Days", calendar1.getTime());
		calendar1.add(calendar1.WEEK_OF_YEAR, -2);
		query.setParameter("Before35Days", calendar1.getTime());
		calendar1.add(calendar1.WEEK_OF_YEAR, 7);
		query.setParameter("After14Days", calendar1.getTime());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public AuditData findDataIdGivenACertId(int certId) {
		
		StringBuilder query = new StringBuilder( " select pd.* " );
		
			Query qry = em.createNativeQuery( query.toString(), AuditData.class );
		qry.setParameter(1, certId);
		return ( AuditData ) qry.getSingleResult();
	
	}

	
}
