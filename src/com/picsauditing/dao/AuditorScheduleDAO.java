package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditorSchedule;

@Transactional
@SuppressWarnings("unchecked")
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
}
