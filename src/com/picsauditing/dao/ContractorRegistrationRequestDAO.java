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
		Query query = em.createQuery("SELECT c FROM ContractorRegistrationRequest c, UserAssignment ua "
				+ "WHERE ua.country = c.country AND ua.user.id = ? "
				+ "AND (c.zip BETWEEN ua.postalStart AND ua.postalEnd OR c.state = ua.state) AND c.open = ? "
				+ "AND c.handledBy = 'PICS' ORDER BY c.lastContactDate, c.deadline");
		query.setParameter(1, csrID);
		query.setParameter(2, open);
		return query.getResultList();
	}

	public List<ContractorRegistrationRequest> findByOp(int opID, boolean open) {
		Query query = em.createQuery("FROM ContractorRegistrationRequest c WHERE c.requestedBy.id = ? "
				+ "AND c.open = ? ORDER BY c.deadline, c.lastContactDate");
		query.setParameter(1, opID);
		query.setParameter(2, (open ? 1 : 0));

		return query.getResultList();
	}

	public List<ContractorRegistrationRequest> findByCorp(int corpID, boolean open) {
		Query query = em
				.createQuery("FROM ContractorRegistrationRequest c WHERE c.requestedBy.id IN "
						+ "(SELECT f.operator.id FROM Facility f WHERE f.corporate.id = ? ) AND c.open = ? ORDER BY c.deadline, c.lastContactDate");
		query.setParameter(1, corpID);
		query.setParameter(2, (open ? 1 : 0));

		return query.getResultList();
	}
}