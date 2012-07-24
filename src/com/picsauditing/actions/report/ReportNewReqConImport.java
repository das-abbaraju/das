package com.picsauditing.actions.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportNewReqConImport extends PicsActionSupport {
	@Autowired
	private ContractorRegistrationRequestDAO crrDAO;
	@Autowired
	private StateDAO stateDAO;
	@Autowired
	private CountryDAO countryDAO;
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	protected EmailAttachmentDAO attachmentDAO;
	@Autowired
	protected EmailSenderSpring emailSenderSpring;
	@Autowired
	protected CountrySubdivisionDAO countrySubdivisionDAO;

	private File file;
	private String fileContentType = null;
	private String fileFileName = null;
	private String fileName = null;

	private final Logger logger = LoggerFactory.getLogger(ReportNewReqConImport.class);
	private static final int INITIAL_EMAIL = 83;

	public String save() throws Exception {
		String extension = null;
		if (file != null && file.length() > 0) {
			extension = fileFileName.substring(fileFileName.lastIndexOf(".") + 1);
			if (!extension.equalsIgnoreCase("xls") && !extension.equalsIgnoreCase("xlsx")) {
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

	private void importData(File file) {
		List<ContractorRegistrationRequest> requests = new ArrayList<ContractorRegistrationRequest>();
		Workbook wb = null;

		try {
			wb = WorkbookFactory.create(new FileInputStream(file));

			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				for (Row row : sheet) {
					// skip the header
					if (row.getCell(0).getRichStringCellValue().getString().contains("Account"))
						continue;

					// skip empty row: Assuming that no company name = empty row
					if (getValue(row, 0) == null)
						continue;

					ContractorRegistrationRequest crr = createdRegistrationRequest(row);

					checkRequestForErrors(row.getRowNum(), crr);

					requests.add(crr);
				}
			}
		} catch (Exception e) {
			addActionError(getText("ReportNewReqConImport.CheckFormat"));
		}

		if (getActionErrors().size() == 0) {
			String notes = "Sent initial contact email.";
			for (ContractorRegistrationRequest crr : requests) {
				int matches = findGap(crr);
				crr.setMatchCount(matches);
				crr.contactByEmail();
				crrDAO.save(crr);

				if (crr.getRequestedBy().getId() != 23325){
					prependToRequestNotes(notes, crr);
					sendEmail(crr);
				}
			}

			addActionMessage(getTextParameterized("ReportNewReqConImport.SuccessfullyImported", requests.size()));
		}
	}

	private void prependToRequestNotes(String note, ContractorRegistrationRequest newContractor) {
		if (newContractor != null && note != null)
			newContractor.setNotes(maskDateFormat(new Date()) + " - " + permissions.getName() + " - " + note
					+ (newContractor.getNotes() != null ? "\n\n" + newContractor.getNotes() : ""));
	}

	public OperatorForm getForm(ContractorRegistrationRequest newContractor) {
		if (newContractor != null && newContractor.getRequestedBy() != null) {
			List<OperatorAccount> hierarchy = new ArrayList<OperatorAccount>();
			hierarchy.add(newContractor.getRequestedBy());

			List<Facility> corpFac = new ArrayList<Facility>(newContractor.getRequestedBy().getCorporateFacilities());
			Collections.reverse(corpFac);

			for (Facility f : corpFac) {
				if (!f.getCorporate().equals(newContractor.getRequestedBy().getTopAccount())
						&& !Account.PICS_CORPORATE.contains(f.getCorporate().getId()))
					hierarchy.add(f.getCorporate());
			}

			if (!newContractor.getRequestedBy().getTopAccount().equals(newContractor.getRequestedBy())
					&& !Account.PICS_CORPORATE.contains(newContractor.getRequestedBy().getTopAccount().getId()))
				hierarchy.add(newContractor.getRequestedBy().getTopAccount());

			for (OperatorAccount o : hierarchy) {
				for (OperatorForm form : o.getOperatorForms()) {
					if (form.getFormName().contains("*"))
						return form;
				}
			}
		}

		return null;
	}

	private void sendEmail(ContractorRegistrationRequest newContractor) {
		if (newContractor.getRequestedBy().getId() != OperatorAccount.SALES) {
			EmailBuilder emailBuilder = prepareEmailBuilder(newContractor);
			try {
				EmailQueue q = emailBuilder.build();
				emailSenderSpring.send(q);
				OperatorForm form = getForm(newContractor);
				if (form != null)
					addAttachments(q, form);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private EmailBuilder prepareEmailBuilder(ContractorRegistrationRequest newContractor) {
		EmailBuilder email = new EmailBuilder();
		email.setToAddresses(newContractor.getEmail());

		email.setFromAddress("info@picsauditing.com");
		email.setTemplate(INITIAL_EMAIL);
		email.addToken("newContractor", newContractor);
		return email;
	}

	private void addAttachments(EmailQueue emailQueue, OperatorForm form) {
		String filename = FileUtils.thousandize(form.getId()) + form.getFile();

		try {
			EmailAttachment attachment = new EmailAttachment();

			File file = new File(getFtpDir() + "/files/" + filename);

			byte[] bytes = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(bytes);

			attachment.setFileName(getFtpDir() + "/files/" + filename);
			attachment.setContent(bytes);
			attachment.setFileSize((int) file.length());
			attachment.setEmailQueue(emailQueue);
			attachmentDAO.save(attachment);
		} catch (Exception e) {
			LOG.error("Unable to open file: /files/{}", filename);
		}
	}

	private ContractorRegistrationRequest createdRegistrationRequest(Row row) {
		ContractorRegistrationRequest crr = new ContractorRegistrationRequest();

		String importedName = (String) getValue(row, 0);
		String importedContact = (String) getValue(row, 1);
		Object phoneValue = getValue(row, 2);
		String importedEmail = (String) getValue(row, 3);
		Object taxIDValue = getValue(row, 4);
		String importedAddress = (String) getValue(row, 5);
		String importedCity = (String) getValue(row, 6);
		State importedState = (State) getValue(row, 7);
		Object zipValue = getValue(row, 8);
		Country importedCountry = (Country) getValue(row, 9);
		OperatorAccount importedRequestedBy = (OperatorAccount) getValue(row, 10);
		Object tagValue = getValue(row, 11);
		User importedRequestedByUser = (User) getValue(row, 12);
		String importedRequestedByUserOther = (String) getValue(row, 13);
		Date importedDeadline = (Date) getValue(row, 14);
		String importedNotes = (String) getValue(row, 15);

		crr.setName(importedName);
		crr.setContact(importedContact);
		if (phoneValue != null) {
			if (phoneValue instanceof Double) {
				BigDecimal phoneValueDec = new BigDecimal((Double) phoneValue);
				crr.setPhone(phoneValueDec.toString());
			} else {
				crr.setPhone(phoneValue.toString());
			}
		}

		crr.setEmail(importedEmail.trim());
		if (taxIDValue != null) {
			if (taxIDValue instanceof Double) {
				BigDecimal taxIDValueDec = new BigDecimal((Double) taxIDValue);
				crr.setTaxID(taxIDValueDec.toString());
			} else {
				crr.setTaxID(taxIDValue.toString());
			}
		}

		crr.setAddress(importedAddress);
		crr.setCity(importedCity);
		crr.setState(importedState);
		String stateIso = crr.getState().getIsoCode();
		String countryIso = crr.getCountry().getIsoCode();
		if (countrySubdivisionDAO.exist(countryIso+"-"+stateIso)){
			CountrySubdivision countrySubdivision = new CountrySubdivision();
			// TODO: Remove in Clean up Phase
			stateIso = StringUtils.remove(stateIso, "GB_");
			countrySubdivision.setIsoCode(countryIso+"-"+stateIso);
			crr.setCountrySubdivision(countrySubdivision);
		}
		if (zipValue != null) {
			if (zipValue instanceof Double) {
				BigDecimal zipValueDec = new BigDecimal((Double) zipValue);
				crr.setZip(zipValueDec.toString());
			} else {
				crr.setZip(zipValue.toString());
			}
		}

		crr.setCountry(importedCountry);
		crr.setRequestedBy(importedRequestedBy);
		if (tagValue != null) {
			if (tagValue instanceof Double) {
				BigDecimal tagValueDec = new BigDecimal((Double) tagValue);
				crr.setOperatorTags(tagValueDec.toString());
			} else {
				crr.setOperatorTags(tagValue.toString());
			}
		}

		crr.setRequestedByUser(importedRequestedByUser);
		crr.setRequestedByUserOther(importedRequestedByUserOther);
		crr.setDeadline(importedDeadline);
		crr.setNotes(importedNotes);

		crr.setAuditColumns(permissions);
		crr.setStatus(ContractorRegistrationRequestStatus.Active);
		return crr;
	}

	private void checkRequestForErrors(int j, ContractorRegistrationRequest crr) {
		if (crr.getRequestedByUser() != null && !Strings.isEmpty(crr.getRequestedByUserOther()))
			crr.setRequestedByUserOther(null);

		if (Strings.isEmpty(crr.getContact()) || crr.getState() == null || crr.getCountry() == null
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
			if (cell == 12 && !Strings.isEmpty(value.toString()))
				value = userDAO.find((int) Double.parseDouble(value.toString()));
			if (cell == 14 && !Strings.isEmpty(value.toString()))
				value = row.getCell(cell).getDateCellValue();

			if (isDebugging())
				logger.debug("{}:{}", cell, value);

			if (value != null && value.toString() == "")
				value = null;
		}

		return value;
	}
}