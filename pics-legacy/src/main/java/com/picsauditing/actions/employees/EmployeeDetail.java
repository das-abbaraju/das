package com.picsauditing.actions.employees;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.util.DoubleMap;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SuppressWarnings("serial")
public class EmployeeDetail extends AccountActionSupport {
	@Autowired
	protected EmployeeDAO employeeDAO;
	@Autowired
	protected AssessmentResultDAO assessmentResultDAO;
	@Autowired
	protected OperatorCompetencyDAO competencyDAO;
	@Autowired
	protected JobSiteTaskDAO siteTaskDAO;
	@Autowired
	protected JobTaskDAO taskDAO;
	@Autowired
	protected ContractorOperatorDAO coDAO;

	protected Employee employee;
	protected Account account;
	protected List<EmployeeCompetency> competencies;
	protected List<EmployeeQualification> tasks;
	protected List<EmployeeSite> worksAt;
	protected Map<JobTask, List<AssessmentResult>> qualification;
	protected DoubleMap<OperatorCompetency, JobRole, Boolean> map = new DoubleMap<OperatorCompetency, JobRole, Boolean>();
	protected Map<JobSite, List<JobTask>> tasksByJob;
	protected Map<JobRole, List<OperatorCompetency>> missingCompetencies;
	protected List<OperatorCompetency> skilledCompetencies;
	protected List<AssessmentResult> nccerData;

	public EmployeeDetail() {
		noteCategory = NoteCategory.Employee;
	}

	@Override
	public String execute() throws Exception {
		if (employee == null || employee.getId() == 0) {
			throw new RecordNotFoundException("Missing employee id");
		}

		account = employee.getAccount();

		notes = getNoteDao().findWhere(account.getId(),
				"noteCategory = 'Employee' AND employeeID = " + employee.getId(), 10);

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	private void buildCompetencies() {
		missingCompetencies = new HashMap<>();
		skilledCompetencies = new ArrayList<>();

		for (EmployeeRole er : employee.getEmployeeRoles()) {
			for (JobCompetency jc : er.getJobRole().getJobCompetencies()) {
				EmployeeCompetency ec = findEmployeeCompetency(jc.getCompetency());

				if (ec != null && ec.isSkilled()) {
					if (!skilledCompetencies.contains(jc.getCompetency())) {
						skilledCompetencies.add(jc.getCompetency());
					}
				} else {
					if (missingCompetencies.get(er.getJobRole()) == null) {
						missingCompetencies.put(er.getJobRole(), new ArrayList<OperatorCompetency>());
					}

					missingCompetencies.get(er.getJobRole()).add(jc.getCompetency());
				}
			}
		}
	}

	private EmployeeCompetency findEmployeeCompetency(OperatorCompetency oc) {
		for (EmployeeCompetency ec : employee.getEmployeeCompetencies()) {
			if (ec.getCompetency().equals(oc)) {
				return ec;
			}
		}

		return null;
	}

	public Map<JobRole, List<OperatorCompetency>> getMissingCompetencies() {
		if (missingCompetencies == null) {
			buildCompetencies();
		}

		return missingCompetencies;
	}

	public List<OperatorCompetency> getSkilledCompetencies() {
		if (skilledCompetencies == null) {
			buildCompetencies();
		}

		return skilledCompetencies;
	}

	public boolean isCanViewContractor() {
		boolean canViewContractor = false;
		if (permissions.isPicsEmployee()) {
			canViewContractor = true;
		}

		if (permissions.isContractor()) {
			canViewContractor = permissions.getAccountId() == employee.getAccount().getId();
		}

		if (permissions.isOperatorCorporate()) {
			ContractorOperator co = coDAO.find(employee.getAccount().getId(), permissions.getAccountId());
			canViewContractor = co != null;
		}

		return canViewContractor;
	}

	public boolean isCanViewOperator() {
		if (permissions.isPicsEmployee()) {
			return true;
		}

		if (permissions.isOperatorCorporate()) {
			return permissions.getVisibleAccounts().contains(employee.getAccount().getId());
		}

		return false;
	}

	public Boolean getCompetenciesByRole(OperatorCompetency opComp, JobRole jobRole) {
		return map.get(opComp, jobRole);
	}

	public List<EmployeeQualification> getJobTasks() {
		if (tasks == null) {
			List<JobTask> jobTasks = taskDAO.findByEmployee(employee.getId());
			tasks = new ArrayList<EmployeeQualification>();

			for (JobTask task : jobTasks) {
				for (EmployeeQualification eq : employee.getEmployeeQualifications()) {
					if (eq.getTask().equals(task) && !tasks.contains(eq) && eq.isCurrent())
						tasks.add(eq);
				}
			}
		}

		return tasks;
	}

	public List<EmployeeSite> getWorksAt() {
		if (worksAt == null) {
			worksAt = new ArrayList<>();
			for (EmployeeSite site : employee.getEmployeeSites()) {
				if (site.isActive() && isCanViewEmployeeSite(site)) {
					worksAt.add(site);
				}
			}
		}

		return worksAt;
	}

	private boolean isCanViewEmployeeSite(EmployeeSite site) {
		boolean canViewEmployeeSite = false;

		if (permissions.isPicsEmployee()) {
			canViewEmployeeSite = true;
		}

		if (permissions.isContractor()) {
			canViewEmployeeSite = permissions.getAccountId() == employee.getAccount().getId();
		}

		if (permissions.isOperatorCorporate()) {
			canViewEmployeeSite = permissions.getVisibleAccounts().contains(site.getOperator().getId());
		}

		return canViewEmployeeSite;
	}

	public Map<JobTask, List<AssessmentResult>> getQualification() {
		if (qualification == null) {
			qualification = new TreeMap<>();
			List<JobTask> tasks = taskDAO.findByEmployee(employee.getId());

			for (JobTask task : tasks) {
				qualification.put(task, task.getQualifiedResults(employee.getAssessmentResults()));
			}
		}

		return qualification;
	}

	public Map<JobSite, List<JobTask>> getTasks() {
		if (tasksByJob == null) {
			tasksByJob = siteTaskDAO.findByEmployee(employee.getId());
		}

		return tasksByJob;
	}

	public List<AssessmentResult> getNccerData() {
		if (nccerData == null) {
			nccerData = new ArrayList<AssessmentResult>(employee.getAssessmentResults());

			Iterator<AssessmentResult> iterator = nccerData.iterator();
			while (iterator.hasNext()) {
				AssessmentResult result = iterator.next();
				if (result.getAssessmentTest().getAssessmentCenter().getId() != Account.ASSESSMENT_NCCER
						|| !result.getAssessmentTest().isCurrent()) {
					iterator.remove();
				}
			}
		}

		return nccerData;
	}
}
