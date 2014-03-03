package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.forms.converter.RequiredSiteSkillFormConverter;
import com.picsauditing.employeeguard.forms.operator.RequiredSiteSkillForm;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.forms.binding.FormBinding;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SuppressWarnings("serial")
public class DashboardAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private RequiredSiteSkillFormConverter requiredSiteSkillFormConverter;
	@Autowired
	private SkillService skillService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	/* forms */
	@FormBinding("operator_required_skill_edit")
	private RequiredSiteSkillForm requiredSiteSkillForm;

	private List<AccountSkill> requiredSkills;
	private Map<AccountModel, List<AccountSkill>> siteSkills;
	private List<AccountSkill> corporateSkills;

	/* pages */

	public String index() {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(permissions.getAccountId());
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		Map<SkillStatus, Integer> employeeStatusCount = getCount(employeeStatuses);

		this.jsonString = new Gson().toJson(employeeStatusCount, HashMap.class);

		return JSON_STRING;
	}

	private Map<SkillStatus, Integer> getCount(final Map<Employee, SkillStatus> employeeStatuses) {
		Map<SkillStatus, Integer> statusCount = buildMapPrepopulatedWithKeys();
		for (Employee employee : employeeStatuses.keySet()) {
			SkillStatus skillStatus = employeeStatuses.get(employee);
			int count = statusCount.get(skillStatus) + 1;
			statusCount.put(skillStatus, count);
		}

		return statusCount;
	}

	private Map<SkillStatus, Integer> buildMapPrepopulatedWithKeys() {
		return new HashMap<SkillStatus, Integer>() {{
			put(SkillStatus.Complete, 0);
			put(SkillStatus.Expiring, 0);
			put(SkillStatus.Expired, 0);
		}};
	}

	public String editRequiredSkillsSection() {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(permissions.getAccountId());
		corporateSkills = skillService.getSkillsForAccounts(corporateIds);
		requiredSkills = skillService.getRequiredSkillsForSite(permissions.getAccountId());

		Collections.sort(corporateSkills);
		Collections.sort(requiredSkills);

		return "required-skills";
	}

	/* other methods */
	public String update() throws Exception {
		List<AccountSkill> requiredSkills = requiredSiteSkillFormConverter.convert(requiredSiteSkillForm);
		skillService.setRequiredSkillsForSite(requiredSkills, id, permissions.getAppUserID());

		return setUrlForRedirect("/employee-guard/operators/dashboard");
	}

	/* getter + setters */

	public RequiredSiteSkillForm getRequiredSiteSkillForm() {
		return requiredSiteSkillForm;
	}

	public void setRequiredSiteSkillForm(RequiredSiteSkillForm requiredSiteSkillForm) {
		this.requiredSiteSkillForm = requiredSiteSkillForm;
	}

	public List<AccountSkill> getRequiredSkills() {
		return requiredSkills;
	}

	public Map<AccountModel, List<AccountSkill>> getSiteSkills() {
		return siteSkills;
	}

	public List<AccountSkill> getCorporateSkills() {
		return corporateSkills;
	}
}
