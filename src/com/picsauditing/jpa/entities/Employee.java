package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
public class Employee extends BaseTable {

	private String firstName;
	private String lastName;
	private Date hireDate;
	private Date fireDate;
	private String title;
	private String location;
	private String email;
	private String phone;
	private String ssn;
	private Date birthDate;
	private String photo;

	private List<AccountEmployee> accounts = new ArrayList<AccountEmployee>();

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

	@Transient
	public String getDisplayName() {
		return firstName + " " + lastName;
	}

	@Temporal(TemporalType.DATE)
	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getFireDate() {
		return fireDate;
	}

	public void setFireDate(Date fireDate) {
		this.fireDate = fireDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	@Temporal(TemporalType.DATE)
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@OneToMany(mappedBy = "employee", cascade = { CascadeType.ALL })
	public List<AccountEmployee> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountEmployee> accounts) {
		this.accounts = accounts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("firstName", firstName);
		json.put("lastName", lastName);
		json.put("hireDate", hireDate == null ? null : hireDate.getTime());
		json.put("fireDate", fireDate == null ? null : fireDate.getTime());
		json.put("title", title);
		json.put("location", location);
		json.put("email", email);
		json.put("phone", phone);
		json.put("ssn", Strings.maskSSN(ssn));
		json.put("birthDate", birthDate == null ? null : birthDate.getTime());
		json.put("photo", photo);

		if (full) {
			JSONArray array = new JSONArray();
			for (AccountEmployee accountEmployee : accounts) {
				array.add(accountEmployee.toJSON());
			}

			json.put("accounts", array);
		}

		return json;
	}
}
