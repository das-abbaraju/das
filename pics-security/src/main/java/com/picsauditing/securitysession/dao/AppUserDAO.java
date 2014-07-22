package com.picsauditing.securitysession.dao;

import com.picsauditing.securitysession.entities.AppUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AppUserDAO {

	@PersistenceContext
	private EntityManager em;

//	public AppUser save(AppUser o) {
//		if (o.getId() == 0) {
//			em.persist(o);
//		} else {
//			o = em.merge(o);
//		}
//
//		em.flush();
//
//		return o;
//	}
//
	public AppUser findById(final int id) {
		return em.find(AppUser.class, id);
	}

//	public List<AppUser> findListByUserName(final String username) {
//		TypedQuery<AppUser> query = em.createQuery("FROM AppUser " +
//				"WHERE username = :username", AppUser.class);
//
//		query.setParameter("username", username);
//
//		return query.getResultList();
//	}
//
//	public AppUser findByUserName(String username) {
//		TypedQuery<AppUser> q = em.createQuery("SELECT a FROM AppUser a " +
//				"WHERE a.username = :username", AppUser.class);
//
//		q.setParameter("username", username);
//
//		try {
//			return q.getSingleResult();
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	public AppUser findByUserNameAndPassword(final String username, final String password) {
//		TypedQuery<AppUser> q = em.createQuery("SELECT a FROM AppUser a WHERE a.username = :username " +
//				"AND a.password = :password", AppUser.class);
//
//		q.setParameter("username", username);
//		q.setParameter("password", password);
//
//		return q.getSingleResult();
//	}
//
//	public boolean duplicateUsername(final String username, final int appUserID) {
//		try {
//			AppUser appUser = findByUserName(username);
//			if (appUser == null) {
//				return false;
//			}
//
//			int id = appUser.getId();
//			if (id > 0) {
//				// found a user with this username
//				if (id != appUserID)
//					// This is in use by another user
//					return true;
//			}
//		} catch (Exception e) {
//			//TODO
//		}
//
//		return false;
//	}
}
