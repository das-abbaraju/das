package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.models.operations.MAttachRoles;
import com.picsauditing.employeeguard.models.operations.MCopyId;
import com.picsauditing.employeeguard.models.operations.MCopyName;
import com.picsauditing.employeeguard.models.operations.MOperations;

import java.util.*;

public class MSkillsManager  extends MModelManager {

	private Map<Integer, MSkill> lookup = new HashMap<>();

	public static Set<MSkill> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, AccountSkill> entityMap = new HashMap<>();

	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}

	public MSkillsManager() {
		operations = new SupportedOperations();
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


	private void addEntityToMap(AccountSkill skill){
		entityMap.put(skill.getId(), skill);
	}

	public Set<MSkillsManager.MSkill> copySkills(List<AccountSkill> skills) throws ReqdInfoMissingException {
		if(skills==null)
			throw new ReqdInfoMissingException("No skills available to copy");

		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();

		for(AccountSkill skill:skills){
			MSkillsManager.MSkill mSkill = this.copySkill(skill);
			mSkills.add(mSkill);
		}

		return mSkills;
	}

	public Set<MSkillsManager.MSkill> copySkills(Collection<AccountSkillRole> accountSkillRoles) throws ReqdInfoMissingException {
		if(accountSkillRoles==null)
			throw new ReqdInfoMissingException("No skills available to copy");

		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();

		for (AccountSkillRole asr : accountSkillRoles) {
			MSkillsManager.MSkill mSkill = this.copySkill(asr.getSkill());
			mSkills.add(mSkill);
		}

		return mSkills;
	}

	public Set<MSkillsManager.MSkill> copyProjectReqdSkills(Project project) throws ReqdInfoMissingException {
		if(project==null)
			throw new ReqdInfoMissingException("No skills available to copy");

		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();

		for (ProjectSkill ps : project.getSkills()) {
			MSkillsManager.MSkill mSkill = this.copySkill(ps.getSkill());
			mSkills.add(mSkill);
		}

		return mSkills;
	}

	public MSkill copySkill(AccountSkill skill) throws ReqdInfoMissingException {
		MSkill mSkill = this.fetchModel(skill.getId());
		if(mSkill!=null){
			return mSkill;
		}

		addEntityToMap(skill);
		MSkillsManager.MSkill model = this.attachWithModel(skill);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_NAME)){
				model.copyName();
			}
			else if(mOperation.equals(MOperations.ATTACH_ROLES)){
				model.attachRoles();
			}

		}

		return model;
	}



/*
	public Set<MSkillsManager.MSkill> copyBasicInfoAttachRolesAndFlagReqdSkills(List<AccountSkill> skills,
																				Map<Integer, AccountSkill> reqdSkills) throws ReqdInfoMissingException {
		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();
		for (AccountSkill skill : skills) {
			MSkillsManager.MSkill mSkill = this.attachWithModel(skill);
			mSkill.copyId().copyName().attachRoles();
			if (reqdSkills.get(skill.getId()) != null) mSkill.setReqdSkill(true);
			mSkills.add(mSkill);
		}
		return mSkills;
	}
*/

/*
	public Set<MSkillsManager.MSkill> copyBasicInfoAttachGroupsReqdSkillsEmployeeCount(List<AccountSkill> skills, MContractor mContractor){
		Set<MSkillsManager.MSkill> mSkills = MSkillsManager.newCollection();
		for(AccountSkill skill: skills){
			MSkillsManager.MSkill mSkill = this.attachWithModel(skill);
			mSkill.copyId().copyName().attachGroups().copyEmployeesTiedToSkillCount(mContractor.getTotalEmployees());
			if(skill.getRuleType()!=null && skill.getRuleType().equals(RuleType.REQUIRED)) mSkill.setReqdSkill(true);
			mSkills.add(mSkill);
		}
		return mSkills;
	}
*/


	public Map<Integer, AccountSkill> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<Integer, AccountSkill> entityMap) {
		this.entityMap = entityMap;
	}

	public class SupportedOperations implements MCopyId, MCopyName, MAttachRoles{

		@Override
		public SupportedOperations copyId() {
			mOperations.add(MOperations.COPY_ID);
			return this;
		}

		@Override
		public SupportedOperations copyName() {
			mOperations.add(MOperations.COPY_NAME);
			return this;
		}

		@Override
		public SupportedOperations attachRoles() {
			mOperations.add(MOperations.ATTACH_ROLES);
			return this;
		}

	}


	public static class MSkill extends MBaseModel implements MCopyId,MCopyName,MAttachRoles {
		@Expose
		private String skillType;
		@Expose
		private String ruleType;
		@Expose
		private String intervalType;
		@Expose
		@SerializedName("isRequiredSkill")
		private Boolean reqdSkill;
		@Expose
		Set<MRolesManager.MRole> roles;
		@Expose
		Integer totalEmployees;


		private AccountSkill skillEntity;

		public MSkill() {
		}

		public MSkill(AccountSkill skillEntity) {
			this.skillEntity = skillEntity;
		}

		@Override
		public MSkill copyName() {
			name= skillEntity.getName();
			return this;
		}

		@Override
		public MSkill copyId(){
			id= skillEntity.getId();
			return this;
		}

		@Override
		public MSkill attachRoles() throws ReqdInfoMissingException {
			this.roles = MModels.fetchRolesManager().copySkillRoles(skillEntity.getRoles());
			return this;
		}

/*

		public MSkill copyDescription(){
			description= skillEntity.getDescription();
			return this;
		}

		public MSkill copySkillType(){
			skillType= skillEntity.getSkillType().toString();
			return this;
		}

		public MSkill copyRuleType(){
			ruleType= skillEntity.getRuleType().toString();
			return this;
		}

		public MSkill copyIntervalType(){
			intervalType= skillEntity.getIntervalType().toString();
			return this;
		}

*/




		//-- Getters


		public AccountSkill getSkillEntity() {
			return skillEntity;
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

		public Boolean isReqdSkill() {
			return reqdSkill;
		}

		public void setReqdSkill(Boolean reqdSkill) {
			this.reqdSkill = reqdSkill;
		}

		public Set<MRolesManager.MRole> getRoles() {
			return roles;
		}

		public Integer getTotalEmployees() {
			return totalEmployees;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MSkill mSkill = (MSkill) o;

			if (skillEntity != null ? !skillEntity.equals(mSkill.skillEntity) : mSkill.skillEntity != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return skillEntity != null ? skillEntity.hashCode() : 0;
		}


	}
}
