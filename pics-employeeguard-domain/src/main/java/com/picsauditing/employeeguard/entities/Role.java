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
@DiscriminatorValue("Role")
@SQLInsert(sql = "INSERT INTO account_group (accountID, createdBy, createdDate, deletedBy, deletedDate, description, name, type, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, 'Role', ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE account_group SET deletedDate = NOW() WHERE id = ?")
public class Role extends AccountGroup implements Comparable<Role> {

	private static final long serialVersionUID = 7074027976165804080L;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<ProjectRole> projects = new ArrayList<>();

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<AccountSkillRole> skills = new ArrayList<>();

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 10)
	private List<SiteAssignment> siteAssignments = new ArrayList<>();

	public Role() {
	}

	public Role(int id, int accountId) {
		this.id = id;
		this.accountId = accountId;
	}

	public Role(Role group) {
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

	public Role(int id) {
		this.id = id;
	}

	public List<ProjectRole> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectRole> projects) {
		this.projects = projects;
	}

	public List<AccountSkillRole> getSkills() {
		return skills;
	}

	public void setSkills(List<AccountSkillRole> skills) {
		this.skills = skills;
	}

	public List<SiteAssignment> getSiteAssignments() {
		return siteAssignments;
	}

	public void setSiteAssignments(List<SiteAssignment> siteAssignments) {
		this.siteAssignments = siteAssignments;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Role that = (Role) o;

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
	public int compareTo(final Role that) {
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
