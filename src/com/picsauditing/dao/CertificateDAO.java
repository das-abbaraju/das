package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;

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

	@SuppressWarnings("unchecked")
	public Map<ContractorAccount, List<Certificate>> findConCertMap(String fileHash) {
		Query q = em.createQuery("FROM Certificate WHERE fileHash = :fileHash ORDER BY contractor.id, fileHash");
		q.setParameter("fileHash", fileHash);

		Map<ContractorAccount, List<Certificate>> conCertMap = new HashMap<ContractorAccount, List<Certificate>>();
		List<Certificate> certificates = q.getResultList();
		for (Certificate c : certificates) {
			if (conCertMap.get(c.getContractor()) == null)
				conCertMap.put(c.getContractor(), new ArrayList<Certificate>());
			
			conCertMap.get(c.getContractor()).add(c);
		}
		
		return conCertMap;
	}

	public Certificate findByFileHash(String fileHash, int conID) {
		Query q = em.createQuery("FROM Certificate WHERE fileHash = :fileHash AND contractor.id = :conID");
		q.setParameter("fileHash", fileHash);
		q.setParameter("conID", conID);

		try {
			return (Certificate) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Certificate> findWhere(String where) {
		String query = "FROM Certificate c WHERE " + where;

		Query q = em.createQuery(query);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Certificate> findWhere(String where, int limit) {
		String query = "FROM Certificate c WHERE " + where;

		Query q = em.createQuery(query);
		q.setMaxResults(limit);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> findDupeHashes(int limit) {
		Query q = em
				.createQuery("SELECT DISTINCT fileHash FROM Certificate WHERE fileHash IS NOT NULL GROUP BY fileHash, contractor.id HAVING COUNT(*) > 1");
		q.setMaxResults(limit);
		return q.getResultList();
	}
}
