package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;

public class SoftDeletedEmployeeBuilder extends AbstractBaseEntityBuilder<SoftDeletedEmployee, SoftDeletedEmployeeBuilder> {

	private final SoftDeletedEmployee softDeletedEmployee;

	public SoftDeletedEmployeeBuilder() {
		softDeletedEmployee = new SoftDeletedEmployee();
	}

	public SoftDeletedEmployeeBuilder id(int id) {
		softDeletedEmployee.setId(id);
		return this;
	}

	public SoftDeletedEmployeeBuilder accountId(int accountId) {
		softDeletedEmployee.setAccountId(accountId);
		return this;
	}

	public SoftDeletedEmployeeBuilder firstName(String firstName) {
		softDeletedEmployee.setFirstName(firstName);
		return this;
	}

	public SoftDeletedEmployeeBuilder lastName(String lastName) {
		softDeletedEmployee.setLastName(lastName);
		return this;
	}

	public SoftDeletedEmployeeBuilder email(String email) {
		softDeletedEmployee.setEmail(email);
		return this;
	}

	public SoftDeletedEmployeeBuilder phoneNumber(String phoneNumber) {
		softDeletedEmployee.setPhone(phoneNumber);
		return this;
	}

	public SoftDeletedEmployeeBuilder slug(String slug) {
		softDeletedEmployee.setSlug(slug);
		return this;
	}

	public SoftDeletedEmployeeBuilder positionName(String positionName) {
		softDeletedEmployee.setPositionName(positionName);
		return this;
	}

	public SoftDeletedEmployeeBuilder profile(final Profile profile) {
		softDeletedEmployee.setProfile(profile);
		return this;
	}

	public SoftDeletedEmployee build() {
		return softDeletedEmployee;
	}
}
