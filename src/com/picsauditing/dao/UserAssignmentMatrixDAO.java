package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

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

	protected List<UserAssignmentMatrix> findAll() {
		return (List<UserAssignmentMatrix>) super.findAll(UserAssignmentMatrix.class);
	}

}