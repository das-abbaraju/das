package com.picsauditing.employeeguard.services.engine;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectCompanyDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.SkillAssignmentHelper;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillEngine {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private ProjectCompanyDAO projectCompanyDAO;
	@Autowired
	private SkillAssignmentHelper skillAssignmentHelper;

	public void updateSiteSkillsForEmployee(final Employee employee, final int siteId) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		List<Integer> childSiteIds = accountService.getChildOperatorIds(corporateIds);
		Set<Integer> otherEmployeeAssignedSiteIds = getOtherEmployeeAssignedSiteIds(childSiteIds, siteId, employee);

		if (CollectionUtils.isEmpty(otherEmployeeAssignedSiteIds)) {
			removeRequiredCorporateSkillsFromEmployee(employee, corporateIds);
		} else {
			final int contractorId = employee.getAccountId();
			// All of the projects the contractor has been assigned to that is not related to the site
			List<ProjectCompany> projectCompanies = projectCompanyDAO.findByContractorExcludingSite(contractorId, siteId);

//			Map<Role, Role> siteToCorporateRoles = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
			Set<AccountSkill> requiredSkills = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(projectCompanies, employee);
			Set<AccountSkillEmployee> deletableSkills = skillAssignmentHelper.filterNoLongerNeededEmployeeSkills(employee, contractorId, requiredSkills);

			accountSkillEmployeeDAO.deleteByIds(PicsCollectionUtil.getIdsFromCollection(deletableSkills,
					new PicsCollectionUtil.Identitifable<AccountSkillEmployee, Integer>() {
						@Override
						public Integer getId(AccountSkillEmployee accountSkillEmployee) {
							return accountSkillEmployee.getId();
						}
					}));
		}
	}

	private void removeRequiredCorporateSkillsFromEmployee(Employee employee, List<Integer> corporateIds) {
		List<AccountSkillEmployee> accountSkillEmployees =
				accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(employee.getId(), corporateIds);
		accountSkillEmployeeDAO.delete(accountSkillEmployees);
	}

	private Set<Integer> getOtherEmployeeAssignedSiteIds(List<Integer> childSiteIds, int siteId, Employee employee) {
		if (CollectionUtils.isEmpty(childSiteIds)) {
			return Collections.emptySet();
		}

		childSiteIds.remove(new Integer(siteId));
		Set<Integer> otherSitesEmployeeIsAssignedTo = new HashSet<>();
		for (int childSiteId : childSiteIds) {
			if (isEmployeeAssignedToSite(employee, childSiteId)) {
				otherSitesEmployeeIsAssignedTo.add(childSiteId);
			}
		}

		return otherSitesEmployeeIsAssignedTo;
	}

	private boolean isEmployeeAssignedToSite(Employee employee, int childSiteId) {
		if (CollectionUtils.isEmpty(employee.getRoles())) {
			return false;
		}

		for (RoleEmployee roleEmployee : employee.getRoles()) {
			if (roleEmployee.getRole().getAccountId() == childSiteId) {
				return true;
			}
		}

		return false;
	}

}
