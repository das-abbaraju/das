package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.models.operations.MOperations;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContractorSkillService {

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private EmployeeEntityService employeeEntityService;

	public Set<MContractorSkillsManager.MContractorSkill> findSkills(int accountId) throws ReqdInfoMissingException {
		MContractorSkillsManager mSkillsManager = MModels.fetchContractorSkillManager();
		mSkillsManager.operations().copyId().copyName().attachGroups().evalEmployeeCount();

		MModels.fetchContractorGroupsManager().operations().copyId().copyName();

		List<AccountSkill> skills = skillEntityService.findSkillsForContractor(accountId);
		int totalContractorEmployees = employeeEntityService.getNumberOfEmployeesForAccount(accountId);
		MContractor mContractor = new MContractor();
		mContractor.setTotalEmployees(totalContractorEmployees);
		mSkillsManager.setmContractor(mContractor);

		return mSkillsManager.copySkills(skills);
	}


}
