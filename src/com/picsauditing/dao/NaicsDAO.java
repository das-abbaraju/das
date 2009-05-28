package com.picsauditing.dao;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.util.Strings;

@Transactional
public class NaicsDAO extends PicsDAO {

	public Naics find(String code) {
		return em.find(Naics.class, code);
	}

	public Naics findByCode(String code) {
		try {
			Query query = em.createNativeQuery("SELECT MIN(code_group) FROM naics_lookup WHERE code = '" + Utilities.escapeQuotes(code) + "'");
			String naicsGroup = query.getSingleResult().toString();
			if (Strings.isEmpty(naicsGroup)) {
				return find(naicsGroup);
			}
		} catch (Exception e) {
			System.out.println("Error: findByCode(" + code + ") " + e.getMessage());
		}
		return null;
	}
}
