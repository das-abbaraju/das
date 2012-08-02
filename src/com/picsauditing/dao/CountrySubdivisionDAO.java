package com.picsauditing.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;

@SuppressWarnings("unchecked")
public class CountrySubdivisionDAO extends PicsDAO {

	public List<CountrySubdivision> findAll() {
		Query query = em.createQuery("FROM CountrySubdivision t ORDER BY t.isoCode");
		return query.getResultList();
	}

	public CountrySubdivision find(String isoCode) {
		return em.find(CountrySubdivision.class, isoCode);
	}

	public Multimap<Country, CountrySubdivision> getCountrySubdivisionMap(String country) {
		Multimap<Country, CountrySubdivision> result = LinkedHashMultimap.create();

		Query query = em.createQuery("FROM CountrySubdivision t ORDER BY t.english");
		List<CountrySubdivision> countrySubdivisions = (List<CountrySubdivision>) query.getResultList();

		for (CountrySubdivision countrySubdivision : countrySubdivisions) {
			result.put(countrySubdivision.getCountry(), countrySubdivision);
		}

		return result;
	}

	public List<CountrySubdivision> findByCountry(Country country) {
		return findByCountry(country.getIsoCode());
	}

	public List<CountrySubdivision> findByCountry(String country) {
		Query query = em.createQuery("FROM CountrySubdivision WHERE country.isoCode = ?");
		query.setParameter(1, country);

		return query.getResultList();
	}

	public List<CountrySubdivision> findByCountries(Collection<String> countries, boolean negative) {
		String q = "FROM CountrySubdivision WHERE country.isoCode ";
		if (negative)
			q += "NOT ";
		q += "IN (:countries)";

		Query query = em.createQuery(q).setParameter("countries", countries);

		return query.getResultList();
	}

	public List<CountrySubdivision> findByCSR(int csrID) {
		Query query = em.createQuery("FROM CountrySubdivision WHERE csr.id = ?");
		query.setParameter(1, csrID);

		return query.getResultList();
	}

	public List<CountrySubdivision> findWhere(String where) {
		Query query = em.createQuery("FROM CountrySubdivision WHERE " + where);
		return query.getResultList();
	}
}