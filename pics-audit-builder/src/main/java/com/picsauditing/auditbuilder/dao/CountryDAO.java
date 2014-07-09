package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.Country;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class CountryDAO extends PicsDAO {
	public List<Country> findAll() {
		Query query = em.createQuery("FROM Country t");
		List<Country> list = new ArrayList<Country>();

		List<Country> results = query.getResultList();
		list.addAll(results);
		return list;
	}
}