package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.UserAssignmentMatrix;

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

	public List<UserAssignmentMatrix> findByContractor(ContractorAccount contractor) {
		Query q = em.createQuery("FROM UserAssignmentMatrix WHERE (state = :state AND country = :country) "
				+ "OR (state IS null AND country = :country) " + "OR (postalEnd IS null AND postalStart = :postal) "
				+ "OR :postal BETWEEN postalStart AND postalEnd");

		q.setParameter("state", contractor.getState());
		q.setParameter("country", contractor.getCountry());
		q.setParameter("postal", contractor.getZip());

		return q.getResultList();
	}

}