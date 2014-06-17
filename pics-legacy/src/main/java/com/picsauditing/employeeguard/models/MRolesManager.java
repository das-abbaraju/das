package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;

import java.util.*;

public class MRolesManager {
	private Map<Integer,MRole> lookup = new HashMap<>();

	public static Set<MRole> newCollection(){
		return new HashSet<>();
	}

	public MRole fetchModel(int id){
		return lookup.get(id);
	}

	public MRole attachWithModel(Role role){
		int id = role.getId();

		if(lookup.get(id)==null){
			lookup.put(id, new MRole(role));
		}

		return lookup.get(id);
	}

	public Set<MRole> copyBasicInfo(List<Role> roles){
		Set<MRole> mRoles = MRolesManager.newCollection();
		for(Role role: roles){
			MRole mRole = this.attachWithModel(role);
			mRole.copyId().copyName();
			mRoles.add(mRole);
		}

		return mRoles;
	}

	public Set<MRole> extractRoleAndCopyWithBasicInfo(List<AccountSkillRole> accountSkillRoles){
		Set<MRole> mRoles = MRolesManager.newCollection();
		for(AccountSkillRole asr: accountSkillRoles){
			MRole mRole = this.attachWithModel(asr.getRole());
			mRole.copyId().copyName();
			mRoles.add(mRole);
		}

		return mRoles;
	}

	public static class MRole {

		@Expose
		private int id;
		@Expose
		private String name;
		@Expose
		private MAssignments assignments;
		@Expose
		Set<MSkillsManager.MSkill> skills;

		private Role role;

		public MRole(Role role) {
			this.role = role;
		}

		public MRole copyId(){
			id=role.getId();
			return this;
		}

		public MRole copyName(){
			name=role.getName();
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
