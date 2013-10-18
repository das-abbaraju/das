package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.IntervalType;
import com.picsauditing.employeeguard.entities.SkillType;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class SkillServiceFactory {
	// These are not thread safe
	private static SkillService skillService = Mockito.mock(SkillService.class);

	public static SkillService getSkillService() {
		Mockito.reset(skillService);

		AccountSkill accountSkill = new AccountSkillBuilder().name("Skill 1").intervalType(IntervalType.NOT_APPLICABLE).skillType(SkillType.Certification).build();
		AccountSkill accountSkill2 = new AccountSkillBuilder().name("Skill 2").skillType(SkillType.Training).build();
		List<AccountSkill> accountSkills = Arrays.asList(accountSkill, accountSkill2);
		when(skillService.search(anyString(), anyInt())).thenReturn(accountSkills);
		when(skillService.getSkillsForAccount(anyInt())).thenReturn(accountSkills);
		when(skillService.getOptionalSkillsForAccount(anyInt())).thenReturn(accountSkills);
		when(skillService.getSkill(anyString(), anyInt())).thenReturn(accountSkill);
		when(skillService.getSkill("ID")).thenReturn(accountSkill);
		when(skillService.getSkill("ID2")).thenReturn(accountSkill2);
		when(skillService.update(any(AccountSkill.class), anyString(), anyInt(), anyInt())).thenReturn(accountSkill);

		return skillService;
	}
}
