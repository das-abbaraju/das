package com.picsauditing.employeeguard.viewmodel.contractor;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class EmployeeSiteAssignmentModel implements Comparable<EmployeeSiteAssignmentModel> {

	private final int assignments;
	private final int accountId;
	private final String accountName;
	private final int employeeId;
	private final String employeeName;
	private final String employeeTitle;
	private final int numberOfRolesAssigned;
	private final SkillStatus status;
	private final List<SkillStatus> skillStatuses;

	public EmployeeSiteAssignmentModel(final Builder builder) {
		this.assignments = builder.assignments;
		this.accountId = builder.accountId;
		this.accountName = builder.accountName;
		this.employeeId = builder.employeeId;
		this.employeeName = builder.employeeName;
		this.employeeTitle = builder.employeeTitle;
		this.numberOfRolesAssigned = builder.numberOfRolesAssigned;
		this.status = builder.status;
		this.skillStatuses = CollectionUtils.isEmpty(builder.skillStatuses)
				? Collections.<SkillStatus>emptyList() : Collections.unmodifiableList(builder.skillStatuses);
	}

	public int getAssignments() {
		return assignments;
	}

	public int getAccountId() {
		return accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public String getEmployeeTitle() {
		return employeeTitle;
	}

	public int getNumberOfRolesAssigned() {
		return numberOfRolesAssigned;
	}

	public SkillStatus getStatus() {
		return status;
	}

	@Override
	public int compareTo(EmployeeSiteAssignmentModel that) {
		return this.employeeName.compareToIgnoreCase(that.employeeName);
	}

	public static class Builder {
		private int assignments;
		private int accountId;
		private String accountName;
		private int employeeId;
		private String employeeName;
		private String employeeTitle;
		private int numberOfRolesAssigned;
		private SkillStatus status;
		private List<SkillStatus> skillStatuses;

		public Builder assignments(int assignments) {
			this.assignments = assignments;
			return this;
		}

		public Builder accountId(int accountId) {
			this.accountId = accountId;
			return this;
		}

		public Builder accountName(String accountName) {
			this.accountName = accountName;
			return this;
		}

		public Builder employeeId(int employeeId) {
			this.employeeId = employeeId;
			return this;
		}

		public Builder employeeName(String employeeName) {
			this.employeeName = employeeName;
			return this;
		}

		public Builder employeeTitle(String employeeTitle) {
			this.employeeTitle = employeeTitle;
			return this;
		}

		public Builder numberOfRolesAssigned(int numberOfRolesAssigned) {
			this.numberOfRolesAssigned = numberOfRolesAssigned;
			return this;
		}

		public Builder status(SkillStatus status) {
			this.status = status;
			return this;
		}

		public Builder skillStatuses(List<SkillStatus> skillStatuses) {
			this.skillStatuses = skillStatuses;
			return this;
		}

		public EmployeeSiteAssignmentModel build() {
			return new EmployeeSiteAssignmentModel(this);
		}
	}
}
