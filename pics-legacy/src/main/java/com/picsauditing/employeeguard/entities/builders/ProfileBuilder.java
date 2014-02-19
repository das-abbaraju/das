package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;

import java.util.List;

public class ProfileBuilder {

	private final Profile profile;

	public ProfileBuilder() {
		this.profile = new Profile();
	}

	public ProfileBuilder id(final int id) {
		profile.setId(id);
		return this;
	}

	public ProfileBuilder documents(final List<ProfileDocument> documents) {
		profile.setDocuments(documents);
		return this;
	}

	public Profile build() {
		return profile;
	}
}
