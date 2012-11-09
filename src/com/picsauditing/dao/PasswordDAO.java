package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.picsauditing.PICS.DateBean;
import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.jpa.entities.PasswordHistory;

public class PasswordDAO extends PicsDAO {

	public List<PasswordHistory> findRecentEntriesByCount(int userId, int entriesToFind) {
		List<PasswordHistory> recentEntries = null;

		Query query = em.createQuery("SELECT ph FROM PasswordHistory ph WHERE ph.user.id = :userId ORDER BY ph.endDate DESC");
		query.setParameter("userId", userId);
		query.setMaxResults(entriesToFind);
		recentEntries = query.getResultList();

		if (CollectionUtils.isEmpty(recentEntries)) {
			recentEntries = new ArrayList<PasswordHistory>();
		}

		return recentEntries;
	}

	public List<PasswordHistory> findRecentEntriesByPreviousMonths(int userId, int recentMonths) {

		Date searchEndDate = DateBean.addMonths(new Date(), Math.abs(recentMonths) * -1);
		return findRecentEntriesByDate(userId, searchEndDate);
	}

	public List<PasswordHistory> findRecentEntriesByDate(int userId, Date searchEndDate) {
		List<PasswordHistory> recentEntries = null;

		Query query = em.createQuery("SELECT ph FROM PasswordHistory ph WHERE ph.user.id = :userId AND ph.endDate > :searchEndDate");
		query.setParameter("userId", userId);
		query.setParameter("searchEndDate", searchEndDate, TemporalType.TIMESTAMP);
		recentEntries = query.getResultList();

		if (CollectionUtils.isEmpty(recentEntries)) {
			recentEntries = new ArrayList<PasswordHistory>();
		}

		return recentEntries;
	}
}
