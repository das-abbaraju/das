package com.picsauditing.employeeguard.entities;

import com.picsauditing.employeeguard.util.Extractor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "project_account_skill")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO project_account_skill (createdBy, createdDate, deletedBy, deletedDate, projectID, skillID, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE project_account_skill SET deletedDate = NOW() WHERE id = ?")
public class ProjectSkill implements BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "skillID", nullable = false)
	private AccountSkill skill;

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

	public ProjectSkill() {
	}

	public ProjectSkill(Project project, AccountSkill skill) {
		this.project = project;
		this.skill = skill;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public AccountSkill getSkill() {
		return skill;
	}

	public void setSkill(AccountSkill skill) {
		this.skill = skill;
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

		ProjectSkill that = (ProjectSkill) o;

		if (project != null ? !project.equals(that.project) : that.project != null) return false;
		if (skill != null ? !skill.equals(that.skill) : that.skill != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = project.getId();
		result = 31 * result + (skill != null ? skill.hashCode() : 0);
		return result;
	}

	public static transient final Comparator<ProjectSkill> COMPARATOR = new Comparator<ProjectSkill>() {
		@Override
		public int compare(ProjectSkill o1, ProjectSkill o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (areEqual(o1, o2)) {
				return 0;
			}

			if (!o1.getSkill().equals(o2.getSkill())) {
				return -1;
			}

			return 1;
		}

		private boolean areEqual(ProjectSkill o1, ProjectSkill o2) {
			return (o1.getProject().equals(o2.getProject())) && o1.getSkill().equals(o2.getSkill());
		}
	};

	public static transient final Extractor<ProjectSkill, AccountSkill> SKILL_EXTRACTOR = new Extractor<ProjectSkill, AccountSkill>() {
		@Override
		public AccountSkill extract(ProjectSkill projectSkill) {
			return projectSkill.getSkill();
		}
	};
}
