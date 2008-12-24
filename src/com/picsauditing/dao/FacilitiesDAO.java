package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Facility;

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
}
