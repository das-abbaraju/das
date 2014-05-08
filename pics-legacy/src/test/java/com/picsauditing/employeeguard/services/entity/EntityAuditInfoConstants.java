package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.entities.builders.SiteSkillBuilder;
import com.picsauditing.employeeguard.models.EntityAuditInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EntityAuditInfoConstants {

	public static final int ENTITY_ID = 123;
	public static final int USER_ID = 22091;
	public static final int ACCOUNT_ID = 234;
	public static final List<Integer> CORPORATE_IDS = Arrays.asList(345, 456);
	public static final Date CREATED_DATE = DateBean.today();
	public static final Date UPDATED_DATE = DateBean.addDays(DateBean.today(), 15);
	public static final String SEARCH_TERM = "Search Term";

	public static final EntityAuditInfo CREATED = new EntityAuditInfo.Builder()
			.appUserId(USER_ID)
			.timestamp(CREATED_DATE)
			.build();

	public static final EntityAuditInfo UPDATED = new EntityAuditInfo.Builder()
			.appUserId(USER_ID)
			.timestamp(UPDATED_DATE)
			.build();

	public static AccountSkill buildFakeAccountSkill() {
		return new AccountSkillBuilder(ACCOUNT_ID)
				.id(ENTITY_ID)
				.accountId(ACCOUNT_ID)
				.skillType(SkillType.Certification)
				.name("Skill 1")
				.build();
	}

	public static List<AccountSkill> buildFakeAccountSkills() {
		return Arrays.asList(
				buildFakeAccountSkill(),
				new AccountSkillBuilder(ACCOUNT_ID)
						.id(ENTITY_ID + 1)
						.accountId(ACCOUNT_ID)
						.skillType(SkillType.Certification)
						.name("Skill 2")
						.build()
		);
	}

	public static List<Project> buildFakeProjects() {
		return Arrays.asList(
				new ProjectBuilder()
						.id(ENTITY_ID)
						.accountId(ACCOUNT_ID)
						.name("Project 1")
						.build(),
				new ProjectBuilder()
						.id(ENTITY_ID + 1)
						.accountId(ACCOUNT_ID)
						.name("Project 2")
						.build());
	}

	public static List<ProjectSkill> buildFakeProjectSkills(List<Project> projects, List<AccountSkill> skills) {
		List<ProjectSkill> projectSkills = new ArrayList<>();
		int index = 0;

		for (Project project : projects) {
			projectSkills.add(new ProjectSkill(project, skills.get(index)));
			index++;
		}

		return projectSkills;
	}

	public static List<Role> buildFakeRoles() {
		return Arrays.asList(
				new RoleBuilder()
						.accountId(ACCOUNT_ID)
						.name("Role 1")
						.build(),
				new RoleBuilder()
						.accountId(ACCOUNT_ID)
						.name("Role 2")
						.build());
	}

	public static List<AccountSkillRole> buildFakeRoleSkills(List<Role> roles, List<AccountSkill> skills) {
		List<AccountSkillRole> skillRoles = new ArrayList<>();
		int index = 0;

		for (Role role : roles) {
			skillRoles.add(new AccountSkillRole(role, skills.get(index)));
			index++;
		}

		return skillRoles;
	}

	public static List<SiteSkill> buildFakeSiteSkills(List<AccountSkill> skills) {
		return Arrays.asList(
				new SiteSkillBuilder()
						.siteId(ACCOUNT_ID)
						.skill(skills.get(0))
						.build(),
				new SiteSkillBuilder()
						.siteId(ACCOUNT_ID)
						.skill(skills.get(1))
						.build()
		);
	}

}
