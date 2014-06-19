package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;

import java.util.*;

public class MSkillsManager {

	private Map<Integer, MSkill> lookup = new HashMap<>();

	public static Set<MSkill> newCollection() {
		return new HashSet<>();
	}

	public MSkill fetchModel(int id) {
		return lookup.get(id);
	}

	public MSkill attachWithModel(AccountSkill skill) {
		int id = skill.getId();

		if (lookup.get(id) == null) {
			lookup.put(id, new MSkill(skill));
		}

		return lookup.get(id);
	}

	public Set<MSkillsManager.MSkill> copyBasicInfo(List<AccountSkill> skills) {
		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();
		for (AccountSkill skill : skills) {
			MSkillsManager.MSkill mSkill = this.attachWithModel(skill);
			mSkill.copyId().copyName();
			mSkills.add(mSkill);
		}

		return mSkills;
	}

	public Set<MSkillsManager.MSkill> copyBasicInfoAndAttachRoles(List<AccountSkill> skills) {
		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();
		for (AccountSkill skill : skills) {
			MSkillsManager.MSkill mSkill = this.attachWithModel(skill);
			mSkill.copyId().copyName().copyRoles();
			mSkills.add(mSkill);
		}
		return mSkills;
	}

	public Set<MSkill> extractSkillAndCopyWithBasicInfo(final Collection<AccountSkillRole> accountSkillRoles) {
		Set<MSkill> mSkills = MSkillsManager.newCollection();
		for (AccountSkillRole asr : accountSkillRoles) {
			MSkill mSkill = this.attachWithModel(asr.getSkill());
			mSkill.copyId().copyName();
			mSkills.add(mSkill);
		}

		return mSkills;
	}

	public Set<MSkillsManager.MSkill> copyBasicInfoAttachRolesAndFlagReqdSkills(List<AccountSkill> skills,
																				Map<Integer, AccountSkill> reqdSkills) {
		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();
		for (AccountSkill skill : skills) {
			MSkillsManager.MSkill mSkill = this.attachWithModel(skill);
			mSkill.copyId().copyName().copyRoles();
			if (reqdSkills.get(skill.getId()) != null) mSkill.setReqdSkill(true);
			mSkills.add(mSkill);
		}
		return mSkills;
	}

	public void copyRoles(Set<MSkill> mSkills) {
		for (MSkill mSkill : mSkills) {
			mSkill.copyRoles();
		}
	}


	public static class MSkill {
		@Expose
		private int id;
		@Expose
		private String name;
		@Expose
		private String skillType;
		@Expose
		private String ruleType;
		@Expose
		private String intervalType;
		@Expose
		private String description;
		@Expose
		@SerializedName("isRequiredSkill")
		private boolean reqdSkill;
		@Expose
		Set<MRolesManager.MRole> roles;


		private AccountSkill skill;

		public MSkill(AccountSkill skill) {
			this.skill = skill;
		}

		public MSkill copyId() {
			id = skill.getId();
			return this;
		}

		public MSkill copyName() {
			name = skill.getName();
			return this;
		}

		public MSkill copyDescription() {
			description = skill.getDescription();
			return this;
		}

		public MSkill copySkillType() {
			skillType = skill.getSkillType().toString();
			return this;
		}

		public MSkill copyRuleType() {
			ruleType = skill.getRuleType().toString();
			return this;
		}

		public MSkill copyIntervalType() {
			intervalType = skill.getIntervalType().toString();
			return this;
		}

		public MSkill copyRoles() {
			MRolesManager mRolesManager = new MRolesManager();
			Set<MRolesManager.MRole> mRoles = mRolesManager.extractRoleAndCopyWithBasicInfo(skill.getRoles());
			this.roles = mRoles;
			return this;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getSkillType() {
			return skillType;
		}

		public String getRuleType() {
			return ruleType;
		}

		public String getIntervalType() {
			return intervalType;
		}

		public String getDescription() {
			return description;
		}

		public boolean isReqdSkill() {
			return reqdSkill;
		}

		public void setReqdSkill(boolean reqdSkill) {
			this.reqdSkill = reqdSkill;
		}

		public Set<MRolesManager.MRole> getRoles() {
			return roles;
		}
	}
}
