package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.ProjectRole;
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

	public Set<MRole> copySkillRoles(Collection<AccountSkillRole> asrs) throws ReqdInfoMissingException {
		if(asrs==null)
			throw new ReqdInfoMissingException("No roles available to copy");

		Set<MRole> mRoles = MRolesManager.newCollection();

		for (AccountSkillRole asr : asrs) {
			MRole mRole = this.copyRole(asr.getRole());
			mRoles.add(mRole);
		}

		return mRoles;
	}

	public Set<MRole> copyProjectRoles(Collection<ProjectRole> collection) throws ReqdInfoMissingException {
		if(collection==null)
			throw new ReqdInfoMissingException("No roles available to copy");

		Set<MRole> mRoles = MRolesManager.newCollection();

		for (ProjectRole entity : collection) {
			MRole mRole = this.copyRole(entity.getRole());
			mRoles.add(mRole);
		}

		return mRoles;
	}

	public MRole copyRole(Role role) throws ReqdInfoMissingException {
		MRole mRole = this.fetchModel(role.getId());
		if(mRole!=null){
			return mRole;
		}

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


	public static class MRole extends MBaseModel{

		@Expose
		private MAssignments assignments;
		@Expose
		Set<MSkillsManager.MSkill> skills;

		private Role role;

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
