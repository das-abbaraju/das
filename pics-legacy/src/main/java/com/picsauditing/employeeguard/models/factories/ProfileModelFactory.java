package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.models.ProfileModel;

public class ProfileModelFactory {

	public ProfileModel create(final Profile profile) {
		ProfileModel profileModel = new ProfileModel();

		profileModel.setId(profile.getId());
		profileModel.setFirstName(profile.getFirstName());
		profileModel.setLastName(profile.getLastName());
		profileModel.setSlug(profile.getSlug());
		profileModel.setEmail(profile.getEmail());

		return profileModel;
	}

}
