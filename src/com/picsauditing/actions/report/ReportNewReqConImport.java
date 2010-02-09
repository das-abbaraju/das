package com.picsauditing.actions.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.FileUtils;

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
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.startsWith("Save")) {
				String extension = null;
				if (file != null && file.length() > 0) {
					extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
					if (!extension.equals("xls")) {
						file = null;
						addActionError("Bad File Extension");
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

	private void importData(File file) throws ParseException {
		// Example from
		// http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/hssf/usermodel/examples/HSSFReadWrite.java
		HSSFWorkbook wb = null;

		try {
			wb = new HSSFWorkbook(new FileInputStream(file));
			// cell.toString on date fields returns something like 01-Jan-2010
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

			for (int k = 0; k < wb.getNumberOfSheets(); k++) {
				HSSFSheet sheet = wb.getSheetAt(k);
				int rows = sheet.getPhysicalNumberOfRows();

				for (int r = 0; r < rows; r++) {
					HSSFRow row = sheet.getRow(r);
					// If the row is blank or the header
					if (row == null || (row.getCell(0) != null && row.getCell(0).getStringCellValue().equals("Account Name")))
						continue;

					ContractorRegistrationRequest crr = new ContractorRegistrationRequest();
					for (int c = 0; c < 15; c++) {
						HSSFCell cell = row.getCell(c);

						if (cell == null)
							continue;

						String value = null;

						// Date fields that would otherwise be mistaken for math
						if (c == 2 || c == 13)
							value = cell.toString();
						else {
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_NUMERIC:
								value = "" + (int) cell.getNumericCellValue();
								break;
							default:
								value = cell.getStringCellValue();
							}
						}

						if (value.equals(""))
							continue;
						// Debugging
						System.out.println("[" + c + "]: " + value);
						
						// Update the relevant field?
						switch (c) {
						case 0:
							crr.setName(value);
							break;
						case 1:
							crr.setContact(value);
							break;
						case 2:
							crr.setPhone(value);
							break;
						case 3:
							crr.setEmail(value);
							break;
						case 4:
							crr.setTaxID(value);
							break;
						case 5:
							crr.setAddress(value);
							break;
						case 6:
							crr.setCity(value);
							break;
						case 7:
							crr.setState(stateDAO.find(value));
							break;
						case 8:
							crr.setZip(value);
							break;
						case 9:
							crr.setCountry(countryDAO.find(value));
							break;
						case 10:
							crr.setRequestedBy((OperatorAccount) accountDAO.find(Integer.parseInt(value)));
							break;
						case 11:
							crr.setRequestedByUser(userDAO.find(Integer.parseInt(value)));
							break;
						case 12:
							crr.setRequestedByUserOther(value);
							break;
						case 13:
							crr.setDeadline((Date) sdf.parse(value));
							break;
						case 14:
							crr.setNotes(value);
						}
					}

					Date now = new Date();
					crr.setCreatedBy(getUser());
					crr.setUpdatedBy(getUser());
					crr.setCreationDate(now);
					crr.setUpdateDate(now);

					if (crr.getRequestedBy() == null) {
						crr = null;
						addActionError("No Requested by Operator entered in row " + (r + 1));
						return;
					}
					
					if (crr.getRequestedByUser() == null && crr.getRequestedByUserOther() == null) {
						crr = null;
						addActionError("No Requested by User entered in row " + (r + 1));
						return;
					}
					// Remove the typed in user name if a user in PICS is selected
					else if (crr.getRequestedByUser() != null && crr.getRequestedByUserOther() != null)
						crr.setRequestedByUserOther(null);
					
					// check other required fields
					if (crr.getName() == null || crr.getContact() == null || crr.getPhone() == null
							|| crr.getState() == null || crr.getCountry() == null || crr.getDeadline() == null) {
						crr = null;
						addActionError("Please enter required information in row " + (r + 1));
						return;
					}

					crrDAO.save(crr);
					crrDAO.clear();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}