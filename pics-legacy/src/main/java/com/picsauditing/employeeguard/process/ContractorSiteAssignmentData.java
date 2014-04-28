package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;

import java.util.Map;
import java.util.Set;

public class ContractorSiteAssignmentData {

//	PicsCollectionUtil.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getSiteAssignmentSkills().keySet());
//	PicsCollectionUtil.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getProjectJobRoleSkills().keySet());
//	PicsCollectionUtil.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getSiteRequiredSkills().keySet());
//	PicsCollectionUtil.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getCorporateRequiredSkills().keySet());

	private Set<AccountSkill> siteAndCorporateRequiredSkills;
	private Map<Project, Set<Role>> projectJobRoles;
	private Set<Role> siteAssignmentRoles;
//	private

}
