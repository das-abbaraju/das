package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;

import java.util.*;

public class MRolesManager extends MModelManager{
	private Map<Integer, MRole> lookup = new HashMap<>();

	public static Set<MRole> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, Role> entityMap = new HashMap<>();

	public MRole fetchModel(int id) {
		return lookup.get(id);
	}

	public MRole attachWithModel(Role role) {
		int id = role.getId();

		if (lookup.get(id) == null) {
			lookup.put(id, new MRole(role));
		}

		return lookup.get(id);
	}

	private void addEntityToMap(Role role){
		entityMap.put(role.getId(), role);
	}

	public Set<MRole> copyRoles(List<Role> roles) throws ReqdInfoMissingException {
		if(roles==null)
			throw new ReqdInfoMissingException("No roles available to copy");

		Set<MRole> mRoles = MRolesManager.newCollection();

		for(Role role:roles){
			MRole mRole = this.copyRole(role);
			mRoles.add(mRole);
		}

		return mRoles;
	}

	public Set<MRole> copyRoles(Collection<AccountSkillRole> asrs) throws ReqdInfoMissingException {
		if(asrs==null)
			throw new ReqdInfoMissingException("No roles available to copy");

		Set<MRole> mRoles = MRolesManager.newCollection();

		for (AccountSkillRole asr : asrs) {
			MRole mRole = this.copyRole(asr.getRole());
			mRoles.add(mRole);
		}

		return mRoles;
	}

	private MRole copyRole(Role role) throws ReqdInfoMissingException {
		addEntityToMap(role);
		MRole model = this.attachWithModel(role);

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

		}

		return model;
	}

/*	public Set<MRole> copyBasicInfo(Collection<Role> roles) {
		Set<MRole> mRoles = MRolesManager.newCollection();
		for (Role role : roles) {
			MRole mRole = MRole.newSerializableModel(role);
			mRole.copyId().copyName();
			mRoles.add(mRole);
		}

		return mRoles;
	}

	public Set<MRole> copyBasicInfoAndAttachSkills(Collection<Role> roles) throws ReqdInfoMissingException {
		Set<MRole> mRoles = MRolesManager.newCollection();
		for (Role role : roles) {
			MRole mRole = MRole.newSerializableModel(role);
			mRole.copyId().copyName().attachSkills();
			mRoles.add(mRole);
		}

		return mRoles;
	}*/

/*
	public Set<MRole> extractRoleAndCopyWithBasicInfo(List<AccountSkillRole> accountSkillRoles) {
		Set<MRole> mRoles = MRolesManager.newCollection();
		for (AccountSkillRole asr : accountSkillRoles) {
			MRole mRole = new MRole(asr.getRole());
			mRole.copyId().copyName();
			mRoles.add(mRole);
		}

		return mRoles;
	}
*/

	public static class MRole extends MBaseModel{

		@Expose
		private MAssignments assignments;
		@Expose
		Set<MSkillsManager.MSkill> skills;

		private Role role;

/*
		public static MRole newFormModel(){
			return new MRole();
		}

		public static MRole newSerializableModel(Role role){
			return new MRole(role);
		}
*/

		public MRole(){

		}

		public MRole(Role role) {
			this.role = role;
		}

		public MRole copyId() {
			id = role.getId();
			return this;
		}

		public MRole copyName() {
			name = role.getName();
			return this;
		}

		public MRole attachSkills() throws ReqdInfoMissingException {
			this.skills = MModels.fetchSkillsManager().copySkills(role.getSkills());
			return this;
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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MRole mRole = (MRole) o;

			if (role != null ? !role.equals(mRole.role) : mRole.role != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return role != null ? role.hashCode() : 0;
		}
	}
}
