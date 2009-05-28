package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Naics;

@Transactional
public class NaicsDAO extends PicsDAO {

	public Naics find(String code) {
		return em.find(Naics.class, code);
	}
}
