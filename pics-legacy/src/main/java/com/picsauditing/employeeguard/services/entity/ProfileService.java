package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProfileDAO;
import com.picsauditing.employeeguard.entities.Profile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ProfileService implements EntityService<Profile, Integer> {

	@Autowired
	private ProfileDAO profileDAO;

	@Override
	public Profile find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null.");
		}

		return profileDAO.find(id);
	}

	@Override
	public Profile save(Profile profile, int createdBy, Date createdDate) {
		profile.setCreatedBy(createdBy);
		profile.setCreatedDate(createdDate);
		return profileDAO.save(profile);
	}

	@Override
	public Profile update(Profile profile, int updatedBy, Date updatedDate) {
		Profile profileToUpdate = find(profile.getId());

		profileToUpdate.setFirstName(profile.getFirstName());
		profileToUpdate.setLastName(profile.getLastName());
		profileToUpdate.setEmail(profile.getEmail());
		profileToUpdate.setPhone(profile.getPhone());
		profileToUpdate.setUpdatedBy(updatedBy);
		profileToUpdate.setUpdatedDate(updatedDate);

		return profileDAO.save(profileToUpdate);
	}

	@Override
	public void delete(Profile profile) {
		profileDAO.delete(profile);
	}

	@Override
	public void deleteById(Integer id) {
		Profile profile = find(id);
		delete(profile);
	}
}
