package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;

public class ReportOQ extends PicsActionSupport {

	Database db = new Database();
	private List<ContractorSite> contractorSites = new ArrayList<ContractorSite>();

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		String contractorSQL = "SELECT a.id, a.name, js.id jobSiteID, js.label jobSite, count(*) totalEmployees FROM accounts a "
				+ " JOIN generalcontractors gc ON a.id = gc.subID AND gc.genID = "
				+ permissions.getAccountId()
				+ " JOIN employee e ON a.id = e.accountID"
				+ " JOIN employee_site es ON e.id = es.employeeID"
				+ " JOIN job_site js ON js.id = es.jobSiteID AND js.opID = gc.genID"
				+ " WHERE a.status IN ('Active','Demo')" + " GROUP BY a.id, js.id" + " ORDER BY a.name, js.name";
		List<BasicDynaBean> contractorData = db.select(contractorSQL, false);
		String conList = "0";
		for (BasicDynaBean row : contractorData) {
			conList += "," + row.get("id").toString();
			contractorSites.add(new ContractorSite(row));
		}

		String taskSQL = "SELECT jt.id, jt.label, jt.name, jst.controlSpan, jst.jobID FROM job_task jt"
				+ " JOIN job_site_task jst ON jst.taskID = jt.id" + " WHERE jt.opID = " + permissions.getAccountId();
		List<BasicDynaBean> taskData = db.select(taskSQL, false);
		String taskList = "0";
		for (BasicDynaBean row : taskData) {
			JobTaskSite jst = new JobTaskSite(row);
			taskList += "," + jst.id;
			for (ContractorSite cs : contractorSites) {
				if (cs.jobSite.id.equals(jst.jobID))
					cs.jobTasks.add(jst);
			}
		}

		String qualificationSQL = "SELECT e.id, e.firstName, e.lastName, e.accountID, eq.taskID, eq.qualified FROM employee_qualification eq"
				+ " JOIN employee e ON eq.employeeID = e.id"
				+ " WHERE eq.effectiveDate < NOW() AND eq.expirationDate > NOW()"
				+ " AND e.accountID IN ("
				+ conList
				+ ") AND eq.taskID IN (" + taskList + ")";
		List<BasicDynaBean> qualificationData = db.select(qualificationSQL, false);
		for (BasicDynaBean row : qualificationData) {
			Employee e = new Employee(row);
			for (ContractorSite cs : contractorSites) {
			}
		}

		return SUCCESS;
	}

	public List<ContractorSite> getContractorSites() {
		return contractorSites;
	}

	public class Base {

		public String id;
		public String name;
	}

	public class ContractorSite {

		public ContractorSite(BasicDynaBean row) {
			contractor.id = row.get("id").toString();
			contractor.name = row.get("name").toString();
			jobSite.id = row.get("jobSiteID").toString();
			jobSite.name = row.get("jobSite").toString();
			totalEmployees = row.get("totalEmployees").toString();
		}

		public Base contractor = new Base();
		public Base jobSite = new Base();
		public List<JobTaskSite> jobTasks = new ArrayList<JobTaskSite>();
		public List<Employee> employees = new ArrayList<Employee>();
		public Employee total;
		public String totalEmployees;
	}

	public class JobTaskSite extends Base {

		public JobTaskSite(BasicDynaBean row) {
			id = row.get("id").toString();
			name = row.get("label").toString();
			description = row.get("name").toString();
			controlSpan = row.get("controlSpan").toString();
			jobID = row.get("jobID").toString();
		}

		public String jobID;
		public String description;
		public String controlSpan;
	}

	public class Employee extends Base {

		public Employee(BasicDynaBean row) {
			// accountID, eq.taskID, eq.qualified
			id = row.get("id").toString();
			name = (row.get("firstName").toString() + " " + row.get("lastName").toString()).trim();
		}

		public List<Base> jobTasks = new ArrayList<Base>();
		public Map<String, Boolean> qualifications = new HashMap<String, Boolean>();
	}
}
