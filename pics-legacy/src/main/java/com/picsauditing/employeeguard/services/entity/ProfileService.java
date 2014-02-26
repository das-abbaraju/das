package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProfileDAO;
import com.picsauditing.employeeguard.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ProfileService implements EntityService<Profile, Integer> {

	@Autowired
	private ProfileDAO profileDAO;

	/* All Find Methods */

	@Override
	public Profile find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return profileDAO.find(id);
	}

	/* All Save Methods */

	@Override
	public Profile save(final Profile profile, final int createdBy, final Date createdDate) {
		profile.setCreatedBy(createdBy);
		profile.setCreatedDate(createdDate);
		return profileDAO.save(profile);
	}

	/* All Update Methods */

	@Override
	public Profile update(final Profile profile, final int updatedBy, final Date updatedDate) {
		Profile profileToUpdate = find(profile.getId());

		profileToUpdate.setFirstName(profile.getFirstName());
		profileToUpdate.setLastName(profile.getLastName());
		profileToUpdate.setEmail(profile.getEmail());
		profileToUpdate.setPhone(profile.getPhone());
		profileToUpdate.setUpdatedBy(updatedBy);
		profileToUpdate.setUpdatedDate(updatedDate);

		return profileDAO.save(profileToUpdate);
	}

	/* All Delete Methods */

	@Override
	public void delete(final Profile profile) {
		if (profile == null) {
			throw new NullPointerException("profile cannot be null");
		}

		profileDAO.delete(profile);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Profile profile = find(id);
		delete(profile);
	}
}
