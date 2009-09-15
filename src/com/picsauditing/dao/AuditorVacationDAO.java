package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

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
		Query query = em.createQuery("SELECT t FROM AuditorVacation t "
				+ "WHERE t.user.id = ? OR t.user IS NULL ORDER BY startDate");
		query.setParameter(1, auditorID);
		return query.getResultList();
	}

	public List<AuditorVacation> findByAuditorID(int auditorID, Date start, Date end) {
		Query query = em
				.createQuery("SELECT t FROM AuditorVacation t "
						+ "WHERE t.user.id = :auditorID OR t.user IS NULL AND startDate BETWEEN :start AND :end ORDER BY startDate");
		query.setParameter("auditorID", auditorID);
		query.setParameter("start", start, TemporalType.TIMESTAMP);
		query.setParameter("end", end, TemporalType.TIMESTAMP);
		return query.getResultList();
	}
}
