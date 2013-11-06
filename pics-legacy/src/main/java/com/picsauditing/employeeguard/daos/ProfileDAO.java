package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;

public class ProfileDAO extends BaseEntityDAO<Profile> {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileDAO.class);

	public ProfileDAO() {
        this.type = Profile.class;
    }

	public Profile findByAppUserId(int appUserId) {
		Query query = em.createNativeQuery("SELECT p.* FROM profile p WHERE p.appUserID = :appUserId", Profile.class);
		query.setParameter("appUserId", appUserId);

		try {
			return (Profile) query.getSingleResult();
		} catch (Exception exception) {
			LOG.error("Error finding profile by app_user. App User ID tried was: {}", appUserId);
		}

		return null;
	}

}
