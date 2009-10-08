package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditorAvailability;

@Transactional
@SuppressWarnings("unchecked")
public class AuditorAvailabilityDAO extends PicsDAO {
	public AuditorAvailability save(AuditorAvailability o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditorAvailability row = find(id);
		remove(row);
	}

	public void remove(AuditorAvailability row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public int removeAll() {
		Query q = em.createQuery("DELETE FROM AuditorAvailability");
		return q.executeUpdate();
	}

	public AuditorAvailability find(int id) {
		return em.find(AuditorAvailability.class, id);
	}

	public List<AuditorAvailability> findAvailable(Date startDate, Permissions permissions) {
		String hql = "SELECT t FROM AuditorAvailability t WHERE t.startDate >= :startDate ";
		if (permissions.isAuditor())
			hql += "AND user.id = :userID ";
		hql += "ORDER BY startDate";
		
		Query query = em.createQuery(hql);
		query.setParameter("startDate", startDate);
		if (permissions.isAuditor())
			query.setParameter("userID", permissions.getUserId());
		return query.getResultList();
	}
	
	public List<AuditorAvailability> findByAuditorID(int auditorID) {
		Query query = em.createQuery("SELECT t FROM AuditorAvailability t "
				+ "WHERE t.user.id = ? ORDER BY startDate");
		query.setParameter(1, auditorID);
		return query.getResultList();
	}

	public List<AuditorAvailability> findByTime(Date timeSelected) {
		Query query = em.createQuery("SELECT t FROM AuditorAvailability t "
				+ "WHERE t.startDate = ?");
		query.setParameter(1, timeSelected);
		return query.getResultList();
	}

}
