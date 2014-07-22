package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.operations.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MProjectsManager extends MModelManager{
	private static Logger log = LoggerFactory.getLogger(MProjectsManager.class);

	private Map<Integer,MProject> lookup = new HashMap<>();


	public static Set<MProject> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, Project> entityMap = new HashMap<>();

	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}

	public MProjectsManager() {
		operations = new SupportedOperations();
	}

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

	public MProjectsManager.MProject evalProjectAssignments(Collection<ProjectRole> projectRoles) throws ReqdInfoMissingException {
		if(projectRoles==null)
			throw new ReqdInfoMissingException("No project roles available to copy");

		MProjectsManager.MProject mProject=null;
		for (ProjectRole pr : projectRoles) {

			if(mProject==null) {
				mProject = this.copyProject(pr.getProject());
				mProject.setEmployees(new HashSet<MContractorEmployeeManager.MContractorEmployee>());
			}

			if(CollectionUtils.isEmpty(pr.getEmployees())){
				log.debug("No employees to evaluate for this Role {}", pr.getRole().getId());
				continue;
			}

			mProject.attachContractorEmployees(pr.getEmployees());

		}

		calcAssignments(mProject);


		return mProject;
	}

	private void calcAssignments(MProjectsManager.MProject mProject){

		int accountId = mProject.getEntity().getAccountId();

		MModels.fetchStatusManager().init();
		MContractorEmployeeManager mContractorEmployeeManager = MModels.fetchContractorEmployeeManager();
		for(MContractorEmployeeManager.MContractorEmployee mContractorEmployee:mContractorEmployeeManager.getAllEmployees()){

			Set<Set<MSkillsManager.MSkill>> mAllSkillsToEvaluate = new LinkedHashSet<>();
			mAllSkillsToEvaluate.add(MModels.fetchCorporateManager().fetchModel(accountId).getReqdSkills());
			mAllSkillsToEvaluate.add(MModels.fetchSitesManager().fetchModel(accountId).getReqdSkills());
			mAllSkillsToEvaluate.add(mProject.getReqdSkills());

			for(Role role: mContractorEmployee.getEmployeeRoles()){
				mAllSkillsToEvaluate.add(MModels.fetchRolesManager().fetchModel(role.getId()).getSkills());
			}

			MModels.fetchStatusManager().calculateStatus(mContractorEmployee,mAllSkillsToEvaluate);
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
				model.attachReqdSkills();
			}

		}

		return model;
	}


	public class SupportedOperations implements MCopyId, MCopyName, MAttachRoles, MAttachReqdSkills, MCopyAccountId {

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
		public SupportedOperations attachRoles() {
			mOperations.add(MOperations.ATTACH_ROLES);
			return this;
		}

		@Override
		public SupportedOperations attachReqdSkills() {
			mOperations.add(MOperations.ATTACH_REQD_SKILLS);
			return this;
		}

		@Override
		public SupportedOperations copyAccountId() {
			mOperations.add(MOperations.COPY_ACCOUNT_ID);
			return this;
		}
	}

	public static class MProject extends MBaseModel  implements MCopyId, MCopyName, MAttachRoles, MAttachReqdSkills, MCopyAccountId {
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

		@Override
		public MProject copyId(){
			id=entity.getId();
			return this;
		}

		@Override
		public MProject copyName(){
			name=entity.getName();
			return this;
		}

		@Override
		public MProject copyAccountId(){
			accountId=entity.getAccountId();
			return this;
		}

		@Override
		public MProject attachRoles() throws ReqdInfoMissingException {
			this.roles = MModels.fetchRolesManager().copyProjectRoles(entity.getRoles());
			return this;
		}

		@Override
		public MProject attachReqdSkills() throws ReqdInfoMissingException {
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

		public void setEmployees(Set<MContractorEmployeeManager.MContractorEmployee> employees) {
			this.employees = employees;
		}
	}


}
