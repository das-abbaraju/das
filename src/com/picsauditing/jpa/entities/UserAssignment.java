package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "user_assignment")
public class UserAssignment extends BaseTable implements Comparable<UserAssignment> {

	private User user;
	private UserAssignmentType assignmentType;
	private CountrySubdivision countrySubdivision;
	private Country country;
	private String postalStart;
	private String postalEnd;
	private ContractorAccount contractor;
	private AuditType auditType;

	@ManyToOne
	@JoinColumn(name = "userID", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public UserAssignmentType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(UserAssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	@ManyToOne
	@JoinColumn(name = "countrySubdivision")
	public CountrySubdivision getCountrySubdivision() {
		return countrySubdivision;
	}

	public void setCountrySubdivision(CountrySubdivision countrySubdivision) {
		this.countrySubdivision = countrySubdivision;
	}

	@ManyToOne
	@JoinColumn(name = "country")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name = "postal_start")
	public String getPostalStart() {
		return postalStart;
	}

	public void setPostalStart(String postalStart) {
		this.postalStart = postalStart;
	}

	@Column(name = "postal_end")
	public String getPostalEnd() {
		return postalEnd;
	}

	public void setPostalEnd(String postalEnd) {
		this.postalEnd = postalEnd;
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@Transient
	public Integer getPriority() {
		int priority = 0;

		if (country != null)
			priority += 1;
		if (countrySubdivision != null)
			priority += 10;
		if (postalStart != null || postalEnd != null)
			priority += 100;
		if (contractor != null)
			priority += 1000;

		return priority;
	}

	@Override
	public int compareTo(UserAssignment o) {
		return getPriority().compareTo(o.getPriority());
	}

	@Override
	public String toString() {
		return String.format("%s countrySubdivision:%s, country:%s, zip:%s-%s, contractor:%s, auditType:%s", user.getName(), countrySubdivision,
				country, postalStart, postalEnd, contractor == null ? null : contractor.getName(),
				auditType == null ? null : auditType.getName().toString());
	}
}
