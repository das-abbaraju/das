package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "assessment_result_stage")
public class AssessmentResultStage extends BaseTable {

	private Account center;
	private int testID;
	private String resultID;
	private String qualificationType;
	private String qualificationMethod;
	private String description;
	private String employeeID;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private String email;
	private int companyID;
	private String companyName;
	private Date qualificationDate;
	private Account picsAccount;
	private Employee picsEmployee;

	@ManyToOne(optional = false)
	@JoinColumn(name = "centerID", nullable = false)
	public Account getCenter() {
		return center;
	}

	public void setCenter(Account center) {
		this.center = center;
	}

	public int getTestID() {
		return testID;
	}

	public void setTestID(int testID) {
		this.testID = testID;
	}

	public String getResultID() {
		return resultID;
	}

	public void setResultID(String resultID) {
		this.resultID = resultID;
	}

	public String getQualificationType() {
		return qualificationType;
	}

	public void setQualificationType(String qualificationType) {
		this.qualificationType = qualificationType;
	}

	public String getQualificationMethod() {
		return qualificationMethod;
	}

	public void setQualificationMethod(String qualificationMethod) {
		this.qualificationMethod = qualificationMethod;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
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

	@Temporal(TemporalType.DATE)
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthdate) {
		this.birthDate = birthdate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getQualificationDate() {
		return qualificationDate;
	}

	public void setQualificationDate(Date qualificationDate) {
		this.qualificationDate = qualificationDate;
	}

	@ManyToOne
	@JoinColumn(name = "picsAccountID")
	public Account getPicsAccount() {
		return picsAccount;
	}

	public void setPicsAccount(Account picsAccount) {
		this.picsAccount = picsAccount;
	}

	@ManyToOne
	@JoinColumn(name = "picsEmployeeID")
	public Employee getPicsEmployee() {
		return picsEmployee;
	}

	public void setPicsEmployee(Employee picsEmployee) {
		this.picsEmployee = picsEmployee;
	}
}