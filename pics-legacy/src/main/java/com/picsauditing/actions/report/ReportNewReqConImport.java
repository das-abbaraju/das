package com.picsauditing.actions.report;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.actions.DataConversionRequestAccount;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.search.ContractorAppIndexSearch;
import com.picsauditing.search.Database;
import com.picsauditing.service.RequestNewContractorService;
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
    @Autowired
    private ContractorAccountDAO contractorAccountDAO;
    @Autowired
    private RequestNewContractorService requestNewContractorService;
    @Autowired
    private OperatorTagDAO operatorTagDAO;

	private boolean forceUpload = false;
	private File file;
	private String fileContentType = null;
	private String fileFileName = null;
	private String fileName = null;
	private Workbook workbook;
	private ContractorAppIndexSearch contractorAppIndexSearch;
    private String notes = null;

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
        requestNewContractorService.setPermissions(permissions);
        int count = 0;

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

                    ContractorAccount contractor = createContractor(row);
                    User primaryContact = createPrimaryContact(row);
                    ContractorOperator requestRelationship = createRelationship(row);
                    List<OperatorTag> operatorTags = findOperatorTags(getString(row, RegistrationRequestColumn.Tags));
                    notes = getString(row, RegistrationRequestColumn.Notes);

                    checkContractorForErrors(row.getRowNum(), contractor);
                    checkPrimaryContactForErrors(row.getRowNum(), primaryContact);
                    checkRelationshipForErrors(row.getRowNum(), requestRelationship);

					if (getActionErrors().size() == 0 || forceUpload) {
                        contractor = requestNewContractorService.saveRequestingContractor(contractor, requestRelationship.getOperatorAccount());
                        primaryContact = requestNewContractorService.savePrimaryContact(contractor, primaryContact);
                        requestRelationship = requestNewContractorService.saveRelationship(contractor, requestRelationship);
                        requestNewContractorService.addTagsToContractor(contractor, operatorTags);

                        if (requestRelationship.getRequestedBy().getId() != OperatorAccount.SALES) {
                            Date now = new Date();

                            contractor.contactByEmail();
                            contractor.setLastContactedByInsideSalesDate(now);
                            contractor.setLastContactedByAutomatedEmailDate(now);
                            if (requestRelationship.getRequestedBy() != null) {
                                contractor.setLastContactedByInsideSales(requestRelationship.getRequestedBy());
                            } else {
                                contractor.setLastContactedByInsideSales(permissions.getUserId());
                            }
                            prependToRequestNotes("Sent initial contact email.");
                            emailHelper.sendInitialEmail(contractor, primaryContact, requestRelationship, getFtpDir());
                        }

                        addNote(contractor, requestRelationship);
                        contractorAccountDAO.save(contractor);
                        count++;
					} else {
						addActionMessage(getTextParameterized("ReportNewReqConImport.DuplicatesFound", 1,
								row.getRowNum() + 1, contractor.getName()));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error while importing contractor registration request.", e);
			addActionError(getText("ReportNewReqConImport.CheckFormat"));
		}

		if (getActionErrors().size() == 0 && count > 0) {
			if (permissions.isOperatorCorporate()
					&& featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_REQUESTNEWCONTRACTORACCOUNT)) {
				DataConversionRequestAccount justInTimeConversion = new DataConversionRequestAccount(dao, permissions);
				justInTimeConversion.upgrade();
			}

			addActionMessage(getTextParameterized("ReportNewReqConImport.SuccessfullyImported", count));
		} else if (getActionErrors().size() == 0) {
            addActionMessage(getTextParameterized("ReportNewReqConImport.SuccessfullyImported", count));
        }
	}

    private List<OperatorTag> findOperatorTags(String tagIdsString) {
        List<OperatorTag> list = new ArrayList<>();
        if (tagIdsString != null) {
            String idsToSesarch = Strings.implodeForDB(tagIdsString.split(" ,"), ",");
            list = operatorTagDAO.findWhere(OperatorTag.class, "id in (" + tagIdsString + ")");
        }
        return list;
    }

    private void addNote(ContractorAccount contractor, ContractorOperator requestRelationship) {
        OperatorAccount clientSiteAccount = requestRelationship.getOperatorAccount();
        int clientSiteId = (clientSiteAccount != null) ? clientSiteAccount.getId() : 1;

        Note note = new Note();
        note.setAccount(contractor);
        note.setAuditColumns(permissions);
        note.setSummary(notes);
        note.setPriority(LowMedHigh.Low);
        note.setNoteCategory(NoteCategory.Registration);
        note.setViewableById(clientSiteId);
        note.setCanContractorView(true);
        note.setStatus(NoteStatus.Closed);
        noteDao.save(note);
    }


    private boolean skipHeaderRow(Row row) {
		Cell cell = row.getCell(0);
		if (cell != null) {
			RichTextString richText = cell.getRichStringCellValue();
			if (richText != null) {
				String cellValue = richText.getString();
				if (!Strings.isEmpty(cellValue) && cellValue.contains("Account Name")) {
					return true;
				}
			}
		}

		return false;
	}

	private void prependToRequestNotes(String message) {
		if (message != null) {
			String stamp = String.format("%s - %s - %s", maskDateFormat(new Date()), permissions.getName(), message);
            notes = (notes == null)? stamp: stamp + "\n\n" + notes;
		}
	}

    private ContractorAccount createContractor(Row row) {
        ContractorAccount contractor = new ContractorAccount();
        contractor.setName(getString(row, RegistrationRequestColumn.Name));
        contractor.setTaxId(getString(row, RegistrationRequestColumn.TaxID));
        contractor.setAddress(getString(row, RegistrationRequestColumn.Address));
        contractor.setCity(getString(row, RegistrationRequestColumn.City));
        contractor.setZip(getString(row, RegistrationRequestColumn.Zip));
        contractor.setCountry((Country) getValue(row, RegistrationRequestColumn.Country));
        contractor.setStatus(AccountStatus.Requested);

        String subdivision = getString(row, RegistrationRequestColumn.CountrySubdivision);
        if (subdivision != null && !subdivision.contains("-")) {
            if (contractor.getCountry() != null) {
                subdivision = contractor.getCountry().getIsoCode() + "-" + subdivision;
            }

            contractor.setCountrySubdivision(countrySubdivisionDAO.find(subdivision));
        }

        return contractor;
    }

    private User createPrimaryContact(Row row) {
        User primaryContact = new User();
        primaryContact.setFirstName(getString(row, RegistrationRequestColumn.ContactFirstName));
        primaryContact.setLastName(getString(row, RegistrationRequestColumn.ContactLastName));
        primaryContact.setPhone(getString(row, RegistrationRequestColumn.Phone));
        primaryContact.setEmail(getString(row, RegistrationRequestColumn.Email));

        return primaryContact;
    }

    private ContractorOperator createRelationship(Row row) {
        ContractorOperator relationship = new ContractorOperator();
        relationship.setRequestedByOther(getString(row, RegistrationRequestColumn.RequestedByOther));
        relationship.setRequestedBy((User) getValue(row, RegistrationRequestColumn.RequestedByUser));
        relationship.setOperatorAccount((OperatorAccount) getValue(row, RegistrationRequestColumn.RequestedBy));
        relationship.setReasonForRegistration(getString(row, RegistrationRequestColumn.ReasonForRegistration));
        relationship.setDeadline((Date) getValue(row, RegistrationRequestColumn.Deadline));

        return relationship;
    }

    public void checkContractorForErrors(int j, ContractorAccount contractor) {
        if (contractor.getCountrySubdivision() == null || contractor.getCountry() == null
                || Strings.isEmpty(contractor.getAddress())) {
            addActionError(getTextParameterized("ReportNewReqConImport.MissingRequiredFields", (j + 1)));
        }
    }

    public void checkPrimaryContactForErrors(int j, User primary) {
        if (Strings.isEmpty(primary.getLastName()) || Strings.isEmpty(primary.getFirstName())) {
            addActionError(getTextParameterized("ReportNewReqConImport.MissingRequiredFields", (j + 1)));
        }

        if (Strings.isEmpty(primary.getEmail()) || Strings.isEmpty(primary.getPhone())) {
            addActionError(getTextParameterized("ReportNewReqConImport.ContactInformationRequired", (j + 1)));
        }

        if (primary.getLastName().length() > 50) {
            prependToRequestNotes("Contact last name truncated from " + primary.getLastName());
            primary.setLastName(primary.getLastName().substring(0, 50));
        }

        if (primary.getFirstName().length() > 50) {
            prependToRequestNotes("Contact first name truncated from " + primary.getFirstName());
            primary.setFirstName(primary.getFirstName().substring(0, 50));
        }

        if (primary.getPhone().length() > 20) {
            String oldPhone = primary.getPhone();
            prependToRequestNotes("Phone number truncated from " + primary.getPhone());
            primary.setPhone(primary.getPhone().substring(0, 20));
        }
    }

    public void checkRelationshipForErrors(int j, ContractorOperator conOp) {
        if (conOp.getOperatorAccount() == null || conOp.getRequestedBy() == null || Strings.isEmpty(conOp.getReasonForRegistration())) {
            addActionError(getTextParameterized("ReportNewReqConImport.MissingRequiredFields", (j + 1)));
        }

        if (Strings.isEmpty(conOp.getRequestedByOther()) && conOp.getRequestedBy() == null) {
            addActionError(getTextParameterized("ReportNewReqConImport.MissingRequestedByUser", (j + 1)));
        }

        if (conOp.getRequestedBy() != null && !Strings.isEmpty(conOp.getRequestedByOther())) {
            conOp.setRequestedByOther(null);
        }
        if (conOp.getDeadline() == null) {
            conOp.setDeadline(DateBean.addMonths(new Date(), 2));
        }
    }

	private int findGap(ContractorAccount contractor, User contact) {
		Set<ContractorAppIndexSearch.SearchResult> results = new HashSet<ContractorAppIndexSearch.SearchResult>();

		try {
			if (!Strings.isEmpty(contractor.getName())) {
				results.addAll(contractorAppIndexSearch.searchOn(contractor.getName(), ContractorAppIndexSearch.INDEX_TYPE_CONTRACTOR));
			}

			if (!Strings.isEmpty(contractor.getAddress())) {
				results.addAll(contractorAppIndexSearch.searchOn(contractor.getAddress(), ContractorAppIndexSearch.INDEX_TYPE_CONTRACTOR));
			}

			if (!Strings.isEmpty(contact.getPhone())) {
				results.addAll(contractorAppIndexSearch.searchOn(contact.getPhone(), ContractorAppIndexSearch.INDEX_TYPE_USER));
			}

			if (!Strings.isEmpty(contact.getEmail())) {
				results.addAll(contractorAppIndexSearch.searchOn(contact.getEmail(), ContractorAppIndexSearch.INDEX_TYPE_USER));
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
		Name, ContactFirstName, ContactLastName, Phone, Email, TaxID, Address, City, CountrySubdivision, Zip, Country, RequestedBy, Tags, RequestedByUser, RequestedByOther, Deadline, ReasonForRegistration, Notes
	}
}