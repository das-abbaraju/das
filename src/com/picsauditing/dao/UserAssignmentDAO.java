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

	public UserAssignment findByContractor(ContractorAccount contractor, UserAssignmentType assignmentType, AuditType auditType) {
		UserAssignment assignment = null;
		try {
			List<UserAssignment> assignments = findUserAssignmentsByContractor(contractor, assignmentType);

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

	// Assume that provided an audit type we just want the auditor
	public UserAssignment findByContractor(ContractorAccount contractor, AuditType auditType) {
		return findByContractor(contractor, UserAssignmentType.Auditor, auditType);
	}

	public List<User> findAuditorsByLocation(ContractorAudit conAudit, UserAssignmentType assignmentType) {
		String state = conAudit.getState();
		String country = conAudit.getCountry();
		String zip = conAudit.getZip();
		int conID = conAudit.getContractorAccount().getId();

		String where = "(ua.country IS NULL OR ua.country.isoCode = :country)";
		where += " AND (ua.state IS NULL OR ua.state.isoCode = :state)";
		where += " AND (ua.postalStart IS NULL OR ua.postalStart < :postal OR :postal LIKE ua.postalStart)";
		where += " AND (ua.postalEnd IS NULL OR ua.postalEnd > :postal OR :postal LIKE CONCAT(ua.postalEnd, '%') )";
		where += " AND ua.contractor IS NULL";
		where = "(" + where + ")" + " OR ua.contractor.id = :conID";

		if (assignmentType != null)
			where = "ua.assignmentType = '" + assignmentType + "' AND " + where;
		if (conAudit != null)
			where = "ua.auditType.id = " + conAudit.getAuditType().getId() + " AND " + where;

		Query q = em.createQuery("select DISTINCT ua.user FROM UserAssignment ua WHERE " + where);

		q.setParameter("state", state);
		q.setParameter("country", country);
		q.setParameter("postal", zip);
		q.setParameter("conID", conID);

		return q.getResultList();
	}

	private List<UserAssignment> findUserAssignmentsByContractor(ContractorAccount contractor, UserAssignmentType assignmentType) {
		String state = contractor.getState() != null ? contractor.getState().getIsoCode() : null;
		String country = contractor.getCountry() != null ? contractor.getCountry().getIsoCode() : null;
		String zip = contractor.getZip();
		int conID = contractor.getId();

		String where = "(ua.country IS NULL OR ua.country.isoCode = :country)";
		where += " AND (ua.state IS NULL OR ua.state.isoCode = :state)";
		where += " AND (ua.postalStart IS NULL OR ua.postalStart < :postal OR :postal LIKE ua.postalStart)";
		where += " AND (ua.postalEnd IS NULL OR ua.postalEnd > :postal OR :postal LIKE CONCAT(ua.postalEnd, '%') )";
		where += " AND ua.contractor IS NULL";
		where = "(" + where + ")" + " OR ua.contractor.id = :conID";

		if (assignmentType != null)
			where = "ua.assignmentType = '" + assignmentType + "' AND " + where;

		Query q = em.createQuery("FROM UserAssignment WHERE " + where);

		q.setParameter("state", state);
		q.setParameter("country", country);
		q.setParameter("postal", zip);
		q.setParameter("conID", conID);

		return q.getResultList();
	}
}