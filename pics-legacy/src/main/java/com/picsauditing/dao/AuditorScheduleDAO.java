package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditorSchedule;

@SuppressWarnings("unchecked")
public class AuditorScheduleDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public AuditorSchedule save(AuditorSchedule o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		AuditorSchedule row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(AuditorSchedule row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void refresh(AuditorSchedule schedule) {
		if (schedule != null && schedule.getId() > 0)
			em.refresh(schedule);
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