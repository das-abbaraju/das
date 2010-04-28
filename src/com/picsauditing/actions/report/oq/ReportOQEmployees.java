package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeQualificationDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class ReportOQEmployees extends PicsActionSupport {

	private int conID;
	private int jobSiteID;
	private List<Employee> employees;
	private List<JobSiteTask> jobSiteTasks;
	private DoubleMap<Employee, JobTask, EmployeeQualification> qualifications;

	private JobSiteTaskDAO siteTaskDAO;
	private EmployeeDAO employeeDAO;
	private EmployeeQualificationDAO qualificationDAO;

	public ReportOQEmployees(JobSiteTaskDAO siteTaskDAO, EmployeeDAO employeeDAO,
			EmployeeQualificationDAO qualificationDAO) {
		this.siteTaskDAO = siteTaskDAO;
		this.employeeDAO = employeeDAO;
		this.qualificationDAO = qualificationDAO;
	}

	@Override
	public String execute() throws Exception {

		employees = employeeDAO.findWhere("e.active = 1 AND e.account.id = " + conID
				+ " AND e IN (SELECT employee FROM EmployeeSite WHERE jobSite.id = " + jobSiteID + ")");
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

}
