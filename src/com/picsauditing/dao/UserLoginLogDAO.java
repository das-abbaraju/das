package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.UserLoginLog;

@Transactional
public class UserLoginLogDAO extends PicsDAO {
	public UserLoginLog save(UserLoginLog o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		UserLoginLog row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public UserLoginLog find(int id) {
		return em.find(UserLoginLog.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<UserLoginLog> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;

		Query query = em.createQuery("FROM UserLoginLog t " + where + " ORDER BY t.date");
		return query.getResultList();
	}
}
