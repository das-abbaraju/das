package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorOperatorFlag;

@Transactional
public class ContractorOperatorFlagDAO extends PicsDAO {
	public ContractorOperatorFlag save(ContractorOperatorFlag o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorOperatorFlag row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorOperatorFlag find(int id) {
		return em.find(ContractorOperatorFlag.class, id);
	}

}
