package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.models.MContractor;
import com.picsauditing.employeeguard.models.MGroupsManager;
import com.picsauditing.employeeguard.models.MSkillsManager;
import com.picsauditing.employeeguard.services.entity.GroupEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class ContractorGroupService {

	@Autowired
	private GroupEntityService groupEntityService;

	public Set<MGroupsManager.MGroup> findGroups(int accountId){
		List<Group> groups = groupEntityService.findGroupsForContractor(accountId);

		Set<MGroupsManager.MGroup> mGroups = new MGroupsManager().copyBasicInfoAttachSkillsAndEmployeeCount(groups);

		return mGroups;
	}


}
