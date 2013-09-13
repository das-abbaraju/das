package com.picsauditing.actions.employees;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.fileimport.EmployeeImport;
import com.picsauditing.service.fileimport.FileImportException;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.util.*;

@SuppressWarnings("serial")
public class ImportEmployees extends PicsActionSupport {
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private EmployeeImport employeeImport;
	@Autowired
	private EmployeeSiteDAO employeeSiteDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;

	// Read Excel Sheet
	private Account account;
	private File upload;
	private String uploadFileName;
	private List<OperatorAccount> operators;
	// Create Excel sheet
	private HSSFSheet sheet;
	private CreationHelper creationHelper;
	// Styles
	private HSSFFont boldedFont;
	private HSSFCellStyle boldedStyle;
	private HSSFFont headerFont;
	private HSSFCellStyle headerStyle;

	private final Logger logger = LoggerFactory.getLogger(ImportEmployees.class);

	public String save() throws Exception {
		if (account != null) {
			if (upload != null && upload.length() > 0) {
				String extension = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
				if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
					importData(upload);
				} else {
					upload = null;
					addActionError(getText("ManageEmployeesUpload.message.MustBeExcel"));
				}
			} else if (upload == null || upload.length() == 0) {
				addActionError(getText("ManageEmployeesUpload.message.NoFileSelected"));
			}
		} else {
			addActionError(getText("ManageEmployeesUpload.message.MissingAccount"));
		}

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
			outstream.close();
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
		List<String> headers = new ArrayList<String>(Arrays.asList(getEmployeeInfo()));

		if (con != null) {
			List<OperatorAccount> operators = new ArrayList<OperatorAccount>(con.getOperatorAccounts());
			// Order alphabetically
			Collections.sort(operators, new Comparator<OperatorAccount>() {
				@Override
				public int compare(OperatorAccount o1, OperatorAccount o2) {
					if (o1.getType().equals(o2.getType()))
						return o1.getName().compareTo(o2.getName());

					return o1.getType().compareTo(o2.getType());
				}
			});

			for (OperatorAccount operator : operators) {
				if (operator.isRequiresCompetencyReview())
					headers.add(getText("ManageEmployeesUpload.label.WorksIn", new Object[]{operator.getName()}));
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

		CellRangeAddressList addressList = new CellRangeAddressList(0, 9999, getEmployeeInfo().length,
				headers.size() - 1);
		DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(new String[]{getText("YesNo.Yes")});
		HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
		dataValidation.setSuppressDropDownArrow(false);
		sheet.addValidationData(dataValidation);
	}

	private void importData(File file) {
		try {
			employeeImport.importEmployees(file, account, permissions);
			addActionMessage(getText("ManageEmployeesUpload.SuccessfullyImported"));
		} catch (FileImportException fileImportException) {
			logger.error(fileImportException.getMessage());
			addActionError(fileImportException.getMessage());
		}
	}

	private boolean parseRow(Row row) throws Exception {
		// Check first name, last name and title only
		if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null) {
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
							e.setEmail(EmailAddressUtils.validate(cell.toString()));
							break;
						case 6:
							e.setPhone(cell.toString());
							break;
						default:
							int index = cell.getColumnIndex() - getEmployeeInfo().length;
							// Get operators?
							if (index >= 0 && operators.size() > index && operators.get(index) != null) {
								employeeDAO.save(e);

								EmployeeSite es = new EmployeeSite();
								es.setEmployee(e);
								es.setAuditColumns(permissions);
								es.setOperator(operators.get(index));
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

	private String[] getEmployeeInfo() {
		return new String[]{getText("ManageEmployeesUpload.label.EmployeeFirstName"),
				getText("ManageEmployeesUpload.label.EmployeeLastName"), getText("ManageEmployeesUpload.label.Title"),
				getText("ManageEmployeesUpload.label.HireDate"), getText("Employee.twicExpiration"),
				getText("Employee.email"), getText("Employee.phone")};
	}
}
