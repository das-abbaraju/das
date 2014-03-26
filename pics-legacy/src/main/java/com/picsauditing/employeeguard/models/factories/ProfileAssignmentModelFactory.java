package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.ProfileAssignmentModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ProfileAssignmentModelFactory {

	public List<ProfileAssignmentModel> create(final Map<AccountModel, Set<Role>> accountToRoles,
											   final Map<AccountModel, Set<Group>> accountToGroups,
											   final Map<AccountModel, SkillStatus> accountToStatus,
											   final Map<Project, SkillStatus> projectToStatus) {

		List<ProfileAssignmentModel> models = new ArrayList<>();
		for (Map.Entry<AccountModel, Set<Role>> entry : accountToRoles.entrySet()) {
			AccountModel account = entry.getKey();
			models.add(createModelForOperator(account, entry.getValue().size(), accountToStatus.get(account)));
		}

		for (Map.Entry<AccountModel, Set<Group>> entry : accountToGroups.entrySet()) {
			AccountModel account = entry.getKey();
			models.add(createModelForContractor(account, entry.getValue().size(), accountToStatus.get(account)));
		}

		Map<Integer, AccountModel> idToOperator = getIdToOperators(accountToRoles.keySet());
		for (Map.Entry<Project, SkillStatus> entry : projectToStatus.entrySet()) {
			Project project = entry.getKey();
			models.add(createModelForProject(project, idToOperator.get(project.getAccountId()), entry.getValue()));
		}

		return Collections.emptyList();
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

	private Map<Integer, AccountModel> getIdToOperators(final Collection<AccountModel> accounts) {
		if (CollectionUtils.isEmpty(accounts)) {
			return Collections.emptyMap();
		}

		Map<Integer, AccountModel> idToAccount = new HashMap<>();

		for (AccountModel account : accounts) {
			idToAccount.put(account.getId(), account);
		}

		return idToAccount;
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
