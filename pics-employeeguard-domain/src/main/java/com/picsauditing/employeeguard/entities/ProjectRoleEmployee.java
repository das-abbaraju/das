package com.picsauditing.employeeguard.entities;

import com.picsauditing.employeeguard.util.Extractor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "project_account_group_employee")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO project_account_group_employee (createdBy, createdDate, deletedBy, deletedDate, employeeID, projectGroupID, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE project_account_group_employee SET deletedDate = NOW() WHERE id = ?")
public class ProjectRoleEmployee implements BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "projectGroupID", nullable = false)
	private ProjectRole projectRole;

	@ManyToOne
	@JoinColumn(name = "employeeID", nullable = false)
	private Employee employee;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	public ProjectRoleEmployee() {
	}

	public ProjectRoleEmployee(ProjectRole projectRole, Employee employee) {
		this.projectRole = projectRole;
		this.employee = employee;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public ProjectRole getProjectRole() {
		return projectRole;
	}

	public void setProjectRole(ProjectRole projectRole) {
		this.projectRole = projectRole;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
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

		ProjectRoleEmployee that = (ProjectRoleEmployee) o;

		if (projectRole != null ? !projectRole.equals(that.projectRole) : that.projectRole != null) return false;
		if (employee != null ? !employee.equals(that.employee) : that.employee != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = projectRole.getId();
		result = 31 * result + (employee != null ? employee.hashCode() : 0);
		return result;
	}

	public static transient final Comparator<ProjectRoleEmployee> COMPARATOR = new Comparator<ProjectRoleEmployee>() {
		@Override
		public int compare(ProjectRoleEmployee o1, ProjectRoleEmployee o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (areEqual(o1, o2)) {
				return 0;
			}

			if (!o1.projectRole.equals(o2.projectRole) || !o1.employee.equals(o2.employee)) {
				return -1;
			}

			return 1;
		}

		private boolean areEqual(ProjectRoleEmployee o1, ProjectRoleEmployee o2) {
			return o1.projectRole.equals(o2.projectRole) && o1.employee.equals(o2.employee);
		}
	};

	public static transient final Extractor<ProjectRoleEmployee, Project> PROJECT_EXTRACTOR = new Extractor<ProjectRoleEmployee, Project>() {
		@Override
		public Project extract(ProjectRoleEmployee projectRoleEmployee) {
			return projectRoleEmployee.getProjectRole().getProject();
		}
	};
}
