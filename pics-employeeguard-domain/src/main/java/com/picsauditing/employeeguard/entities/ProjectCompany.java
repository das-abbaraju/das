package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "project_account")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO project_account (accountID, createdBy, createdDate, deletedBy, deletedDate, projectID, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE project_account SET deletedDate = NOW() WHERE id = ?")
public class ProjectCompany implements BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "accountID")
	private int accountId;

	@ManyToOne
	@JoinColumn(name = "projectID", nullable = false)
	private Project project;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	public ProjectCompany() {
	}

	public ProjectCompany(Project project, int accountId) {
		this.project = project;
		this.accountId = accountId;
	}

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

	public void setAccountId(int companyId) {
		this.accountId = companyId;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProjectCompany that = (ProjectCompany) o;

		if (accountId != that.accountId) return false;
		if (project != null ? !project.equals(that.project) : that.project != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = accountId;
		result = 31 * result + (project != null ? project.hashCode() : 0);
		return result;
	}

	public static transient final Comparator<ProjectCompany> COMPARATOR = new Comparator<ProjectCompany>() {
		@Override
		public int compare(ProjectCompany o1, ProjectCompany o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (areEqual(o1, o2)) {
				return 0;
			}

			if (o1.getAccountId() != o2.getAccountId()) {
				return -1;
			}

			return 1;
		}

		private boolean areEqual(ProjectCompany o1, ProjectCompany o2) {
			return ((o1.getProject().equals(o2.getProject())) && o1.getAccountId() == o2.getAccountId());
		}
	};
}
