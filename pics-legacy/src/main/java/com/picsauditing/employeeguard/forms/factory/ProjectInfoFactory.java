package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.forms.operator.ProjectInfo;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectInfoFactory {
	@Autowired
	private AccountService accountService;

	public List<ProjectInfo> build(final List<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		List<ProjectInfo> projectInfos = new ArrayList<>();
		Map<Integer, AccountModel> accountMap = mapIdToAccountModel(projects);

		for (Project project : projects) {
			String siteName = accountMap.get(project.getAccountId()).getName();

			ProjectInfo projectInfo = new ProjectInfo.Builder()
					.id(project.getId())
					.accountId(project.getAccountId())
					.site(siteName)
					.name(project.getName())
					.location(project.getLocation())
					.startDate(project.getStartDate())
					.endDate(project.getEndDate())
					.roles(project.getRoles())
					.skills(project.getSkills())
					.build();

			projectInfos.add(projectInfo);
		}

		return projectInfos;
	}

	private Map<Integer, AccountModel> mapIdToAccountModel(List<Project> projects) {
		Set<Integer> siteIds = new HashSet<>();
		for (Project project : projects) {
			siteIds.add(project.getAccountId());
		}

		List<AccountModel> accounts = accountService.getAccountsByIds(siteIds);

		Map<Integer, AccountModel> accountMap = new TreeMap<>();
		for (AccountModel account : accounts) {
			accountMap.put(account.getId(), account);
		}

		return accountMap;
	}

	public ProjectInfo build(final Project project) {
		AccountModel account = accountService.getAccountById(project.getAccountId());

		ProjectInfo projectInfo = new ProjectInfo.Builder()
				.id(project.getId())
				.accountId(project.getAccountId())
				.site(account.getName())
				.name(project.getName())
				.location(project.getLocation())
				.startDate(project.getStartDate())
				.endDate(project.getEndDate())
				.roles(project.getRoles())
				.skills(project.getSkills())
				.build();

		return projectInfo;
	}
}
