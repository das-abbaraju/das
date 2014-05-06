package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Map;
import java.util.Set;

public class ProfileSkillData {

	private Map<AccountSkill, SkillStatus> skillStatusMap;
	private Map<AccountModel, Set<Role>> roles;
	private Map<Role, Set<AccountSkill>> allRoleSkills;
	private Map<AccountModel, Set<Project>> siteProjects;
	private Map<Project, Set<AccountSkill>> projectRequiredSkills;


	private Map<Project, Set<Role>> projectRoles;
	private Set<AccountSkill> siteAndCorporateRequiredSkills;
	private Map<Project, SkillStatus> projectStatuses;
	private Map<Role, SkillStatus> roleStatuses;
	private Set<Role> siteAssignmentRoles;

}
