package com.picsauditing.model.events;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;

public class AuditDataSaveEventListener implements ApplicationListener<AuditDataSaveEvent> {
	private final Logger logger = LoggerFactory.getLogger(AuditDataSaveEventListener.class);

	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;

	@Override
	public void onApplicationEvent(AuditDataSaveEvent event) {
		AuditData auditData = (AuditData) event.getSource();

		if (clockNeedsReseting(auditData)) {
			resetClock(getApplicableOperators(auditData));
		}

		logger.debug("here");
	}

	public boolean clockNeedsReseting(AuditData auditData) {
		// currently the event is only fired when question is answered for the
		// first time
		// we can add logic here if need be later

		return true;
	}

	public Set<ContractorOperator> getApplicableOperators(AuditData auditData) {
		Set<ContractorOperator> contractorOperators = new HashSet<ContractorOperator>();
		List<ContractorAuditOperator> caos = auditData.getAudit().getOperatorsVisible();

		for (ContractorAuditOperator cao : caos) {
			contractorOperators.add(contractorOperatorDAO.find(cao.getAudit().getContractorAccount().getId(), cao.getOperator()
					.getId()));
		}

		return contractorOperators;
	}

	public void resetClock(Set<ContractorOperator> contractorOperators) {
		for (ContractorOperator contractorOperator : contractorOperators) {
			contractorOperator.setLastStepToGreenDate(new Date());
		}
	}
}
