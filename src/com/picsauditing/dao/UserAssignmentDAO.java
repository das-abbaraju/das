package com.picsauditing.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.UserAssignmentType;

@SuppressWarnings("unchecked")
public class UserAssignmentDAO extends PicsDAO {

	public UserAssignment find(int id) {
		UserAssignment u = em.find(UserAssignment.class, id);
		return u;
	}

	public List<UserAssignment> findByUser(int userID) {
		Query q = em.createQuery("FROM UserAssignment WHERE user.id = :userID");
		q.setParameter("userID", userID);

		return q.getResultList();
	}

	public List<UserAssignment> findAll() {
		return (List<UserAssignment>) super.findAll(UserAssignment.class);
	}

	public List<UserAssignment> findByType(UserAssignmentType type) {
		Query q = em.createQuery("FROM UserAssignment WHERE assignmentType = :type");
		q.setParameter("type", type);
		return (List<UserAssignment>) q.getResultList();
	}

	public UserAssignment findByContractor(ContractorAccount contractor, UserAssignmentType type, AuditType auditType) {
		UserAssignment assignment = null;
		try {
			List<UserAssignment> assignments = findList(contractor, type, auditType);
			// TODO implement comparable on UserAssignment and return the
			// first entry
			if (assignments.size() > 0) {
				if (assignments.size() > 1) {
					// Sort in DESC order to make the highest priority rule in the front
					Collections.sort(assignments);
					Collections.reverse(assignments);
				}
				assignment = assignments.get(0);
			}
		} catch (Exception justReturnNull) {
		}

		return assignment;
	}

	// Do we want to find a CSR by default?
	public UserAssignment findByContractor(ContractorAccount contractor) {
		return findByContractor(contractor, UserAssignmentType.CSR, null);
	}

	public UserAssignment findByContractor(ContractorAccount contractor, UserAssignmentType type) {
		return findByContractor(contractor, type, null);
	}

	// Assume that provided an audit type we just want the auditor
	public UserAssignment findByContractor(ContractorAccount contractor, AuditType auditType) {
		return findByContractor(contractor, UserAssignmentType.Auditor, auditType);
	}

	public List<UserAssignment> findList(ContractorAccount contractor, UserAssignmentType type, AuditType auditType) {
		return findList(contractor.getCountrySubdivision() != null ? contractor.getCountrySubdivision().getIsoCode() : null, contractor
				.getCountry() != null ? contractor.getCountry().getIsoCode() : null, contractor.getZip(), contractor
				.getId(), type, auditType);
	}

	public List<UserAssignment> findList(ContractorAudit conAudit, UserAssignmentType type, AuditType auditType) {
		return findList(conAudit.getCountrySubdivision(), conAudit.getCountry(), conAudit.getZip(), conAudit.getContractorAccount()
				.getId(), type, auditType);
	}

	public List<UserAssignment> findList(String countrySubdivision, String country, String zip, int conID, UserAssignmentType type,
			AuditType auditType) {
		String where = "(country IS NULL OR country.isoCode = :country)";
		where += " AND (countrySubdivision IS NULL OR countrySubdivision.isoCode = :countrySubdivision)";
		// If you want the assignment to be based on any zip code starting
		// with 9, then use 9% in the postalStart
		where += " AND ((postalStart IS NULL OR postalStart < :postal OR :postal LIKE postalStart) AND country.isoCode = :country)";
		// postalEnd works the same way as postalStart but with the added
		// wildcard. This allows us to include 92604-1234 even though the
		// end is 92604
		where += " AND ((postalEnd IS NULL OR postalEnd > :postal OR :postal LIKE CONCAT(postalEnd, '%')) AND country.isoCode = :country)";
		// For these 3 cases, the contractor has to be null
		where += " AND contractor IS NULL";
		// contractor is used as an override. this has the highest priority.
		where = "(" + where  + " OR contractor.id = :conID" + ")";

		if (type != null)
			where = "assignmentType = '" + type + "' AND " + where;
		if (auditType != null)
			where = "auditType.id = " + auditType.getId() + " AND " + where;

		Query q = em.createQuery("FROM UserAssignment WHERE " + where);

		q.setParameter("countrySubdivision", countrySubdivision);
		q.setParameter("country", country);
		q.setParameter("postal", zip);
		q.setParameter("conID", conID);

		return q.getResultList();
	}

	public List<User> findAuditorsByLocation(ContractorAudit conAudit, UserAssignmentType assignmentType) {
		String countrySubdivision = conAudit.getCountrySubdivision();
		String country = conAudit.getCountry();
		String zip = conAudit.getZip();
		int conID = conAudit.getContractorAccount().getId();

		String where = "(ua.country IS NULL OR ua.country.isoCode = :country)";
		where += " AND (ua.countrySubdivision IS NULL OR ua.countrySubdivision.isoCode = :countrySubdivision)";
		where += " AND (ua.postalStart IS NULL OR ua.postalStart < :postal OR :postal LIKE ua.postalStart)";
		where += " AND (ua.postalEnd IS NULL OR ua.postalEnd > :postal OR :postal LIKE CONCAT(ua.postalEnd, '%') )";
		where += " AND ua.contractor IS NULL";
		where = "(" + where + ")" + " OR ua.contractor.id = :conID";

		if (assignmentType != null)
			where = "ua.assignmentType = '" + assignmentType + "' AND " + where;
		if (conAudit != null)
			where = "ua.auditType.id = " + conAudit.getAuditType().getId() + " AND " + where;

		Query q = em.createQuery("select DISTINCT ua.user FROM UserAssignment ua WHERE " + where);

		q.setParameter("countrySubdivision", countrySubdivision);
		q.setParameter("country", country);
		q.setParameter("postal", zip);
		q.setParameter("conID", conID);

		return q.getResultList();
	}
}