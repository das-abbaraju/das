package com.picsauditing.actions.employees;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeCompetencies extends ReportEmployee {
	@Autowired
	protected EmployeeCompetencyDAO employeeCompetencyDAO;
	@Autowired
	protected JobRoleDAO jobRoleDAO;

	private Database database = new Database();

	protected Account account;
	protected Employee employee;
	protected OperatorCompetency competency;
	protected boolean skilled;
	private ContractorAudit audit;

	private List<Employee> employees = new ArrayList<Employee>();
	private List<OperatorCompetency> competencies = new ArrayList<OperatorCompetency>();
	private Map<Employee, String> employeeJobRoles = new TreeMap<Employee, String>();
	private DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map = new DoubleMap<Employee, OperatorCompetency, EmployeeCompetency>();
	private Table<Employee, OperatorCompetency, EmployeeCompetency> employeeCompetencyTable = TreeBasedTable.create();
	// Competency Table
	private Table<JobRole, OperatorCompetency, JobCompetency> matrixTable = TreeBasedTable.create();

	public String execute() throws Exception {
		findAccount();
		initializeCompetenciesForEmployees();

		setDefaultFilters();
		buildQuery();
		sql.addGroupBy("e.id");
		run(sql);

		buildMap();

		matrixTable = jobRoleDAO.buildJobCompetencyTable(account.getId(), true);

		if (download || "download".equals(button)) {
			return download();
		}

		return SUCCESS;
	}

	public String changeCompetency() throws Exception {
		if (employee != null && competency != null) {
			EmployeeCompetency employeeCompetency = findEmployeeCompetency();

			if (employeeCompetency == null) {
				employeeCompetency = createNewEmployeeCompetency();
			}

			employeeCompetency.setSkilled(!employeeCompetency.isSkilled());
			employeeCompetencyDAO.save(employeeCompetency);
			employee.getEmployeeCompetencies().add(employeeCompetency);

			addResultToActionMessage(employeeCompetency);
		} else {
			addActionError(getText("EmployeeCompetencies.message.MissingEmployeeCompetency"));
		}

		return BLANK;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

	public boolean isSkilled() {
		return skilled;
	}

	public void setSkilled(boolean skilled) {
		this.skilled = skilled;
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public Map<Employee, String> getEmployeeJobRoles() {
		return employeeJobRoles;
	}

	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> getMap() {
		return map;
	}

	public Table<Employee, OperatorCompetency, EmployeeCompetency> getEmployeeCompetencyTable() {
		return employeeCompetencyTable;
	}

	public Table<JobRole, OperatorCompetency, JobCompetency> getMatrixTable() {
		return matrixTable;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN employee_role er ON er.employeeID = e.id");
		sql.addJoin("JOIN job_role jr ON jr.id = er.jobRoleID AND jr.active = 1");
		sql.addJoin("JOIN job_competency jc ON jc.jobRoleID = jr.id");
		sql.addJoin("JOIN operator_competency oc ON oc.id = jc.competencyID");

		sql.addWhere("a.id = " + account.getId());

		sql.addOrderBy(getOrderBy());
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		if (filterOn(getFilter().getJobRoles())) {
			sql.addWhere("jr.id IN (" + Strings.implode(getFilter().getJobRoles()) + ")");
		}
	}

	protected HSSFWorkbook buildWorkbook(String name) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		wb.setSheetName(0, name);
		// Styles
		HSSFFont headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFCellStyle green = wb.createCellStyle();
		green.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		green.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		green.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFFont redFont = wb.createFont();
		redFont.setColor(HSSFColor.RED.index);

		HSSFCellStyle red = wb.createCellStyle();
		red.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		red.setFont(redFont);
		// Data
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(new HSSFRichTextString(getText("global.Employees")));

		int cellCount = 1;
		for (OperatorCompetency oc : getCompetencies()) {
			cell = row.createCell(cellCount);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(new HSSFRichTextString(oc.getLabel()));
			cellCount++;
		}

		int rowCount = 1;
		for (Employee e : getEmployees()) {
			row = sheet.createRow(rowCount);

			cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString(e.getLastName() + ", " + e.getFirstName()));

			cellCount = 1;
			for (OperatorCompetency oc : getCompetencies()) {
				cell = row.createCell(cellCount);
				if (map.get(e, oc) != null) {
					if (map.get(e, oc).isSkilled()) {
						cell.setCellStyle(green);
						cell.setCellValue(new HSSFRichTextString("X"));
					} else {
						cell.setCellStyle(red);
						cell.setCellValue(new HSSFRichTextString(getText("EmployeeCompetencies.status.Missing")));
					}
				}

				cellCount++;
			}

			rowCount++;
		}

		for (int i = 0; i < cellCount; i++) {
			sheet.autoSizeColumn(i);
		}

		return wb;
	}

	private void findAccount() throws RecordNotFoundException {
		if (audit != null) {
			account = audit.getContractorAccount();
		}

		if (account == null && permissions.isContractor())
			account = accountDAO.find(permissions.getAccountId());

		if (account == null) {
			throw new RecordNotFoundException(getText("EmployeeCompetencies.message.MissingAccount"));
		}
	}

	private void initializeCompetenciesForEmployees() {
		for (Employee employee : account.getEmployees()) {
			boolean noCompetenciesForEmployeeWithRole = employee.getEmployeeRoles().size() > 0
					&& employee.getEmployeeCompetencies().isEmpty();

			if (noCompetenciesForEmployeeWithRole) {
				for (EmployeeRole employeeRole : employee.getEmployeeRoles()) {
					for (JobCompetency jobCompetency : employeeRole.getJobRole().getJobCompetencies()) {
						EmployeeCompetency employeeCompetency = new EmployeeCompetency();

						employeeCompetency.setEmployee(employee);
						employeeCompetency.setCompetency(jobCompetency.getCompetency());
						employeeCompetency.setSkilled(true);
						employeeCompetency.setAuditColumns(permissions);

						employeeCompetencyDAO.save(employeeCompetency);
						employee.getEmployeeCompetencies().add(employeeCompetency);
					}
				}
			}
		}
	}

	private EmployeeCompetency findEmployeeCompetency() {
		for (EmployeeCompetency employeeCompetency : employee.getEmployeeCompetencies()) {
			if (employeeCompetency.getCompetency().equals(competency)) {
				return employeeCompetency;
			}
		}

		return null;
	}

	private void addResultToActionMessage(EmployeeCompetency employeeCompetency) {
		String label = employeeCompetency.getCompetency().getLabel();
		String lastName = employeeCompetency.getEmployee().getLastName();
		String firstName = employeeCompetency.getEmployee().getFirstName();

		if (employeeCompetency.isSkilled()) {
			addActionMessage(getText("EmployeeCompetencies.message.AddedTo",
					new Object[] { label, lastName, firstName }));
		} else {
			addActionMessage(getText("EmployeeCompetencies.message.RemovedFrom", new Object[] { label, lastName,
					firstName }));
		}
	}

	private void setDefaultFilters() {
		getFilter().setPermissions(permissions);
		getFilter().setAccountID(account.getId());

		getFilter().setShowJobRoles(true);
		getFilter().setShowCompetencies(true);
		getFilter().setShowSsn(false);

		if (permissions.isContractor()) {
			getFilter().setShowAccountName(false);
		}
	}

	private EmployeeCompetency createNewEmployeeCompetency() {
		EmployeeCompetency employeeCompetency = new EmployeeCompetency();

		employeeCompetency.setSkilled(false);
		employeeCompetency.setEmployee(employee);
		employeeCompetency.setCompetency(competency);
		employeeCompetency.setAuditColumns(permissions);

		return employeeCompetency;
	}

	private void buildMap() throws SQLException {
		sql = new SelectSQL("employee e");
		buildQuery();

		sql.addJoin("LEFT JOIN employee_competency ec ON ec.employeeID = e.id AND ec.competencyID = oc.id");

		sql.addField("jr.id jobRoleID");
		sql.addField("jr.name jobRoleName");
		sql.addField("oc.id competencyID");
		sql.addField("oc.category");
		sql.addField("oc.label");
		sql.addField("oc.description");
		sql.addField("ec.id ecID");
		sql.addField("ec.skilled");

		sql.addOrderBy("oc.label");

		if (filterOn(getFilter().getCompetencies())) {
			sql.addWhere("oc.id IN (" + Strings.implode(getFilter().getCompetencies()) + ")");
		}

		List<BasicDynaBean> data2 = database.select(sql.toString(), true);

		employees = new ArrayList<Employee>();
		competencies = new ArrayList<OperatorCompetency>();
		employeeJobRoles = new HashMap<Employee, String>();
		map = new DoubleMap<Employee, OperatorCompetency, EmployeeCompetency>();

		Map<Employee, Set<String>> jobRoles = new HashMap<Employee, Set<String>>();

		buildEntitiesForMapAndAddJobRoles(data2, jobRoles);
		fillEmployeeJobRoles(jobRoles);
	}

	private void buildEntitiesForMapAndAddJobRoles(List<BasicDynaBean> data, Map<Employee, Set<String>> jobRoles) {
		for (BasicDynaBean dynaBean : data) {
			Employee employee = buildEmployeeForMap(dynaBean);
			OperatorCompetency competency = buildCompetencyForMap(dynaBean);
			EmployeeCompetency employeeCompetency = buildEmployeeCompetencyForMap(dynaBean, employee, competency);

			String jobRole = dynaBean.get("jobRoleName").toString();

			if (jobRoles.get(employee) == null) {
				jobRoles.put(employee, new TreeSet<String>());
			}

			jobRoles.get(employee).add(jobRole);

			map.put(employee, competency, employeeCompetency);
			employeeCompetencyTable.put(employee, competency, employeeCompetency);
		}
	}

	private Employee buildEmployeeForMap(BasicDynaBean d) {
		Employee e = new Employee();
		e.setAccount(account);
		e.setId(Integer.parseInt(d.get("employeeID").toString()));
		e.setLastName(d.get("lastName").toString());
		e.setFirstName(d.get("firstName").toString());

		if (!employees.contains(e))
			employees.add(e);

		return e;
	}

	private OperatorCompetency buildCompetencyForMap(BasicDynaBean data) {
		OperatorCompetency competency = new OperatorCompetency();
		competency.setId(Integer.parseInt(data.get("competencyID").toString()));
		competency.setCategory(data.get("category").toString());
		competency.setLabel(data.get("label").toString());
		competency.setDescription(data.get("description").toString());

		if (!competencies.contains(competency)) {
			competencies.add(competency);
		}

		return competency;
	}

	private EmployeeCompetency buildEmployeeCompetencyForMap(BasicDynaBean data, Employee employee,
			OperatorCompetency competency) {
		EmployeeCompetency employeeCompetency = new EmployeeCompetency();
		employeeCompetency.setSkilled(false);

		if (data.get("ecID") != null) {
			employeeCompetency.setId(Integer.parseInt(data.get("ecID").toString()));
			employeeCompetency.setEmployee(employee);
			employeeCompetency.setCompetency(competency);
			employeeCompetency.setSkilled(data.get("skilled").toString().equals("1"));
		}

		return employeeCompetency;
	}

	private void fillEmployeeJobRoles(Map<Employee, Set<String>> jobRoles) {
		for (Employee employee : jobRoles.keySet()) {
			List<String> roles = new ArrayList<String>(jobRoles.get(employee));

			int last = roles.size();
			if (last > 3) {
				last = 3;
			}

			String suffix = "";
			if (roles.size() > 3) {
				suffix = "...";
			}

			employeeJobRoles.put(employee, Strings.implode(roles.subList(0, last), ", ") + suffix);
		}
	}
}
