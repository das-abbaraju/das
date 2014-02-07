package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.forms.operator.ProjectRoleAssignment;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class SiteAssignmentAction extends PicsRestActionSupport {

	private static final long serialVersionUID = 1288428610452669599L;

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private SkillService skillService;

	private int siteId;
	private AccountModel site;

	private SiteAssignmentModel siteAssignmentModel;
	private List<ProjectRoleAssignment> projectRoleAssignments;

	public String status() {
		if (permissions.isOperator()) {
			int siteId = permissions.getAccountId();
			site = accountService.getAccountById(siteId);

			List<AccountModel> contractors = accountService.getContractors(siteId);
			Set<Integer> contractorIds = Utilities.getIdsFromCollection(
					contractors,
					new Utilities.Identitifable<AccountModel, Integer>() {
						@Override
						public Integer getId(AccountModel element) {
							return element.getId();
						}
					});

			List<Employee> employeesAtSite = employeeService.getEmployeesAssignedToSite(contractorIds, siteId);

			siteAssignmentModel = ViewModelFactory.getOperatorSiteAssignmentModelFactory().create(employeesAtSite);
		}

		return "status";
	}

	public String role() {
		return "role";
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public AccountModel getSite() {
		return site;
	}

	public SiteAssignmentModel getSiteAssignmentModel() {
		return siteAssignmentModel;
	}

	public List<ProjectRoleAssignment> getProjectRoleAssignments() {
		return projectRoleAssignments;
	}
}
