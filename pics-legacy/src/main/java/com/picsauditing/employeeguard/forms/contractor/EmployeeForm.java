package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.forms.PersonalInformationForm;
import com.picsauditing.employeeguard.forms.PhotoForm;
import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.employeeguard.web.SessionInfoProviderFactory;

import java.io.File;

public class EmployeeForm implements AddAnotherForm, PersonalInformationForm, PhotoForm, DuplicateInfoProvider {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String employeeId;
	private String title;
	private String[] groups;
	private File photo;
	private String photoFileName;
	private String photoContentType;

	private boolean addAnother;

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

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public File getPhoto() {
		return photo;
	}

	@Override
	public void setPhoto(File photo) {
		this.photo = photo;
	}

	@Override
	public String getPhotoFileName() {
		return photoFileName;
	}

	@Override
	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	@Override
	public String getPhotoContentType() {
		return photoContentType;
	}

	@Override
	public void setPhotoContentType(String photoContentType) {
		this.photoContentType = photoContentType;
	}

	public String[] getGroups() {
		return groups;
	}

	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	@Override
	public boolean isAddAnother() {
		return addAnother;
	}

	@Override
	public void setAddAnother(boolean addAnother) {
		this.addAnother = addAnother;
	}

	public Employee buildEmployee(int accountId) {
		return new EmployeeBuilder().accountId(accountId).firstName(firstName).lastName(lastName)
				.email(email).phoneNumber(phoneNumber).slug(employeeId).positionName(title).groups(groups).build();
	}

	public Employee buildEmployee(int accountId, int id) {
		return new EmployeeBuilder(id, accountId).firstName(firstName).lastName(lastName)
				.email(email).phoneNumber(phoneNumber).slug(employeeId).positionName(title).groups(groups).build();
	}

    @Override
    public UniqueIndexable getUniqueIndexable() {
        return new Employee.EmployeeAccountEmailAndSlugUniqueKey(0, SessionInfoProviderFactory.getSessionInfoProvider().getAccountId(), email, employeeId);
    }

    @Override
    public Class<?> getType() {
        return Employee.class;
    }

    public static class Builder {

		private Employee employee;

		public Builder employee(Employee employee) {
			this.employee = employee;
			return this;
		}

		public EmployeeForm build() {
			EmployeeForm form = new EmployeeForm();
			form.setFirstName(employee.getFirstName());
			form.setLastName(employee.getLastName());
			form.setEmail(employee.getEmail());
			form.setPhoneNumber(employee.getPhone());
			form.setEmployeeId(employee.getSlug());
			form.setTitle(employee.getPositionName());

			String[] groups = new String[employee.getGroups().size()];
			int index = 0;
			for (GroupEmployee groupEmployee : employee.getGroups()) {
				Group group = groupEmployee.getGroup();
				groups[index++] = group.getName();
			}

			form.setGroups(groups);

			return form;
		}

	}
}