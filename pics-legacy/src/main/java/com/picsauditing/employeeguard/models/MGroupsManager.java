package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;

import java.util.*;

public class MGroupsManager {
	private Map<Integer,MGroup> lookup = new HashMap<>();

	public static Set<MGroup> newCollection(){
		return new HashSet<>();
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

	public Set<MGroup> copyBasicInfo(List<Group> groups){
		Set<MGroup> mGroups = MGroupsManager.newCollection();
		for(Group group: groups){
			MGroup mGroup = this.attachWithModel(group);
			mGroup.copyId().copyName();
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

	public static class MGroup {

		@Expose
		private int id;
		@Expose
		private String name;
		@Expose
		private MAssignments assignments;
		@Expose
		Set<MSkillsManager.MSkill> skills;

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

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Set<MSkillsManager.MSkill> getSkills() {
			return skills;
		}

		public void setSkills(Set<MSkillsManager.MSkill> skills) {
			this.skills = skills;
		}

		public MAssignments getAssignments() {
			return assignments;
		}

		public void setAssignments(MAssignments assignments) {
			this.assignments = assignments;
		}
	}


}
