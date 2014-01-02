package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "account_group")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class AccountGroup implements BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "accountID", nullable = false)
	private int accountId;

	private String name;

	private String description;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<AccountSkillGroup> skills = new ArrayList<>();

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<AccountGroupEmployee> employees = new ArrayList<>();

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<ProjectRole> projects = new ArrayList<>();

	public AccountGroup() {
	}

	public AccountGroup(int id, int accountId) {
		this.id = id;
		this.accountId = accountId;
	}

	public AccountGroup(AccountGroup group) {
		this.id = group.id;
		this.accountId = group.accountId;
		this.name = group.name;
		this.description = group.description;
		this.createdBy = group.createdBy;
		this.createdDate = group.createdDate;
		this.updatedBy = group.updatedBy;
		this.updatedDate = group.updatedDate;
		this.deletedBy = group.deletedBy;
		this.deletedDate = group.deletedDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public int getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(int deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public List<AccountSkillGroup> getSkills() {
		return skills;
	}

	public void setSkills(List<AccountSkillGroup> skills) {
		this.skills = skills;
	}

	public List<AccountGroupEmployee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<AccountGroupEmployee> accountGroupEmployees) {
		this.employees = accountGroupEmployees;
	}

	public List<ProjectRole> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectRole> projects) {
		this.projects = projects;
	}

	public abstract boolean equals(final Object o);

	public abstract int hashCode();

	@Override
	public String toString() {
		return id + " " + name;
	}
}
