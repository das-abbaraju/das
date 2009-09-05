package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditorVacation;

@Transactional
@SuppressWarnings("unchecked")
public class AuditorVacationDAO extends PicsDAO {
	public AuditorVacation save(AuditorVacation o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditorVacation row = find(id);
		remove(row);
	}

	public void remove(AuditorVacation row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditorVacation find(int id) {
		return em.find(AuditorVacation.class, id);
	}

	public List<AuditorVacation> findAll() {
		Query query = em.createQuery("SELECT t FROM AuditorVacation t");
		return query.getResultList();
	}
	
	public List<AuditorVacation> findByAuditorID(int auditorID) {
		Query query = em.createQuery("SELECT t FROM AuditorVacation t " +
				"WHERE t.user.id = ? OR t.user IS NULL ORDER BY startDate");
		query.setParameter(1, auditorID);
		return query.getResultList();
	}
}
