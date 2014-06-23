package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.entity.GroupEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContractorGroupService {

	@Autowired
	private GroupEntityService groupEntityService;

	public Set<MGroupsManager.MGroup> findGroups(int accountId) throws ReqdInfoMissingException {
		MGroupsManager mGroupsManager = MModels.fetchContractorGroupsManager();
		List<MOperations> mGroupsOperations = new ArrayList<>();mGroupsOperations.add(MOperations.COPY_ID);mGroupsOperations.add(MOperations.COPY_NAME);mGroupsOperations.add(MOperations.EVAL_EMPLOYEE_COUNT);
		mGroupsManager.setmOperations(mGroupsOperations);

		MContractorSkillsManager mSkillsManager = MModels.fetchContractorSkillManager();
		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);
		mSkillsManager.setmOperations(mSkillsOperations);

		List<Group> groups = groupEntityService.findGroupsForContractor(accountId);

/*
		Set<MGroupsManager.MGroup> mGroups = new MGroupsManager().copyBasicInfoAttachSkillsAndEmployeeCount(groups);
		return mGroups;
*/
		return mGroupsManager.copyGroups(groups);
	}


}
