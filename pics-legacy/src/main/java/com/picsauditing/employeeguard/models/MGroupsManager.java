package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class MGroupsManager extends MModelManager{
	private Map<Integer,MGroup> lookup = new HashMap<>();

	public static Set<MGroup> newCollection(){
		return new HashSet<>();
	}

	private Map<Integer, Group> entityMap = new HashMap<>();

	public MGroup fetchModel(int id){
		return lookup.get(id);
	}

	public MGroup attachWithModel(Group group){
		int id = group.getId();

		if(lookup.get(id)==null){
			lookup.put(id, new MGroup(group));
		}

		return lookup.get(id);
	}

	private void addEntityToMap(Group group){
		entityMap.put(group.getId(), group);
	}

	public Set<MGroup> copyGroups(List<Group> groups) throws ReqdInfoMissingException {
		if(groups==null)
			throw new ReqdInfoMissingException("No groups available to copy");

		Set<MGroup> mGroups = MGroupsManager.newCollection();

		for(Group group:groups){
			MGroup mGroup = this.copyGroup(group);
			mGroups.add(mGroup);
		}

		return mGroups;
	}

	public Set<MGroup> copyGroups(Collection<AccountSkillGroup> asrs) throws ReqdInfoMissingException {
		if(asrs==null)
			throw new ReqdInfoMissingException("No groups available to copy");

		Set<MGroup> mGroups = MGroupsManager.newCollection();

		for (AccountSkillGroup asr : asrs) {
			MGroup mGroup = this.copyGroup(asr.getGroup());
			mGroups.add(mGroup);
		}

		return mGroups;
	}

	private MGroup copyGroup(Group group) throws ReqdInfoMissingException {
		addEntityToMap(group);
		MGroup model = this.attachWithModel(group);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_NAME)){
				model.copyName();
			}
			else if(mOperation.equals(MOperations.ATTACH_SKILLS)){
				model.attachSkills();
			}
			else if(mOperation.equals(MOperations.EVAL_EMPLOYEE_COUNT)){
				model.evalEmployeeCount();
			}

		}

		return model;
	}
/*

	public Set<MGroup> copyBasicInfo(List<Group> groups){
		Set<MGroup> mGroups = MGroupsManager.newCollection();
		for(Group group: groups){
			MGroup mGroup = this.attachWithModel(group);
			mGroup.copyId().copyName();
			mGroups.add(mGroup);
		}

		return mGroups;
	}

	public Set<MGroup> copyBasicInfoAttachSkillsAndEmployeeCount(List<Group> groups) throws ReqdInfoMissingException {
		Set<MGroup> mGroups = MGroupsManager.newCollection();
		for(Group group: groups){
			MGroup mGroup = this.attachWithModel(group);
			mGroup.copyId().copyName().attachSkills().evalEmployeeCount();
			mGroups.add(mGroup);
		}
		return mGroups;
	}

	public Set<MGroupsManager.MGroup> extractGroupAndCopyWithBasicInfo(List<AccountSkillGroup> asgs){
		Set<MGroupsManager.MGroup> mGroups = MGroupsManager.newCollection();
		for(AccountSkillGroup asg: asgs){
			MGroupsManager.MGroup mGroup = this.attachWithModel(asg.getGroup());
			mGroup.copyId().copyName();
			mGroups.add(mGroup);
		}

		return mGroups;
	}
*/

	public static class MGroup {

		@Expose
		private Integer id;
		@Expose
		private String name;
		@Expose
		private MAssignments assignments;
		@Expose
		Set<MContractorSkillsManager.MContractorSkill> skills;
		@Expose
		int totalEmployees;

		private Group group;

		public MGroup(Group group) {
			this.group = group;
		}

		public MGroup copyId(){
			id=group.getId();
			return this;
		}

		public MGroup copyName(){
			name=group.getName();
			return this;
		}

		public MGroup attachSkills() throws ReqdInfoMissingException {
			this.skills = MModels.fetchContractorSkillManager().copySkills(group.getSkills());
			//this.skills= new MSkillsManager().extractSkillAndCopyWithBasicInfo(group.getSkills());
			return this;
		}

		public MGroup evalEmployeeCount(){
			totalEmployees= CollectionUtils.isEmpty(group.getEmployees())?0:group.getEmployees().size();
			return this;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Set<MContractorSkillsManager.MContractorSkill> getSkills() {
			return skills;
		}

		public MAssignments getAssignments() {
			return assignments;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MGroup mGroup = (MGroup) o;

			if (!group.equals(mGroup.group)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return group.hashCode();
		}
	}


}
