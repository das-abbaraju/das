package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.beanutils.BasicDynaBean;
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

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.JobSiteTask;
import com.picsauditing.jpa.entities.JobTask;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportOQEmployees extends ReportEmployee {
	private Database db = new Database();

	private List<Employee> employees;
	private Map<JobSite, List<JobSiteTask>> jobSiteTasks = new TreeMap<JobSite, List<JobSiteTask>>();
	private DoubleMap<Employee, JobSiteTask, Boolean> map = new DoubleMap<Employee, JobSiteTask, Boolean>();
	private DoubleMap<JobTask, Employee, Set<AssessmentResult>> results;

	@Override
	public String execute() throws Exception {
		loadPermissions();
		getFilter().setShowSsn(false);
		getFilter().setShowLimitEmployees(true);
		getFilter().setShowProjects(true);
		getFilter().setPermissions(permissions);
		
		if (permissions.isContractor())
			getFilter().setShowAccountName(false);

		orderByDefault = "a.name, e.lastName, e.firstName";

		buildQuery();
		run(sql);

		buildEmployees();
		buildJobSites();
		buildEmployeeQualifications();
		buildResults();

		if (download) {
			filename = "ReportOQByEmployee";
			getDownload();
		}

		return SUCCESS;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public Map<JobSite, List<JobSiteTask>> getJobSiteTasks() {
		return jobSiteTasks;
	}

	public DoubleMap<Employee, JobSiteTask, Boolean> getMap() {
		return map;
	}

	public DoubleMap<JobTask, Employee, Set<AssessmentResult>> getResults() {
		return results;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN employee_site es ON es.employeeID = e.id"
				+ dateRange("es.effectiveDate", "es.expirationDate", true));
		sql.addJoin("JOIN job_site js ON js.id = es.jobSiteID" + dateRange("js.projectStart", "js.projectStop", true));

		sql.addField("es.jobSiteID");
		sql.addField("a.type");

		sql.addGroupBy("e.id");

		if (filterOn(getFilter().getProjects()))
			sql.addWhere("js.id IN (" + Strings.implode(getFilter().getProjects()) + ")");
	}

	private void buildEmployees() {
		if (data != null) {
			employees = new ArrayList<Employee>();

			for (BasicDynaBean d : data) {
				Employee e = new Employee();
				e.setId(Integer.parseInt(d.get("employeeID").toString()));
				e.setLastName(d.get("lastName").toString());
				e.setFirstName(d.get("firstName").toString());
				e.setAccount(new Account());
				e.getAccount().setId(Integer.parseInt(d.get("accountID").toString()));
				e.getAccount().setName(d.get("name").toString());
				e.getAccount().setType(d.get("type").toString());

				employees.add(e);
			}
		}
	}

	private void buildJobSites() throws Exception {
		SelectSQL sql2 = new SelectSQL("job_site js");

		sql2.addJoin("JOIN accounts o ON o.id = js.opID");
		sql2.addJoin("LEFT JOIN job_site_task jst ON jst.jobID = js.id");
		sql2.addJoin("LEFT JOIN job_task jt ON jst.taskID = jt.id");

		sql2.addField("js.id jobSiteID");
		sql2.addField("js.label jobSiteLabel");
		sql2.addField("js.name jobSiteName");
		sql2.addField("jst.id jstID");
		sql2.addField("jst.controlSpan");
		sql2.addField("jt.id taskID");
		sql2.addField("jt.label taskLabel");
		sql2.addField("jt.name taskName");
		sql2.addField("o.name opName");

		if (permissions.isOperatorCorporate())
			sql2.addWhere("o.id = " + permissions.getAccountId());
		if (permissions.isContractor()) {
			sql2.addJoin("JOIN employee_site es ON es.jobSiteID = js.id");
			sql2.addJoin("JOIN employee e ON e.id = es.employeeID AND e.accountID = " + permissions.getAccountId());
		}

		sql2.addWhere(dateRange("js.projectStart", "js.projectStop", false));

		if (filterOn(getFilter().getProjects()))
			sql2.addWhere("js.id IN (" + Strings.implode(getFilter().getProjects()) + ")");

		sql2.addOrderBy("js.projectStart, jt.displayOrder");
		List<BasicDynaBean> data2 = db.select(sql2.toString(), false);

		for (BasicDynaBean d : data2) {
			JobSite js = new JobSite();
			js.setOperator(new OperatorAccount());
			js.getOperator().setName(d.get("opName").toString());
			js.setId(Integer.parseInt(d.get("jobSiteID").toString()));
			js.setLabel(d.get("jobSiteLabel").toString());
			js.setName(d.get("jobSiteName").toString());

			if (jobSiteTasks.get(js) == null)
				jobSiteTasks.put(js, new ArrayList<JobSiteTask>());

			if (d.get("jstID") != null) {
				JobSiteTask jst = new JobSiteTask();
				jst.setId(Integer.parseInt(d.get("jstID").toString()));
				jst.setControlSpan(Integer.parseInt(d.get("controlSpan").toString()));
				jst.setJob(js);

				jst.setTask(new JobTask());
				jst.getTask().setId(Integer.parseInt(d.get("taskID").toString()));
				jst.getTask().setLabel(d.get("taskLabel").toString());
				jst.getTask().setName(d.get("taskName").toString());

				if (!jobSiteTasks.get(js).contains(jst))
					jobSiteTasks.get(js).add(jst);
			}
		}
	}

	private void buildEmployeeQualifications() throws Exception {
		List<Integer> eIDs = new ArrayList<Integer>();
		for (Employee e : getEmployees()) {
			eIDs.add(e.getId());
		}

		SelectSQL sql2 = new SelectSQL("employee_qualification eq");
		sql2.addJoin("JOIN job_site_task jst ON jst.taskID = eq.taskID");
		sql2.addJoin("JOIN job_site js ON js.id = jst.jobID" + dateRange("js.projectStart", "js.projectStop", true));
		sql2.addJoin("JOIN employee_site es ON es.jobSiteID = jst.jobID AND es.employeeID = eq.employeeID"
				+ dateRange("es.effectiveDate", "es.expirationDate", true));

		sql2.addField("eq.employeeID");
		sql2.addField("jst.id jstID");

		sql2.addWhere("eq.qualified = 1");
		sql2.addWhere(dateRange("eq.effectiveDate", "eq.expirationDate", false));

		sql2.addGroupBy("eq.employeeID, jst.id");

		List<BasicDynaBean> data2 = db.select(sql2.toString(), false);
		for (BasicDynaBean d : data2) {
			Employee e = findEmployee(Integer.parseInt(d.get("employeeID").toString()));
			JobSiteTask jst = findJobSiteTask(Integer.parseInt(d.get("jstID").toString()));

			if (e != null && jst != null)
				map.put(e, jst, true);
		}
	}

	private void buildResults() throws Exception {
		SelectSQL sql2 = new SelectSQL("job_task_criteria jtc");

		sql2.addJoin("JOIN job_task jt ON jt.id = jtc.taskID");
		sql2.addJoin("JOIN assessment_result ar ON ar.assessmentTestID = jtc.assessmentTestID"
				+ dateRange("ar.effectiveDate", "ar.expirationDate", true));
		sql2.addJoin("JOIN assessment_test test ON test.id = jtc.assessmentTestID AND test.id = ar.assessmentTestID");
		sql2.addJoin("JOIN accounts center ON center.id = test.assessmentCenterID");

		sql2.addField("jt.id taskID");
		sql2.addField("ar.employeeID");
		sql2.addField("ar.id resultID");
		sql2.addField("center.name");
		sql2.addField("test.qualificationMethod");
		sql2.addField("test.qualificationType");
		sql2.addField("test.description");
		sql2.addField("ar.effectiveDate");
		sql2.addField("ar.expirationDate");

		sql2.addGroupBy("jt.id");
		sql2.addGroupBy("ar.id");
		sql2.addGroupBy("test.id");

		sql2.addOrderBy("jt.label, test.qualificationMethod");
		List<BasicDynaBean> data2 = db.select(sql2.toString(), false);

		results = new DoubleMap<JobTask, Employee, Set<AssessmentResult>>();
		for (BasicDynaBean d : data2) {
			JobTask jt = findJobTask(Integer.parseInt(d.get("taskID").toString()));
			Employee e = findEmployee(Integer.parseInt(d.get("employeeID").toString()));

			if (jt != null && e != null) {
				AssessmentResult ar = new AssessmentResult();
				ar.setId(Integer.parseInt(d.get("resultID").toString()));
				ar.setAssessmentTest(new AssessmentTest());
				ar.getAssessmentTest().setAssessmentCenter(new Account());
				ar.getAssessmentTest().getAssessmentCenter().setName(d.get("name").toString());
				ar.getAssessmentTest().setQualificationMethod(d.get("qualificationMethod").toString());
				ar.getAssessmentTest().setQualificationType(d.get("qualificationType").toString());
				ar.getAssessmentTest().setDescription(d.get("description").toString());
				ar.setEffectiveDate(DateBean.parseDate(d.get("effectiveDate").toString()));
				ar.setExpirationDate(DateBean.parseDate(d.get("expirationDate").toString()));

				if (results.get(jt, e) == null)
					results.put(jt, e, new HashSet<AssessmentResult>());

				results.get(jt, e).add(ar);
			}
		}
	}

	private Employee findEmployee(int id) {
		for (Employee e : getEmployees()) {
			if (e.getId() == id)
				return e;
		}

		return null;
	}

	private JobTask findJobTask(int id) {
		for (JobSite js : jobSiteTasks.keySet()) {
			for (JobSiteTask jst : jobSiteTasks.get(js)) {
				if (jst.getTask().getId() == id)
					return jst.getTask();
			}
		}

		return null;
	}

	private JobSiteTask findJobSiteTask(int jstID) {
		for (JobSite js : jobSiteTasks.keySet()) {
			for (JobSiteTask jst : jobSiteTasks.get(js)) {
				if (jst.getId() == jstID)
					return jst;
			}
		}

		return null;
	}

	private String dateRange(String start, String end, boolean prefix) {
		return (prefix ? " AND " : "") + "(" + start + " IS NULL OR " + start + " < NOW()) AND (" + end
				+ " IS NULL OR " + end + " > NOW())";
	}

	@Override
	protected HSSFWorkbook buildWorkbook(String filename) throws Exception {
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

		for (JobSite site : getJobSiteTasks().keySet()) {
			int count = 0;

			for (JobSiteTask jst : getJobSiteTasks().get(site)) {
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
					+ getJobSiteTasks().get(site).size()));

			prevSize += getJobSiteTasks().get(site).size();
		}

		// Employees
		int rowNum = 2;
		for (Employee e : getEmployees()) {
			row = sheet.createRow(rowNum);
			rowNum++;

			cell = row.createCell(0);
			cell.setCellValue(h.createRichTextString(e.getDisplayName()));
			cell = row.createCell(1);
			cell.setCellValue(h.createRichTextString(e.getAccount().getName()));

			int cellCount = 2;
			for (JobSite js : getJobSiteTasks().keySet()) {
				for (JobSiteTask jst : getJobSiteTasks().get(js)) {
					cell = row.createCell(cellCount);
					cell.setCellStyle(centerStyle);
					cellCount++;

					if (map.get(e, jst) != null && map.get(e, jst) == true)
						cell.setCellValue(h.createRichTextString("X"));
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

			for (Employee e : getEmployees()) {
				if (map.get(e, jst) != null && map.get(e, jst) == true)
					spanOfControl++;
			}

			int total = jst.getMinimumQualified(getEmployees().size());
			cell = row.createCell(totalColumns);
			cell.setCellValue(h.createRichTextString(spanOfControl + " of " + total));
			cell.setCellStyle((spanOfControl < total) ? redStyle : centerStyle);

			totalColumns++;
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);

		return wb;
	}
}
