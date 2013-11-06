package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class DashboardAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private SkillService skillService;

	private List<AccountSkill> requiredSkills;

	/* pages */

	public String index() {
		loadRequiredSkills();

		return "dashboard";
	}

	private void loadRequiredSkills() {
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());
		List<AccountSkill> skills = skillService.getSkillsForAccounts(accountIds);
		requiredSkills = new ArrayList<>();

		for (AccountSkill skill : skills) {
			if (skill.getRuleType().isRequired()) {
				requiredSkills.add(skill);
			}
		}

		Collections.sort(requiredSkills);
	}

	/* other methods */

	/* getter + setters */

	public List<AccountSkill> getRequiredSkills() {
		return requiredSkills;
	}
}
