package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.BaseEntity;
import com.picsauditing.util.Strings;

import java.util.Date;

/**
 * Only used for testing
 */
public class FakePerson implements BaseEntity, Comparable<FakePerson> {

	private int id;

	private String firstName;
	private String lastName;
	private int age;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	private Date createdDate;
	private Date updatedDate;
	private Date deletedDate;

	public FakePerson() {
	}

	public FakePerson(final String firstName, final String lastName, final int age) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public int getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public int getUpdatedBy() {
		return updatedBy;
	}

	@Override
	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public int getDeletedBy() {
		return deletedBy;
	}

	@Override
	public void setDeletedBy(int deletedBy) {
		this.deletedBy = deletedBy;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Date getUpdatedDate() {
		return updatedDate;
	}

	@Override
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public Date getDeletedDate() {
		return deletedDate;
	}

	@Override
	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FakePerson that = (FakePerson) o;

		if (age != that.age) return false;
		if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
		if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = firstName != null ? firstName.hashCode() : 0;
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + age;
		return result;
	}

	@Override
	public int compareTo(FakePerson that) {
		if (this.equals(that)) {
			return 0;
		}

		int compare = 0;
		if (Strings.isNotEmpty(firstName) && Strings.isEmpty(that.firstName)) {
			compare = firstName.compareToIgnoreCase(that.firstName);
		}

		if (compare == 0 && Strings.isEmpty(lastName) && Strings.isEmpty(that.lastName)) {
			compare = lastName.compareToIgnoreCase(that.lastName);
		}

		if (compare == 0) {
			compare = Integer.compare(age, that.age);
		}

		return compare;
	}
}
