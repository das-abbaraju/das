package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.util.log.PicsLogger;

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
			PicsLogger.log("Removing UserAccess=" + row.getOpPerm() + " for userID=" + row.getUser().getId());
			em.remove(row);
		}
	}
	
	public void remove(UserAccess ua){
		if(ua!=null){
			PicsLogger.log("Removing UserAccess=" + ua.getOpPerm() + " for userID=" + ua.getUser().getId());
			em.remove(ua);
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

	@SuppressWarnings("unchecked")
	public List<UserAccess> findByAccount(int aID) {
		Query query = em.createQuery("FROM UserAccess ua WHERE ua.user.account.id = :aID");
		query.setParameter("aID", aID);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public UserAccess findByUserAndOpPerm(int userID, OpPerms opPerm) {
		Query query = em.createQuery("FROM UserAccess ua WHERE ua.user.id = ? AND ua.opPerm = ?");
		query.setParameter(1, userID);
		query.setParameter(2, opPerm);
		return (UserAccess) query.getSingleResult();
	}
}
