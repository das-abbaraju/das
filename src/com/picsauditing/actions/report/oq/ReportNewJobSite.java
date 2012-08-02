package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.JobContractor;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportNewJobSite extends ReportActionSupport {
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected EmployeeDAO employeeDAO;
	@Autowired
	protected EmployeeSiteDAO esDAO;
	@Autowired
	protected JobSiteDAO jsDAO;
	@Autowired
	protected FacilityChanger facilityChanger;
	// Filters
	@Autowired
	protected CountryDAO countryDAO;
	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;

	protected SelectSQL sql;
	protected ReportFilterJobSite filter = new ReportFilterJobSite();

	protected ContractorAccount contractor;
	protected JobSite jobSite;
	protected Employee employee;

	protected List<JobSite> current;
	protected List<Employee> employees;
	protected List<Employee> newEmployees;
	protected List<EmployeeSite> prevEmployees;

	@Before
	public void startup() throws Exception {
		if (contractor == null) {
			if (permissions.isContractor())
				contractor = contractorAccountDAO.find(permissions.getAccountId());
			else
				throw new RecordNotFoundException(getText(String.format(".message.ContractorMissing", getScope())));
		}

		filter.setPermissions(permissions);

		if (!permissions.isContractor() && !permissions.isAdmin())
			throw new NoRightsException("Contractor or PICS Administrator");

		if (ActionContext.getContext().getSession().get("actionErrors") != null) {
			setActionErrors((Collection<String>) ActionContext.getContext().getSession().get("actionErrors"));
			ActionContext.getContext().getSession().remove("actionErrors");
		}
	}

	@Override
	public String execute() throws Exception {
		if (getActionErrors().size() > 0)
			return SUCCESS;

		buildQuery();
		run(sql);

		return SUCCESS;
	}

	public String add() throws Exception {
		boolean worksForOperator = false;
		for (ContractorOperator co : contractor.getOperators()) {
			if (co.getOperatorAccount().equals(jobSite.getOperator()))
				worksForOperator = true;
		}

		if (!worksForOperator) {
			// TODO Allow contractor to choose which contractor type
			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(jobSite.getOperator());
			facilityChanger.setPermissions(permissions);
			facilityChanger.add();
		}

		boolean hasJobSite = false;
		for (JobContractor jc : contractor.getJobSites()) {
			if (jc.getJob().equals(jobSite))
				hasJobSite = true;
		}

		if (!hasJobSite) {
			JobContractor jc = new JobContractor();
			jc.setContractor(contractor);
			jc.setJob(jobSite);
			jc.setAuditColumns(permissions);
			jsDAO.save(jc);
		} else {
			addActionError(getText(String.format(".message.AlreadyAssignedToSite", getScope())));
			ActionContext.getContext().getSession().put("actionErrors", getActionErrors());
		}

		return setUrlForRedirect("ReportNewProjects.action");
	}

	public String remove() throws Exception {
		JobContractor jc = jsDAO.findJobContractorBySiteContractor(jobSite.getId(), permissions.getAccountId());
		// Prevent orphan data -- Check current and expired sites
		boolean found = false;
		for (Employee e : contractor.getAllEmployees()) {
			for (EmployeeSite es : e.getEmployeeSites()) {
				if (es.getJobSite().equals(jc.getJob()))
					found = true;
			}
		}

		if (found) {
			addActionError(getText(String.format("%s.message.ProjectHasEmployees", getScope()))
					+ "This project was not removed because employees are or were assigned to this project.");
			ActionContext.getContext().getSession().put("actionErrors", getActionErrors());
		} else {
			contractor.getJobSites().remove(jc);
			jsDAO.remove(jc);
		}

		return setUrlForRedirect("ReportNewProjects.action");
	}

	public String employees() throws Exception {
		if (getActionErrors().size() > 0)
			return SUCCESS;

		employees = esDAO.findEmployeesBySite(jobSite.getId(), permissions.getAccountId());
		getNewEmployees();

		return "employees";
	}

	public String addEmployee() throws Exception {
		List<EmployeeSite> eSites = new ArrayList<EmployeeSite>();

		for (EmployeeSite site : employee.getEmployeeSites()) {
			if (jobSite.equals(site.getJobSite()) && site.isCurrent())
				eSites.add(site);
		}

		if (eSites.size() > 0) {
			addActionError(getText(String.format("%s.message.EmployeeAlreadyAssignedToSite", getScope())));
			return "employees";
		} else {
			EmployeeSite es = new EmployeeSite();
			es.setAuditColumns(permissions);
			es.setEmployee(employee);
			es.setJobSite(jobSite);
			es.setOperator(jobSite.getOperator());
			es.defaultDates();

			esDAO.save(es);
		}

		return employees();
	}

	public String removeEmployee() throws Exception {
		for (EmployeeSite site : employee.getEmployeeSites()) {
			if (jobSite.equals(site.getJobSite()) && site.isCurrent()) {
				site.expire();
				esDAO.save(site);
			}
		}

		return employees();
	}

	protected void buildQuery() {
		sql = new SelectSQL("job_site js");
		sql.addJoin("JOIN accounts o ON o.id = js.opID");

		sql.addField("js.id");
		sql.addField("js.label");
		sql.addField("js.name");
		sql.addField("js.city");
		sql.addField("js.countrySubdivision");
		sql.addField("js.country");
		sql.addField("js.projectStart");
		sql.addField("o.name operatorName");

		sql.addWhere("js.active = 1");
		sql.addWhere("js.projectStop IS NULL OR js.projectStop > NOW()");
		sql.addWhere("js.id NOT IN (SELECT jobID FROM job_contractor WHERE conID = " + contractor.getId() + ")");
		sql.addWhere("o.status IN ('Active'"
				+ (permissions.isAdmin() || permissions.getAccountStatus().isDemo() ? ", 'Demo'" : "") + ")");

		sql.addOrderBy("js.projectStart");

		addFilterToSQL();
	}

	protected void addFilterToSQL() {
		ReportFilterJobSite f = getFilter();

		if (filterOn(f.getName()))
			sql.addWhere("js.name LIKE '%" + f.getName() + "%'");

		if (filterOn(f.getStart()))
			sql.addWhere("js.projectStart >= " + DateBean.format(f.getStart(), "M/d/yy"));

		if (filterOn(f.getOperator()))
			sql.addWhere("o.id IN (" + Strings.implode(f.getOperator()) + ")");

		if (filterOn(f.getCity(), ReportFilterAccount.getDefaultCity()))
			report.addFilter(new SelectFilter("city", "js.city LIKE '%?%'", f.getCity()));

		String countrySubdivisionList = Strings.implodeForDB(f.getCountrySubdivision(), ",");
		if (filterOn(countrySubdivisionList)) {
			sql.addWhere("js.countrySubdivision IN (" + countrySubdivisionList + ")");
			setFiltered(true);
		}

		String countryList = Strings.implodeForDB(f.getCountry(), ",");
		if (filterOn(countryList) && !filterOn(countrySubdivisionList)) {
			sql.addWhere("js.country IN (" + countryList + ")");
			setFiltered(true);
		}
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public JobSite getJobSite() {
		return jobSite;
	}

	public void setJobSite(JobSite jobSite) {
		this.jobSite = jobSite;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<JobSite> getCurrent() {
		if (current == null)
			current = jsDAO.findByContractor(permissions.getAccountId(), true);

		return current;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public List<Employee> getNewEmployees() {
		if (newEmployees == null) {
			newEmployees = new ArrayList<Employee>(contractor.getAllEmployees());
			Iterator<Employee> iterator = newEmployees.iterator();

			while (iterator.hasNext()) {
				if (employees.contains(iterator.next()))
					iterator.remove();
			}
		}

		return newEmployees;
	}

	public List<EmployeeSite> getPrevEmployees() {
		if (prevEmployees == null) {
			prevEmployees = new ArrayList<EmployeeSite>();
			List<EmployeeSite> formerSites = esDAO.findWhere("e.employee.account.id = " + permissions.getAccountId()
					+ " AND e.jobSite.id = " + jobSite.getId() + " ORDER BY e.expirationDate DESC");

			for (EmployeeSite es : formerSites) {
				if (!es.isCurrent())
					prevEmployees.add(es);
			}
		}

		return prevEmployees;
	}

	public ReportFilterJobSite getFilter() {
		return filter;
	}

	public String getAddress(BasicDynaBean d) {
		List<String> parts = new ArrayList<String>();

		if (d.get("city") != null)
			parts.add(d.get("city").toString());
		if (d.get("countrySubdivision") != null)
			parts.add(d.get("countrySubdivision").toString());
		if (d.get("country") != null)
			parts.add(d.get("country").toString());

		return Strings.implode(parts, ", ");
	}

	public class ReportFilterJobSite extends ReportFilter {
		private Permissions permissions;
		private String name;
		private Date start;
		private String city;
		private String[] countrySubdivision;
		private String[] country;
		private int[] operator;

		@Override
		public void setPermissions(Permissions permissions) {
			this.permissions = permissions;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getStart() {
			return start;
		}

		public void setStart(Date start) {
			this.start = start;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String[] getCountrySubdivision() {
			return countrySubdivision;
		}

		public void setCountrySubdivision(String[] countrySubdivision) {
			this.countrySubdivision = countrySubdivision;
		}

		public String[] getCountry() {
			return country;
		}

		public void setCountry(String[] country) {
			this.country = country;
		}

		public int[] getOperator() {
			return operator;
		}

		public void setOperator(int[] operator) {
			this.operator = operator;
		}

		public List<CountrySubdivision> getCountrySubdivisionList() {
			List<CountrySubdivision> result;
			if (!Strings.isEmpty(permissions.getCountry())) {
				Set<String> accountCountries = new HashSet<String>();
				accountCountries.add(permissions.getCountry());
				result = countrySubdivisionDAO.findByCountries(accountCountries, false);
			} else
				result = countrySubdivisionDAO.findAll();

			return result;
		}

		public List<Country> getCountryList() {
			return countryDAO.findAll();
		}

		public List<OperatorAccount> getOperatorList() throws Exception {
			if (permissions == null)
				return null;
			return operatorAccountDAO.findWhere(false, "a.requiresOQ = 1", permissions);
		}
	}
}