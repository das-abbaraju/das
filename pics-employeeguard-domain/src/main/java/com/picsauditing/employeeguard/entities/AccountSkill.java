package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "account_skill")
@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
@SQLInsert(sql = "INSERT INTO account_skill (accountID, createdBy, createdDate, deletedBy, deletedDate, description, intervalPeriod, intervalType, name, ruleType, skillType, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
public class AccountSkill implements BaseEntity, Comparable<AccountSkill> {

	private static final long serialVersionUID = -3528270237051318527L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "accountID", nullable = false)
	private int accountId;

	@Enumerated(EnumType.STRING)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {
			@Parameter(name = "enumClass", value = "com.picsauditing.employeeguard.entities.SkillType"),
			@Parameter(name = "identifierMethod", value = "getDbValue"),
			@Parameter(name = "valueOfMethod", value = "fromDbValue")})
	private SkillType skillType;

	@Enumerated(EnumType.STRING)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {
			@Parameter(name = "enumClass", value = "com.picsauditing.employeeguard.entities.RuleType"),
			@Parameter(name = "identifierMethod", value = "getDbValue"),
			@Parameter(name = "valueOfMethod", value = "fromDbValue")})
	private RuleType ruleType;

	private String name;

	@Enumerated(EnumType.STRING)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {
			@Parameter(name = "enumClass", value = "com.picsauditing.employeeguard.entities.IntervalType"),
			@Parameter(name = "identifierMethod", value = "getDbValue"),
			@Parameter(name = "valueOfMethod", value = "fromDbValue")})
	private IntervalType intervalType;

	private int intervalPeriod;

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

	@OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
    @BatchSize(size = 5)
	private List<AccountSkillGroup> groups = new ArrayList<>();

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "deletedDate IS NULL AND deletedBy = 0")
    @BatchSize(size = 10)
    private List<AccountSkillEmployee> employees = new ArrayList<>();

	@OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
	@BatchSize(size = 10)
	private List<ProjectSkill> projects = new ArrayList<>();

	public AccountSkill() {
	}

	public AccountSkill(int id, int accountId) {
		this.id = id;
		this.accountId = accountId;
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


	public SkillType getSkillType() {
		return skillType;
	}

	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IntervalType getIntervalType() {
		return intervalType;
	}

	public void setIntervalType(IntervalType intervalType) {
		this.intervalType = intervalType;
	}

	public int getIntervalPeriod() {
		return intervalPeriod;
	}

	public void setIntervalPeriod(int intervalPeriod) {
		this.intervalPeriod = intervalPeriod;
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

	public List<AccountSkillGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<AccountSkillGroup> groups) {
		this.groups = groups;
	}

    public List<AccountSkillEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<AccountSkillEmployee> employees) {
        this.employees = employees;
    }

	public List<ProjectSkill> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectSkill> projects) {
		this.projects = projects;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccountSkill that = (AccountSkill) o;

		if (getAccountId() != that.getAccountId()) return false;
		if (getSkillType() != null ? !(getSkillType() == that.getSkillType()) : that.getSkillType() != null)
			return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * getAccountId();
		result = 31 * result + (getSkillType() != null ? getSkillType().hashCode() : 0);
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(final AccountSkill that) {
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
