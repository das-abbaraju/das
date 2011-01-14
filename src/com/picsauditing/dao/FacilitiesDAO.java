package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class FacilitiesDAO extends PicsDAO {
	public Facility save(Facility o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		Facility row = find(id);
		remove(row);
	}

	public void remove(Facility row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public Facility find(int id) {
		return em.find(Facility.class, id);
	}

	public Facility findByCorpOp(int corpID, int opID) {
		try {
			Query query = em.createQuery("FROM Facility where corporate.id = ? and operator.id = ?");
			query.setParameter(1, corpID);
			query.setParameter(2, opID);
			return (Facility) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Facility> findSiblings(int opID) {
		Query query = em.createQuery("FROM Facility WHERE corporate.id IN (SELECT corporate.id FROM Facility "
				+ "WHERE operator.id = ?) AND corporate.id NOT IN (" + Strings.implode(Account.PICS_CORPORATE) + ")");
		query.setParameter(1, opID);

		return query.getResultList();
	}
}
