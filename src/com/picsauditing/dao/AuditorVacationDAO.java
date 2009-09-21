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
		String query = "SELECT t FROM AuditorVacation t " + "WHERE";
		if (auditorID == 0)
			query += " t.user IS NULL";
		else
			query += " t.user.id = :auditorID";
		query += " AND startDate BETWEEN :start AND :end ORDER BY startDate";
		Query q = em.createQuery(query);
		if (auditorID > 0)
			q.setParameter("auditorID", auditorID);
		q.setParameter("start", start, TemporalType.TIMESTAMP);
		q.setParameter("end", end, TemporalType.TIMESTAMP);
		return q.getResultList();
	}
}
