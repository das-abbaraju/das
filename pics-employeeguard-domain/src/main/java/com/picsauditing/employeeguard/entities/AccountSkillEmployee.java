package com.picsauditing.employeeguard.entities;

import com.picsauditing.util.Strings;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "account_skill_employee")
@Where(clause = "deletedDate IS NULL")
//@SQLInsert(sql = "INSERT INTO account_skill_employee (createdBy, createdDate, deletedBy, deletedDate, employeeID, finishDate, documentID, skillID, startDate, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
//@SQLDelete(sql = "UPDATE account_skill_employee SET deletedDate = NOW() WHERE id = ?")
public class AccountSkillEmployee implements BaseEntity {

	private static final long serialVersionUID = 2117121655234863607L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "skillID", nullable = false)
	private AccountSkill skill;

	@ManyToOne
	@JoinColumn(name = "employeeID", nullable = false)
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "documentID")
	private ProfileDocument profileDocument;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "finishDate")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	public static transient final Comparator<AccountSkillEmployee> COMPARATOR = new Comparator<AccountSkillEmployee>() {
		@Override
		public int compare(AccountSkillEmployee o1, AccountSkillEmployee o2) {
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

		private boolean areEqual(AccountSkillEmployee o1, AccountSkillEmployee o2) {
			return ((o1.getSkill().equals(o2.getSkill())) && o1.getEmployee().equals(o2.getEmployee()));
		}
	};

	public AccountSkillEmployee() {
	}

	public AccountSkillEmployee(AccountSkill skill, Employee employee) {
		this.skill = skill;
		this.employee = employee;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AccountSkill getSkill() {
		return skill;
	}

	public void setSkill(AccountSkill skill) {
		this.skill = skill;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public ProfileDocument getProfileDocument() {
		return profileDocument;
	}

	public void setProfileDocument(ProfileDocument profileDocument) {
		this.profileDocument = profileDocument;
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

		AccountSkillEmployee accountSkillEmployee = (AccountSkillEmployee) o;

		if (getSkill() != null ? !getSkill().equals(accountSkillEmployee.getSkill()) : accountSkillEmployee.getSkill() != null)
			return false;
		if (getEmployee() != null ? !getEmployee().equals(accountSkillEmployee.getEmployee()) : accountSkillEmployee.getEmployee() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * (getSkill() != null ? getSkill().hashCode() : 0);
		result = 31 * result + (getEmployee() != null ? getEmployee().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		String string = Strings.EMPTY_STRING;

		if (employee != null) {
			string += " " + employee.toString();
		}

		if (skill != null) {
			string += " " + skill.getName();
		}

		return string;
	}
}
