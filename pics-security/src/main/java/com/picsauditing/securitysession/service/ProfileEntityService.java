package com.picsauditing.securitysession.service;

import com.picsauditing.securitysession.dao.ProfileDAO;
import com.picsauditing.securitysession.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileEntityService /*implements EntityService<Profile, Integer>*/ {

	@Autowired
	private ProfileDAO profileDAO;

//	/* All Find Methods */
//
//	@Override
//	public Profile find(final Integer id) {
//		if (id == null) {
//			throw new NullPointerException("id cannot be null");
//		}
//
//		return profileDAO.find(id);
//	}
//
	public Profile findByAppUserId(final int appUserId) {
		return profileDAO.findByAppUserId(appUserId);
	}

//	/* All Save Methods */
//
//	@Override
//	public Profile save(Profile profile, final EntityAuditInfo entityAuditInfo) {
//		profile = EntityHelper.setCreateAuditFields(profile, entityAuditInfo);
//
//		if (Strings.isEmpty(profile.getSlug())) {
//			String hash = Strings.hashUrlSafe(profile.getId() + profile.getEmail());
//			profile.setSlug("PID-" + hash.substring(0, 8).toUpperCase());
//		}
//
//		return profileDAO.save(profile);
//	}
//
//	/* All Update Methods */
//
//	@Override
//	public Profile update(final Profile profile, final EntityAuditInfo entityAuditInfo) {
//		Profile profileToUpdate = find(profile.getId());
//
//		profileToUpdate.setFirstName(profile.getFirstName());
//		profileToUpdate.setLastName(profile.getLastName());
//		profileToUpdate.setEmail(profile.getEmail());
//		profileToUpdate.setPhone(profile.getPhone());
//
//		profileToUpdate = EntityHelper.setUpdateAuditFields(profileToUpdate, entityAuditInfo);
//
//		return profileDAO.save(profileToUpdate);
//	}
//
//	/* All Delete Methods */
//
//	@Override
//	public void delete(final Profile profile) {
//		if (profile == null) {
//			throw new NullPointerException("profile cannot be null");
//		}
//
//		profileDAO.delete(profile);
//	}
//
//	@Override
//	public void deleteById(final Integer id) {
//		if (id == null) {
//			throw new NullPointerException("id cannot be null");
//		}
//
//		Profile profile = find(id);
//		delete(profile);
//	}
}
