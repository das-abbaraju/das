package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorRegistrationRequest;

@Transactional
public class ContractorRegistrationRequestDAO extends PicsDAO {
	public ContractorRegistrationRequest save(ContractorRegistrationRequest o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public ContractorRegistrationRequest find(int id) {
		ContractorRegistrationRequest a = em.find(ContractorRegistrationRequest.class, id);
		return a;
	}
}