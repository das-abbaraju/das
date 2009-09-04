package com.picsauditing.dao;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditorAvailability;

@Transactional
@SuppressWarnings("unchecked")
public class AuditorAvailabilityDAO extends PicsDAO {
	public AuditorAvailability save(AuditorAvailability o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditorAvailability row = find(id);
		remove(row);
	}

	public void remove(AuditorAvailability row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public int removeAll() {
		Query q = em.createQuery("DELETE FROM AuditorAvailability");
		return q.executeUpdate();
	}

	public AuditorAvailability find(int id) {
		return em.find(AuditorAvailability.class, id);
	}
}
