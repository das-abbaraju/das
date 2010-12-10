package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeQualificationDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobSiteTaskDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.EmployeeSite;
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
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		filter.setShowSsn(false);
		filter.setShowLimitEmployees(true);
		filter.setShowProjects(true);
		filter.setPermissions(permissions);

		setOrderBy(getOrderBy() == null ? "e.account.name, e.lastName, e.firstName" : getOrderBy());

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
			where += " AND e.account.name LIKE '%" + filter.getAccountName() + "%' OR e.account.id = '"
					+ Utilities.escapeQuotes(filter.getAccountName()) + "'";
		if (filter.isLimitEmployees() && conID == 0 && filterOn(filter.getAccountName()) == false)
			where += " AND e.account.id = " + permissions.getAccountId();

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
			List<Integer> jobSiteIDs = new ArrayList<Integer>();

			if (filterOn(filter.getProjects())) {
				for (Integer id : filter.getProjects())
					jobSiteIDs.add(id);
			}

			for (JobSiteTask task : jobSiteTasks) {
				if (task.isCurrent() && task.getJob().isActive(new Date())) {
					if (jobSiteIDs.contains(task.getJob().getId()) || jobSiteIDs.isEmpty()) {
						if (jobSites.get(task.getJob()) == null)
							jobSites.put(task.getJob(), new ArrayList<JobSiteTask>());

						jobSites.get(task.getJob()).add(task);
					}
				}
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

			List<EmployeeSite> sites = employeeSiteDAO.findWhere("e.employee.id IN (" + Strings.implode(employeeIDs)
					+ ") AND e.jobSite.id IN (" + Strings.implode(jobSiteIDs) + ")");

			for (EmployeeSite site : sites) {
				if (site.isCurrent())
					worksAtSite.put(site.getEmployee(), site.getJobSite(), true);
			}
		}

		return worksAtSite;
	}

	// Filter methods
	public ReportFilterEmployee getFilter() {
		return filter;
	}

	public void getExcelDownload() throws Exception {
		execute();

		String filename = "ReportOQByEmployee";
		filename += ".xls";

		// Create spreadsheet here
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Report OQ Employees");
		CreationHelper h = wb.getCreationHelper();

		// Set header font and style
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setFontHeightInPoints((short) 12);

		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);

		// Center
		HSSFCellStyle centerStyle = wb.createCellStyle();
		centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// Red font
		HSSFFont redFont = wb.createFont();
		redFont.setColor(HSSFColor.RED.index);

		HSSFCellStyle redStyle = wb.createCellStyle();
		redStyle.setFont(redFont);
		redStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// Set number style
		HSSFCellStyle headerRightStyle = wb.createCellStyle();
		headerRightStyle.setFont(headerFont);
		headerRightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		// Header
		HSSFRow row = sheet.createRow(0);
		HSSFRow row2 = sheet.createRow(1);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(h.createRichTextString("Employee"));
		cell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		cell = row.createCell(1);
		cell.setCellValue(h.createRichTextString("Company"));
		cell.setCellStyle(headerStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));

		int totalColumns = 2;

		// Job Sites and Tasks
		int prevSize = 0;
		List<JobSiteTask> orderedJST = new ArrayList<JobSiteTask>();

		for (JobSite site : getJobSites().keySet()) {
			int count = 0;

			for (JobSiteTask jst : getJobSites().get(site)) {
				orderedJST.add(jst);
				cell = row2.createCell(2 + prevSize + count);
				sheet.setColumnWidth(cell.getColumnIndex(), 256 * (jst.getTask().getLabel().length() + 10));
				cell.setCellStyle(headerStyle);
				cell.setCellValue(new HSSFRichTextString(jst.getTask().getLabel() + " (1 of " + jst.getControlSpan()
						+ ")"));

				count++;
				totalColumns++;
			}

			cell = row.createCell(2 + prevSize);
			cell.setCellStyle(headerStyle);
			cell.setCellValue(new HSSFRichTextString(site.getOperator().getName() + ": " + site.getLabel()));
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 2 + prevSize, 1 + prevSize
					+ getJobSites().get(site).size()));

			prevSize += getJobSites().get(site).size();
		}

		// Employees
		int rowNum = 2;
		List<Employee> sortedEmployees = getEmployees();
		Collections.sort(sortedEmployees, new Comparator<Employee>() {
			@Override
			public int compare(Employee o1, Employee o2) {
				if (o1.getLastName().toLowerCase().compareTo(o2.getLastName().toLowerCase()) == 0)
					return o1.getFirstName().toLowerCase().compareTo(o2.getLastName().toLowerCase());

				return o1.getLastName().toLowerCase().compareTo(o2.getLastName().toLowerCase());
			}
		});

		for (Employee e : getEmployees()) {
			row = sheet.createRow(rowNum);
			rowNum++;

			cell = row.createCell(0);
			cell.setCellValue(h.createRichTextString(e.getDisplayName()));
			cell = row.createCell(1);
			cell.setCellValue(h.createRichTextString(e.getAccount().getName()));

			int cellCount = 2;
			for (JobSite js : getJobSites().keySet()) {
				for (JobSiteTask jst : getJobSites().get(js)) {
					cell = row.createCell(cellCount);
					cell.setCellStyle(centerStyle);
					cellCount++;

					String marked = "";
					if (getWorksAtSite().get(e, js) != null && getWorksAtSite().get(e, js)) {
						if (getQualifications().get(e, jst.getTask()) != null
								&& getQualifications().get(e, jst.getTask()).isCurrent()
								&& getQualifications().get(e, jst.getTask()).isQualified())
							marked += "X";
					}

					cell.setCellValue(h.createRichTextString(marked.trim()));
				}
			}
		}

		// Totals
		row = sheet.createRow(rowNum);
		rowNum++;
		cell = row.createCell(0);
		cell.setCellValue(h.createRichTextString("Total Qualified"));
		cell.setCellStyle(headerRightStyle);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

		totalColumns = 2;
		for (JobSiteTask jst : orderedJST) {
			int spanOfControl = 0;

			for (Employee e : sortedEmployees) {
				if (getWorksAtSite().get(e, jst.getJob()) != null && getWorksAtSite().get(e, jst.getJob())) {
					if (getQualifications().get(e, jst.getTask()) != null
							&& getQualifications().get(e, jst.getTask()).isQualified())
						spanOfControl++;
				}
			}

			int total = jst.getMinimumQualified(sortedEmployees.size());
			cell = row.createCell(totalColumns);
			cell.setCellValue(h.createRichTextString(spanOfControl + " of " + total));
			cell.setCellStyle((spanOfControl < total) ? redStyle : centerStyle);

			totalColumns++;
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
	}
}
