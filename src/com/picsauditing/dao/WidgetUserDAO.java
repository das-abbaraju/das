package com.picsauditing.dao;

import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.WidgetUser;

import java.util.List;

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
		Query query = em.createQuery("FROM WidgetUser WHERE user.id = ? ORDER BY sortOrder");
		query.setParameter(1, permissions.getUserId());

		permissions.getAccountType();
		// This will go away when we support per user configuration of dashboard
		if (permissions.isPicsEmployee())
			query.setParameter(1, 941); // tallred
		if (permissions.isOnlyAuditor())
			query.setParameter(1, 910); // ddooly
		if (permissions.isOperator() || permissions.isCorporate())
			query.setParameter(1, 616); // kevin.dyer
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
