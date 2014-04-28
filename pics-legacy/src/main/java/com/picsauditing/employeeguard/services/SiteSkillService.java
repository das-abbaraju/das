package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.SiteSkill;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SiteSkillService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private SiteSkillDAO siteSkillDAO;


	public Map<AccountModel, Set<AccountSkill>> getRequiredSkillsForProjects(final List<ProjectCompany> projectCompanies) {
		if (CollectionUtils.isEmpty(projectCompanies)) {
			return Collections.emptyMap();
		}

		List<AccountModel> operators = getOperatorAccountModels(projectCompanies);

		Map<AccountModel, Set<AccountSkill>> requiredSkills = new HashMap<>();

		for (AccountModel operator : operators) {
			List<Integer> siteAndCorporate = accountService.getTopmostCorporateAccountIds(operator.getId());
			siteAndCorporate.add(operator.getId());

			List<SiteSkill> siteSkills = getSiteRequiredSkills(siteAndCorporate);
			List<AccountSkill> accountSkills = ExtractorUtil.extractList(siteSkills, SiteSkill.SKILL_EXTRACTOR);

			requiredSkills.put(operator, new HashSet<>(accountSkills));
		}

		return requiredSkills;
	}

	private List<SiteSkill> getSiteRequiredSkills(final List<Integer> siteAndCorporate) {
		if (CollectionUtils.isEmpty(siteAndCorporate)) {
			return Collections.emptyList();
		}

		return siteSkillDAO.findByAccountIds(siteAndCorporate);
	}

	private List<AccountModel> getOperatorAccountModels(List<ProjectCompany> projectCompanies) {
		Set<Integer> siteIds = new HashSet<>(ExtractorUtil.extractList(projectCompanies, new Extractor<ProjectCompany, Integer>() {
			@Override
			public Integer extract(ProjectCompany projectCompany) {
				return projectCompany.getProject().getAccountId();
			}
		}));

		return accountService.getAccountsByIds(siteIds);
	}
}
