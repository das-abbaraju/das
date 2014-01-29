package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("SiteAssignment")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO account_group (accountID, createdBy, createdDate, deletedBy, deletedDate, description, name, type, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, 'SiteAssignment', ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE account_group SET deletedDate = NOW() WHERE id = ?")
public class SiteAssignment extends AccountGroup implements Comparable<SiteAssignment> {

	@OneToMany(mappedBy = "siteAssignment", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	protected List<SiteAssignmentEmployee> employees = new ArrayList<>();

	@OneToMany(mappedBy = "siteAssignment", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	protected List<AccountSkillSiteAssignment> skills = new ArrayList<>();

	public SiteAssignment() {
	}

	public SiteAssignment(int id, int accountId) {
		this.id = id;
		this.accountId = accountId;
	}

	public SiteAssignment(Group group) {
		super.id = group.id;
		super.accountId = group.accountId;
		super.name = group.name;
		super.description = group.description;
		super.createdBy = group.createdBy;
		super.createdDate = group.createdDate;
		super.updatedBy = group.updatedBy;
		super.updatedDate = group.updatedDate;
		super.deletedBy = group.deletedBy;
		super.deletedDate = group.deletedDate;
	}

	public List<SiteAssignmentEmployee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<SiteAssignmentEmployee> groupEmployees) {
		this.employees = groupEmployees;
	}

	public List<AccountSkillSiteAssignment> getSkills() {
		return skills;
	}

	public void setSkills(List<AccountSkillSiteAssignment> skills) {
		this.skills = skills;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Group that = (Group) o;

		if (getAccountId() != that.getAccountId()) return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * getAccountId();
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(final SiteAssignment that) {
		if (this == that) {
			return 0;
		}

		int comparison = this.getName().compareToIgnoreCase(that.getName());
		if (comparison != 0) {
			return comparison;
		}

		return 0;
	}
}
