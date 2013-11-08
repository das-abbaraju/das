package com.picsauditing.employeeguard.forms.converter;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.forms.operator.RequiredSiteSkillForm;
import com.picsauditing.employeeguard.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class RequiredSiteSkillFormConverter {
	@Autowired
	private SkillService skillService;

	public List<AccountSkill> convert(RequiredSiteSkillForm requiredSiteSkillForm) {
		if (requiredSiteSkillForm == null) {
			return Collections.emptyList();
		}

		return skillService.getSkills(Utilities.primitiveArrayToList(requiredSiteSkillForm.getSkills()));
	}
}
