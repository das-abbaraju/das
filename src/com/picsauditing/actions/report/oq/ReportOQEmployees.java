package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeQualificationDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.EmployeeSiteTask;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportOQEmployees extends ReportActionSupport {

	private int conID = 0;
	private int jobSiteID = 0;
	private List<Employee> employees;
	private List<JobSiteTask> jobSiteTasks;
	private DoubleMap<Employee, JobTask, EmployeeQualification> qualifications;
	private Map<JobSite, List<JobSiteTask>> jobSites;
	private DoubleMap<Employee, JobSite, Boolean> worksAtSite;
	private DoubleMap<Employee, JobSiteTask, Boolean> assigned;

	private JobSiteTaskDAO siteTaskDAO;
	private EmployeeDAO employeeDAO;
	private EmployeeQualificationDAO qualificationDAO;
	private EmployeeSiteDAO employeeSiteDAO;
	
	// Filter
	private ReportFilterEmployee filter = new ReportFilterEmployee();

	public ReportOQEmployees(JobSiteTaskDAO siteTaskDAO, EmployeeDAO employeeDAO,
			EmployeeQualificationDAO qualificationDAO, EmployeeSiteDAO employeeSiteDAO) {
		this.siteTaskDAO = siteTaskDAO;
		this.employeeDAO = employeeDAO;
		this.qualificationDAO = qualificationDAO;
		this.employeeSiteDAO = employeeSiteDAO;
		
		filter.setShowSsn(false);
		orderByDefault = "e.lastName, e.firstName";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (permissions.isContractor())
			conID = permissions.getAccountId();

		String where = "e.active = 1 ";
		if (conID > 0)
			where += " AND e.account.id = " + conID;
		if (jobSiteID > 0)
			where += " AND e IN (SELECT employee FROM EmployeeSite WHERE operator.id = " + permissions.getAccountId()
					+ " AND jobSite.id = " + jobSiteID + ")";
		else {
			if (permissions.isOperatorCorporate())
				where += " AND e IN (SELECT employee FROM EmployeeSite WHERE operator.id = "
					+ permissions.getAccountId() + ")";
		}
		
		if (filterOn(filter.getFirstName()))
			where += " AND e.firstName LIKE '%" + filter.getFirstName() + "%'";
		if (filterOn(filter.getLastName()))
			where += " AND e.lastName LIKE '%" + filter.getLastName() + "%'";
		if (filterOn(filter.getEmail()))
			where += " AND e.email LIKE '%" + filter.getEmail() + "%'";
		if (filterOn(filter.getAccountName()))
			where += " AND e.account.name LIKE '%" + filter.getAccountName() + "%'";

		employees = employeeDAO.findWhere(where + " ORDER BY " + getOrderBy());
		
		if (permissions.isContractor() || permissions.isAdmin())
			jobSiteTasks = siteTaskDAO.findByEmployeeAccount(conID);
		else if (permissions.isOperatorCorporate() && jobSiteID == 0)
			jobSiteTasks = siteTaskDAO.findByOperator(permissions.getAccountId());
		else
			jobSiteTasks = siteTaskDAO.findByJob(jobSiteID);

		qualifications = qualificationDAO.find(employees, jobSiteTasks);
		return SUCCESS;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public int getJobSiteID() {
		return jobSiteID;
	}

	public void setJobSiteID(int jobSiteID) {
		this.jobSiteID = jobSiteID;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public List<JobSiteTask> getJobSiteTasks() {
		return jobSiteTasks;
	}

	public DoubleMap<Employee, JobTask, EmployeeQualification> getQualifications() {
		return qualifications;
	}

	public Map<JobSite, List<JobSiteTask>> getJobSites() {
		if (jobSites == null) {
			jobSites = new HashMap<JobSite, List<JobSiteTask>>();
			
			for (JobSiteTask task : jobSiteTasks) {
				if (jobSites.get(task.getJob()) == null)
					jobSites.put(task.getJob(), new ArrayList<JobSiteTask>());
				
				jobSites.get(task.getJob()).add(task);
			}
		}
		
		return jobSites;
	}
	
	public DoubleMap<Employee, JobSite, Boolean> getWorksAtSite() {
		if (worksAtSite == null) {
			worksAtSite = new DoubleMap<Employee, JobSite, Boolean>();
			
			List<Integer> employeeIDs = new ArrayList<Integer>();
			for (Employee e : employees) {
				employeeIDs.add(e.getId());
			}
			
			List<Integer> jobSiteIDs = new ArrayList<Integer>();
			for (JobSite j : jobSites.keySet()) {
				jobSiteIDs.add(j.getId());
			}
			
			List<EmployeeSite> sites = employeeSiteDAO.findWhere("e.employee.id IN (" 
					+ Strings.implode(employeeIDs) + ") AND e.jobSite.id IN (" 
					+ Strings.implode(jobSiteIDs) + ")");
			
			for (EmployeeSite site : sites) {
				if (site.isCurrent())
					worksAtSite.put(site.getEmployee(), site.getJobSite(), true);
			}
		}
		
		return worksAtSite;
	}
	
	public DoubleMap<Employee, JobSiteTask, Boolean> getAssigned() {
		if (assigned == null) {
			assigned = new DoubleMap<Employee, JobSiteTask, Boolean>();
			
			int opID = 0;
			for (JobSite j : jobSites.keySet()) {
				opID = j.getOperator().getId();
				break;
			}
			
			List<EmployeeSiteTask> all = employeeSiteDAO.findTasksByOperator(opID);
			// There should be an easier way to do this
			for (EmployeeSiteTask a : all) {
				Employee e = a.getEmployeeSite().getEmployee();
				JobSite j = a.getEmployeeSite().getJobSite();
				
				if (employees.contains(e) && jobSites.keySet().contains(j)) {
					for (JobSiteTask jst : jobSites.get(j)) {
						if (jst.getTask().equals(a.getTask()))
							assigned.put(e, jst, true);
					}
				}
			}
		}
		
		return assigned;
	}
	
	// Filter methods
	public ReportFilterEmployee getFilter() {
		return filter;
	}
	
	public void setFilter(ReportFilterEmployee filter) {
		this.filter = filter;
	}
}
