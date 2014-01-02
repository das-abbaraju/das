package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable Object that contains all the information about an Employee's skills and their relationship
 * to different areas of EmployeeGUARD
 */
public class SkillUsages {

	private final Employee employee;
	private final Map<AccountSkill, Set<Project>> projectRequiredSkills;
	private final Map<AccountSkill, Set<Group>> contractorGroupSkills;
	private final Map<AccountSkill, Set<Group>> projectJobRoleSkills;
	private final Map<AccountSkill, Set<Integer>> corporateRequiredSkills;
	private final Map<AccountSkill, Set<Integer>> siteRequiredSkills;
	private final Map<AccountSkill, Set<SiteAssignment>> siteAssignmentSkills;

	public SkillUsages(final Builder builder) {
		this.employee = builder.employee;

		this.projectRequiredSkills = builder.projectRequiredSkills == null
				? Collections.<AccountSkill, Set<Project>>emptyMap()
				: Collections.unmodifiableMap(builder.projectRequiredSkills);

		this.contractorGroupSkills = Collections.unmodifiableMap(builder.contractorGroupSkills);

		this.projectJobRoleSkills = Collections.unmodifiableMap(builder.projectJobRoleSkills);

		this.corporateRequiredSkills = Collections.unmodifiableMap(builder.corporateRequiredSkills);

		this.siteRequiredSkills = Collections.unmodifiableMap(builder.siteRequiredSkills);

		this.siteAssignmentSkills = Collections.unmodifiableMap(builder.siteAssignmentSkills);
	}

	public Employee getEmployee() {
		return employee;
	}

	public Map<AccountSkill, Set<Project>> getProjectRequiredSkills() {
		return projectRequiredSkills;
	}

	public Map<AccountSkill, Set<Group>> getContractorGroupSkills() {
		return contractorGroupSkills;
	}

	public Map<AccountSkill, Set<Group>> getProjectJobRoleSkills() {
		return projectJobRoleSkills;
	}

	public Map<AccountSkill, Set<Integer>> getCorporateRequiredSkills() {
		return corporateRequiredSkills;
	}

	public Map<AccountSkill, Set<Integer>> getSiteRequiredSkills() {
		return siteRequiredSkills;
	}

	public Map<AccountSkill, Set<SiteAssignment>> getSiteAssignmentSkills() {
		return siteAssignmentSkills;
	}

	public Set<AccountSkill> allSkills() {
		Set<AccountSkill> allSkills = new HashSet<>();
		allSkills.addAll(projectRequiredSkills.keySet());
		allSkills.addAll(contractorGroupSkills.keySet());
		allSkills.addAll(projectJobRoleSkills.keySet());
		allSkills.addAll(corporateRequiredSkills.keySet());
		allSkills.addAll(siteRequiredSkills.keySet());
		return allSkills;
	}

	public boolean usedOnMultipleProjects(final AccountSkill skill) {
		return checkMap(skill, projectRequiredSkills);
	}

	public boolean usedOnMultipleContractorGroups(final AccountSkill skill) {
		return checkMap(skill, contractorGroupSkills);
	}

	public boolean usedOnMultipleProjectRoles(final AccountSkill skill) {
		return checkMap(skill, projectJobRoleSkills);
	}

	private <E> boolean checkMap(final AccountSkill skill, final Map<AccountSkill, Set<E>> map) {
		if (!map.containsKey(skill)) {
			return false;
		}

		Set<E> usages = map.get(skill);
		return CollectionUtils.isEmpty(usages) ? false : usages.size() > 1;
	}

	public static class Builder {

		private Employee employee;
		private Map<AccountSkill, Set<Project>> projectRequiredSkills;
		private Map<AccountSkill, Set<Group>> contractorGroupSkills;
		private Map<AccountSkill, Set<Group>> projectJobRoleSkills;
		private Map<AccountSkill, Set<Integer>> corporateRequiredSkills;
		private Map<AccountSkill, Set<Integer>> siteRequiredSkills;
		private Map<AccountSkill, Set<SiteAssignment>> siteAssignmentSkills;

		public Builder employee(final Employee employee) {
			this.employee = employee;
			return this;
		}

		public Builder projectRequiredSkills(final Map<AccountSkill, Set<Project>> projectRequiredSkills) {
			this.projectRequiredSkills = projectRequiredSkills;
			return this;
		}

		public Builder contractorGroupSkills(final Map<AccountSkill, Set<Group>> contractorGroupSkills) {
			this.contractorGroupSkills = contractorGroupSkills;
			return this;
		}

		public Builder projectJobRoleSkills(final Map<AccountSkill, Set<Group>> projectJobRoleSkills) {
			this.projectJobRoleSkills = projectJobRoleSkills;
			return this;
		}

		public Builder corporateRequiredSkills(final Map<AccountSkill, Set<Integer>> corporateRequiredSkills) {
			this.corporateRequiredSkills = corporateRequiredSkills;
			return this;
		}

		public Builder siteRequiredSkills(final Map<AccountSkill, Set<Integer>> siteRequiredSkills) {
			this.siteRequiredSkills = siteRequiredSkills;
			return this;
		}

		public Builder siteAssignmentSkills(final Map<AccountSkill, Set<SiteAssignment>> siteAssignmentSkills) {
			this.siteAssignmentSkills = siteAssignmentSkills;
			return this;
		}

		public SkillUsages build() {
			return new SkillUsages(this);
		}
	}
}