package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.picsauditing.jpa.entities.TimeZoneListByCountry;

public class TimeZoneDAO extends PicsDAO {

	@SuppressWarnings("unchecked")
	public List<TimeZoneListByCountry> findByCountryCode(String countryCode) {
		Query query = em.createQuery("SELECT t FROM TimeZoneListByCountry t WHERE t.countryCode = UPPER(:countryCode)");
		query.setParameter("countryCode", countryCode);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<TimeZoneListByCountry> findAll() {
		Query q = em.createQuery("FROM TimeZoneListByCountry t ORDER BY t.id");
		return q.getResultList();
	}

}
