package com.picsauditing.actions;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Anonymous;
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
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class DataConversionRequestAccount extends AccountActionSupport {
	private static Logger logger = LoggerFactory.getLogger(DataConversionRequestAccount.class);

	private int limit = 10;
	private List<ContractorRegistrationRequest> requestsNeedingConversion = Collections.emptyList();

	@Anonymous
	@Override
	public String execute() {
		loadPermissions();

		requestsNeedingConversion = findRequestsNeedingConversion();

		if (needsUpgrade()) {
			try {
				upgrade();
				addActionMessage("Successfully converted " + requestsNeedingConversion.size() + " requests");
			} catch (Exception e) {
				logger.error("Error in upgrade", e);
				addActionError(e.getLocalizedMessage());
			}
		}

		return SUCCESS;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	private boolean needsUpgrade() {
		return !requestsNeedingConversion.isEmpty();
	}

	private void upgrade() {
		for (ContractorRegistrationRequest request : requestsNeedingConversion) {
			ContractorAccount contractor = createContractorFrom(request);
			createContractorOperatorFrom(request, contractor);
			User user = createUserFrom(request, contractor);

			// Move notes and tag
			Note note = addNote(contractor, "Imported from Registration Request", NoteCategory.Registration,
					LowMedHigh.Med, false, 1, request.getLastContactedBy());
			note.setBody(request.getNotes());
			dao.save(note);

			addContractorTags(request, contractor);

			contractor.setPrimaryContact(user);
			contractor.getUsers().add(user);
			contractor = (ContractorAccount) dao.save(contractor);

			request.setContractor(contractor);
			dao.save(request);
		}
	}

	private List<ContractorRegistrationRequest> findRequestsNeedingConversion() {
		return dao.findWhere(ContractorRegistrationRequest.class, "t.contractor IS NULL", limit);
	}

	private ContractorAccount createContractorFrom(ContractorRegistrationRequest request) {
		ContractorAccount contractor = new ContractorAccount();

		if (ContractorRegistrationRequestStatus.ClosedUnsuccessful == request.getStatus()) {
			contractor.setStatus(AccountStatus.Deactivated);
		} else {
			contractor.setStatus(AccountStatus.Requested);
		}

		contractor.setName(request.getName());
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

		return (ContractorAccount) dao.save(contractor);
	}

	private void createContractorOperatorFrom(ContractorRegistrationRequest request, ContractorAccount contractor) {
		ContractorOperator link = new ContractorOperator();
		link.setContractorAccount(contractor);
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

	private User createUserFrom(ContractorRegistrationRequest request, ContractorAccount contractor) {
		User user = new User();
		user.setName(request.getContact());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setUsername(request.getEmail() + "-" + contractor.getId());
		user.setAccount(contractor);
		// Other required fields
		user.setIsGroup(YesNo.No);
		user.setAuditColumns();

		user = userDAO.save(user);
		return user;
	}

	private void addContractorTags(ContractorRegistrationRequest request, ContractorAccount contractor) {
		if (!Strings.isEmpty(request.getOperatorTags())) {
			for (String tag : request.getOperatorTags().split(",")) {
				try {
					int tagID = Integer.parseInt(tag);

					OperatorTag operatorTag = dao.find(OperatorTag.class, tagID);

					ContractorTag contractorTag = new ContractorTag();
					contractorTag.setContractor(contractor);
					contractorTag.setTag(operatorTag);
					contractorTag.setAuditColumns(permissions);

					dao.save(contractorTag);
					contractor.getOperatorTags().add(contractorTag);
				} catch (Exception e) {
					logger.error("Could not parse operator tag {} from {} for registration request #{}", new Object[] {
							tag, request.getOperatorTags(), request.getId() });
				}
			}
		}
	}
}
