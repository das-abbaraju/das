package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "project_account_group")
@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
@SQLInsert(sql = "INSERT INTO project_account_group (createdBy, createdDate, deletedBy, deletedDate, projectID, groupID, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
public class ProjectRole implements BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "groupID", nullable = false)
	private AccountGroup role;

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

	public ProjectRole() {
	}

	public ProjectRole(Project project, AccountGroup role) {
		this.project = project;
		this.role = role;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public AccountGroup getRole() {
		return role;
	}

	public void setRole(AccountGroup group) {
		this.role = group;
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

		ProjectRole that = (ProjectRole) o;

		if (project != null ? !project.equals(that.project) : that.project != null) return false;
		if (role != null ? !role.equals(that.role) : that.role != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = project.getId();
		result = 31 * result + (role != null ? role.hashCode() : 0);
		return result;
	}

	public static transient final Comparator<ProjectRole> COMPARATOR = new Comparator<ProjectRole>() {
		@Override
		public int compare(ProjectRole o1, ProjectRole o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (areEqual(o1, o2)) {
				return 0;
			}

			if (!o1.getRole().equals(o2.getRole())) {
				return -1;
			}

			return 1;
		}

		private boolean areEqual(ProjectRole o1, ProjectRole o2) {
			return ((o1.getProject().equals(o2.getProject())) && o1.getRole().equals(o2.getRole()));
		}
	};
}
