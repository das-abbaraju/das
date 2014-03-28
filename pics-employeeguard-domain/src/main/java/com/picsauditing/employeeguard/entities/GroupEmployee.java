package com.picsauditing.employeeguard.entities;

import com.picsauditing.employeeguard.util.Extractor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "account_group_employee")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO account_group_employee (createdBy, createdDate, deletedBy, deletedDate, employeeID, groupID, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE account_group_employee SET deletedDate = NOW() WHERE id = ?")
public class GroupEmployee implements BaseEntity {

	private static final long serialVersionUID = 1587847390689342196L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	private int id;

	@ManyToOne(targetEntity = Group.class)
	@JoinColumn(name = "groupID", nullable = false)
	private Group group;

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

	public static transient final Comparator<GroupEmployee> COMPARATOR = new Comparator<GroupEmployee>() {
		@Override
		public int compare(GroupEmployee o1, GroupEmployee o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (areEqual(o1, o2)) {
				return 0;
			}

			if (!o1.getGroup().equals(o2.getGroup())) {
				return -1;
			}

			return 1;
		}

		private boolean areEqual(GroupEmployee o1, GroupEmployee o2) {
			return ((o1.getGroup().equals(o2.getGroup())) && (o1.getEmployee().equals(o2.getEmployee())));
		}
	};

	public GroupEmployee() {
	}

	public GroupEmployee(Employee employee, Group group) {
		this.employee = employee;
		this.group = group;
	}

	public GroupEmployee(GroupEmployee groupEmployee) {
		this.id = groupEmployee.getId();
		this.group = groupEmployee.getGroup();
		this.employee = groupEmployee.getEmployee();
		this.createdBy = groupEmployee.getCreatedBy();
		this.createdDate = groupEmployee.getCreatedDate();
		this.updatedBy = groupEmployee.getUpdatedBy();
		this.updatedDate = groupEmployee.getUpdatedDate();
		this.deletedBy = groupEmployee.getDeletedBy();
		this.deletedDate = groupEmployee.getDeletedDate();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GroupEmployee that = (GroupEmployee) o;

		if (getGroup() != null ? !getGroup().equals(that.getGroup()) : that.getGroup() != null) return false;
		if (getEmployee() != null ? !getEmployee().equals(that.getEmployee()) : that.getEmployee() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * (getGroup() != null ? getGroup().hashCode() : 0);
		result = 31 * result + (getEmployee() != null ? getEmployee().hashCode() : 0);
		return result;
	}

	public static transient final Extractor<GroupEmployee, Group> GROUP_EXTRACTOR = new Extractor<GroupEmployee, Group>() {
		@Override
		public Group extract(GroupEmployee groupEmployee) {
			return groupEmployee.getGroup();
		}
	};
}
