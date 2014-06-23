package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractorSkillService {

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private EmployeeEntityService employeeEntityService;

	public Set<MContractorSkillsManager.MContractorSkill> findSkills(int accountId) throws ReqdInfoMissingException {
		MContractorSkillsManager mSkillsManager = MModels.fetchContractorSkillManager();
		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);mSkillsOperations.add(MOperations.ATTACH_GROUPS);mSkillsOperations.add(MOperations.EVAL_EMPLOYEE_COUNT);
		mSkillsManager.setmOperations(mSkillsOperations);

		MGroupsManager mGroupsManager = MModels.fetchContractorGroupsManager();
		List<MOperations> mGroupsOperations = new ArrayList<>();mGroupsOperations.add(MOperations.COPY_ID);mGroupsOperations.add(MOperations.COPY_NAME);
		mGroupsManager.setmOperations(mGroupsOperations);


		List<AccountSkill> skills = skillEntityService.findSkillsForContractor(accountId);
		int totalContractorEmployees = employeeEntityService.getNumberOfEmployeesForAccount(accountId);
		MContractor mContractor = new MContractor();
		mContractor.setTotalEmployees(totalContractorEmployees);
		mSkillsManager.setmContractor(mContractor);

/*
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfoAttachGroupsReqdSkillsEmployeeCount(skills, mContractor);
		return mSkills;
*/
		return mSkillsManager.copySkills(skills);
	}


}
