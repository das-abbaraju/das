package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.operations.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class MGroupsManager extends MModelManager{
	private Map<Integer,MGroup> lookup = new HashMap<>();

	public static Set<MGroup> newCollection(){
		return new HashSet<>();
	}

	private Map<Integer, Group> entityMap = new HashMap<>();

	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}

	public MGroupsManager() {
		operations = new SupportedOperations();
	}

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
		MGroup mGroup = this.fetchModel(group.getId());
		if(mGroup!=null){
			return mGroup;
		}

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

	public class SupportedOperations implements MCopyId, MCopyName, MAttachSkills, MEvalEmployeeCount {

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
		public SupportedOperations attachSkills() {
			mOperations.add(MOperations.ATTACH_SKILLS);
			return this;
		}

		@Override
		public SupportedOperations evalEmployeeCount() {
			mOperations.add(MOperations.EVAL_EMPLOYEE_COUNT);
			return this;
		}

	}

	public static class MGroup extends MBaseModel implements MCopyId, MCopyName, MAttachSkills, MEvalEmployeeCount  {

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

		@Override
		public MGroup copyId(){
			id=group.getId();
			return this;
		}

		@Override
		public MGroup copyName(){
			name=group.getName();
			return this;
		}

		@Override
		public MGroup attachSkills() throws ReqdInfoMissingException {
			this.skills = MModels.fetchContractorSkillManager().copySkills(group.getSkills());
			return this;
		}

		@Override
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
