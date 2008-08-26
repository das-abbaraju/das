package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.UserAccess;

@Transactional
public class UserAccessDAO extends PicsDAO {

	public UserAccess save(UserAccess o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		UserAccess row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public UserAccess find(int id) {
		return em.find(UserAccess.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<UserAccess> findByUser(int userID) {
		Query query = em.createQuery("FROM UserAccess ua WHERE ua.user.id = :userID");
		query.setParameter("userID", userID);
		return query.getResultList();
	}
}
