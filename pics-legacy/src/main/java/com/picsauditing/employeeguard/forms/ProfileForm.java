package com.picsauditing.employeeguard.forms;

import com.picsauditing.employeeguard.entities.Profile;

public class ProfileForm {
	private String email;
	private String emailRetype;
	private String password;
	private String firstName;
	private String lastName;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailRetype() {
		return emailRetype;
	}

	public void setEmailRetype(String emailRetype) {
		this.emailRetype = emailRetype;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Profile buildProfile(int userId) {
		Profile profile = new Profile();
		profile.setFirstName(firstName);
		profile.setLastName(lastName);
		profile.setEmail(email);
		profile.setUserId(userId);

		return profile;
	}
}
