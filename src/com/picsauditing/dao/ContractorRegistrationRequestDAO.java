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
				"(SELECT DISTINCT a.state FROM ContractorAccount a WHERE a.auditor.id = ?) " +
				"AND c.open = 1 AND c.handledBy = 'PICS' ORDER BY c.lastContactDate, c.deadline");
		query.setParameter(1, csrID);
		
		return query.getResultList();
	}
	
	public List<ContractorRegistrationRequest> findByOp(int opID, boolean open){
		Query query = em.createQuery("FROM ContractorRegistrationRequest c WHERE c.requestedBy.id = ? " + 
				"AND c.open = 1 ORDER BY c.deadline, c.lastContactDate");
		query.setParameter(1, opID);
		
		return query.getResultList();
	}
	
	public List<ContractorRegistrationRequest> findByCorp(int corpID, boolean open){
		Query query = em.createQuery("FROM ContractorRegistrationRequest c WHERE c.requestedBy.id IN " +
				"(SELECT f.operator.id FROM Facility f WHERE f.corporate.id = ? ) AND c.open = 1 ORDER BY c.deadline, c.lastContactDate");
		query.setParameter(1, corpID);
		
		return query.getResultList();
	}
}