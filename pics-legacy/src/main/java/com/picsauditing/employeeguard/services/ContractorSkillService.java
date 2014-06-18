package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.models.MContractor;
import com.picsauditing.employeeguard.models.MSkillsManager;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractorSkillService {

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private EmployeeEntityService employeeEntityService;

	public Set<MSkillsManager.MSkill> findSkillsForContractor(int accountId){
		List<AccountSkill> skills = skillEntityService.findSkillsForContractor(accountId);

		MSkillsManager skillsManager = new MSkillsManager();

		MContractor mContractor = new MContractor();
		int totalContractorEmployees = employeeEntityService.getNumberOfEmployeesForAccount(accountId);
		mContractor.setTotalEmployees(totalContractorEmployees);
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfoAttachGroupsReqdSkillsEmployeeCount(skills, mContractor);

		return mSkills;
	}


}
