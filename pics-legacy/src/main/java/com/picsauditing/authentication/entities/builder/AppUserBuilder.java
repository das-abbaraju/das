package com.picsauditing.authentication.entities.builder;

import com.picsauditing.authentication.entities.AppUser;

public class AppUserBuilder {

	private AppUser appUser;

	public AppUserBuilder() {
		this.appUser = new AppUser();
	}

	public AppUserBuilder id(int id) {
		appUser.setId(id);
		return this;
	}

	public AppUserBuilder username(String username) {
		appUser.setUsername(username);
		return this;
	}

	public AppUserBuilder password(String password) {
		appUser.setPassword(password);
		return this;
	}

	public AppUserBuilder hashSalt(String hashSalt) {
		appUser.setHashSalt(hashSalt);
		return this;
	}

	public AppUserBuilder resetHash(String resetHash) {
		appUser.setResetHash(resetHash);
		return this;
	}

	public AppUser build() {
		return appUser;
	}

}
