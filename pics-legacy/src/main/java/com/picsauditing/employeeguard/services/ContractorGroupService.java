package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.models.operations.MOperations;
import com.picsauditing.employeeguard.services.entity.GroupEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContractorGroupService {

	@Autowired
	private GroupEntityService groupEntityService;

	public Set<MGroupsManager.MGroup> findGroups(int accountId) throws ReqdInfoMissingException {
		MGroupsManager mGroupsManager = MModels.fetchContractorGroupsManager();
		mGroupsManager.operations().copyId().copyName().evalEmployeeCount();

		MModels.fetchContractorSkillManager().operations().copyId().copyName();

		List<Group> groups = groupEntityService.findGroupsForContractor(accountId);

		return mGroupsManager.copyGroups(groups);
	}


}
