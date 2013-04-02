package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.PICS.DateBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditorAvailability;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class AuditorAvailabilityDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public AuditorAvailability save(AuditorAvailability o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		AuditorAvailability row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(AuditorAvailability row) {
		if (row != null) {
			em.remove(row);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public int removeAll() {
		Query q = em.createQuery("DELETE FROM AuditorAvailability");
		return q.executeUpdate();
	}

	public AuditorAvailability find(int id) {
		return em.find(AuditorAvailability.class, id);
	}

	public List<AuditorAvailability> findAvailable(Date startDate, String contractorCountry) {
		String sql = "SELECT aa.* FROM auditor_availability aa\n" +
				"JOIN user_country uc ON uc.userID = aa.userID\n" +
				"WHERE uc.isoCode = '" + contractorCountry + "'\n" +
				"AND aa.startDate >= '" + DateBean.toDBFormat(startDate) + "'" +
				"ORDER BY aa.startDate";
		Query q = em.createNativeQuery(sql, AuditorAvailability.class);

		return q.getResultList();
	}

	public List<AuditorAvailability> findAvailableLocal(Date startDate, List<User> auditors) {
		Query query = em
				.createQuery("SELECT t FROM AuditorAvailability t WHERE t.startDate >= :startDate AND t.user IN (:users)"
						+ "ORDER BY t.startDate");
		query.setParameter("startDate", startDate);
		query.setParameter("users", auditors);
		return query.getResultList();
	}

	public List<AuditorAvailability> findByAuditorID(int auditorID) {
		return findByAuditorID(auditorID, null);
	}

	public List<AuditorAvailability> findByAuditorID(int auditorID, Date startDate) {
		String where = "t.user.id = ?";
		if (startDate != null)
			where += " AND t.startDate >= ?";

		Query query = em.createQuery("SELECT t FROM AuditorAvailability t WHERE " + where + " ORDER BY startDate");
		query.setParameter(1, auditorID);

		if (startDate != null)
			query.setParameter(2, startDate);

		return query.getResultList();
	}

	public List<AuditorAvailability> findByTime(Date timeSelected) {
		Query query = em.createQuery("SELECT t FROM AuditorAvailability t WHERE t.startDate = :timeSelected");
		query.setParameter("timeSelected", timeSelected);
		return query.getResultList();
	}

    public List<AuditorAvailability> findByTimeAndCountry(Date timeSelected, String contractorCountry) {
        String sql = "SELECT aa.* FROM auditor_availability aa " +
                        "JOIN user_country uc ON uc.userID = aa.userID " +
                        "WHERE aa.startDate = :timeSelected " +
                        "AND uc.isoCode = :contractorCountry";

        Query query = em.createNativeQuery(sql, AuditorAvailability.class);
        query.setParameter("timeSelected", timeSelected);
        query.setParameter("contractorCountry", contractorCountry);
        return query.getResultList();
    }
}
