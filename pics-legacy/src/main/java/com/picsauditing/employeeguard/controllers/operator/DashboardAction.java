package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.forms.converter.RequiredSiteSkillFormConverter;
import com.picsauditing.employeeguard.forms.operator.RequiredSiteSkillForm;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.forms.binding.FormBinding;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class DashboardAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private RequiredSiteSkillFormConverter requiredSiteSkillFormConverter;
	@Autowired
	private SkillService skillService;

	/* forms */
	@FormBinding("operator_required_skill_edit")
	private RequiredSiteSkillForm requiredSiteSkillForm;

	private List<AccountSkill> requiredSkills;
	private Map<AccountModel, List<AccountSkill>> siteSkills;
	private List<AccountSkill> corporateSkills;

	/* pages */

	public String index() {
		requiredSkills = skillService.getRequiredSkillsForSite(permissions.getAccountId());

		if (permissions.isCorporate()) {
			siteSkills = skillService.getSiteRequiredSkills(permissions.getAccountId());
		}

		Collections.sort(requiredSkills);

		return "dashboard";
	}

	public String editRequiredSkillsSection() {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());
		corporateSkills = skillService.getSkillsForAccounts(corporateIds);
		requiredSkills = skillService.getRequiredSkillsForSite(permissions.getAccountId());

		Collections.sort(corporateSkills);
		Collections.sort(requiredSkills);

		return "required-skills";
	}

	/* other methods */
	public String update() throws Exception {
		List<AccountSkill> requiredSkills = requiredSiteSkillFormConverter.convert(requiredSiteSkillForm);
		skillService.setRequiredSkillsForSite(requiredSkills, id, permissions.getAppUserID());

		return setUrlForRedirect("/employee-guard/operators/dashboard");
	}

	/* getter + setters */

	public RequiredSiteSkillForm getRequiredSiteSkillForm() {
		return requiredSiteSkillForm;
	}

	public void setRequiredSiteSkillForm(RequiredSiteSkillForm requiredSiteSkillForm) {
		this.requiredSiteSkillForm = requiredSiteSkillForm;
	}

	public List<AccountSkill> getRequiredSkills() {
		return requiredSkills;
	}

	public Map<AccountModel, List<AccountSkill>> getSiteSkills() {
		return siteSkills;
	}

	public List<AccountSkill> getCorporateSkills() {
		return corporateSkills;
	}
}
