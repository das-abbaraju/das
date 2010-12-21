package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.dao.NoteDAO;
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
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class EmployeeDetail extends AccountActionSupport implements Preparable {
	protected EmployeeDAO employeeDAO;
	protected OperatorCompetencyDAO competencyDAO;
	protected JobSiteTaskDAO siteTaskDAO;
	protected JobTaskDAO taskDAO;
	protected ContractorOperatorDAO coDAO;
	protected NoteDAO noteDAO;

	protected Employee employee;
	protected Account account;
	protected TreeSet<OperatorCompetency> opComps;
	protected List<EmployeeCompetency> competencies;
	protected List<EmployeeQualification> tasks;
	protected List<EmployeeSite> worksAt;
	protected Map<EmployeeQualification, List<AssessmentResult>> qualification;
	protected DoubleMap<OperatorCompetency, JobRole, Boolean> map = new DoubleMap<OperatorCompetency, JobRole, Boolean>();
	protected Map<JobSite, List<JobTask>> tasksByJob;

	public EmployeeDetail(EmployeeDAO employeeDAO, OperatorCompetencyDAO competencyDAO, JobSiteTaskDAO siteTaskDAO,
			ContractorOperatorDAO coDAO, JobTaskDAO taskDAO, NoteDAO noteDAO) {
		this.employeeDAO = employeeDAO;
		this.competencyDAO = competencyDAO;
		this.siteTaskDAO = siteTaskDAO;
		this.coDAO = coDAO;
		this.taskDAO = taskDAO;
		this.noteDAO = noteDAO;
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
		
		notes = noteDAO.findWhere(account.getId(), "noteCategory = 'Employee'", 10);

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public TreeSet<OperatorCompetency> getOpComps() {
		if (opComps == null) {
			List<Integer> jobRoleIDs = new ArrayList<Integer>();
			opComps = new TreeSet<OperatorCompetency>();

			for (EmployeeRole er : employee.getEmployeeRoles()) {
				jobRoleIDs.add(er.getJobRole().getId());
			}

			List<JobCompetency> jcs = competencyDAO.findByJobRoles(jobRoleIDs);

			for (JobCompetency jc : jcs) {
				opComps.add(jc.getCompetency());
				map.put(jc.getCompetency(), jc.getJobRole(), false);
			}

			for (EmployeeCompetency ec : employee.getEmployeeCompetencies()) {
				for (JobCompetency jc : jcs) {
					if (jc.getCompetency().equals(ec.getCompetency())) {
						map.put(jc.getCompetency(), jc.getJobRole(), ec.isSkilled());
					}
				}
			}
		}

		return opComps;
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
					if (eq.getTask().equals(task) && !tasks.contains(eq))
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
					if (eq.getTask().equals(task)) {
						Map<Integer, Set<JobTaskCriteria>> map = task.getJobTaskCriteriaMap();

						for (Integer group : map.keySet()) {
							results.clear();

							for (JobTaskCriteria jtc : map.get(group)) {
								for (AssessmentResult r : employee.getAssessmentResults()) {
									if (jtc.getAssessmentTest().equals(r.getAssessmentTest()))
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
