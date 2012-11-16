package com.picsauditing.actions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

/**
 * ETL ContractorRegistrationRequest to Account, User, and ContractorOperator
 * entities.
 * 
 * @author UAung
 * 
 */
@Deprecated
@SuppressWarnings("serial")
public class DataConversionRequestAccount extends PicsActionSupport {
	private static Logger logger = LoggerFactory.getLogger(DataConversionRequestAccount.class);

	private int limit = 10;
	private List<ContractorRegistrationRequest> requestsNeedingConversion;
	private OperatorAccount restrictToOperator = new OperatorAccount();

	public DataConversionRequestAccount() {
	}

	public DataConversionRequestAccount(BasicDAO dao, Permissions permissions) {
		this.dao = dao;
		this.permissions = permissions;

		if (permissions.isOperatorCorporate()) {
			restrictToOperator.setId(permissions.getAccountId());
		}

		limit = 0;
	}

	@Override
	public String execute() {
		loadPermissions();

		if (needsUpgrade()) {
			String operatorRestriction = Strings.EMPTY_STRING;
			if (restrictToOperator.getId() > 0) {
				operatorRestriction = String.format(" for operator ID %d", restrictToOperator.getId());
			}

			addActionMessage(String.format("There are %d requests ready for conversion%s",
					findRequestsNeedingConversion().size(), operatorRestriction));
		}

		return SUCCESS;
	}

	public String upgrade() {
		try {
			performUpgrade();
			addActionMessage(String
					.format("Successfully converted %d requests", findRequestsNeedingConversion().size()));
		} catch (Exception e) {
			logger.error("Error in upgrade", e);
			addActionError(e.getLocalizedMessage());
		}

		return SUCCESS;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public OperatorAccount getRestrictToOperator() {
		return restrictToOperator;
	}

	public void setRestrictToOperator(OperatorAccount restrictToOperator) {
		this.restrictToOperator = restrictToOperator;
	}

	private boolean needsUpgrade() {
		return !findRequestsNeedingConversion().isEmpty();
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void performUpgrade() {
		for (ContractorRegistrationRequest request : findRequestsNeedingConversion()) {
			ContractorAccount toContractor = createContractorFrom(request);
			User user = copyUserFrom(request, toContractor);
			copyRequestingOperatorFrom(request, toContractor);

			// Move notes and tag
			copyNotesFrom(request, toContractor);
			copyTagsFrom(request, toContractor);

			toContractor.setPrimaryContact(user);
			toContractor.getUsers().add(user);
			toContractor = (ContractorAccount) dao.save(toContractor);

			request.setContractor(toContractor);
			dao.save(request);
		}
	}

	private List<ContractorRegistrationRequest> findRequestsNeedingConversion() {
		if (requestsNeedingConversion == null) {
			String where = "t.contractor IS NULL";

			if (restrictToOperator.getId() > 0) {
				where += " AND t.requestedBy.id = " + restrictToOperator.getId();
			}

			requestsNeedingConversion = dao.findWhere(ContractorRegistrationRequest.class, where, limit);
		}

		return requestsNeedingConversion;
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private ContractorAccount createContractorFrom(ContractorRegistrationRequest request) {
		ContractorAccount contractor = new ContractorAccount();

		if (ContractorRegistrationRequestStatus.ClosedUnsuccessful == request.getStatus()) {
			contractor.setStatus(AccountStatus.Declined);
		} else {
			contractor.setStatus(AccountStatus.Requested);
		}

		String name = request.getName();
		if (name.length() > 50) {
			contractor.setName(name.substring(0, 50));
			contractor.setDbaName(name);
		} else {
			contractor.setName(name);
		}

		contractor.setTaxId(request.getTaxID());
		contractor.setAddress(request.getAddress());
		contractor.setCity(request.getCity());
		contractor.setZip(request.getZip());
		contractor.setCountry(request.getCountry());
		contractor.setCountrySubdivision(request.getCountrySubdivision());
		contractor.setRequestedBy(request.getRequestedBy());
		// Other required fields
		contractor.setNaics(new Naics());
		contractor.getNaics().setCode("0");
		contractor.setNaicsValid(false);
		contractor.setCreatedBy(request.getCreatedBy());
		contractor.setCreationDate(request.getCreationDate());
		contractor.setUpdateDate(request.getUpdateDate());
		contractor.setUpdatedBy(request.getUpdatedBy());
		// Contact information
		contractor.setContactCountByEmail(request.getContactCountByEmail());
		contractor.setContactCountByPhone(request.getContactCountByPhone());
		contractor.setLastContactedByAutomatedEmailDate(request.getLastContactedByAutomatedEmailDate());
		contractor.setLastContactedByInsideSales(request.getLastContactedBy());
		contractor.setLastContactedByInsideSalesDate(request.getLastContactDate());
		// Reason
		contractor.setReason(request.getReasonForDecline());

		return (ContractorAccount) dao.save(contractor);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void copyRequestingOperatorFrom(ContractorRegistrationRequest request, ContractorAccount toContractor) {
		ContractorOperator link = new ContractorOperator();
		link.setContractorAccount(toContractor);
		link.setOperatorAccount(request.getRequestedBy());

		if (request.getRequestedByUser() != null) {
			link.setRequestedBy(request.getRequestedByUser());
		} else {
			link.setRequestedByOther(request.getRequestedByUserOther());
		}

		link.setDeadline(request.getDeadline());
		link.setReasonForRegistration(request.getReasonForRegistration());

		// Other required fields
		link.setFlagColor(FlagColor.Clear);
		link.setAuditColumns();

		link = (ContractorOperator) dao.save(link);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private User copyUserFrom(ContractorRegistrationRequest request, ContractorAccount toContractor) {
		User user = new User();
		user.setName(request.getContact());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setPhoneIndex(Strings.stripPhoneNumber(user.getPhone()));
		user.setUsername(request.getEmail() + "-" + toContractor.getId());
		user.setAccount(toContractor);
		// Other required fields
		user.setIsGroup(YesNo.No);
		user.setAuditColumns();

		user = (User) dao.save(user);
		return user;
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void copyNotesFrom(ContractorRegistrationRequest request, ContractorAccount toContractor) {
		Note note = new Note();
		note.setAccount(toContractor);
		note.setAuditColumns(permissions);
		note.setSummary("Imported from Registration Request");
		note.setPriority(LowMedHigh.Med);
		note.setNoteCategory(NoteCategory.Registration);
		note.setViewableBy(request.getRequestedBy());
		note.setCanContractorView(false);
		note.setStatus(NoteStatus.Closed);
		note.setBody(request.getNotes());
		dao.save(note);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void copyTagsFrom(ContractorRegistrationRequest request, ContractorAccount toContractor) {
		if (!Strings.isEmpty(request.getOperatorTags())) {
			for (String tag : request.getOperatorTags().split(",")) {
				try {
					int tagID = Integer.parseInt(tag);

					OperatorTag operatorTag = dao.find(OperatorTag.class, tagID);

					ContractorTag contractorTag = new ContractorTag();
					contractorTag.setContractor(toContractor);
					contractorTag.setTag(operatorTag);
					contractorTag.setAuditColumns(permissions);

					dao.save(contractorTag);
					toContractor.getOperatorTags().add(contractorTag);
				} catch (Exception e) {
					logger.error("Could not parse operator tag {} from {} for registration request #{}", new Object[] {
							tag, request.getOperatorTags(), request.getId() });
				}
			}
		}
	}
}
