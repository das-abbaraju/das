package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;

import java.util.*;

public class MProjectsManager extends MModelManager{
	private Map<Integer,MProject> lookup = new HashMap<>();


	public static Set<MProject> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, Project> entityMap = new HashMap<>();

	public MProject fetchModel(int id) {
		return lookup.get(id);
	}

	public MProject attachWithModel(Project project) {
		int id = project.getId();

		if (lookup.get(id) == null) {
			lookup.put(id, new MProject(project));
		}

		return lookup.get(id);
	}


	private void addEntityToMap(Project project){
		entityMap.put(project.getId(), project);
	}

	public Set<MProjectsManager.MProject> copyProjects(List<Project> projects) throws ReqdInfoMissingException {
		if(projects==null)
			throw new ReqdInfoMissingException("No projects available to copy");

		Set<MProjectsManager.MProject> mProjects = MProjectsManager.newCollection();

		for(Project project:projects){
			MProjectsManager.MProject mProject = this.copyProject(project);
			mProjects.add(mProject);
		}

		return mProjects;
	}

	public MProjectsManager.MProject evalProjectAssignment(Collection<ProjectRole> projectRoles) throws ReqdInfoMissingException {
		if(projectRoles==null)
			throw new ReqdInfoMissingException("No project roles available to copy");

		MProjectsManager.MProject mProject=null;
		Set<MSkillsManager.MSkill> mAllRoleSkills = new HashSet<>();
		for (ProjectRole pr : projectRoles) {
			if(mProject==null) {
				mProject = this.copyProject(pr.getProject());
			}

			mProject.attachContractorEmployees(pr.getEmployees());

			mAllRoleSkills.addAll(MModels.fetchRolesManager().fetchModel(pr.getRole().getId()).getSkills());

		}

		int accountId = mProject.getEntity().getAccountId();
		calcAssignments(
						mProject,
						MModels.fetchContractorEmployeeManager().getAllEmployees(),
						MModels.fetchCorporateManager().fetchModel(accountId).getReqdSkills(),
						MModels.fetchSitesManager().fetchModel(accountId).getReqdSkills(),
						mProject.getReqdSkills(),
						mAllRoleSkills
						);


		return mProject;
	}

	public MProjectsManager.MProject evalProjectRoleAssignment(ProjectRole pr) throws ReqdInfoMissingException {
		if(pr ==null)
			throw new ReqdInfoMissingException("No project role available to copy");

		MProjectsManager.MProject mProject = this.copyProject(pr.getProject());

		mProject.attachContractorEmployees(pr.getEmployees());

		Set<MSkillsManager.MSkill> mAllRoleSkills = new HashSet<>();
		mAllRoleSkills.addAll(MModels.fetchRolesManager().fetchModel(pr.getRole().getId()).getSkills());

		int accountId = mProject.getEntity().getAccountId();
		calcAssignments(
						mProject,
						MModels.fetchContractorEmployeeManager().getAllEmployees(),
						MModels.fetchCorporateManager().fetchModel(accountId).getReqdSkills(),
						MModels.fetchSitesManager().fetchModel(accountId).getReqdSkills(),
						mAllRoleSkills
		);

		return mProject;
	}


	private void calcAssignments(
					MProjectsManager.MProject mProject,
					Collection<MContractorEmployeeManager.MContractorEmployee> mContractorEmployees,
					Set<MSkillsManager.MSkill>... mSkillSets){

		MModels.fetchStatusManager().init();
		for(MContractorEmployeeManager.MContractorEmployee mContractorEmployee:MModels.fetchContractorEmployeeManager().getAllEmployees()){
			MModels.fetchStatusManager().calculateStatus(mContractorEmployee, mSkillSets);

		}

		MAssignments mAssignments = MModels.fetchStatusManager().getmAssignments();
		mProject.setAssignments(mAssignments);

	}

	public MProject copyProject(Project project) throws ReqdInfoMissingException {
		MProject mProject = this.fetchModel(project.getId());
		if(mProject!=null){
			return mProject;
		}

		addEntityToMap(project);
		MProjectsManager.MProject model = this.attachWithModel(project);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_NAME)){
				model.copyName();
			}
			else if(mOperation.equals(MOperations.COPY_ACCOUNT_ID)){
				model.copyAccountId();
			}

			else if(mOperation.equals(MOperations.ATTACH_ROLES)){
				model.attachRoles();
			}
			else if(mOperation.equals(MOperations.ATTACH_REQD_SKILLS)){
				model.attachSkills();
			}

		}

		return model;
	}




	public static class MProject extends MBaseModel{
		private Integer accountId;

		@Expose
		private MAssignments assignments;
		@Expose
		private Set<MContractorManager.MContractor> companies;
		@Expose
		Set<MRolesManager.MRole> roles;
		@Expose
		private Set<MSkillsManager.MSkill> reqdSkills;
		@Expose
		private Set<MContractorEmployeeManager.MContractorEmployee> employees;

		private Project entity;

		public MProject() {
		}

		public MProject(Project entity) {
			this.entity = entity;
		}

		public MProject copyId(){
			id=entity.getId();
			return this;
		}

		public MProject copyName(){
			name=entity.getName();
			return this;
		}

		public MProject copyAccountId(){
			accountId=entity.getAccountId();
			return this;
		}

		public MProject attachRoles() throws ReqdInfoMissingException {
			this.roles = MModels.fetchRolesManager().copyProjectRoles(entity.getRoles());
			return this;
		}

		public MProject attachSkills() throws ReqdInfoMissingException {
			this.reqdSkills = MModels.fetchSkillsManager().copyProjectReqdSkills(entity);
			return this;
		}

		public MProject attachContractorEmployees(List<ProjectRoleEmployee> pres) throws ReqdInfoMissingException {
			Set<MContractorEmployeeManager.MContractorEmployee> empsAdded= MModels.fetchContractorEmployeeManager().copyProjectRoleEmployees(pres);
			this.employees.addAll(empsAdded);
			return this;
		}

		//-- Getters/Setters


		public Project getEntity() {
			return entity;
		}

		public Integer getAccountId() {
			return accountId;
		}

		public MAssignments getAssignments() {
			return assignments;
		}

		public void setAssignments(MAssignments assignments) {
			this.assignments = assignments;
		}

		public Set<MRolesManager.MRole> getRoles() {
			return roles;
		}

		public Set<MSkillsManager.MSkill> getReqdSkills() {
			return reqdSkills;
		}

		public Set<MContractorEmployeeManager.MContractorEmployee> getEmployees() {
			return employees;
		}

		public Set<MContractorManager.MContractor> getCompanies() {
			return companies;
		}

		public void setCompanies(Set<MContractorManager.MContractor> companies) {
			this.companies = companies;
		}
	}


}
