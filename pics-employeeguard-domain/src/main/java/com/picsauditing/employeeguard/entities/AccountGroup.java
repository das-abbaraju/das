package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "account_group")
@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
@SQLInsert(sql = "INSERT INTO account_group (accountID, createdBy, createdDate, deletedBy, deletedDate, description, name, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
public class AccountGroup implements BaseEntity, Comparable<AccountGroup> {

	private static final long serialVersionUID = 7074027976165804080L;

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

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
	private List<AccountSkillGroup> skills = new ArrayList<>();

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
	private List<AccountGroupEmployee> employees = new ArrayList<>();

	public AccountGroup() {

	}

	public AccountGroup(int id, int accountId) {
		this.id = id;
		this.accountId = accountId;
	}

	public AccountGroup(AccountGroup accountGroup) {
		this.id = accountGroup.id;
		this.accountId = accountGroup.accountId;
		this.name = accountGroup.name;
		this.description = accountGroup.description;
		this.createdBy = accountGroup.createdBy;
		this.createdDate = accountGroup.createdDate;
		this.updatedBy = accountGroup.updatedBy;
		this.updatedDate = accountGroup.updatedDate;
		this.deletedBy = accountGroup.deletedBy;
		this.deletedDate = accountGroup.deletedDate;
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

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccountGroup that = (AccountGroup) o;

		if (getAccountId() != that.getAccountId()) return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
//		if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * getAccountId();
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
//		result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(final AccountGroup that) {
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
