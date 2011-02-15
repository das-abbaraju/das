package com.picsauditing.actions.employees;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class ImportEmployees extends PicsActionSupport {
	private EmployeeDAO employeeDAO;

	private int accountID;
	private File upload;
	private String uploadFileName;

	public ImportEmployees(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		if (accountID == 0 && (permissions.isRequiresCompetencyReview() || permissions.isRequiresOQ()))
			accountID = permissions.getAccountId();

		if (button != null && button.startsWith("Save")) {
			if (accountID > 0) {
				if (upload != null && upload.length() > 0) {
					String extension = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
					if (!extension.equalsIgnoreCase("xls") && !extension.equalsIgnoreCase("xlsx")) {
						upload = null;
						addActionError("Must be an Excel file");
						return SUCCESS;
					}

					importData(upload);
				} else if (upload == null || upload.length() == 0)
					addActionError("No file was selected");
			} else
				addActionError("Missing account id");
		}

		return SUCCESS;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
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

	private void importData(File file) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(file));
			List<Employee> toSave = new ArrayList<Employee>();

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				
				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					Row row = rows.next();
					
					if (row.getCell(0) != null) {
						if (row.getCell(0).getRichStringCellValue().getString().contains("Employee"))
							continue;

						Employee e = parseRow(row);
						e.setAccount(new Account());
						e.getAccount().setId(accountID);
						e.setAuditColumns(permissions);
						toSave.add(e);
					}
				}
			}
			
			for (Employee e : toSave) {
				employeeDAO.save(e);
			}
			
			if (toSave.size() > 0)
				addActionMessage("Successfully imported " + toSave.size() + " employee" + (toSave.size() > 1 ? "s" : ""));
			else
				addActionMessage("No employee records were found in the excel file");
		} catch (Exception e) {
			addActionError("Error reading in excel file, please check the format");
		}
	}

	private Employee parseRow(Row row) throws Exception {
		if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null && row.getCell(3) != null) {
			Employee e = new Employee();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

			e.setFirstName(row.getCell(0).toString());
			e.setLastName(row.getCell(1).toString());
			e.setTitle(row.getCell(2).toString());
			e.setHireDate(sdf.parse(row.getCell(3).toString()));

			if (row.getCell(4) != null)
				e.setTwicExpiration(sdf.parse(row.getCell(4).toString()));
			if (row.getCell(5) != null)
				e.setBirthDate(sdf.parse(row.getCell(5).toString()));
			if (row.getCell(6) != null)
				e.setEmail(row.getCell(6).toString());
			if (row.getCell(7) != null)
				e.setPhone(row.getCell(7).toString());
			if (row.getCell(8) != null)
				e.setSsn(row.getCell(8).toString().replaceAll("[^0-9]", ""));
			if (row.getCell(9) != null)
				e.setLocation(row.getCell(9).toString());

			return e;
		}

		return null;
	}
}
