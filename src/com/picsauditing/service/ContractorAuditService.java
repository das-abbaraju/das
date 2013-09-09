package com.picsauditing.service;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractorAuditService {

	private static final Logger logger = LoggerFactory.getLogger(ContractorAuditService.class);

	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;

	public ContractorAudit findContractorAudit(int auditId) {
		return contractorAuditDAO.find(auditId);
	}


}
