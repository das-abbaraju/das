package com.picsauditing.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.UserAssignmentType;

@Transactional
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

	public UserAssignment findByContractor(ContractorAccount contractor) {
		UserAssignment assignment = null;
		try {
			String where = "(country IS NULL OR country = :country)";
			where += " AND (state IS NULL OR state = :state)";
			// If you want the assignment to be based on any zip code starting
			// with
			// 9, then use 9% in the postalStart
			where += " AND (postalStart IS NULL OR postalStart < :postal OR :postal LIKE postalStart)";
			// postalEnd works the same way as postalStart but with the added
			// wildcard. This allows us to include 92604-1234 even though the
			// end is
			// 92604
			where += " AND (postalEnd IS NULL OR postalEnd > :postal OR :postal LIKE CONCAT(postalEnd, '%') )";
			// For these 3 cases, the contractor has to be null
			where += " AND contractor IS NULL";

			// contractor is used as an override. this has the highest priority.
			where = "(" + where + ")" + " OR contractor.id = :conID";
			Query q = em.createQuery("FROM UserAssignment WHERE " + where);

			// TODO implement comparable on UserAssignment and return the
			// first entry
			q.setParameter("state", contractor.getState());
			q.setParameter("country", contractor.getCountry());
			q.setParameter("postal", contractor.getZip());
			q.setParameter("conID", contractor.getId());

			List<UserAssignment> assignments = q.getResultList();

			if (assignments.size() > 0) {
				if (assignments.size() > 1) {
					// Sort in DESC order to make the highest priority rule in
					// the
					// front
					Collections.sort(assignments);
					Collections.reverse(assignments);
				}
				assignment = assignments.get(0);
				if (!assignment.getUser().equals(contractor.getAuditor())) {
					contractor.setAuditor(assignment.getUser());
				}
			}
		} catch (Exception justReturnNull) {
		}

		return assignment;
	}

}