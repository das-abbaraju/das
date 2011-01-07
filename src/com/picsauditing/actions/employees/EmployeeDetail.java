package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.JobTaskCriteria;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class EmployeeDetail extends AccountActionSupport implements Preparable {
	protected EmployeeDAO employeeDAO;
	protected OperatorCompetencyDAO competencyDAO;
	protected JobSiteTaskDAO siteTaskDAO;
	protected JobTaskDAO taskDAO;
	protected ContractorOperatorDAO coDAO;

	protected Employee employee;
	protected Account account;
	protected List<EmployeeCompetency> competencies;
	protected List<EmployeeQualification> tasks;
	protected List<EmployeeSite> worksAt;
	protected Map<EmployeeQualification, List<AssessmentResult>> qualification;
	protected DoubleMap<OperatorCompetency, JobRole, Boolean> map = new DoubleMap<OperatorCompetency, JobRole, Boolean>();
	protected Map<JobSite, List<JobTask>> tasksByJob;
	protected Map<JobRole, List<OperatorCompetency>> missingCompetencies;
	protected List<OperatorCompetency> skilledCompetencies;

	public EmployeeDetail(EmployeeDAO employeeDAO, OperatorCompetencyDAO competencyDAO, JobSiteTaskDAO siteTaskDAO,
			ContractorOperatorDAO coDAO, JobTaskDAO taskDAO) {
		this.employeeDAO = employeeDAO;
		this.competencyDAO = competencyDAO;
		this.siteTaskDAO = siteTaskDAO;
		this.coDAO = coDAO;
		this.taskDAO = taskDAO;

		noteCategory = NoteCategory.Employee;
	}

	@Override
	public void prepare() throws Exception {
		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
			account = employee.getAccount();
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (employee == null || employee.getId() == 0)
			throw new RecordNotFoundException("Missing employee id");

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
		missingCompetencies = new HashMap<JobRole, List<OperatorCompetency>>();
		skilledCompetencies = new ArrayList<OperatorCompetency>();

		for (EmployeeRole er : employee.getEmployeeRoles()) {
			for (JobCompetency jc : er.getJobRole().getJobCompetencies()) {
				EmployeeCompetency ec = findEmployeeCompetency(jc.getCompetency());
				
				if (ec != null && ec.isSkilled()) {
					if (!skilledCompetencies.contains(jc.getCompetency()))
						skilledCompetencies.add(jc.getCompetency());
				} else {
					if (missingCompetencies.get(er.getJobRole()) == null)
						missingCompetencies.put(er.getJobRole(), new ArrayList<OperatorCompetency>());
					
					missingCompetencies.get(er.getJobRole()).add(jc.getCompetency());
				}
			}
		}
	}
	
	private EmployeeCompetency findEmployeeCompetency(OperatorCompetency oc) {
		for (EmployeeCompetency ec : employee.getEmployeeCompetencies()) {
			if (ec.getCompetency().equals(oc))
				return ec;
		}
		
		return null;
	}

	public Map<JobRole, List<OperatorCompetency>> getMissingCompetencies() {
		if (missingCompetencies == null)
			buildCompetencies();

		return missingCompetencies;
	}

	public List<OperatorCompetency> getSkilledCompetencies() {
		if (skilledCompetencies == null)
			buildCompetencies();

		return skilledCompetencies;
	}

	public boolean isCanViewContractor() {
		ContractorOperator co = coDAO.find(employee.getAccount().getId(), permissions.getAccountId());

		if (co != null)
			return true;

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
			worksAt = new ArrayList<EmployeeSite>();
			for (EmployeeSite site : employee.getEmployeeSites()) {
				if (site.isCurrent() && site.getJobSite().isActive(new Date()))
					worksAt.add(site);
			}
		}

		return worksAt;
	}

	public Map<EmployeeQualification, List<AssessmentResult>> getQualification() {
		if (qualification == null) {
			qualification = new HashMap<EmployeeQualification, List<AssessmentResult>>();

			List<JobTask> jobTasks = taskDAO.findByEmployee(employee.getId());
			List<AssessmentResult> results = new ArrayList<AssessmentResult>();

			for (JobTask task : jobTasks) {
				for (EmployeeQualification eq : employee.getEmployeeQualifications()) {
					if (eq.isCurrent() && eq.getTask().equals(task)) {
						Map<Integer, Set<JobTaskCriteria>> map = task.getJobTaskCriteriaMap();

						for (Integer group : map.keySet()) {
							results.clear();

							for (JobTaskCriteria jtc : map.get(group)) {
								for (AssessmentResult r : employee.getAssessmentResults()) {
									if (jtc.getAssessmentTest().equals(r.getAssessmentTest()) && r.isCurrent())
										results.add(r);
								}
							}

							if (results.size() == map.get(group).size() && qualification.get(eq) == null)
								qualification.put(eq, results);
						}
					}
				}
			}
		}

		return qualification;
	}

	public Map<JobSite, List<JobTask>> getTasks() {
		if (tasksByJob == null)
			tasksByJob = siteTaskDAO.findByEmployee(employee.getId());

		return tasksByJob;
	}
}
