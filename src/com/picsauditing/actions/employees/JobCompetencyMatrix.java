package com.picsauditing.actions.employees;

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

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class JobCompetencyMatrix extends AccountActionSupport {
	protected JobRoleDAO jobRoleDAO;
	protected AccountDAO accountDAO;
	private OperatorCompetencyDAO competencyDAO;

	private List<JobRole> roles;
	private List<OperatorCompetency> competencies;
	private DoubleMap<JobRole, OperatorCompetency, JobCompetency> map;

	public JobCompetencyMatrix(AccountDAO accountDAO, JobRoleDAO jobRoleDAO, OperatorCompetencyDAO competencyDAO) {
		this.accountDAO = accountDAO;
		this.jobRoleDAO = jobRoleDAO;
		this.competencyDAO = competencyDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		getPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		if (id == 0)
			throw new Exception("Missing id");

		account = accountDAO.find(id);
		roles = jobRoleDAO.findMostUsed(id, true);
		competencies = competencyDAO.findMostUsed(id, true);
		map = jobRoleDAO.findJobCompetencies(id, true);

		this.subHeading = account.getName();

		if (button != null && "Download".equals(button)) {
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

		return SUCCESS;
	}

	public List<JobRole> getRoles() {
		return roles;
	}
	
	public List<JobRole> getRoles(OperatorCompetency operatorCompetency) {
		// need to check if forward entries are all null to not include in list
		boolean usedRole = false;
		for(JobRole role : roles)
			if(map.get(role, operatorCompetency) != null) {
				usedRole = true;
				break;
			}

		return (usedRole) ? roles : null;
	}

	public List<OperatorCompetency> getCompetencies() {
		return competencies;
	}

	public JobCompetency getJobCompetency(JobRole role, OperatorCompetency comp) {
		return map.get(role, comp);
	}
	
	private HSSFWorkbook buildWorkbook(String name) {
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
		
		// Add the Column Headers to the top of the report
		int rowNumber = 0;
		int columnCount = 0;
		HSSFRow r = sheet.createRow(rowNumber);
		
		// HSE Competency Category & Label
		HSSFCell c = r.createCell(columnCount);
		c.setCellValue(new HSSFRichTextString("HSE Competency Category"));
		c.setCellStyle(headerStyle);
		columnCount++;
		
		c = r.createCell(columnCount);
		c.setCellValue(new HSSFRichTextString("HSE Competency Label"));
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