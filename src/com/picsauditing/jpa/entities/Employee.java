package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.search.IndexOverrideWeight;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.search.IndexableOverride;
import com.picsauditing.util.Strings;
import org.hibernate.annotations.Where;
import org.json.simple.JSONObject;

import javax.persistence.*;
import javax.persistence.Column;
import java.util.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "employee")
@IndexableOverride(overrides = {@IndexOverrideWeight(methodName = "getId", weight = 3)})
public class Employee extends AbstractIndexableTable implements Comparable<Employee> {
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
	private List<OperatorCompetencyEmployeeFile> competencyFiles = new ArrayList<>();

	@Column(nullable = false)
	@IndexableField(type = IndexValueType.CLEANSTRING, weight = 4)
	@ReportField(type = FieldType.String, importance = FieldImportance.Required)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(nullable = false)
	@IndexableField(type = IndexValueType.CLEANSTRING, weight = 5)
	@ReportField(type = FieldType.String, importance = FieldImportance.Required)
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
	//TODO add EmployeeClassification DR FieldType
	@ReportField(type = FieldType.String)
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

	// @Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings",
	// parameters = { @Parameter(name = "enumClass", value =
	// "com.picsauditing.jpa.entities.UserStatus") })
	@Enumerated(EnumType.STRING)
	//TODO Add UserStatus DR FieldType
	@ReportField(type = FieldType.String, importance = FieldImportance.Average)
	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
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
	@ReportField(type = FieldType.Date)
	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	@Temporal(TemporalType.DATE)
	@ReportField(type = FieldType.Date)
	public Date getFireDate() {
		return fireDate;
	}

	public void setFireDate(Date fireDate) {
		this.fireDate = fireDate;
	}

	@ReportField(type = FieldType.String)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@ReportField(type = FieldType.String)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@IndexableField(type = IndexValueType.EMAILTYPE, weight = 5)
	@ReportField(type = FieldType.String)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@IndexableField(type = IndexValueType.PHONETYPE, weight = 2)
	@ReportField(type = FieldType.String)
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
	@ReportField(type = FieldType.Date)
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

	@OneToMany(mappedBy = "employee", cascade = {CascadeType.ALL})
	public Set<EmployeeQualification> getEmployeeQualifications() {
		return employeeQualifications;
	}

	public void setEmployeeQualifications(Set<EmployeeQualification> employeeQualifications) {
		this.employeeQualifications = employeeQualifications;
	}

	@OneToMany(mappedBy = "employee", cascade = {CascadeType.REMOVE})
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

	@OneToMany(mappedBy = "employee")
	public List<OperatorCompetencyEmployeeFile> getCompetencyFiles() {
		return competencyFiles;
	}

	public void setCompetencyFiles(List<OperatorCompetencyEmployeeFile> competencyFiles) {
		this.competencyFiles = competencyFiles;
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
		String last = (Strings.isEmpty(lastName)) ? "" : lastName.trim();
		String first = (Strings.isEmpty(firstName)) ? "" : firstName.trim();
		String position = (Strings.isEmpty(title)) ? "" : title.trim();
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
		sb.append(this.getReturnType()).append('|').append("Employee").append('|').append(this.id).append('|')
				.append(this.getDisplayName()).append('|').append(this.account.name).append('|')
				.append(this.getStatus()).append("\n");
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

	@Transient
	public OperatorCompetencyEmployeeFileStatus getOverallFileStatus() {
		OperatorCompetencyEmployeeFileStatus fileStatus = OperatorCompetencyEmployeeFileStatus.NA;

		for (OperatorCompetency requiresDocumentation : getCompetenciesRequiringDocumentation()) {
			fileStatus = getFileStatusForCompetency(requiresDocumentation);
			if (fileStatus == OperatorCompetencyEmployeeFileStatus.NEEDED) {
				break;
			}
		}

		return fileStatus;
	}

	@Transient
	public OperatorCompetencyEmployeeFileStatus getFileStatusForCompetency(OperatorCompetency operatorCompetency) {
		OperatorCompetencyEmployeeFileStatus fileStatus = OperatorCompetencyEmployeeFileStatus.NEEDED;

		for (OperatorCompetencyEmployeeFile competencyEmployeeFile : getCompetencyFiles()) {
			if (competencyEmployeeFile.getCompetency().equals(operatorCompetency)) {
				fileStatus = OperatorCompetencyEmployeeFileStatus.PROVIDED;
			}
		}

		return fileStatus;
	}

	@Transient
	private List<OperatorCompetency> getCompetenciesRequiringDocumentation() {
		List<OperatorCompetency> competenciesRequiringDocumentation = new ArrayList<>();
		for (EmployeeCompetency employeeCompetency : getEmployeeCompetencies()) {
			if (employeeCompetency.getCompetency().isRequiresDocumentation()) {
				competenciesRequiringDocumentation.add(employeeCompetency.getCompetency());
			}
		}

		return competenciesRequiringDocumentation;
	}

	@Override
	public int compareTo(Employee o) {
		String otherLastName = Strings.isEmpty(o.getLastName()) ? "" : o.getLastName();
		String otherFirstName = Strings.isEmpty(o.getFirstName()) ? "" : o.getFirstName();
		String otherTitle = Strings.isEmpty(o.getTitle()) ? "" : o.getTitle();

		if (!Strings.isEmpty(lastName) && !otherLastName.equals(lastName)) {
			return lastName.compareTo(otherLastName);
		}

		if (!Strings.isEmpty(firstName) && !otherFirstName.equals(firstName)) {
			return firstName.compareTo(otherFirstName);
		}

		if (!Strings.isEmpty(title) && !otherTitle.equals(title)) {
			return title.compareTo(otherTitle);
		}

		if (id < o.getId()) {
			return -1;
		} else if (id > o.getId()) {
			return 1;
		} else {
			return 0;
		}
	}
}
