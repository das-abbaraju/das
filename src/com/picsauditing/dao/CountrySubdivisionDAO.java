package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.CountrySubdivision;

@SuppressWarnings("unchecked")
public class CountrySubdivisionDAO extends PicsDAO  {
	public List<CountrySubdivision> findAll() {
		Query query = em.createQuery("FROM CountrySubdivision t ORDER BY t.isoCode");
		return query.getResultList();
	}

	public CountrySubdivision find(String isoCode) {
		return em.find(CountrySubdivision.class, isoCode);
	}

	public boolean exist(String isoCode) {
		if (em.find(CountrySubdivision.class, isoCode) == null)
			return false;
		else
			return true;
	}

}
