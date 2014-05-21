package com.picsauditing.securitysession.dao;

import com.picsauditing.securitysession.entities.Profile;

import javax.persistence.TypedQuery;

public class ProfileDAO extends PicsDAO {

	public Profile findByAppUserId(final int appUserId) {
		TypedQuery<Profile> query = em.createQuery("SELECT p FROM Profile p " +
				"WHERE p.userId = :appUserId", Profile.class);

		query.setParameter("appUserId", appUserId);

		try {
			return query.getSingleResult();
		} catch (Exception exception) {
			return null;
		}
	}

}
