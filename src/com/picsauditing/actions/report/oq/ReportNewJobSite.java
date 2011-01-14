package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.JobContractor;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportNewJobSite extends ReportActionSupport implements Preparable {
	protected AccountDAO accountDAO;
	protected EmployeeDAO employeeDAO;
	protected EmployeeSiteDAO esDAO;
	protected JobSiteDAO jsDAO;
	protected FacilityChanger facilityChanger;

	protected SelectSQL sql = new SelectSQL("job_site js");
	protected ReportFilterJobSite filter = new ReportFilterJobSite();

	protected int id;
	protected int jobSiteID;
	protected int employeeID;

	protected Account account;
	protected JobSite jobSite;

	protected List<JobSite> current;
	protected List<Employee> employees = new ArrayList<Employee>();
	protected List<Employee> newEmployees;
	protected List<EmployeeSite> prevEmployees;

	public ReportNewJobSite(AccountDAO accountDAO, EmployeeDAO employeeDAO, EmployeeSiteDAO esDAO, JobSiteDAO jsDAO,
			FacilityChanger facilityChanger) {
		this.accountDAO = accountDAO;
		this.employeeDAO = employeeDAO;
		this.esDAO = esDAO;
		this.jsDAO = jsDAO;
		this.facilityChanger = facilityChanger;
	}

	@Override
	public void prepare() throws Exception {
		loadPermissions();
		id = getParameter("id");

		if (id == 0) {
			if (permissions.isContractor())
				id = permissions.getAccountId();
			else
				throw new RecordNotFoundException("Contractor ID");
		}

		account = accountDAO.find(id);
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		filter.setPermissions(permissions);

		if (!permissions.isContractor() && !permissions.isAdmin())
			throw new NoRightsException("Contractor or PICS Administrator");

		if (ActionContext.getContext().getSession().get("actionErrors") != null) {
			setActionErrors((Collection<String>) ActionContext.getContext().getSession().get("actionErrors"));
			ActionContext.getContext().getSession().remove("actionErrors");

			if (getActionErrors().size() > 0)
				return SUCCESS;
		}

		if (button != null) {
			// TODO add notes in the future
			if ("Add".equals(button)) {
				JobSite js = jsDAO.find(jobSiteID);
				ContractorAccount con = (ContractorAccount) account;

				boolean worksForOperator = false;
				for (ContractorOperator co : con.getOperators()) {
					if (co.getOperatorAccount().equals(js.getOperator()))
						worksForOperator = true;
				}

				if (!worksForOperator) {
					// TODO Allow contractor to choose which contractor type
					facilityChanger.setContractor(con);
					facilityChanger.setOperator(js.getOperator());
					facilityChanger.setPermissions(permissions);
					facilityChanger.setType(ContractorType.Onsite);
					facilityChanger.add();
				}

				boolean hasJobSite = false;
				for (JobContractor jc : con.getJobSites()) {
					if (jc.getJob().equals(js))
						hasJobSite = true;
				}

				if (!hasJobSite) {
					JobContractor jc = new JobContractor();
					jc.setContractor(con);
					jc.setJob(js);
					jc.setAuditColumns(permissions);
					jsDAO.save(jc);
				} else {
					addActionError("You have already been assigned this job site.");
					ActionContext.getContext().getSession().put("actionErrors", getActionErrors());
				}

				return redirect("ReportNewProjects.action");
			}

			if ("Remove".equals(button)) {
				JobContractor jc = jsDAO.findJobContractorBySiteContractor(jobSiteID, permissions.getAccountId());
				// Prevent orphan data -- Check current and expired sites
				boolean found = false;
				for (Employee e : account.getEmployees()) {
					for (EmployeeSite es : e.getEmployeeSites()) {
						if (es.getJobSite().equals(jc.getJob()))
							found = true;
					}
				}

				if (found) {
					addActionError("This project was not removed because employees are or were assigned to this project.");
					ActionContext.getContext().getSession().put("actionErrors", getActionErrors());
				} else {
					ContractorAccount con = (ContractorAccount) account;

					con.getJobSites().remove(jc);
					jsDAO.remove(jc);
				}

				return redirect("ReportNewProjects.action");
			}

			if ((button.contains("employee") || button.contains("Employee")) && jobSiteID > 0) {
				jobSite = jsDAO.find(jobSiteID);

				if ("removeEmployee".equalsIgnoreCase(button)) {
					List<EmployeeSite> eSites = esDAO.findWhere("e.employee.id = " + employeeID
							+ " AND e.jobSite.id = " + jobSiteID + " ORDER BY e.creationDate DESC");

					EmployeeSite es = eSites.get(0);

					if (es.isCurrent()) {
						es.expire();
						esDAO.save(es);
					}
				}

				if ("addEmployee".equalsIgnoreCase(button)) {
					List<EmployeeSite> eSites = esDAO.findWhere("e.employee.id = " + employeeID
							+ " AND e.jobSite.id = " + jobSiteID + " ORDER BY e.expirationDate DESC");

					if (eSites.size() > 0 && eSites.get(0).isCurrent()) {
						addActionError("Employee is all ready assigned to this site");
						return SUCCESS;
					} else {
						JobSite js = jsDAO.find(jobSiteID);

						EmployeeSite es = new EmployeeSite();
						es.setAuditColumns(permissions);
						es.setEmployee(new Employee());
						es.getEmployee().setId(employeeID);
						es.setJobSite(js);
						es.setOperator(js.getOperator());
						es.defaultDates();

						esDAO.save(es);
					}
				}

				employees = esDAO.findEmployeesBySite(jobSiteID, permissions.getAccountId());
				getNewEmployees();

				return SUCCESS;
			}
		}

		buildQuery();
		run(sql);

		return SUCCESS;
	}

	protected void buildQuery() {
		sql.addJoin("JOIN accounts o ON o.id = js.opID");

		sql.addField("js.id");
		sql.addField("js.label");
		sql.addField("js.name");
		sql.addField("js.city");
		sql.addField("js.state");
		sql.addField("js.country");
		sql.addField("js.projectStart");
		sql.addField("o.name operatorName");

		sql.addWhere("js.active = 1");
		sql.addWhere("js.projectStop IS NULL OR js.projectStop > NOW()");
		sql.addWhere("js.id NOT IN (SELECT jobID FROM job_contractor WHERE conID = " + account.getId() + ")");

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

		if (filterOn(f.getCity(), ReportFilterAccount.DEFAULT_CITY))
			report.addFilter(new SelectFilter("city", "js.city LIKE '%?%'", f.getCity()));

		String stateList = Strings.implodeForDB(f.getState(), ",");
		if (filterOn(stateList)) {
			sql.addWhere("js.state IN (" + stateList + ")");
			setFiltered(true);
		}

		String countryList = Strings.implodeForDB(f.getCountry(), ",");
		if (filterOn(countryList) && !filterOn(stateList)) {
			sql.addWhere("js.country IN (" + countryList + ")");
			setFiltered(true);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobSiteID() {
		return jobSiteID;
	}

	public void setJobSiteID(int jobSiteID) {
		this.jobSiteID = jobSiteID;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public Account getAccount() {
		return account;
	}

	public JobSite getJobSite() {
		return jobSite;
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
			newEmployees = new ArrayList<Employee>(account.getEmployees());
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
		if (d.get("state") != null)
			parts.add(d.get("state").toString());
		if (d.get("country") != null)
			parts.add(d.get("country").toString());

		return Strings.implode(parts, ", ");
	}

	public class ReportFilterJobSite extends ReportFilter {
		private Permissions permissions;
		private String name;
		private Date start;
		private String city;
		private String[] state;
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

		public String[] getState() {
			return state;
		}

		public void setState(String[] state) {
			this.state = state;
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

		public List<State> getStateList() {
			StateDAO stateDAO = (StateDAO) SpringUtils.getBean("StateDAO");
			List<State> result;
			if (!Strings.isEmpty(permissions.getCountry())) {
				Set<String> accountCountries = new HashSet<String>();
				accountCountries.add(permissions.getCountry());
				result = stateDAO.findByCountries(accountCountries, false);
			} else
				result = stateDAO.findAll();

			return result;
		}

		public List<Country> getCountryList() {
			CountryDAO countryDAO = (CountryDAO) SpringUtils.getBean("CountryDAO");
			return countryDAO.findAll();
		}

		public List<OperatorAccount> getOperatorList() throws Exception {
			if (permissions == null)
				return null;
			OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
			return dao.findWhere(false, "a.requiresOQ = 1", permissions);
		}
	}
}