package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;

import java.util.*;

public class MContractorEmployeeManager extends MModelManager{

	private Map<Integer, MContractorEmployee> lookup = new HashMap<>();

	public static Set<MContractorEmployee> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, Employee> entityMap = new HashMap<>();

	public MContractorEmployee fetchModel(int id) {
		return lookup.get(id);
	}

	public Collection<MContractorEmployee> getAllEmployees() {
		return lookup.values();
	}

	public MContractorEmployee attachWithModel(Employee employee) {
		int id = employee.getId();

		if (lookup.get(id) == null) {
			lookup.put(id, new MContractorEmployee(employee));
		}

		return lookup.get(id);
	}

	private void addEntityToMap(Employee employee){
		entityMap.put(employee.getId(), employee);
	}

	public Set<MContractorEmployee> copyEmployees(List<Employee> employees) throws ReqdInfoMissingException {
		if(employees==null)
			throw new ReqdInfoMissingException("No employees available to copy");

		Set<MContractorEmployee> mContractorEmployees = MContractorEmployeeManager.newCollection();

		for(Employee employee:employees){
			MContractorEmployee mContractorEmployee = this.copyEmployee(employee);
			mContractorEmployees.add(mContractorEmployee);
		}

		return mContractorEmployees;
	}

	public Set<MContractorEmployee> copyProjectRoleEmployees(List<ProjectRoleEmployee> pres) throws ReqdInfoMissingException {
		if(pres ==null)
			throw new ReqdInfoMissingException("No employees available to copy");

		Set<MContractorEmployee> mContractorEmployees = MContractorEmployeeManager.newCollection();

		for (ProjectRoleEmployee pre : pres) {
			MContractorEmployee mContractorEmployee = this.copyEmployee(pre.getEmployee());
			mContractorEmployee.addToEmployeeRoles(pre.getProjectRole().getRole());
			mContractorEmployee.setHasProjectSkills(true);
			mContractorEmployee.setWorkingAtSiteOrProject(true);
			mContractorEmployees.add(mContractorEmployee);
		}

		return mContractorEmployees;
	}

	public MContractorEmployee copyEmployee(Employee employee) throws ReqdInfoMissingException {

		MContractorEmployee mContractorEmployee = this.fetchModel(employee.getId());
		if(mContractorEmployee!=null){
			return mContractorEmployee;
		}

		addEntityToMap(employee);
		MContractorEmployee model = this.attachWithModel(employee);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_NAME)){
				model.copyName();
			}
			else if(mOperation.equals(MOperations.ATTACH_CONTRACTOR)){
				model.copyContractor();
			}
			else if(mOperation.equals(MOperations.ATTACH_DOCUMENTATION)){
				model.attachDocumentations();
			}
			else if(mOperation.equals(MOperations.COPY_FIRST_NAME)){
				model.copyFirstName();
			}
			else if(mOperation.equals(MOperations.COPY_LAST_NAME)){
				model.copyLastName();
			}
			else if(mOperation.equals(MOperations.COPY_TITLE)){
				model.copyTitle();
			}
			else if(mOperation.equals(MOperations.ENABLE_ASSIGNMENT_FLAG)){
				model.enableAssignmentFlag();
			}


		}

		return model;
	}


	public static class MContractorEmployee extends MBaseModel{

		private Employee employeeEntity;

		private Map<Integer, AccountSkillProfile> employeeDocumentation;

		private Set<Role> employeeRoles = new HashSet<>();

		@Expose
		@SerializedName("company")
		private MContractorManager.MContractor contractor;

		@Expose
		@SerializedName("status")
		private MEmployeeStatus employeeStatus;

		private Boolean hasProjectSkills=false;

		@Expose
		private String firstName;
		@Expose
		private String lastName;
		@Expose
		private String title;
		@Expose
		@SerializedName("assigned")
		private Boolean workingAtSiteOrProject;

		@Expose
		@SerializedName("assignments")
		private Integer totalRolesAssigned;

		public MContractorEmployee(Employee employeeEntity) {
			this.employeeEntity = employeeEntity;
		}

		public MContractorEmployee copyId(){
			id= employeeEntity.getId();
			return this;
		}

		public MContractorEmployee copyName(){
			name= employeeEntity.getName();
			return this;
		}

		public MContractorEmployee copyFirstName(){
			firstName = employeeEntity.getFirstName();
			return this;
		}

		public MContractorEmployee copyLastName(){
			lastName = employeeEntity.getLastName();
			return this;
		}

		public MContractorEmployee copyTitle(){
			title = employeeEntity.getPositionName();
			return this;
		}

		public MContractorEmployee copyContractor() throws ReqdInfoMissingException {
			contractor = MModels.fetchContractorManager().copyContractor(employeeEntity.getAccountId());
			return this;
		}

		public MContractorEmployee enableAssignmentFlag() throws ReqdInfoMissingException {
			if(workingAtSiteOrProject==null)
				workingAtSiteOrProject=false;

			return this;
		}

		public MContractorEmployee attachDocumentations() throws ReqdInfoMissingException {
			employeeDocumentation = prepareEmployeeDocumentationsLookup();
			return this;
		}

		private Map<Integer, AccountSkillProfile> prepareEmployeeDocumentationsLookup() {
			if (employeeEntity.getProfile() == null) {
				return Collections.EMPTY_MAP;
			}

			List<AccountSkillProfile> employeeDocumentations = employeeEntity.getProfile().getSkills();
			Map<Integer, AccountSkillProfile> employeeDocumentationLookup = PicsCollectionUtil.convertToMap(employeeDocumentations, new PicsCollectionUtil.MapConvertable<Integer, AccountSkillProfile>() {
				@Override
				public Integer getKey(AccountSkillProfile entity) {
					return entity.getSkill().getId();
				}
			});

			return employeeDocumentationLookup;
		}

		//-- Getters


		public Integer getTotalRolesAssigned() {
			return totalRolesAssigned;
		}

		public void setTotalRolesAssigned(Integer totalRolesAssigned) {
			this.totalRolesAssigned = totalRolesAssigned;
		}

		public Boolean hasProjectSkills() {
			return hasProjectSkills;
		}

		public void setHasProjectSkills(Boolean hasProjectSkills) {
			this.hasProjectSkills = hasProjectSkills;
		}

		public Employee getEmployeeEntity() {
			return employeeEntity;
		}

		public MEmployeeStatus getEmployeeStatus() {
			return employeeStatus;
		}

		public void setEmployeeStatus(MEmployeeStatus employeeStatus) {
			this.employeeStatus = employeeStatus;
		}

		public MContractorManager.MContractor getContractor() {
			return contractor;
		}

		public Map<Integer, AccountSkillProfile> getEmployeeDocumentation() {
			return employeeDocumentation;
		}

		public void addToEmployeeRoles(Role role){
			employeeRoles.add(role);
			totalRolesAssigned = employeeRoles.size();
		}

		public Set<Role> getEmployeeRoles() {
			return employeeRoles;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getTitle() {
			return title;
		}

		public Boolean getWorkingAtSiteOrProject() {
			return workingAtSiteOrProject;
		}

		public void setWorkingAtSiteOrProject(Boolean workingAtSiteOrProject) {
			this.workingAtSiteOrProject = workingAtSiteOrProject;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MContractorEmployee mContractorEmployee = (MContractorEmployee) o;

			if (employeeEntity != null ? !employeeEntity.equals(mContractorEmployee.employeeEntity) : mContractorEmployee.employeeEntity != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return employeeEntity != null ? employeeEntity.hashCode() : 0;
		}
	}
}
