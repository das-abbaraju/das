package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ContractorRegistrationRequestDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
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
	
	public List<ContractorRegistrationRequest> findWhere(String where) {
		if (!Strings.isEmpty(where)) {
			where = " WHERE " + where;
		} else {
			where = "";
		}
		
		Query query = em.createQuery("FROM ContractorRegistrationRequest c" + where);
		return query.getResultList();
	}

	public List<ContractorRegistrationRequest> findByCSR(int csrID, boolean open) {
		return findByCSR(csrID, open, WaitingOn.PICS, 10);
	}

	public List<ContractorRegistrationRequest> findByCSR(int csrID, boolean open, WaitingOn handledBy, int limit) {
		Query query = em.createQuery("SELECT c FROM ContractorRegistrationRequest c, UserAssignment ua "
				+ "WHERE ua.country = c.country AND ua.user.id = ? "
				+ "AND (c.zip BETWEEN ua.postalStart AND ua.postalEnd OR c.state = ua.state) AND c.open = ? "
				+ "AND c.handledBy = ? ORDER BY c.lastContactDate, c.deadline");
		query.setParameter(1, csrID);
		query.setParameter(2, open);
		query.setParameter(3, handledBy);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<ContractorRegistrationRequest> findByOp(int opID, boolean open) {
		return findByOp(opID, open, WaitingOn.PICS, 10);
	}

	public List<ContractorRegistrationRequest> findByOp(int opID, boolean open, WaitingOn handledBy, int limit) {
		Query query = em.createQuery("FROM ContractorRegistrationRequest c WHERE c.requestedBy.id = ? "
				+ "AND c.open = ? AND c.handledBy = ? ORDER BY c.deadline, c.lastContactDate");
		query.setParameter(1, opID);
		query.setParameter(2, open);
		query.setParameter(3, handledBy);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<ContractorRegistrationRequest> findByCorp(int corpID, boolean open) {
		return findByCorp(corpID, open, WaitingOn.PICS, 10);
	}

	public List<ContractorRegistrationRequest> findByCorp(int corpID, boolean open, WaitingOn handledBy, int limit) {
		Query query = em.createQuery("FROM ContractorRegistrationRequest c WHERE c.requestedBy.id IN "
				+ "(SELECT f.operator.id FROM Facility f WHERE f.corporate.id = ? ) "
				+ "AND c.open = ? AND c.handledBy = ? ORDER BY c.deadline, c.lastContactDate");
		query.setParameter(1, corpID);
		query.setParameter(2, open);
		query.setParameter(3, handledBy);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public List<ContractorRegistrationRequest> findActiveByDate(String whereClause) {
		String sql = "SELECT * FROM contractor_registration_request c " + "WHERE c.status = 'Active' AND " + whereClause;
		Query query = em.createNativeQuery(sql, ContractorRegistrationRequest.class);
		return query.getResultList();
	}
}