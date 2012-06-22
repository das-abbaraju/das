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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;
import org.json.simple.JSONObject;

import com.picsauditing.search.IndexOverrideWeight;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.search.IndexableOverride;
import com.picsauditing.util.Strings;
 

@SuppressWarnings("serial")
@Entity
@Table(name = "employee")
@IndexableOverride(overrides = { @IndexOverrideWeight(methodName = "getId", weight = 3) })
public class Employee extends AbstractIndexableTable {

	private String firstName;
	private String lastName;
	private Account account;
	private EmployeeClassification classification = EmployeeClassification.FullTime;
	private boolean active = true;
	private UserStatus status = UserStatus.Active;
	private boolean needsIndexing = false;
	private String picsNumber;
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
	private int needsRecalculation;
	private Date lastRecalculation;
	private User user;

	List<EmployeeRole> employeeRoles = new ArrayList<EmployeeRole>();
	List<EmployeeCompetency> employeeCompetencies = new ArrayList<EmployeeCompetency>();
	List<EmployeeSite> employeeSites = new ArrayList<EmployeeSite>();
	Set<EmployeeQualification> employeeQualifications = new HashSet<EmployeeQualification>();
	List<AssessmentResult> assessmentResults = new ArrayList<AssessmentResult>();
	protected List<ContractorAudit> audits = new ArrayList<ContractorAudit>();

	@IndexableField(type = IndexValueType.CLEANSTRING, weight = 4)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@IndexableField(type = IndexValueType.CLEANSTRING, weight = 5)
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

	/**
	 * Used for Indexable/Interface
	 * 
	 * @return Returns display name
	 */
	@Transient
	public String getName() {
		return getDisplayName();
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
	//@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.UserStatus") })
	@Enumerated(EnumType.STRING)
	public UserStatus getStatus(){
		return status;
	}
	public void setStatus(UserStatus status){
		this.status = status;
	}
	/**
	 * PICS Worker Number aka Worker Access Code
	 * 
	 * @return
	 */
	public String getPicsNumber() {
		return picsNumber;
	}

	public void setPicsNumber(String picsNumber) {
		this.picsNumber = picsNumber;
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

	@IndexableField(type = IndexValueType.EMAILTYPE, weight = 5)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@IndexableField(type = IndexValueType.PHONETYPE, weight = 2)
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
	public List<EmployeeCompetency> getEmployeeCompetencies() {
		return employeeCompetencies;
	}

	public void setEmployeeCompetencies(List<EmployeeCompetency> employeeCompetencies) {
		this.employeeCompetencies = employeeCompetencies;
	}

	@Transient
	public boolean isPrevAssigned() {
		for (EmployeeSite site : employeeSites) {
			if (!site.isCurrent())
				return true;
		}
		return false;
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

	public int getNeedsRecalculation() {
		return needsRecalculation;
	}

	public void setNeedsRecalculation(int needsRecalculation) {
		this.needsRecalculation = needsRecalculation;
	}

	public Date getLastRecalculation() {
		return lastRecalculation;
	}

	public void setLastRecalculation(Date lastRecalculation) {
		this.lastRecalculation = lastRecalculation;
	}

	@ManyToOne
	@JoinColumn(name = "userID")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToMany(mappedBy = "employee")
	@Where(clause = "expiresDate > NOW() OR expiresDate IS NULL")
	public List<ContractorAudit> getAudits() {
		return this.audits;
	}

	public void setAudits(List<ContractorAudit> audits) {
		this.audits = audits;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("firstName", firstName);
		json.put("lastName", lastName);
		json.put("account", account.toJSON());
		json.put("classification", classification == null ? null : classification.toString());
		json.put("status", status);
		json.put("hireDate", hireDate == null ? null : hireDate.getTime());
		json.put("fireDate", fireDate == null ? null : fireDate.getTime());
		json.put("title", title);
		json.put("location", location);
		json.put("email", email);
		json.put("phone", phone);
		json.put("ssn", Strings.maskSSN(ssn));
		json.put("birthDate", birthDate == null ? null : birthDate.getTime());
		json.put("photo", photo);
		json.put("twicExpiration", twicExpiration == null ? null : twicExpiration.getTime());

		return json;
	}

	@Override
	public String toString() {
		return (firstName + " " + lastName).trim() + " (" + id + ")";
	}

	@Transient
	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 2)
	public String getType() {
		return "EMPLOYEE";
	}
	
	@Transient
	public String getNameTitle() {
		String last = (Strings.isEmpty(lastName))? "" : lastName.trim();
		String first = (Strings.isEmpty(firstName))? "" : firstName.trim();
		String position = (Strings.isEmpty(title))? "" : title.trim();
		String nameTitle = first + " " + last;
		if (!Strings.isEmpty(position))
			nameTitle += " / " + position;
		return nameTitle;
	}

	@Transient
	public String getIndexType() {
		return "E";
	}

	@Override
	public boolean isNeedsIndexing() {
		return needsIndexing;
	}

	@Override
	public void setNeedsIndexing(boolean needsIndexing) {
		this.needsIndexing = needsIndexing;
	}

	@Transient
	public String getReturnType() {
		return "employee";
	}

	@Transient
	public String getSearchText() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getReturnType()).append('|').append("Employee").append('|').append(this.id).append('|').append(
				this.getDisplayName()).append('|').append(this.account.name).append("\n");
		return sb.toString();
	}

	@Transient
	public String getViewLink() {
		return "ManageEmployees.action?employee=" + this.id;
	}

	@Transient
	public boolean isRemoved() {
		return (status == UserStatus.Inactive || status == UserStatus.Deleted);
	}
}
