package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.operations.*;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;

import java.util.*;

public class MContractorSkillsManager extends MModelManager{

	private Map<Integer, MContractorSkill> lookup = new HashMap<>();

	public static Set<MContractorSkill> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, AccountSkill> entityMap = new HashMap<>();
	private MContractor mContractor;

	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}

	public MContractorSkillsManager() {
		operations = new SupportedOperations();
	}

	public MContractorSkill fetchModel(int id) {
		return lookup.get(id);
	}

	public MContractorSkill attachWithModel(AccountSkill skill) {
		int id = skill.getId();

		if (lookup.get(id) == null) {
			lookup.put(id, new MContractorSkill(skill));
		}

		return lookup.get(id);
	}

	private void addEntityToMap(AccountSkill skill){
		entityMap.put(skill.getId(), skill);
	}

	public Set<MContractorSkillsManager.MContractorSkill> copySkills(List<AccountSkill> skills) throws ReqdInfoMissingException {
		if(skills==null)
			throw new ReqdInfoMissingException("No skills available to copy");

		Set<MContractorSkillsManager.MContractorSkill> mContractorSkills = MContractorSkillsManager.newCollection();

		for(AccountSkill skill:skills){
			MContractorSkillsManager.MContractorSkill mContractorSkill = this.copySkill(skill);
			mContractorSkills.add(mContractorSkill);
		}

		return mContractorSkills;
	}

	public Set<MContractorSkillsManager.MContractorSkill> copySkills(Collection<AccountSkillGroup> asgs) throws ReqdInfoMissingException {
		if(asgs ==null)
			throw new ReqdInfoMissingException("No skills available to copy");

		Set<MContractorSkillsManager.MContractorSkill> mContractorSkills = MContractorSkillsManager.newCollection();

		for (AccountSkillGroup asg : asgs) {
			MContractorSkillsManager.MContractorSkill mContractorSkill = this.copySkill(asg.getSkill());
			mContractorSkills.add(mContractorSkill);
		}

		return mContractorSkills;
	}

	private MContractorSkill copySkill(AccountSkill skill) throws ReqdInfoMissingException {
		MContractorSkill mSkill = this.fetchModel(skill.getId());
		if(mSkill!=null){
			return mSkill;
		}

		addEntityToMap(skill);
		MContractorSkillsManager.MContractorSkill model = this.attachWithModel(skill);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_NAME)){
				model.copyName();
			}
			else if(mOperation.equals(MOperations.ATTACH_GROUPS)){
				model.attachGroups();
			}
			else if(mOperation.equals(MOperations.EVAL_EMPLOYEE_COUNT)){
				model.evalEmployeeCount();
			}

		}

		return model;
	}

/*
	public Set<MContractorSkillsManager.MContractorSkill> copyBasicInfoAttachGroupsReqdSkillsEmployeeCount(List<AccountSkill> skills, MContractor mContractor){
		Set<MContractorSkillsManager.MContractorSkill> mContractorSkills = MContractorSkillsManager.newCollection();
		for(AccountSkill skill: skills){
			MContractorSkillsManager.MContractorSkill mContractorSkill = this.attachWithModel(skill);
			mContractorSkill.copyId().copyName().attachGroups().copyEmployeesTiedToSkillCount(mContractor.getTotalEmployees());
			if(skill.getRuleType()!=null && skill.getRuleType().equals(RuleType.REQUIRED)) mContractorSkill.setReqdSkill(true);
			mContractorSkills.add(mContractorSkill);
		}
		return mContractorSkills;
	}
*/

	public MContractor getmContractor() {
		return mContractor;
	}

	public void setmContractor(MContractor mContractor) {
		this.mContractor = mContractor;
	}

	public class SupportedOperations implements MCopyId, MCopyName, MAttachGroups, MEvalEmployeeCount {

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
		public SupportedOperations attachGroups() {
			mOperations.add(MOperations.ATTACH_GROUPS);
			return this;
		}

		@Override
		public SupportedOperations evalEmployeeCount() {
			mOperations.add(MOperations.EVAL_EMPLOYEE_COUNT);
			return this;
		}
	}


	public static class MContractorSkill extends MBaseModel  implements MCopyId, MCopyName, MAttachGroups, MEvalEmployeeCount {
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
		Set<MGroupsManager.MGroup> groups;
		@Expose
		Integer totalEmployees;


		private AccountSkill skill;

		public MContractorSkill(AccountSkill skill) {
			this.skill = skill;
		}

		@Override
		public MContractorSkill copyId(){
			id=skill.getId();
			return this;
		}

		@Override
		public MContractorSkill copyName(){
			name=skill.getName();
			return this;
		}
/*

		public MContractorSkill copyDescription(){
			description=skill.getDescription();
			return this;
		}

		public MContractorSkill copySkillType(){
			skillType=skill.getSkillType().toString();
			return this;
		}

		public MContractorSkill copyRuleType(){
			ruleType=skill.getRuleType().toString();
			return this;
		}

		public MContractorSkill copyIntervalType(){
			intervalType=skill.getIntervalType().toString();
			return this;
		}
*/

		@Override
		public MContractorSkill attachGroups() throws ReqdInfoMissingException {
			Set<MGroupsManager.MGroup> mGroups = MModels.fetchContractorGroupsManager().copyGroups(skill.getGroups());
			this.groups = mGroups;
			return this;
		}

		@Override
		public MContractorSkill evalEmployeeCount() throws ReqdInfoMissingException {
			MContractorSkillsManager mContractorSkillsManager = MModels.fetchContractorSkillManager();
			MContractor mContractor = mContractorSkillsManager.getmContractor();
			if(mContractor==null)
				throw new ReqdInfoMissingException("Contractor info missing to eval Employee Count");
			if (skill.getRuleType() != null && skill.getRuleType().isRequired()) {
				totalEmployees = mContractor.getTotalEmployees();
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
		}

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

			MContractorSkill mContractorSkill = (MContractorSkill) o;

			if (skill != null ? !skill.equals(mContractorSkill.skill) : mContractorSkill.skill != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return skill != null ? skill.hashCode() : 0;
		}
	}
}
