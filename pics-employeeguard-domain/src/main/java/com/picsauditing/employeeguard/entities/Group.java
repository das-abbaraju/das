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
@DiscriminatorValue("Group")
@SQLInsert(sql = "INSERT INTO account_group (accountID, createdBy, createdDate, deletedBy, deletedDate, description, name, type, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, 'Group', ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE account_group SET deletedDate = NOW() WHERE id = ?")
public class Group extends AccountGroup implements Comparable<Group> {

	private static final long serialVersionUID = 7074027976165804080L;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	protected List<GroupEmployee> employees = new ArrayList<>();

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	protected List<AccountSkillGroup> skills = new ArrayList<>();

	public Group() {
	}

	public Group(int id, int accountId) {
		this.id = id;
		this.accountId = accountId;
	}

	public Group(Group group) {
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

	public List<GroupEmployee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<GroupEmployee> groupEmployees) {
		this.employees = groupEmployees;
	}

//	public List<ProjectRole> getProjects() {
//		return projects;
//	}
//
//	public void setProjects(List<ProjectRole> projects) {
//		this.projects = projects;
//	}

	public List<AccountSkillGroup> getSkills() {
		return skills;
	}

	public void setSkills(List<AccountSkillGroup> skills) {
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
	public int compareTo(final Group that) {
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
