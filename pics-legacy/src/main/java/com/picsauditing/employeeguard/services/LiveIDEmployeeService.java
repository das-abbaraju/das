package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.models.factories.LiveIDEmployeeModelFactory;
import com.picsauditing.employeeguard.models.factories.ProjectStatusModelFactory;
import com.picsauditing.employeeguard.models.factories.RoleStatusModelFactory;
import com.picsauditing.employeeguard.models.factories.SkillStatusModelFactory;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusProcess;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class LiveIDEmployeeService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeSiteStatusProcess employeeSiteStatusProcess;
	@Autowired
	private EmployeeService employeeService;

	public LiveIDEmployeeModelFactory.LiveIDEmployeeModel buildLiveIDEmployee(String id, int siteId) {
		Employee employee = employeeService.findEmployee(id);
		Collection<Integer> parentAccountIds = accountService.extractParentAccountIds(siteId);

		//-- Rollup collections but they are still tied to entities
		EmployeeSiteStatusResult essr = employeeSiteStatusProcess.getEmployeeSiteStatusResult(employee.getId(), siteId, parentAccountIds);

		//-- Now copy data from entities into models (DTOs).

		//-- site/corporate required skills
		SkillStatusModelFactory skillStatusModelFactory = ModelFactory.getSkillStatusModelFactory();
		List<SkillStatusModel> reqdSkills = skillStatusModelFactory.create(
				essr.getSiteAndCorporateRequiredSkills(),
				essr.getSkillStatus());
		RequiredSkills corpSiteReqdSkills = new RequiredSkills(reqdSkills);


		//-- Status of project skills
		Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusMap = skillStatusModelFactory.
				createProjectIdToSkillStatusModelMap(
						essr.getProjectRequiredSkills(),
						essr.getSkillStatus()
				);

		//-- status of role skills
		Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusMap = skillStatusModelFactory.
				createRoleIdToSkillStatusModelMap(
						essr.getAllRoleSkills(),
						essr.getSkillStatus()
				);

		//-- skill status within each role within each project  Project-->Role-->Skill
		RoleStatusModelFactory roleStatusModelFactory = ModelFactory.getRoleStatusModelFactory();
		Map<Integer, List<RoleStatusModel>> roleStatusModelMap = roleStatusModelFactory.createProjectIdToRoleModelMap(essr.getProjects(),
				essr.getProjectRoles(),
				roleIdToSkillStatusMap,
				essr.getRoleStatuses()
		);

		Map<Integer, RequiredSkills> projectReqdSkills = createProjectReqdSkillsMapByProjectId(essr.getProjectRequiredSkills(), essr);


		ProjectStatusModelFactory projectStatusModelFactory = ModelFactory.getProjectStatusModelFactory();
		List<ProjectStatusModel> projectStatusModelList = projectStatusModelFactory.create(essr.getProjects(),
				roleStatusModelMap,
				projectReqdSkills,
				essr.getProjectStatuses()
		);


		List<RoleStatusModel> roleStatusModelList = roleStatusModelFactory.create(
				essr.getSiteAssignmentRoles(),
				roleIdToSkillStatusMap,
				essr.getRoleStatuses());

		LiveIDEmployeeModelFactory.LiveIDEmployeeModel liveIDEmployeeModel = ModelFactory.getLiveIDEmployeeModelFactory().prepareLiveIDEmployeeModel(corpSiteReqdSkills, projectStatusModelList, roleStatusModelList);


		return liveIDEmployeeModel;

	}


	private Map<Integer, RequiredSkills> createProjectReqdSkillsMapByProjectId(Map<Project, Set<AccountSkill>> map, EmployeeSiteStatusResult essr) {
		Map<Integer, RequiredSkills> newMap = new HashMap<>();

		SkillStatusModelFactory skillStatusModelFactory = ModelFactory.getSkillStatusModelFactory();

		for (Map.Entry<Project, Set<AccountSkill>> entry : map.entrySet()) {
			List<SkillStatusModel> skillStatusModelList = skillStatusModelFactory.create(entry.getValue(), essr.getSkillStatus());
			newMap.put(entry.getKey().getId(), new RequiredSkills(skillStatusModelList));
		}

		return newMap;
	}

}//--  LiveIDEmployeeService
