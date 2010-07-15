package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.UserGroup;

@Transactional
@SuppressWarnings("unchecked")
public class UserGroupDAO extends PicsDAO {
	public UserGroup save(UserGroup o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		UserGroup row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public UserGroup find(int id) {
		return em.find(UserGroup.class, id);
	}

	public List<UserGroup> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("SELECT ug FROM UserGroup ug " + where);
		return query.getResultList();
	}
	
	public List<UserGroup> findByUser(int userID){
		Query query = em.createQuery("FROM UserGroup ug WHERE ug.user.id = ?");
		query.setParameter(1, userID);
		return query.getResultList();
	}
}
