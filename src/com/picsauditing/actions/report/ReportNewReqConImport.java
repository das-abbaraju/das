package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.actions.DataConversionRequestAccount;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.search.ContractorAppIndexSearch;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

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
	private FeatureToggle featureToggle;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private RegistrationRequestEmailHelper emailHelper;
	@Autowired
	private UserDAO userDAO;

	private boolean forceUpload = false;
	private File file;
	private String fileContentType = null;
	private String fileFileName = null;
	private String fileName = null;
	private Workbook workbook;
	private ContractorAppIndexSearch contractorAppIndexSearch;

	public String save() throws Exception {
		Database database = new Database();
		contractorAppIndexSearch = new ContractorAppIndexSearch(permissions);

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

	public boolean isForceUpload() {
		return forceUpload;
	}

	public void setForceUpload(boolean forceUpload) {
		this.forceUpload = forceUpload;
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
					if (Strings.isEmpty((String) getValue(row, RegistrationRequestColumn.Name))) {
						continue;
					}

					ContractorRegistrationRequest request = createdRegistrationRequest(row);
					checkRequestForErrors(row.getRowNum(), request);

					int matches = findGap(request);
					request.setMatchCount(matches);

					if (matches == 0 || forceUpload) {
						requests.add(request);
					} else {
						addActionMessage(getTextParameterized("ReportNewReqConImport.DuplicatesFound", matches,
								row.getRowNum() + 1, request.getName()));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while importing contractor registration request.", e);
			addActionError(getText("ReportNewReqConImport.CheckFormat"));
		}

		if (getActionErrors().size() == 0) {
			String notes = "Sent initial contact email.";
			for (ContractorRegistrationRequest crr : requests) {
				crrDAO.save(crr);

				if (crr.getRequestedBy().getId() != OperatorAccount.SALES) {
					crr.contactByEmail();
					prependToRequestNotes(notes, crr);
					emailHelper.sendInitialEmail(crr, getFtpDir());

					crrDAO.save(crr);
				}
			}

			if (permissions.isOperatorCorporate()
					&& featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_REQUESTNEWCONTRACTORACCOUNT)) {
				DataConversionRequestAccount justInTimeConversion = new DataConversionRequestAccount(dao, permissions);
				justInTimeConversion.upgrade();
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

	private void prependToRequestNotes(String note, ContractorRegistrationRequest request) {
		if (request != null && note != null) {
			String stamp = String.format("%s - %s - %s", maskDateFormat(new Date()), permissions.getName(), note);
			request.setNotes(stamp + (request.getNotes() != null ? "\n\n" + request.getNotes() : ""));
		}
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
		request.setOperatorTags(getString(row, RegistrationRequestColumn.Tags));
		request.setReasonForRegistration(getString(row, RegistrationRequestColumn.ReasonForRegistration));
		// Notes
		prependToRequestNotes(getString(row, RegistrationRequestColumn.Notes), request);
		// Other objects
		request.setRequestedBy((OperatorAccount) getValue(row, RegistrationRequestColumn.RequestedBy));
		request.setCountry((Country) getValue(row, RegistrationRequestColumn.Country));
		request.setRequestedByUser((User) getValue(row, RegistrationRequestColumn.RequestedByUser));
		request.setDeadline((Date) getValue(row, RegistrationRequestColumn.Deadline));
		// Country Subdivision
		String subdivision = getString(row, RegistrationRequestColumn.CountrySubdivision);

		if (subdivision != null && !subdivision.contains("-")) {
			if (request.getCountry() != null) {
				subdivision = request.getCountry().getIsoCode() + "-" + subdivision;
			}

			request.setCountrySubdivision(countrySubdivisionDAO.find(subdivision));
		}
		// Defaults
		request.setAuditColumns(permissions);
		request.setStatus(ContractorRegistrationRequestStatus.Active);

		return request;
	}

	private void checkRequestForErrors(int j, ContractorRegistrationRequest crr) {
		if (crr.getRequestedByUser() != null && !Strings.isEmpty(crr.getRequestedByUserOther())) {
			crr.setRequestedByUserOther(null);
		}

		if (Strings.isEmpty(crr.getContact()) || crr.getCountrySubdivision() == null || crr.getCountry() == null
				|| crr.getRequestedBy() == null || Strings.isEmpty(crr.getReasonForRegistration())) {
			addActionError(getTextParameterized("ReportNewReqConImport.MissingRequiredFields", (j + 1)));
		}

		if (Strings.isEmpty(crr.getEmail()) || Strings.isEmpty(crr.getPhone())) {
			addActionError(getTextParameterized("ReportNewReqConImport.ContactInformationRequired", (j + 1)));
		}

		if (Strings.isEmpty(crr.getRequestedByUserOther()) && crr.getRequestedByUser() == null) {
			addActionError(getTextParameterized("ReportNewReqConImport.MissingRequestedByUser", (j + 1)));
		}

		if (crr.getDeadline() == null) {
			crr.setDeadline(DateBean.addMonths(new Date(), 2));
		}

		if (crr.getContact().length() > 30) {
			String oldContact = crr.getContact();
			crr.setContact(crr.getContact().substring(0, 30));
			prependToRequestNotes("Contact name truncated from " + oldContact, crr);
		}

		if (crr.getPhone().length() > 20) {
			String oldPhone = crr.getPhone();
			crr.setPhone(crr.getPhone().substring(0, 20));
			prependToRequestNotes("Phone number truncated from " + oldPhone, crr);
		}
	}

	private int findGap(ContractorRegistrationRequest newContractor) {
		Set<ContractorAppIndexSearch.SearchResult> results = new HashSet<ContractorAppIndexSearch.SearchResult>();

		try {
			if (!Strings.isEmpty(newContractor.getName())) {
				results.addAll(contractorAppIndexSearch.searchOn(newContractor.getName(), ContractorAppIndexSearch.INDEX_TYPE_CONTRACTOR));
			}

			if (!Strings.isEmpty(newContractor.getAddress())) {
				results.addAll(contractorAppIndexSearch.searchOn(newContractor.getAddress(), ContractorAppIndexSearch.INDEX_TYPE_CONTRACTOR));
			}

			if (!Strings.isEmpty(newContractor.getPhone())) {
				results.addAll(contractorAppIndexSearch.searchOn(newContractor.getPhone(), ContractorAppIndexSearch.INDEX_TYPE_USER));
			}

			if (!Strings.isEmpty(newContractor.getEmail())) {
				results.addAll(contractorAppIndexSearch.searchOn(newContractor.getEmail(), ContractorAppIndexSearch.INDEX_TYPE_USER));
			}

			if (!Strings.isEmpty(newContractor.getContact())) {
				results.addAll(contractorAppIndexSearch.searchOn(newContractor.getContact(), ContractorAppIndexSearch.INDEX_TYPE_USER));
			}
		} catch (SQLException sqlException) {
			logger.error("Error searching for matching contractors", sqlException);
		}

		return results.size();
	}

	private Object getValue(Row row, RegistrationRequestColumn column) {
		Cell cell = row.getCell(column.ordinal());
		if (cell != null) {
			try {
				switch (column) {
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
						new Object[]{column.ordinal(), row.getRowNum(), fileFileName, exception});
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

			if (!Strings.isEmpty(value.toString())) {
				return value.toString().trim();
			}
		}

		return null;
	}

	protected enum RegistrationRequestColumn {
		Name, Contact, Phone, Email, TaxID, Address, City, CountrySubdivision, Zip, Country, RequestedBy, Tags, RequestedByUser, RequestedByOther, Deadline, ReasonForRegistration, Notes
	}
}