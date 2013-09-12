package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AppProperty;

@SuppressWarnings("unchecked")
public class AppPropertyDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public AppProperty save(AppProperty o) {
		o = em.merge(o);
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
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
	public List<AppProperty> getPropertyList(String where){		
		Query query = em.createQuery("FROM AppProperty "+where);		
		return query.getResultList();
	}

	public String getProperty(String property) {
		AppProperty p = find(property);
		if (p == null)
			return null;

		return p.getValue();
	}

	@Transactional(propagation = Propagation.NESTED)
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
