package com.picsauditing.securitysession.dao;

import com.picsauditing.securitysession.entities.UserLoginLog;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
public class UserLoginLogDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public UserLoginLog save(UserLoginLog o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

//	@Transactional(propagation = Propagation.NESTED)
//	public void remove(int id) {
//		UserLoginLog row = find(id);
//		if (row != null) {
//			em.remove(row);
//		}
//	}
//
//	public UserLoginLog find(int id) {
//		return em.find(UserLoginLog.class, id);
//	}
//
//	public List<UserLoginLog> findRecentLogins(int userID, int startIndex, int maxResults) {
//		Query query = em.createQuery("FROM UserLoginLog t WHERE t.user.id = :userID ORDER BY t.loginDate DESC");
//		query.setParameter("userID", userID);
//		query.setFirstResult(startIndex);
//		query.setMaxResults(maxResults);
//		return query.getResultList();
//	}
//
//	public List<UserLoginLog> findRecentLogins(int userID, int maxResults) {
//		return findRecentLogins(userID, 0, maxResults);
//	}
}
