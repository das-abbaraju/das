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
		return null;
	}

	@Override
	public Profile update(Profile profile, int updatedBy, Date updatedDate) {
		return null;
	}

	@Override
	public void delete(Profile profile) {

	}

	@Override
	public void deleteById(Integer integer) {

	}
}
