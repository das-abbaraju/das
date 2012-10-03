package com.picsauditing.actions.report;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportNewReqConImport extends PicsActionSupport {
	private final Logger logger = LoggerFactory.getLogger(ReportNewReqConImport.class);

	@Autowired
	private ContractorRegistrationRequestDAO crrDAO;
	@Autowired
	private CountryDAO countryDAO;
	@Autowired
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private RegistrationRequestEmailHelper emailHelper;
	@Autowired
	private UserDAO userDAO;

	private File file;
	private String fileContentType = null;
	private String fileFileName = null;
	private String fileName = null;

	private Workbook workbook;

	public String save() throws Exception {
		String extension = null;
		if (file != null && file.length() > 0) {
			extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);

			if (!extension.toLowerCase().startsWith("xls")) {
				file = null;
				addActionError(getText("ReportNewReqConImport.MustBeExcel"));
				return SUCCESS;
			}

			importData(file);
		} else if (file == null || file.length() == 0) {
			addActionError(getText("ReportNewReqConImport.NoFileWasSelected"));
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

	private void importData(File file) throws Exception {
		List<ContractorRegistrationRequest> requests = new ArrayList<ContractorRegistrationRequest>();

		try {
			if (workbook == null) {
				workbook = WorkbookFactory.create(new FileInputStream(file));
			}

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					if (skipHeaderRow(row)) {
						continue;
					}

					// skip empty row: Assuming that no company name = empty row
					if (getValue(row, RegistrationRequestColumn.Name) == null) {
						continue;
					}

					ContractorRegistrationRequest request = createdRegistrationRequest(row);
					checkRequestForErrors(row.getRowNum(), request);

					requests.add(request);
				}
			}
		} catch (Exception e) {
			logger.error("Error while importing contractor registration request.", e);
			addActionError(getText("ReportNewReqConImport.CheckFormat"));
		}

		if (getActionErrors().size() == 0) {
			String notes = "Sent initial contact email.";
			for (ContractorRegistrationRequest crr : requests) {
				int matches = findGap(crr);
				crr.setMatchCount(matches);
				crr.contactByEmail();
				crrDAO.save(crr);

				if (crr.getRequestedBy().getId() != 23325) {
					prependToRequestNotes(notes, crr);
					emailHelper.sendInitialEmail(crr, getFtpDir());
				}
			}

			addActionMessage(getTextParameterized("ReportNewReqConImport.SuccessfullyImported", requests.size()));
		}
	}

	private boolean skipHeaderRow(Row row) {
		Cell cell = row.getCell(0);
		if (cell != null) {
			RichTextString richText = cell.getRichStringCellValue();
			if (richText != null) {
				String cellValue = richText.getString();
				if (!Strings.isEmpty(cellValue) && cellValue.contains("Account")) {
					return true;
				}
			}
		}

		return false;
	}

	private void prependToRequestNotes(String note, ContractorRegistrationRequest newContractor) {
		if (newContractor != null && note != null)
			newContractor.setNotes(maskDateFormat(new Date()) + " - " + permissions.getName() + " - " + note
					+ (newContractor.getNotes() != null ? "\n\n" + newContractor.getNotes() : ""));
	}

	private ContractorRegistrationRequest createdRegistrationRequest(Row row) {
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();
		// Strings
		request.setName(getString(row, RegistrationRequestColumn.Name));
		request.setContact(getString(row, RegistrationRequestColumn.Contact));
		request.setPhone(getString(row, RegistrationRequestColumn.Phone));
		request.setEmail(getString(row, RegistrationRequestColumn.Email));
		request.setTaxID(getString(row, RegistrationRequestColumn.TaxID));
		request.setAddress(getString(row, RegistrationRequestColumn.Address));
		request.setCity(getString(row, RegistrationRequestColumn.City));
		request.setZip(getString(row, RegistrationRequestColumn.Zip));
		request.setRequestedByUserOther(getString(row, RegistrationRequestColumn.RequestedByOther));
		request.setNotes(getString(row, RegistrationRequestColumn.Notes));
		request.setOperatorTags(getString(row, RegistrationRequestColumn.Tags));
		// Other objects
		request.setRequestedBy((OperatorAccount) getValue(row, RegistrationRequestColumn.RequestedBy));
		request.setCountrySubdivision((CountrySubdivision) getValue(row, RegistrationRequestColumn.CountrySubdivision));
		request.setCountry((Country) getValue(row, RegistrationRequestColumn.Country));
		request.setRequestedByUser((User) getValue(row, RegistrationRequestColumn.RequestedByUser));
		request.setDeadline((Date) getValue(row, RegistrationRequestColumn.Deadline));
		// Defaults
		request.setAuditColumns(permissions);
		request.setStatus(ContractorRegistrationRequestStatus.Active);

		return request;
	}

	private void checkRequestForErrors(int j, ContractorRegistrationRequest crr) {
		if (crr.getRequestedByUser() != null && !Strings.isEmpty(crr.getRequestedByUserOther()))
			crr.setRequestedByUserOther(null);

		if (Strings.isEmpty(crr.getContact()) || crr.getCountrySubdivision() == null || crr.getCountry() == null
				|| crr.getRequestedBy() == null)
			addActionError(getTextParameterized("ReportNewReqConImport.MissingRequiredFields", (j + 1)));

		if (Strings.isEmpty(crr.getEmail()))
			addActionError(getTextParameterized("ReportNewReqConImport.ContactInformationRequired", (j + 1)));

		if (Strings.isEmpty(crr.getRequestedByUserOther()) && crr.getRequestedByUser() == null)
			addActionError(getTextParameterized("ReportNewReqConImport.MissingRequestedByUser", (j + 1)));

		if (crr.getDeadline() == null)
			crr.setDeadline(DateBean.addMonths(new Date(), 2));

		if (!Strings.isEmpty(crr.getNotes()))
			crr.setNotes(maskDateFormat(new Date()) + " - " + permissions.getName() + " - " + crr.getNotes());

		if (crr.getContact().length() > 30)
			crr.setContact(crr.getContact().substring(0, 30));

		if (crr.getPhone().length() > 20)
			crr.setPhone(crr.getPhone().substring(0, 20));
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
				logger.error("Error running query in RequestNewCon");
				logger.error("{}", e.getCause());
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

	private Object getValue(Row row, RegistrationRequestColumn column) {
		Cell cell = row.getCell(column.ordinal());
		if (cell != null) {
			try {
				switch (column) {
				case CountrySubdivision:
					return countrySubdivisionDAO.find(cell.getRichStringCellValue().getString());
				case Country:
					return countryDAO.find(cell.getRichStringCellValue().getString());
				case RequestedBy:
					return operatorAccountDAO.find((int) cell.getNumericCellValue());
				case RequestedByUser:
					return userDAO.find((int) cell.getNumericCellValue());
				case Deadline:
					return cell.getDateCellValue();
				default:
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						return cell.getNumericCellValue();
					} else {
						return cell.getRichStringCellValue().getString();
					}
				}
			} catch (Exception exception) {
				logger.error("Exception trying to parse cell {} in row {} from file {}\n{}",
						new Object[] { column.ordinal(), row.getRowNum(), fileFileName, exception });
				addActionError(String.format("Could not parse cell %d in row %d as %s", column, row.getRowNum(),
						column.toString()));
			}
		}

		return null;
	}

	private String getString(Row row, RegistrationRequestColumn column) {
		Object value = getValue(row, column);

		if (value != null) {
			if (value instanceof Double) {
				BigDecimal valueDecimal = new BigDecimal((Double) value);
				return valueDecimal.toString();
			}

			return value.toString();
		}

		return null;
	}

	protected enum RegistrationRequestColumn {
		Name, Contact, Phone, Email, TaxID, Address, City, CountrySubdivision, Zip, Country, RequestedBy, Tags, RequestedByUser, RequestedByOther, Deadline, Notes
	}
}