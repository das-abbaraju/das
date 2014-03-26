package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.ProfileAssignmentModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.*;

public class ProfileAssignmentModelFactory {

	public List<ProfileAssignmentModel> create(final Map<Integer, AccountModel> accountModels,
											   final Map<Integer, Set<Role>> accountToRoles,
											   final Map<Integer, Set<Group>> accountToGroups,
											   final Map<Integer, SkillStatus> accountToStatus,
											   final Map<Project, SkillStatus> projectToStatus) {

		List<ProfileAssignmentModel> models = new ArrayList<>();
		for (Map.Entry<Integer, Set<Role>> entry : accountToRoles.entrySet()) {
			Integer accountId = entry.getKey();
			AccountModel account = accountModels.get(accountId);
			models.add(createModelForOperator(account, entry.getValue().size(), accountToStatus.get(account)));
		}

		for (Map.Entry<Integer, Set<Group>> entry : accountToGroups.entrySet()) {
			Integer accountId = entry.getKey();
			AccountModel account = accountModels.get(accountId);
			models.add(createModelForContractor(account, entry.getValue().size(), accountToStatus.get(account)));
		}

		for (Map.Entry<Project, SkillStatus> entry : projectToStatus.entrySet()) {
			Project project = entry.getKey();
			models.add(createModelForProject(project, accountModels.get(project.getAccountId()), entry.getValue()));
		}

		Collections.sort(models);

		return models;
	}

	private ProfileAssignmentModel createModelForOperator(final AccountModel account, final int count, final SkillStatus status) {
		ProfileAssignmentModel model = buildWithNameAndStatus(account.getName(), status);
		model.setRoles(count);
		return model;
	}

	public ProfileAssignmentModel createModelForContractor(final AccountModel account, final int count, final SkillStatus status) {
		ProfileAssignmentModel model = buildWithNameAndStatus(account.getName(), status);
		model.setGroups(count);
		return model;
	}

	public ProfileAssignmentModel createModelForProject(final Project project, final AccountModel account, final SkillStatus status) {
		ProfileAssignmentModel model = buildWithNameAndStatus(project.getName(), status);
		model.setSite(account.getName());
		return model;
	}

	private ProfileAssignmentModel buildWithNameAndStatus(final String name, final SkillStatus status) {
		ProfileAssignmentModel model = new ProfileAssignmentModel();

		model.setName(name);
		model.setStatus(status);

		return model;
	}

}
