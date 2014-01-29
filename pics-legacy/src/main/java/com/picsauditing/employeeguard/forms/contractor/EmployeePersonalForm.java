package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.forms.PersonalInformationForm;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.employeeguard.web.SessionInfoProviderFactory;

public class EmployeePersonalForm implements PersonalInformationForm {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;

	public EmployeePersonalForm() {
	}

	public EmployeePersonalForm(final Employee employee) {
		this.firstName = employee.getFirstName();
		this.lastName = employee.getLastName();
		this.phoneNumber = employee.getPhone();
		this.email = employee.getEmail();
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

    @Override
    public UniqueIndexable getUniqueIndexable() {
        return new Employee.EmployeeAccountEmailUniqueKey(SessionInfoProviderFactory.getSessionInfoProvider().getId(),
                SessionInfoProviderFactory.getSessionInfoProvider().getAccountId(), this.email);
    }

    @Override
    public Class<?> getType() {
        return Employee.class;
    }
}
