package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.entities.RuleType;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.entities.AccountSkillRole;

import java.util.*;

public class MSkillsManager  extends MModelManager {

	private Map<Integer, MSkill> lookup = new HashMap<>();

	public static Set<MSkill> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, AccountSkill> entityMap = new HashMap<>();
	//private Map<Integer, AccountSkill> reqdSkillsMap= new HashMap<>();
	//private List<AccountSkill> skills;
	//private List<AccountSkill> reqdSkills;

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

	private MSkill copySkill(AccountSkill skill) throws ReqdInfoMissingException {
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

	public Set<MSkill> extractSkillAndCopyWithBasicInfo(final Collection<AccountSkillRole> accountSkillRoles) {
		Set<MSkill> mSkills = MSkillsManager.newCollection();
		for (AccountSkillRole asr : accountSkillRoles) {
			MSkill mSkill = this.attachWithModel(asr.getSkill());
			mSkill.copyId().copyName();
			mSkills.add(mSkill);
		}

		return mSkills;
	}
*/

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

/*

	public Set<MSkill> extractSkillAndCopyWithBasicInfo(List<AccountSkillGroup> asgs){
		Set<MSkill> mSkills = MSkillsManager.newCollection();
		for(AccountSkillGroup asg: asgs){
			MSkill mSkill = this.attachWithModel(asg.getSkill());
			mSkill.copyId().copyName();
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

	public static class MSkill extends MBaseModel{
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
		Set<MGroupsManager.MGroup> groups;
		@Expose
		Integer totalEmployees;


		private AccountSkill skill;

		public MSkill() {
		}

		public MSkill(AccountSkill skill) {
			this.skill = skill;
		}

		public MSkill copyId(){
			id=skill.getId();
			return this;
		}

		public MSkill copyName(){
			name=skill.getName();
			return this;
		}

		public MSkill copyDescription(){
			description=skill.getDescription();
			return this;
		}

		public MSkill copySkillType(){
			skillType=skill.getSkillType().toString();
			return this;
		}

		public MSkill copyRuleType(){
			ruleType=skill.getRuleType().toString();
			return this;
		}

		public MSkill copyIntervalType(){
			intervalType=skill.getIntervalType().toString();
			return this;
		}

		public MSkill attachRoles() throws ReqdInfoMissingException {
			this.roles = MModels.fetchRolesManager().copyRoles(skill.getRoles());
			return this;

/*
			MRolesManager mRolesManager = new MRolesManager();
			Set<MRolesManager.MRole> mRoles = mRolesManager.extractRoleAndCopyWithBasicInfo(skill.getRoles());
			this.roles = mRoles;
			return this;
*/
		}

/*
		public MSkill attachGroups(){
			MGroupsManager mGroupsManager = new MGroupsManager();
			Set<MGroupsManager.MGroup> mGroups = mGroupsManager.extractGroupAndCopyWithBasicInfo(skill.getGroups());
			this.groups = mGroups;
			return this;
		}
*/

/*		public MSkill copyEmployeesTiedToSkillCount(int totalEmployeesForAccount){
			if (skill.getRuleType() != null && skill.getRuleType().isRequired()) {
				totalEmployees = totalEmployeesForAccount;
			}
			else {
				List<AccountSkillGroup> accountSkillGroups = skill.getGroups();
				totalEmployees = getEmployeeCount(accountSkillGroups);
			}
			return this;
		}

		private int getEmployeeCount(List<AccountSkillGroup> accountSkillGroups) {
			Set<Integer> employeeIds = new HashSet<>();
			for (AccountSkillGroup accountSkillGroup : accountSkillGroups) {
				employeeIds.addAll(PicsCollectionUtil.getIdsFromCollection(accountSkillGroup.getGroup().getEmployees(), new PicsCollectionUtil.Identitifable<GroupEmployee, Integer>() {

					@Override
					public Integer getId(GroupEmployee groupEmployee) {
						return groupEmployee.getEmployee().getId();
					}
				}));
			}


			return employeeIds.size();
		}*/

		//-- Getters


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

		public Set<MGroupsManager.MGroup> getGroups() {
			return groups;
		}

		public Integer getTotalEmployees() {
			return totalEmployees;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MSkill mSkill = (MSkill) o;

			if (skill != null ? !skill.equals(mSkill.skill) : mSkill.skill != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return skill != null ? skill.hashCode() : 0;
		}
	}
}
