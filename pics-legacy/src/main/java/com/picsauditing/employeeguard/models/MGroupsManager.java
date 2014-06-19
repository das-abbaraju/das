package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.RuleType;
import org.apache.commons.collections.CollectionUtils;

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

	public Set<MGroup> copyBasicInfoAttachSkillsAndEmployeeCount(List<Group> groups){
		Set<MGroup> mGroups = MGroupsManager.newCollection();
		for(Group group: groups){
			MGroup mGroup = this.attachWithModel(group);
			mGroup.copyId().copyName().copySkills().copyEmployeesTiedToGroupCount();
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
		private Integer id;
		@Expose
		private String name;
		@Expose
		private MAssignments assignments;
		@Expose
		Set<MSkillsManager.MSkill> skills;
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

		public MGroup copySkills(){
			this.skills= new MSkillsManager().extractSkillAndCopyWithBasicInfo(group.getSkills());
			return this;
		}

		public MGroup copyEmployeesTiedToGroupCount(){
			totalEmployees= CollectionUtils.isEmpty(group.getEmployees())?0:group.getEmployees().size();
			return this;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Set<MSkillsManager.MSkill> getSkills() {
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
