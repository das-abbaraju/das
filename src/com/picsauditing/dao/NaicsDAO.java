package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Naics;

@Transactional
public class NaicsDAO extends PicsDAO {

	public Naics find(int id) {
		return em.find(Naics.class, id);
	}
}
