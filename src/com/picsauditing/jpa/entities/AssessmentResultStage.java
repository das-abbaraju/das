package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private int companyID;
	private String companyName;
	private Date qualificationDate;
	private int picsAccountID;
	private int picsEmployeeID;
	
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
	
	public int getPicsAccountID() {
		return picsAccountID;
	}
	
	public void setPicsAccountID(int picsAccountID) {
		this.picsAccountID = picsAccountID;
	}
	
	public int getPicsEmployeeID() {
		return picsEmployeeID;
	}
	
	public void setPicsEmployeeID(int picsEmployeeID) {
		this.picsEmployeeID = picsEmployeeID;
	}
}