package com.picsauditing.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;

@Transactional
@SuppressWarnings("unchecked")
public class CountryDAO extends PicsDAO {

	public List<Country> findAll() {
		Query query = em.createQuery("FROM Country t ORDER BY t.english");
		return query.getResultList();
	}

	public Country find(String id) {
		return em.find(Country.class, id);
	}
	
	public Map<String, String> findMap() {
		List<Country> countryList = findAll();
		Map<String, String> map = new TreeMap<String, String>();
		
		for (Country country : countryList) {
			map.put(country.getIsoCode(), country.getName());
		}
		
		return map;
	}
	
	public List<Country> findByCSR(int csrID) {
		Query query = em.createQuery("FROM Country WHERE csr.id = ?");
		query.setParameter(1, csrID);

		return query.getResultList();
	}
}
