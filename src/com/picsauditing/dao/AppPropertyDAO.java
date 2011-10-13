package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AppProperty;

@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class AppPropertyDAO extends PicsDAO {
	@Transactional
	public AppProperty save(AppProperty o) {
		o = em.merge(o);
		return o;
	}

	@Transactional
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

	public List<AppProperty> findAll() {
		Query query = em.createQuery("FROM AppProperty");

		return query.getResultList();
	}

	public String getProperty(String property) {
		AppProperty p = find(property);
		if (p == null)
			return null;

		return p.getValue();
	}

	public AppProperty setProperty(String property, String value) {
		AppProperty p = find(property);
		if (p == null) {
			p = new AppProperty();
			p.setProperty(property);
		}
		p.setValue(value);
		return save(p);
	}
}
