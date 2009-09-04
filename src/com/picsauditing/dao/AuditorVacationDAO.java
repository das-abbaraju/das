package com.picsauditing.dao;

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
}
