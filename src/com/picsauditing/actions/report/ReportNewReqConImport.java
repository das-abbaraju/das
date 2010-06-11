package com.picsauditing.actions.report;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportNewReqConImport extends PicsActionSupport {
	private File file;
	protected String fileContentType = null;
	protected String fileFileName = null;
	protected String fileName = null;
	protected StateDAO stateDAO;
	protected CountryDAO countryDAO;
	protected AccountDAO accountDAO;
	protected UserDAO userDAO;
	protected ContractorRegistrationRequestDAO crrDAO;

	public ReportNewReqConImport(StateDAO stateDAO, CountryDAO countryDAO, AccountDAO accountDAO, UserDAO userDAO,
			ContractorRegistrationRequestDAO crrDAO) {
		this.stateDAO = stateDAO;
		this.countryDAO = countryDAO;
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.crrDAO = crrDAO;
	}

	public String execute() throws Exception {
		if (button != null) {
			if (button.startsWith("Save")) {
				String extension = null;
				if (file != null && file.length() > 0) {
					extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
					if (!extension.equalsIgnoreCase("xls") && !extension.equalsIgnoreCase("xlsx")) {
						file = null;
						addActionError("Must be an Excel file");
						return SUCCESS;
					}
					
					importData(file);
				}
			}
		}

		return SUCCESS;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileSize() {
		return FileUtils.size(file);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private void importData(File file) {
		List<ContractorRegistrationRequest> requests = new ArrayList<ContractorRegistrationRequest>();
		Workbook wb = null;
		
		try {
			wb = WorkbookFactory.create(new FileInputStream(file));
			
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				
				for (int j = 0; j < sheet.getLastRowNum(); j++) {
					Row row = sheet.getRow(j);
					
					if (row.getCell(0).getRichStringCellValue().getString().contains("Account"))
						continue;
					
					ContractorRegistrationRequest crr = new ContractorRegistrationRequest();
					crr.setName(isEmpty(row, 0) == true ? null : getValue(row, 0).toString());
					crr.setContact(isEmpty(row, 1) == true ? null : getValue(row, 1).toString());
					crr.setPhone(isEmpty(row, 2) == true ? null : getValue(row, 2).toString());
					crr.setEmail(isEmpty(row, 3) == true ? null : getValue(row, 3).toString());
					crr.setTaxID(isEmpty(row, 4) == true ? null : getValue(row, 4).toString());
					crr.setAddress(isEmpty(row, 5) == true ? null : getValue(row, 5).toString());
					crr.setCity(isEmpty(row, 6) == true ? null : getValue(row, 6).toString());
					crr.setState(isEmpty(row, 7) == true ? null : (State) getValue(row, 7));
					crr.setZip(isEmpty(row, 8) == true ? null : getValue(row, 8).toString());
					crr.setCountry(isEmpty(row, 9) == true ? null : (Country) getValue(row, 9));
					crr.setRequestedBy(isEmpty(row, 10) == true ? null : (OperatorAccount) getValue(row, 10));
					crr.setRequestedByUser(isEmpty(row, 11) == true ? null : (User) getValue(row, 11));
					crr.setRequestedByUserOther(isEmpty(row, 12) == true ? null : getValue(row, 12).toString());
					crr.setDeadline(isEmpty(row, 13) == true ? null : (Date) getValue(row, 13));
					crr.setNotes(isEmpty(row, 14) == true ? null : getValue(row, 14).toString());
					
					if (crr.getRequestedByUser() != null && !Strings.isEmpty(crr.getRequestedByUserOther()))
						crr.setRequestedByUserOther(null);
					
					if (Strings.isEmpty(crr.getName()) || Strings.isEmpty(crr.getContact()) 
							|| crr.getState() == null || crr.getCountry() == null || crr.getRequestedBy() == null)
						addActionError("Missing required fields in row " + j);
					
					if (Strings.isEmpty(crr.getPhone()) && Strings.isEmpty(crr.getEmail()))
						addActionError("Contact information is required. Missing phone and/or email in row " + j);
					
					if (Strings.isEmpty(crr.getRequestedByUserOther()) && crr.getRequestedByUser() == null)
						addActionError("Missing requested by user field in row " + j);
					
					requests.add(crr);
				}
			}
		} catch (Exception e) {
			addActionError("Error reading in excel file, please check the format");
		}
		
		if (getActionErrors().size() == 0) {
			addActionMessage("File successfully imported");
			
			for (ContractorRegistrationRequest crr : requests) {
				crrDAO.save(crr);
			}
		}
	}
	
	private boolean isEmpty(Row row, int cell) {
		if (row.getCell(cell) != null) {
			if (row.getCell(cell).getCellType() == Cell.CELL_TYPE_STRING)
				return Strings.isEmpty(row.getCell(cell).getRichStringCellValue().getString());
			if (row.getCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC)
				return row.getCell(cell).getNumericCellValue() == 0;
		}
		
		return true;
	}
	
	private Object getValue(Row row, int cell) {
		Object value = null;
		if (row.getCell(cell).getCellType() == Cell.CELL_TYPE_STRING)
			value = row.getCell(cell).getRichStringCellValue().getString();
		if (row.getCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC)
			value = row.getCell(cell).getNumericCellValue();
		
		if (cell == 7)
			value = stateDAO.find(value.toString());
		if (cell == 9)
			value = countryDAO.find(value.toString());
		if (cell == 10)
			value = accountDAO.find((int) Double.parseDouble(value.toString()), "Operator");
		if (cell == 11)
			value = userDAO.find((int) Double.parseDouble(value.toString()));
		if (cell == 13)
			value = row.getCell(cell).getDateCellValue();
		
		System.out.println(cell + ": " + value.toString());
		
		return value;
	}
}