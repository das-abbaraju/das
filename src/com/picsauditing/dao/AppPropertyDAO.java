package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AppProperty;

@Transactional
@SuppressWarnings("unchecked")
public class AppPropertyDAO extends PicsDAO {
	public AppProperty save(AppProperty o) {
		o = em.merge(o);
		return o;
	}

	public void remove(String property) {
		AppProperty row = find(property);
		if (row != null) {
			em.remove(row);
		}
	}

	public AppProperty find(String property) {
		AppProperty a = em.find(AppProperty.class, property);
		return a;
	}

}
