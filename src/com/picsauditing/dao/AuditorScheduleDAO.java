package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditorSchedule;

@SuppressWarnings("unchecked")
@Transactional
public class AuditorScheduleDAO extends PicsDAO {
	public AuditorSchedule save(AuditorSchedule o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditorSchedule row = find(id);
		remove(row);
	}

	public void remove(AuditorSchedule row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditorSchedule find(int id) {
		return em.find(AuditorSchedule.class, id);
	}

	public List<AuditorSchedule> findByAuditorID(int userID) {
		Query query = em.createQuery("SELECT t FROM AuditorSchedule t WHERE t.user.id = ? ORDER BY weekDay, startTime");
		query.setParameter(1, userID);
		return query.getResultList();
	}

	public List<AuditorSchedule> findAll() {
		Query query = em.createQuery("SELECT t FROM AuditorSchedule t");
		return query.getResultList();
	}
}
