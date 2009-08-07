package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Certificate;

@Transactional
public class CertificateDAO extends PicsDAO {

	public Certificate save(Certificate o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public Certificate find(int id) {
		return em.find(Certificate.class, id);
	}

	public void remove(int id) {
		Certificate row = find(id);
		if (row != null)
			remove(row);
	}

	public void remove(Certificate row) {
		if (row != null)
			em.remove(row);
	}

	@SuppressWarnings("unchecked")
	public List<Certificate> findByConId(int conID) {
		Query q = em.createQuery("SELECT c FROM Certificate c WHERE c.contractor.id = ? ");
		q.setParameter(1, conID);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Certificate> findByConId(int conID, Permissions permissions) {
		String query = "SELECT c FROM Certificate c WHERE c.contractor.id = ? ";
		if (permissions.isOperatorCorporate()) {
			query += " AND (c.createdBy = " + permissions.getUserId()
					+ " OR c IN (SELECT cao.certificate FROM c.caos cao WHERE cao.operator = "
					+ "(SELECT o.inheritInsurance FROM OperatorAccount o WHERE o.id = " + permissions.getAccountId()
					+ ")))";
		}
		Query q = em.createQuery(query);
		q.setParameter(1, conID);
		return q.getResultList();
	}

	public Certificate findByFileHash(String fileHash, int conID) {
		Query q = em.createQuery("FROM Certificate c WHERE fileHash = :fileHash AND contractor.id = :conID");
		q.setParameter("fileHash", fileHash);
		q.setParameter("conID", conID);
		return (Certificate) q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Certificate> findWhere(String where) {
		String query = "FROM Certificate c WHERE " + where;

		Query q = em.createQuery(query);

		return q.getResultList();
	}

	public List<Certificate> findWhere(String where, int limit) {
		return findWhere(where, limit, 0);
	}

	@SuppressWarnings("unchecked")
	public List<Certificate> findWhere(String where, int limit, int start) {
		String query = "FROM Certificate c WHERE " + where;

		Query q = em.createQuery(query);
		q.setMaxResults(limit);
		q.setFirstResult(start);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> findDupeHashes(int limit) {
		Query q = em
				.createQuery("SELECT fileHash FROM Certificate WHERE fileHash IS NOT NULL GROUP BY fileHash HAVING COUNT(*) > 1 ORDER BY fileHash");
		q.setMaxResults(limit);
		return q.getResultList();
	}
}
