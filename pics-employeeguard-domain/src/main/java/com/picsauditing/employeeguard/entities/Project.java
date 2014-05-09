package com.picsauditing.employeeguard.entities;

import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.util.Extractor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "project")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO project (accountId, createdBy, createdDate, deletedBy, deletedDate, endDate, location, name, startDate, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE project SET deletedDate = NOW() WHERE id = ?")
public class Project implements BaseEntity, Comparable<Project> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "accountID")
	private int accountId;

	private String name;
	private String location;

	private Date startDate;
	private Date endDate;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<ProjectSkill> skills = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<ProjectRole> roles = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<ProjectCompany> companies = new ArrayList<>();

	private int createdBy;
	private Date createdDate;
	private int updatedBy;
	private Date updatedDate;
	private int deletedBy;
	private Date deletedDate;

	@Override
	public int getId() {
		return id;
	}

	@Override
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<ProjectSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<ProjectSkill> skills) {
		this.skills = skills;
	}

	public List<ProjectRole> getRoles() {
		return roles;
	}

	public void setRoles(List<ProjectRole> groups) {
		this.roles = groups;
	}

	public List<ProjectCompany> getCompanies() {
		return companies;
	}

	public void setCompanies(List<ProjectCompany> companies) {
		this.companies = companies;
	}

	@Override
	public int getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public int getUpdatedBy() {
		return updatedBy;
	}

	@Override
	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public int getDeletedBy() {
		return deletedBy;
	}

	@Override
	public void setDeletedBy(int deletedBy) {
		this.deletedBy = deletedBy;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Date getUpdatedDate() {
		return updatedDate;
	}

	@Override
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public Date getDeletedDate() {
		return deletedDate;
	}

	@Override
	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public final static class ProjectUniqueIndex implements UniqueIndexable {

		private final int id;
		private final int accountId;
		private final String name;

		public ProjectUniqueIndex(final int id, final int accountId, final String name) {
			this.id = id;
			this.accountId = accountId;
			this.name = name;
		}

		@Override
		public Map<String, Map<String, Object>> getUniqueIndexableValues() {
			return Collections.unmodifiableMap(new HashMap<String, Map<String, Object>>() {
				{
					put("accountId", new HashMap<String, Object>() {{
						put("accountId", accountId);
					}});

					put("name", new HashMap<String, Object>() {{
						put("name", name);
					}});
				}
			});
		}

		@Override
		public int getId() {
			return id;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Project project = (Project) o;

		if (accountId != project.accountId) return false;
		if (id != project.id) return false;
		if (name != null ? !name.equals(project.name) : project.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + accountId;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(final Project that) {
		if (this == that) {
			return 0;
		}

		int comparison = this.getName().compareToIgnoreCase(that.getName());
		if (comparison != 0) {
			return comparison;
		}

		return 0;
	}

	public static final Extractor<Project, Integer> ACCOUNT_ID_EXTRACTOR = new Extractor<Project, Integer>() {
		@Override
		public Integer extract(Project project) {
			return project.getAccountId();
		}
	};
}
