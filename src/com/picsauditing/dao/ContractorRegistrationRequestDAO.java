package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorRegistrationRequest;

@Transactional
@SuppressWarnings("unchecked")
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
	
	public List<ContractorRegistrationRequest> findByCSR(int csrID, boolean open) {
		Query query = em.createQuery("FROM ContractorRegistrationRequest c WHERE c.state IN " +
				"(SELECT s from State s where s.csr.id = ?) AND c.open = 1 ORDER BY c.deadline");
		query.setParameter(1, csrID);
		
		return query.getResultList();
	}
}