package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.UserAssignmentMatrix;
import com.picsauditing.jpa.entities.UserAssignmentMatrixType;

@Transactional
@SuppressWarnings("unchecked")
public class UserAssignmentMatrixDAO extends PicsDAO {

	public UserAssignmentMatrix find(int id) {
		UserAssignmentMatrix u = em.find(UserAssignmentMatrix.class, id);
		return u;
	}

	public List<UserAssignmentMatrix> findByUser(int userID) {
		Query q = em.createQuery("FROM UserAssignmentMatrix WHERE user.id = :userID");
		q.setParameter("userID", userID);

		return q.getResultList();
	}

	public List<UserAssignmentMatrix> findAll() {
		return (List<UserAssignmentMatrix>) super.findAll(UserAssignmentMatrix.class);
	}

	public List<UserAssignmentMatrix> findByType(UserAssignmentMatrixType type) {
		Query q = em.createQuery("FROM UserAssignmentMatrix WHERE assignmentType = :type");
		q.setParameter("type", type);
		return (List<UserAssignmentMatrix>) q.getResultList();
	}

	public List<UserAssignmentMatrix> findByContractor(ContractorAccount contractor) {
		String where = "(country is null OR country = :country)";
		where += " AND (state is null OR state = :state)";
		// If you want the assignment to be based on any zip code starting with
		// 9, then use 9% in the postalStart
		where += " AND (postalStart is null OR postalStart < :postal OR :postal LIKE postalStart)";
		// postalEnd works the same way as postalStart but with the added
		// wildcard. This allows us to include 92604-1234 even though the end is
		// 92604
		where += " AND (postalEnd is null OR postalEnd > :postal OR :postal LIKE CONCAT(postalEnd, '%') )";
		Query q = em.createQuery("FROM UserAssignmentMatrix WHERE " + where);

		// TODO implement comparable on UserAssignmentMatrix and return the
		// first entry
		q.setParameter("state", contractor.getState());
		q.setParameter("country", contractor.getCountry());
		q.setParameter("postal", contractor.getZip());

		return q.getResultList();
	}

}