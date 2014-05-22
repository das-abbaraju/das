package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.ProfileAssignmentModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class ProfileAssignmentModelFactory {

	public List<ProfileAssignmentModel> create(final Map<Integer, AccountModel> sites,
											   final Map<AccountModel, SkillStatus> accountStatus,
											   final Map<AccountModel, Set<Group>> accountGroups,
											   final Map<AccountModel, Set<Role>> accountRoles,
											   final Map<Project, SkillStatus> projectStatus) {

		List<ProfileAssignmentModel> models = new ArrayList<>();
		for (AccountModel accountModel : accountRoles.keySet()) {
			models.add(createModelForOperator(accountModel, count(accountRoles, accountModel), accountStatus.get(accountModel)));
		}

		for (AccountModel accountModel : accountGroups.keySet()) {
			models.add(createModelForContractor(accountModel, count(accountGroups, accountModel), accountStatus.get(accountModel)));
		}

		for (Project project : projectStatus.keySet()) {
			models.add(createModelForProject(project, sites.get(project.getAccountId()), projectStatus.get(project)));
		}

		Collections.sort(models);

		return models;
	}

	private static <K, V> int count(final Map<K, ? extends Collection<V>> map, final K key) {
		if (MapUtils.isEmpty(map)) {
			return 0;
		}

		if (!map.containsKey(key)) {
			return 0;
		}

		Collection<V> values = map.get(key);

		return CollectionUtils.isEmpty(values) ? 0 : values.size();
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
