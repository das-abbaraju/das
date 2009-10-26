package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.UserSwitch;

@Transactional
@SuppressWarnings("unchecked")
public class UserSwitchDAO extends PicsDAO {
	public UserSwitch save(UserSwitch o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	
	public void remove(UserSwitch row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void remove(int id) {
		UserSwitch row = find(id);
		if (row != null)
			em.remove(row);
	}

	public UserSwitch find(int id) {
		return em.find(UserSwitch.class, id);
	}

	public List<UserSwitch> findBySwitchToId(int switchToId) {
		Query query = em.createQuery("SELECT u FROM UserSwitch u WHERE u.switchTo.id = ?");
		query.setParameter(1, switchToId);

		return query.getResultList();
	}

	public List<UserSwitch> findByUserId(int userId) {
		Query query = em.createQuery("SELECT u FROM UserSwitch u where u.user.id = ? ORDER BY u.switchTo.name");
		query.setParameter(1, userId);

		return query.getResultList();
	}

	public UserSwitch findByUserIdAndSwitchToId(int userId, int switchToId) {
		try {
			Query query = em.createQuery("SELECT u FROM UserSwitch u where u.user.id = ? AND u.switchTo.id = ?");
			query.setParameter(1, userId);
			query.setParameter(2, switchToId);

			return (UserSwitch) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
