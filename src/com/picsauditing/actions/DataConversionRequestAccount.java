package com.picsauditing.actions;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;

@SuppressWarnings("serial")
public class DataConversionRequestAccount extends PicsActionSupport {
	private int limit = 10;

	private static Logger logger = LoggerFactory.getLogger(DataConversionRequestAccount.class);

	@Autowired
	private ContractorAccountDAO contractorDAO;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private OperatorAccountDAO operatorDAO;

	List<ContractorRegistrationRequest> requestsNeedingConversion = Collections.emptyList();

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

			contractor.setPrimaryContact(user);
			contractor.getUsers().add(user);
			contractor = (ContractorAccount) contractorDAO.save(contractor);

			request.setContractor(contractor);
			requestDAO.save(request);
		}
	}

	private ContractorAccount createContractorFrom(ContractorRegistrationRequest request) {
		ContractorAccount contractor = new ContractorAccount();
		contractor.setStatus(AccountStatus.Requested);
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

		return (ContractorAccount) contractorDAO.save(contractor);
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

		link = (ContractorOperator) contractorOperatorDAO.save(link);
	}

	private User createUserFrom(ContractorRegistrationRequest request, ContractorAccount contractor) {
		User user = new User();
		user.setName(request.getContact());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setUsername(request.getEmail() + "-" + request.getId());
		user.setAccount(contractor);
		// Other required fields
		user.setIsGroup(YesNo.No);
		user.setAuditColumns();

		user = userDAO.save(user);
		return user;
	}

	private List<ContractorRegistrationRequest> findRequestsNeedingConversion() {
		return requestDAO.findWhere(ContractorRegistrationRequest.class,
				"t.contractor IS NULL AND t.status IN ('Active', 'Hold')", limit);
	}
}
