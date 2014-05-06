package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class ProfileDAO extends AbstractBaseEntityDAO<Profile> {

	public ProfileDAO() {
        this.type = Profile.class;
    }

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
