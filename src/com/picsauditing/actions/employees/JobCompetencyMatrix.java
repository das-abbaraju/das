package com.picsauditing.actions.employees;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class JobCompetencyMatrix extends AccountActionSupport {
	@Autowired
	protected JobRoleDAO jobRoleDAO;
	@Autowired
	protected OperatorCompetencyDAO operatorCompetencyDAO;

	private ContractorAudit audit;
	private List<JobRole> roles = Collections.emptyList();
	private List<OperatorCompetency> competencies = Collections.emptyList();
	private DoubleMap<JobRole, OperatorCompetency, JobCompetency> map = new DoubleMap<JobRole, OperatorCompetency, JobCompetency>();

	@Override
	public String execute() throws Exception {
		if (audit != null) {
			account = audit.getContractorAccount();
		}

		if (account == null && permissions.isContractor()) {
			account = accountDAO.find(permissions.getAccountId());
		}

		loadRolesAndCompetencies();
		addAccountJobRoles();

		return SUCCESS;
	}

	public String download() throws Exception {
		String filename = "HSECompetencyMatrix";
		HSSFWorkbook wb = buildWorkbook(filename);
		filename += ".xls";

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		return null;
	}

	public List<JobRole> getRoles() {
		return roles;
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public JobCompetency getJobCompetency(JobRole role, OperatorCompetency comp) {
		return map.get(role, comp);
	}

	public List<JobRole> getRoles(OperatorCompetency operatorCompetency) {
		// need to check if forward entries are all null to not include in list
		for (JobRole role : roles) {
			if (map.get(role, operatorCompetency) != null) {
				return roles;
			}
		}

		return null;
	}

	private void loadRolesAndCompetencies() {
		roles = jobRoleDAO.findMostUsed(account.getId(), true);
		competencies = operatorCompetencyDAO.findMostUsed(account.getId(), true);
		map = jobRoleDAO.findJobCompetencies(account.getId(), true);
	}

	private void addAccountJobRoles() {
		List<JobRole> jobRoles = account.getJobRoles();
		Collections.sort(jobRoles, new Comparator<JobRole>() {
			public int compare(JobRole o1, JobRole o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		for (JobRole jr : jobRoles) {
			if (!roles.contains(jr) && jr.isActive())
				roles.add(jr);
		}
	}

	private HSSFWorkbook buildWorkbook(String name) {
		loadRolesAndCompetencies();
		addAccountJobRoles();

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(name);

		HSSFFont headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFFont normalFont = wb.createFont();
		normalFont.setColor(HSSFColor.DARK_BLUE.index);
		normalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle normalStyle = wb.createCellStyle();
		normalStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		normalStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		normalStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		normalStyle.setFont(normalFont);

		// Add the Column Headers/Company Name to the top of the report
		int rowNumber = 0;
		int columnCount = 0;
		HSSFRow r = sheet.createRow(rowNumber);
		HSSFCell c = r.createCell(columnCount);

		if (permissions.isOperatorCorporate()) {
			c.setCellStyle(headerStyle);
			c.setCellValue(new HSSFRichTextString(account.getName()));
			rowNumber++;
			r = sheet.createRow(rowNumber);
		}

		// HSE Competency Category & Label
		c = r.createCell(columnCount);
		c.setCellValue(new HSSFRichTextString("HSE Competency Category"));
		c.setCellStyle(headerStyle);
		columnCount++;

		c = r.createCell(columnCount);
		c.setCellValue(new HSSFRichTextString("Label"));
		c.setCellStyle(headerStyle);

		// Header role names
		for (JobRole role : roles) {
			columnCount++;
			c = r.createCell(columnCount);
			c.setCellValue(new HSSFRichTextString(role.getName()));
			c.setCellStyle(headerStyle);
		}

		for (OperatorCompetency competency : competencies) {
			rowNumber++;
			columnCount = 0;

			r = sheet.createRow(rowNumber);
			c = r.createCell(columnCount);
			c.setCellValue(new HSSFRichTextString(competency.getCategory()));
			columnCount++;
			c = r.createCell(columnCount);
			c.setCellValue(new HSSFRichTextString(competency.getLabel()));

			for (JobRole role : roles) {
				columnCount++;

				if (getJobCompetency(role, competency) != null) {
					c = r.createCell(columnCount);
					c.setCellValue(new HSSFRichTextString("X"));
					c.setCellStyle(normalStyle);
				}
			}
		}

		for (int i = 0; i < (roles.size() + 2); i++) {
			sheet.autoSizeColumn((short) i);
		}

		return wb;
	}
}