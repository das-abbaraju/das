package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeQualificationDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobCompetencyDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
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
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class EmployeeDetail extends AccountActionSupport implements Preparable {
	private EmployeeDAO employeeDAO;
	protected EmployeeCompetencyDAO ecDAO;
	protected EmployeeRoleDAO erDAO;
	protected JobCompetencyDAO jcDAO;
	protected OperatorCompetencyDAO competencyDAO;
	protected EmployeeQualificationDAO qualDAO;
	protected JobSiteTaskDAO siteTaskDAO;
	protected EmployeeSiteDAO esDAO;
	protected ContractorOperatorDAO coDAO;

	protected Employee employee;
	protected TreeSet<OperatorCompetency> opComps;
	protected List<EmployeeCompetency> competencies;
	protected List<EmployeeQualification> tasks;
	protected List<EmployeeSite> worksAt;
	protected Map<EmployeeQualification, List<AssessmentResult>> qualification;
	protected DoubleMap<OperatorCompetency, JobRole, Boolean> map = new DoubleMap<OperatorCompetency, JobRole, Boolean>();
	protected Map<JobSite, List<JobTask>> tasksByJob;

	public EmployeeDetail(EmployeeDAO employeeDAO, EmployeeCompetencyDAO ecDAO, EmployeeRoleDAO erDAO,
			JobCompetencyDAO jcDAO, OperatorCompetencyDAO competencyDAO, EmployeeQualificationDAO qualDAO,
			JobSiteTaskDAO siteTaskDAO, EmployeeSiteDAO esDAO, ContractorOperatorDAO coDAO) {
		this.employeeDAO = employeeDAO;
		this.ecDAO = ecDAO;
		this.erDAO = erDAO;
		this.jcDAO = jcDAO;
		this.competencyDAO = competencyDAO;
		this.qualDAO = qualDAO;
		this.siteTaskDAO = siteTaskDAO;
		this.esDAO = esDAO;
		this.coDAO = coDAO;
	}

	@Override
	public void prepare() throws Exception {
		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (employee == null || employee.getId() == 0)
			throw new RecordNotFoundException("Missing employee id");

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

			for (EmployeeCompetency ec : getCompetencies()) {
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

	public List<EmployeeCompetency> getCompetencies() {
		if (competencies == null)
			competencies = ecDAO.findByEmployee(employee.getId());

		return competencies;
	}

	public Boolean getCompetenciesByRole(OperatorCompetency opComp, JobRole jobRole) {
		return map.get(opComp, jobRole);
	}

	public List<EmployeeQualification> getJobTasks() {
		if (tasks == null) {
			tasks = new ArrayList<EmployeeQualification>(employee.getEmployeeQualifications());

			Iterator<EmployeeQualification> iterator = tasks.iterator();
			while (iterator.hasNext()) {
				EmployeeQualification e = iterator.next();

				if (e.getEffectiveDate() != null && e.getExpirationDate() != null && !e.isCurrent())
					iterator.remove();
			}

			Collections.sort(tasks, new SortTaskByLabel());
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
			Set<JobTask> allTasks = new HashSet<JobTask>();

			for (EmployeeSite es : getWorksAt()) {
				for (JobSiteTask jst : es.getJobSite().getTasks()) {
					allTasks.add(jst.getTask());
				}
			}

			ArrayList<JobTask> sortedList = new ArrayList<JobTask>(allTasks);
			Collections.sort(sortedList, new Comparator<JobTask>() {
				@Override
				public int compare(JobTask o1, JobTask o2) {
					if (o1.getDisplayOrder() == o2.getDisplayOrder())
						return o1.getLabel().compareTo(o2.getLabel());

					return o1.getDisplayOrder() - o2.getDisplayOrder();
				}
			});

		}

		return qualification;
	}

	public Map<JobSite, List<JobTask>> getTasks() {
		if (tasksByJob == null) {
			tasksByJob = siteTaskDAO.findByEmployee(employee.getId());
		}

		return tasksByJob;
	}

	private class SortTaskByLabel implements Comparator<EmployeeQualification> {
		public int compare(EmployeeQualification o1, EmployeeQualification o2) {
			try {
				Double d1 = Double.parseDouble(o1.getTask().getLabel());
				Double d2 = Double.parseDouble(o2.getTask().getLabel());

				return d1.compareTo(d2);
			} catch (Exception e) {
				return o1.getTask().getLabel().compareTo(o2.getTask().getLabel());
			}
		}
	}
}
