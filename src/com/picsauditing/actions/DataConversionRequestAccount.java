package com.picsauditing.actions;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;

@SuppressWarnings("serial")
public class DataConversionRequestAccount extends PicsActionSupport {
	@Autowired
	private ContractorAccountDAO contractorDAO;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorRegistrationRequest requestDAO;
	@Autowired
	private OperatorAccountDAO operatorDAO;

	@Anonymous
	@Override
	public String execute() {
		return SUCCESS;
	}

	private boolean needsUpgrade() {
		return false;
	}

	private void upgrade() {

	}

	private List<ContractorRegistrationRequest> findRequestsNeedingConversion() {
		return Collections.emptyList();
	}
}
