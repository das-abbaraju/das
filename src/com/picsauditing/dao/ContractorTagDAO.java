package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorTag;

@Transactional
public class ContractorTagDAO extends PicsDAO {
	public ContractorTag save(ContractorTag o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorTag row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(ContractorTag row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorTag find(int id) {
		return em.find(ContractorTag.class, id);
	}

}
