package com.picsauditing.employeeguard.forms.factory;

import org.springframework.beans.factory.annotation.Autowired;

public class FormBuilderFactory {

	@Autowired
	private CompanySkillsFormBuilder companySkillsFormBuilder;
	@Autowired
	private EmployeeProfileFormBuilder employeeProfileFormBuilder;
	@Autowired
	private EmployeeProfileEditFormBuilder employeeProfileEditFormBuilder;
	@Autowired
	private ProjectInfoFactory projectInfoFactory;

	private final SkillDocumentFormBuilder skillDocumentFormBuilder = new SkillDocumentFormBuilder();
	private final SkillInfoBuilder skillInfoBuilder = new SkillInfoBuilder();
	private final ProjectCompaniesFormBuilder projectCompaniesFormBuilder = new ProjectCompaniesFormBuilder();
	private final ContractorDetailProjectFormBuilder contratorDetailProjectFormBuilder = new ContractorDetailProjectFormBuilder();
	private final OperatorProjectFormBuilder operatorProjectFormBuilder = new OperatorProjectFormBuilder();
	private final ContractorEmployeeProjectAssignmentFactory contractorEmployeeProjectAssignmentFactory = new ContractorEmployeeProjectAssignmentFactory();
	private final OperatorEmployeeProjectAssignmentFactory operatorEmployeeProjectAssignmentFactory = new OperatorEmployeeProjectAssignmentFactory();
	private final RoleInfoFactory roleInfoFactory = new RoleInfoFactory();
	private final ProfileDocumentInfoBuilder profileDocumentInfoBuilder = new ProfileDocumentInfoBuilder();

	public CompanySkillsFormBuilder getCompanySkillsFormBuilder() {
		return companySkillsFormBuilder;
	}

	public EmployeeProfileFormBuilder getEmployeeProfileFormBuilder() {
		return employeeProfileFormBuilder;
	}

	public EmployeeProfileEditFormBuilder getEmployeeProfileEditFormBuilder() {
		return employeeProfileEditFormBuilder;
	}

	public SkillDocumentFormBuilder getSkillDocumentFormBuilder() {
		return skillDocumentFormBuilder;
	}

	public SkillInfoBuilder getSkillInfoBuilder() {
		return skillInfoBuilder;
	}

	public ProjectCompaniesFormBuilder getProjectCompaniesFormBuilder() {
		return projectCompaniesFormBuilder;
	}

	public ContractorDetailProjectFormBuilder getContratorDetailProjectFormBuilder() {
		return contratorDetailProjectFormBuilder;
	}

	public OperatorProjectFormBuilder getOperatorProjectFormBuilder() {
		return operatorProjectFormBuilder;
	}

	public ContractorEmployeeProjectAssignmentFactory getContractorEmployeeProjectAssignmentFactory() {
		return contractorEmployeeProjectAssignmentFactory;
	}

	public OperatorEmployeeProjectAssignmentFactory getOperatorEmployeeProjectAssignmentFactory() {
		return operatorEmployeeProjectAssignmentFactory;
	}

	public RoleInfoFactory getRoleInfoFactory() {
		return roleInfoFactory;
	}

	public ProjectInfoFactory getProjectInfoFactory() {
		return projectInfoFactory;
	}

	public ProfileDocumentInfoBuilder getProfileDocumentInfoBuilder() {
		return profileDocumentInfoBuilder;
	}
}
