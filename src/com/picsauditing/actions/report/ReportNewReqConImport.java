package com.picsauditing.actions.report;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.picsauditing.PICS.DateBean;
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
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
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
				} else if (file == null || file.length() == 0) {
					addActionError("No file was selected");
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
					crr.setName((String) getValue(row, 0));
					crr.setContact((String) getValue(row, 1));
					crr.setPhone((String) getValue(row, 2));
					crr.setEmail((String) getValue(row, 3));
					crr.setTaxID((String) getValue(row, 4));
					crr.setAddress((String) getValue(row, 5));
					crr.setCity((String) getValue(row, 6));
					crr.setState((State) getValue(row, 7));
					crr.setZip((String) getValue(row, 8));
					crr.setCountry((Country) getValue(row, 9));
					crr.setRequestedBy((OperatorAccount) getValue(row, 10));
					crr.setRequestedByUser((User) getValue(row, 11));
					crr.setRequestedByUserOther((String) getValue(row, 12));
					crr.setDeadline((Date) getValue(row, 13));
					crr.setNotes((String) getValue(row, 14));

					// Assuming that no company name = empty row
					if (crr.getName() == null)
						continue;

					if (crr.getRequestedByUser() != null && !Strings.isEmpty(crr.getRequestedByUserOther()))
						crr.setRequestedByUserOther(null);

					if (Strings.isEmpty(crr.getContact()) || crr.getState() == null || crr.getCountry() == null
							|| crr.getRequestedBy() == null)
						addActionError("Missing required fields in row " + j);

					if (Strings.isEmpty(crr.getEmail()))
						addActionError("Contact information is required. Missing email in row " + j);

					if (Strings.isEmpty(crr.getRequestedByUserOther()) && crr.getRequestedByUser() == null)
						addActionError("Missing requested by user field in row " + j);

					if (crr.getDeadline() == null)
						crr.setDeadline(DateBean.addMonths(new Date(), 2));

					if (!Strings.isEmpty(crr.getNotes()))
						crr.setNotes(maskDateFormat(new Date()) + " - " + permissions.getName() + " - "
								+ crr.getNotes());

					requests.add(crr);
				}
			}
		} catch (Exception e) {
			addActionError("Error reading in excel file, please check the format");
		}

		if (getActionErrors().size() == 0) {
			for (ContractorRegistrationRequest crr : requests) {
				int matches = findGap(crr);
				crr.setMatchCount(matches);
				crrDAO.save(crr);
			}

			addActionMessage("Successfully imported <b>" + requests.size() + "</b> registration request"
					+ (requests.size() == 1 ? "" : "s"));
		}
	}

	private int findGap(ContractorRegistrationRequest newContractor) {
		List<String> searchTerms = new ArrayList<String>();

		if (!Strings.isEmpty(newContractor.getName()))
			searchTerms.add(newContractor.getName());
		if (!Strings.isEmpty(newContractor.getAddress()))
			searchTerms.add(newContractor.getAddress());
		if (!Strings.isEmpty(newContractor.getPhone()))
			searchTerms.add(newContractor.getPhone());
		if (!Strings.isEmpty(newContractor.getEmail()))
			searchTerms.add(newContractor.getEmail());
		if (!Strings.isEmpty(newContractor.getContact()))
			searchTerms.add(newContractor.getContact());

		String term = Strings.implode(searchTerms, " ");

		List<String> unusedTerms = new ArrayList<String>();

		SearchEngine searchEngine = new SearchEngine(permissions);

		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();
		Database db = new Database();
		List<String> termsArray = searchEngine.sortSearchTerms(searchEngine.buildTerm(term, false, false), true);

		while (results.isEmpty() && termsArray.size() > 0) {
			String query = searchEngine.buildQuery(null, termsArray, "i1.indexType = 'C'", null, 20, false, true);
			try {
				results = db.select(query, false);
			} catch (SQLException e) {
				System.out.println("Error running query in RequestNewCon");
				e.printStackTrace();
				return 0;
			}

			if (!searchEngine.getNullTerms().isEmpty() && unusedTerms.isEmpty()) {
				unusedTerms.addAll(searchEngine.getNullTerms());
				termsArray.removeAll(searchEngine.getNullTerms());
			}

			termsArray = termsArray.subList(0, termsArray.size() - 1);
		}

		return results.size();
	}

	private Object getValue(Row row, int cell) {
		Object value = null;

		if (row.getCell(cell) != null) {
			if (row.getCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC)
				value = row.getCell(cell).getNumericCellValue();
			else
				value = row.getCell(cell).getRichStringCellValue().getString();

			if (cell == 7 && !Strings.isEmpty(value.toString()))
				value = stateDAO.find(value.toString());
			if (cell == 9 && !Strings.isEmpty(value.toString()))
				value = countryDAO.find(value.toString());
			if (cell == 10 && !Strings.isEmpty(value.toString()))
				value = accountDAO.find((int) Double.parseDouble(value.toString()), "Operator");
			if (cell == 11 && !Strings.isEmpty(value.toString()))
				value = userDAO.find((int) Double.parseDouble(value.toString()));
			if (cell == 13 && !Strings.isEmpty(value.toString()))
				value = row.getCell(cell).getDateCellValue();

			if (isDebugging() && value != null && !value.toString().equals(""))
				System.out.println(cell + ": " + value.toString());

			if (value != null && value.toString() == "")
				value = null;
		}

		return value;
	}
}