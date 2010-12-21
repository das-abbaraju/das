package com.picsauditing.actions.employees;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.search.Database;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeCompetencies extends ReportEmployee {
	protected AccountDAO accountDAO;
	protected EmployeeDAO employeeDAO;
	protected EmployeeCompetencyDAO ecDAO;
	protected OperatorCompetencyDAO ocDAO;

	protected int id;
	protected int employeeID;
	protected int competencyID;
	protected boolean skilled;

	private List<Employee> employees;
	private List<OperatorCompetency> competencies;
	private DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map;

	public EmployeeCompetencies(AccountDAO accountDAO, EmployeeDAO employeeDAO, EmployeeCompetencyDAO ecDAO,
			OperatorCompetencyDAO ocDAO) {
		this.accountDAO = accountDAO;
		this.employeeDAO = employeeDAO;
		this.ecDAO = ecDAO;
		this.ocDAO = ocDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor())
			id = permissions.getAccountId();

		if (id > 0)
			account = accountDAO.find(id);
		else
			throw new RecordNotFoundException("Missing account ID");

		if (button != null) {
			if ("ChangeCompetency".equals(button)) {
				if (employeeID > 0 && competencyID > 0) {
					EmployeeCompetency ec = null;
					try {
						ec = ecDAO.find(employeeID, competencyID);
					} catch (Exception e) {
						ec = new EmployeeCompetency();
						ec.setSkilled(false);
						ec.setEmployee(employeeDAO.find(employeeID));
						ec.setCompetency(ocDAO.find(competencyID));
						ec.setAuditColumns(permissions);
					}

					ec.setSkilled(!ec.isSkilled());
					ecDAO.save(ec);

					addActionMessage("Successfully" + (ec.isSkilled() ? " added " : " removed ")
							+ ec.getCompetency().getLabel() + (ec.isSkilled() ? " to " : " from ")
							+ ec.getEmployee().getLastName() + ", " + ec.getEmployee().getFirstName());
				} else
					addActionError("Missing employee and/or competency");
			}

			if (getActionErrors().size() > 0)
				return SUCCESS;
		}

		getFilter().setPermissions(permissions);
		getFilter().setAccountID(account.getId());

		getFilter().setShowJobRoles(true);
		getFilter().setShowCompetencies(true);

		if (permissions.isContractor())
			getFilter().setShowAccountName(false);

		buildQuery();
		run(sql);
		buildMap();

		if (download || "download".equals(button)) {
			if (Strings.isEmpty(filename)) {
				String className = this.getClass().getName();
				filename = className.substring(className.lastIndexOf("."));
			}
			
			HSSFWorkbook wb = buildWorkbook(filename);
			
			excelSheet.setName(filename);
			filename += ".xls";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		return SUCCESS;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN employee_role er ON er.employeeID = e.id");
		sql.addJoin("JOIN job_role jr ON jr.id = er.jobRoleID AND jr.active = 1");

		sql.addWhere("a.id = " + account.getId());
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		if (filterOn(getFilter().getJobRoles()))
			sql.addWhere("jr.id IN (" + Strings.implode(getFilter().getJobRoles()) + ")");
	}

	private void buildMap() throws SQLException {
		sql.addJoin("JOIN job_competency jc ON jc.jobRoleID = jr.id");
		sql.addJoin("JOIN operator_competency oc ON oc.id = jc.competencyID");
		sql.addJoin("LEFT JOIN employee_competency ec ON ec.employeeID = e.id AND ec.competencyID = oc.id");

		sql.addField("jr.id jobRoleID");
		sql.addField("jr.name jobRoleName");
		sql.addField("oc.id competencyID");
		sql.addField("oc.category");
		sql.addField("oc.label");
		sql.addField("oc.description");
		sql.addField("ec.id ecID");
		sql.addField("ec.skilled");
		sql.addOrderBy("oc.category, oc.label");

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
		redFont.setColor(HSSFColor.WHITE.index);
		
		HSSFCellStyle red = wb.createCellStyle();
		red.setFont(redFont);
		red.setFillForegroundColor(HSSFColor.RED.index);
		red.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		red.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// Data
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellStyle(headerStyle);
		cell.setCellValue(new HSSFRichTextString("Employees"));
		
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
						cell.setCellValue(new HSSFRichTextString("OK"));
					} else {
						cell.setCellStyle(red);
						cell.setCellValue(new HSSFRichTextString("Missing"));
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public int getCompetencyID() {
		return competencyID;
	}

	public void setCompetencyID(int competencyID) {
		this.competencyID = competencyID;
	}

	public boolean isSkilled() {
		return skilled;
	}

	public void setSkilled(boolean skilled) {
		this.skilled = skilled;
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
