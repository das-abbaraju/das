package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ContractorRegistrationRequestDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorRegistrationRequest save(ContractorRegistrationRequest o) {
		// check for existing entry based on name and requestedById
		String name = o.getName();
		int opId = o.getRequestedBy().getId();
		List<ContractorRegistrationRequest> existingRecords = findByNameAndRequestedById(name, opId);

		if (o.getId() != 0) {
			o = em.merge(o);
		} else {
			if (existingRecords.size() == 1) { // exist
				ContractorRegistrationRequest existingRecord = existingRecords.get(0);
				o.setId(existingRecord.getId());
				o = em.merge(o);
			} else {// new record
				em.persist(o);
			}
		}

		return o;
	}

	public ContractorRegistrationRequest find(int id) {
		return em.find(ContractorRegistrationRequest.class, id);
	}

	public List<ContractorRegistrationRequest> findByPermissions(Permissions permissions) {
		String whereClause = " WHERE status = 'Active'";

		if (permissions.isOperatorCorporate()) {
			whereClause += " AND crr.requestedBy.id ";

			if (permissions.isCorporate()) {
				whereClause += "IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")";
			} else {
				whereClause += "= " + permissions.getAccountId();
			}
		}

		Query query = em.createQuery("FROM ContractorRegistrationRequest crr" + whereClause
				+ " ORDER BY crr.deadline ASC");
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<ContractorAccount> findActiveByDate(String whereClause) {
		String sql = "SELECT * FROM ContractorAccount c " + "WHERE c.status = 'Requested' AND " + whereClause;
		Query query = em.createQuery(sql);

		return query.getResultList();
	}

	public List<ContractorRegistrationRequest> findByNameAndRequestedById(String contractorName, int opID) {
		Query query = em.createQuery("FROM ContractorRegistrationRequest c WHERE c.name = ? AND c.requestedBy.id = ? ");
		query.setParameter(1, contractorName);
		query.setParameter(2, opID);
		return query.getResultList();
	}
}
