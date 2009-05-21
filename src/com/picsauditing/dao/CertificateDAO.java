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
	public List<Certificate> findByConId(int conID, Permissions permissions) {
		String query = "SELECT c FROM Certificate c WHERE c.contractor.id = ? ";
		if(permissions.isOperatorCorporate())
			query += " c.createdBy.id = "+ permissions.getUserId();
		
		Query q = em.createQuery(query);

		q.setParameter(1, conID);

		return q.getResultList();
	}
}
