package com.picsauditing.authentication.dao;

import com.picsauditing.authentication.entities.AppUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AppUserDAO {

	@PersistenceContext
	private EntityManager em;

	public AppUser save(AppUser o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}

		em.flush();

		return o;
	}

	public AppUser find(int id) {
		return em.find(AppUser.class, id);
	}

	public List<AppUser> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;

		Query query = em.createQuery("select a from AppUser a " + where);

		return query.getResultList();
	}

	public List<AppUser> findListByUserName(String username) {
		return findWhere("username = '" + username + "'");
	}

	public AppUser findByUserName(String username) {
		TypedQuery<AppUser> q = em.createQuery("SELECT a FROM AppUser a " +
				"WHERE a.username = :username", AppUser.class);

		q.setParameter("username", username);

		try {
			return q.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public AppUser findByAppUserID(int appUserID) {
		return find(appUserID);
		//return findWhere("id = '" + appUserID + "'");
	}

	public AppUser findByUserNameAndPassword(String username, String password) {
		TypedQuery<AppUser> q = em.createQuery("SELECT a FROM AppUser a WHERE a.username = :username " +
				"AND a.password = :password", AppUser.class);

		q.setParameter("username", username);
		q.setParameter("password", password);

		try {
			return q.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean duplicateUsername(String username, int appUserID) {
		try {
			AppUser appUser = findByUserName(username);
			if (appUser == null) {
				return false;
			}

			int id = appUser.getId();
			if (id > 0) {
				// found a user with this username
				if (id != appUserID)
					// This is in use by another user
					return true;
			}
		} catch (Exception e) {
			//TODO
		}

		return false;
	}
}
