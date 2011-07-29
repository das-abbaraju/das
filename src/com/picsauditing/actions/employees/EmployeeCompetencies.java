package com.picsauditing.actions.employees;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeCompetencies extends ReportEmployee {
	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;
	@Autowired
	protected EmployeeCompetencyDAO employeeCompetencyDAO;

	protected Account account;
	protected Employee employee;
	protected OperatorCompetency competency;
	protected boolean skilled;
	private int auditID;

	private List<Employee> employees;
	private List<OperatorCompetency> competencies;
	private DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map;

	public String execute() throws Exception {
		if (account == null && permissions.isContractor())
			account = accountDAO.find(permissions.getAccountId());

		// Get auditID
		if (auditID > 0) {
			ActionContext.getContext().getSession().put("auditID", auditID);

			if (permissions.isAdmin()) {
				ContractorAudit audit = contractorAuditDAO.find(auditID);
				account = audit.getContractorAccount();
			}
		} else {
			auditID = (ActionContext.getContext().getSession().get("auditID") == null ? 0 : (Integer) ActionContext
					.getContext().getSession().get("auditID"));
		}

		if (account == null)
			throw new RecordNotFoundException(getText("EmployeeCompetencies.message.MissingAccount"));

		getFilter().setPermissions(permissions);
		getFilter().setAccountID(account.getId());

		getFilter().setShowJobRoles(true);
		getFilter().setShowCompetencies(true);
		getFilter().setShowSsn(false);

		if (permissions.isContractor())
			getFilter().setShowAccountName(false);

		buildQuery();
		sql.addGroupBy("e.id");
		run(sql);
		buildMap();

		if (download || "download".equals(button))
			return download();

		return SUCCESS;
	}

	public String changeCompetency() throws Exception {
		if (employee != null && competency != null) {
			EmployeeCompetency ec = null;
			for (EmployeeCompetency employeeCompetency : employee.getEmployeeCompetencies()) {
				if (employeeCompetency.getCompetency().equals(competency)) {
					ec = employeeCompetency;
					break;
				}
			}

			if (ec == null) {
				ec = new EmployeeCompetency();
				ec.setSkilled(false);
				ec.setEmployee(employee);
				ec.setCompetency(competency);
				ec.setAuditColumns(permissions);
			}

			ec.setSkilled(!ec.isSkilled());
			employeeCompetencyDAO.save(ec);

			if (ec.isSkilled()) {
				addActionMessage(getText("EmployeeCompetencies.message.AddedTo",
						new Object[] { ec.getCompetency().getLabel(), ec.getEmployee().getLastName(),
								ec.getEmployee().getFirstName() }));
			} else {
				addActionMessage(getText("EmployeeCompetencies.message.RemovedFrom",
						new Object[] { ec.getCompetency().getLabel(), ec.getEmployee().getLastName(),
								ec.getEmployee().getFirstName() }));
			}
		} else
			addActionError(getText("EmployeeCompetencies.message.MissingEmployeeCompetency"));

		return BLANK;
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

		if (filterOn(getFilter().getJobRoles()))
			sql.addWhere("jr.id IN (" + Strings.implode(getFilter().getJobRoles()) + ")");
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

		if (filterOn(getFilter().getCompetencies()))
			sql.addWhere("oc.id IN (" + Strings.implode(getFilter().getCompetencies()) + ")");

		Database db = new Database();
		List<BasicDynaBean> data2 = db.select(sql.toString(), true);

		employees = new ArrayList<Employee>();
		competencies = new ArrayList<OperatorCompetency>();
		map = new DoubleMap<Employee, OperatorCompetency, EmployeeCompetency>();

		for (BasicDynaBean d : data2) {
			Employee e = new Employee();
			e.setAccount(account);
			e.setId(Integer.parseInt(d.get("employeeID").toString()));
			e.setLastName(d.get("lastName").toString());
			e.setFirstName(d.get("firstName").toString());

			if (!employees.contains(e))
				employees.add(e);

			OperatorCompetency o = new OperatorCompetency();
			o.setId(Integer.parseInt(d.get("competencyID").toString()));
			o.setCategory(d.get("category").toString());
			o.setLabel(d.get("label").toString());
			o.setDescription(d.get("description").toString());

			if (!competencies.contains(o))
				competencies.add(o);

			EmployeeCompetency c = new EmployeeCompetency();
			c.setSkilled(false);

			if (d.get("ecID") != null) {
				c.setId(Integer.parseInt(d.get("ecID").toString()));
				c.setEmployee(e);
				c.setCompetency(o);
				c.setSkilled(d.get("skilled").toString().equals("1"));
			}

			map.put(e, o, c);
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

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> getMap() {
		return map;
	}
}
