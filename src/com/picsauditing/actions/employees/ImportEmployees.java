package com.picsauditing.actions.employees;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ImportEmployees extends PicsActionSupport {
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private EmployeeSiteDAO employeeSiteDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	// Read Excel Sheet
	private Account account;
	private File upload;
	private String uploadFileName;
	private OperatorAccount[] operators;
	// Create Excel sheet
	private HSSFSheet sheet;
	private CreationHelper creationHelper;
	private String[] employeeInfo = new String[] { "Employee First Name *", "Employee Last Name *", "Title *",
			"Hire Date *", "TWIC Expiration", "Birthdate", "Email", "Phone", "SSN", "Location" };
	// Styles
	private HSSFFont boldedFont;
	private HSSFCellStyle boldedStyle;
	private HSSFFont headerFont;
	private HSSFCellStyle headerStyle;

	public String save() throws Exception {
		if (account != null) {
			if (upload != null && upload.length() > 0) {
				String extension = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
				if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
					importData(upload);
				} else {
					upload = null;
					addActionError("Must be an Excel file");
				}
			} else if (upload == null || upload.length() == 0)
				addActionError("No file was selected");
		} else
			addActionError("Missing account");

		return SUCCESS;
	}

	public String download() throws Exception {
		if (account == null)
			addActionError("Missing account");
		else {
			HSSFWorkbook wb = new HSSFWorkbook();
			setup(wb);
			addHeader();

			String filename = "ImportEmployees.xls";
			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
		}

		return SUCCESS;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	private void setup(HSSFWorkbook wb) {
		sheet = wb.createSheet("Import Employees");
		creationHelper = wb.getCreationHelper();

		// Bold
		boldedFont = wb.createFont();
		boldedFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		boldedFont.setFontHeightInPoints((short) 12);

		boldedStyle = wb.createCellStyle();
		boldedStyle.setFont(boldedFont);
		boldedStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// Normal
		headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 12);

		headerStyle = wb.createCellStyle();
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	}

	private void addHeader() {
		ContractorAccount con = null;
		if (account.isContractor())
			con = (ContractorAccount) account;

		HSSFRow row = sheet.createRow(0);
		List<String> headers = new ArrayList<String>(Arrays.asList(employeeInfo));

		if (con != null) {
			for (OperatorAccount operator : con.getOperatorAccounts()) {
				if (operator.isRequiresCompetencyReview())
					headers.add(String.format("Works in: %s", operator.getName()));
			}
		}

		HSSFCell cell = null;
		int count = 0;
		for (String header : headers) {
			cell = row.createCell(count);
			cell.setCellValue(creationHelper.createRichTextString(header));

			if (header.contains("*"))
				cell.setCellStyle(boldedStyle);
			else
				cell.setCellStyle(headerStyle);

			sheet.autoSizeColumn(count);
			if (sheet.getColumnWidth(count) < (256 * 15))
				sheet.setColumnWidth(count, 256 * 15);

			count++;
		}

		CellRangeAddressList addressList = new CellRangeAddressList(0, 9999, employeeInfo.length, headers.size() - 1);
		DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(new String[] { "Yes" });
		HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
		dataValidation.setSuppressDropDownArrow(false);
		sheet.addValidationData(dataValidation);
	}

	private void importData(File file) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(file));
			int importedEmployees = 0;

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);

				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					Row row = rows.next();

					if (row.getCell(0) != null) {
						if (row.getCell(0).getRichStringCellValue().getString().contains("Employee")) {
							operators = new OperatorAccount[row.getLastCellNum() - employeeInfo.length];
							for (int j = employeeInfo.length; j < row.getLastCellNum(); j++) {
								if (row.getCell(j) != null && !Strings.isEmpty(row.getCell(j).toString())) {
									List<OperatorAccount> operatorResults = operatorAccountDAO.findWhere(false,
											String.format("a.name = '%s'", row.getCell(j).toString().substring(10)));
									operators[j - employeeInfo.length] = operatorResults.get(0);
								}
							}

							continue;
						}

						if (parseRow(row))
							importedEmployees++;
					}
				}
			}

			if (importedEmployees > 0)
				addActionMessage("Successfully imported " + importedEmployees + " employee"
						+ (importedEmployees > 1 ? "s" : ""));
			else
				addActionMessage("No employee records were found in the excel file");
		} catch (Exception e) {
			addActionError("Error reading in excel file, please check the format");
		}
	}

	private boolean parseRow(Row row) throws Exception {
		if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null && row.getCell(3) != null) {
			Employee e = new Employee();
			e.setAccount(account);
			e.setAuditColumns(permissions);

			Iterator<Cell> iterator = row.cellIterator();
			while (iterator.hasNext()) {
				Cell cell = iterator.next();
				if (cell != null && !Strings.isEmpty(cell.toString())) {
					switch (cell.getColumnIndex()) {
					case 0:
						e.setFirstName(cell.toString());
						break;
					case 1:
						e.setLastName(cell.toString());
						break;
					case 2:
						e.setTitle(cell.toString());
						break;
					case 3:
						e.setHireDate(cell.getDateCellValue());
						break;
					case 4:
						e.setTwicExpiration(cell.getDateCellValue());
						break;
					case 5:
						e.setBirthDate(cell.getDateCellValue());
						break;
					case 6:
						e.setEmail(cell.toString());
						break;
					case 7:
						e.setPhone(cell.toString());
						break;
					case 8:
						e.setSsn(cell.toString().replaceAll("[^0-9]", ""));
						break;
					case 9:
						e.setLocation(cell.toString());
						break;
					default:
						// Get operators?
						if (cell.getColumnIndex() - employeeInfo.length >= 0
								&& operators[cell.getColumnIndex() - employeeInfo.length] != null) {
							employeeDAO.save(e);

							EmployeeSite es = new EmployeeSite();
							es.setEmployee(e);
							es.setAuditColumns(permissions);
							es.setOperator(operators[cell.getColumnIndex() - employeeInfo.length]);
							es.setEffectiveDate(new Date());
							employeeSiteDAO.save(es);
						}
					}
				}
			}

			employeeDAO.save(e);
			return true;
		}

		return false;
	}
}
