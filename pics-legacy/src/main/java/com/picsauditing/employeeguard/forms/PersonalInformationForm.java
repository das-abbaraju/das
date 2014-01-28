package com.picsauditing.employeeguard.forms;

import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;

public interface PersonalInformationForm extends DuplicateInfoProvider {
	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getPhoneNumber();

	void setPhoneNumber(String phoneNumber);

	String getEmail();

	void setEmail(String email);
}
