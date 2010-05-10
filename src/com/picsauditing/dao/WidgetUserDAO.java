package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.WidgetUser;

@Transactional
@SuppressWarnings("unchecked")
public class WidgetUserDAO extends PicsDAO {
	public WidgetUser save(WidgetUser o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		WidgetUser row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public WidgetUser find(int id) {
		WidgetUser a = em.find(WidgetUser.class, id);
		return a;
	}

	public List<WidgetUser> findByUser(Permissions permissions) {

		Query query = em.createQuery("SELECT wu FROM WidgetUser wu WHERE wu.user.id = ? ORDER BY wu.sortOrder");
		query.setParameter(1, permissions.getUserId());

		permissions.getAccountType();
		// This will go away when we support per user configuration of dashboard
		if (permissions.isPicsEmployee())
			query.setParameter(1, 941); // tallred
		if(permissions.hasGroup(959)) 
			query.setParameter(1, 959); // For CSRs 
		if (permissions.isOnlyAuditor())
			query.setParameter(1, 910); // ddooly
		if (permissions.isOperator())
			query.setParameter(1, 616); // kevin.dyer
		if (permissions.isCorporate())
			query.setParameter(1, 646); // shellcorporate
		if (permissions.isContractor())
			query.setParameter(1, 1); // contractor

		return query.getResultList();
	}

	public List<WidgetUser> findByUser() {
		Query query = em.createQuery("FROM WidgetUser WHERE user.id = ? ORDER BY sortOrder");
		query.setParameter(1, 941);

		return query.getResultList();
	}
}
