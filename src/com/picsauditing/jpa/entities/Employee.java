package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private Account account;
	private EmployeeClassification classification = EmployeeClassification.FullTime;
	private boolean active = true;
	private Date hireDate;
	private Date fireDate;
	private String title;
	private String location;
	private String email;
	private String phone;
	private String ssn;
	private Date birthDate;
	private String photo;
	private Date twicExpiration;
	private Date shellTraining;

	List<EmployeeRole> employeeRoles = new ArrayList<EmployeeRole>();
	List<EmployeeSite> employeeSites = new ArrayList<EmployeeSite>();
	Set<EmployeeQualification> employeeQualifications = new HashSet<EmployeeQualification>();
	List<AssessmentResult> assessmentResults = new ArrayList<AssessmentResult>();

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

	@ManyToOne(optional = false)
	@JoinColumn(name = "accountID", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Enumerated(EnumType.STRING)
	public EmployeeClassification getClassification() {
		return classification;
	}

	public void setClassification(EmployeeClassification classification) {
		this.classification = classification;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	@Column(length = 9)
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

	@OneToMany(mappedBy = "employee")
	public List<EmployeeRole> getEmployeeRoles() {
		return employeeRoles;
	}

	public void setEmployeeRoles(List<EmployeeRole> jobRoles) {
		this.employeeRoles = jobRoles;
	}

	@OneToMany(mappedBy = "employee")
	public List<EmployeeSite> getEmployeeSites() {
		return employeeSites;
	}

	public void setEmployeeSites(List<EmployeeSite> employeeSites) {
		this.employeeSites = employeeSites;
	}

	@OneToMany(mappedBy = "employee", cascade = { CascadeType.ALL })
	public Set<EmployeeQualification> getEmployeeQualifications() {
		return employeeQualifications;
	}

	public void setEmployeeQualifications(Set<EmployeeQualification> employeeQualifications) {
		this.employeeQualifications = employeeQualifications;
	}

	@OneToMany(mappedBy = "employee", cascade = { CascadeType.REMOVE })
	public List<AssessmentResult> getAssessmentResults() {
		return assessmentResults;
	}

	public void setAssessmentResults(List<AssessmentResult> assessmentResults) {
		this.assessmentResults = assessmentResults;
	}
	@Temporal(TemporalType.DATE)
	public Date getTwicExpiration() {
		return twicExpiration;
	}
	
	public void setTwicExpiration(Date twicExpiration) {
		this.twicExpiration = twicExpiration;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getShellTraining() {
		return shellTraining;
	}
	
	public void setShellTraining(Date shellTraining) {
		this.shellTraining = shellTraining;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("firstName", firstName);
		json.put("lastName", lastName);
		json.put("account", account.toJSON());
		json.put("classification", classification == null ? null : classification.toString());
		json.put("status", active);
		json.put("hireDate", hireDate == null ? null : hireDate.getTime());
		json.put("fireDate", fireDate == null ? null : fireDate.getTime());
		json.put("title", title);
		json.put("location", location);
		json.put("email", email);
		json.put("phone", phone);
		json.put("ssn", Strings.maskSSN(ssn));
		json.put("birthDate", birthDate == null ? null : birthDate.getTime());
		json.put("photo", photo);

		return json;
	}

	@SuppressWarnings("unchecked")
	@Transient
	public JSONArray toTableJSON() {
		return new JSONArray() {

			{
				add(id);
				add(lastName);
				add(firstName);
				add(title == null ? "" : title);
				add(classification == null ? null : classification.toString());
			}
		};
	}
	
	@Override
	public String toString() {
		return (firstName + " " + lastName).trim() + " (" + id + ")";
	}
}
